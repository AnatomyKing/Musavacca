package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlMenu;
import space.anatomyuniverse.musavacca.portal.PearlPortalCreator;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;
import space.anatomyuniverse.musavacca.tint.PearlPlacementColorMemory;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class FlintAndPearlItem extends FlintAndSteelItem {
    public static final int DEFAULT_HEX_COLOR = 0xD5CD49;

    private static final Component TITLE = Component.literal("Pearl Fire Hex");

    public FlintAndPearlItem(Properties properties) {
        super(properties);
    }

    protected BlockState getCustomFireState(Level level, BlockPos pos) {
        return ModBlocks.PEARL_FIRE.get().getPlacementState(level, pos);
    }

    private static EquipmentSlot slotForHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND;
    }

    private static boolean shouldOpenGui(Level level, Player player) {
        return getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getType() == HitResult.Type.MISS;
    }

    private static void openGui(ServerPlayer serverPlayer, InteractionHand hand, ItemStack stack) {
        int initialHex = getStoredHexOrDefault(stack);

        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (containerId, playerInventory, openingPlayer) ->
                                new FlintAndPearlMenu(containerId, playerInventory, hand, initialHex),
                        TITLE
                ),
                buf -> {
                    buf.writeBoolean(hand == InteractionHand.OFF_HAND);
                    buf.writeInt(initialHex);
                }
        );
    }

    public static int getStoredHexOrDefault(ItemStack stack) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        return TintColorUtil.rgb(savedHex != null ? savedHex : DEFAULT_HEX_COLOR);
    }

    public static void ensureDefaultColorComponent(ItemStack stack) {
        if (stack.get(ModDataComponents.HEX_COLOR.get()) == null) {
            stack.set(ModDataComponents.HEX_COLOR.get(), DEFAULT_HEX_COLOR);
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        ensureDefaultColorComponent(stack);

        int hexColor = getStoredHexOrDefault(stack);
        InteractionResult candleResult = tryUseOnCandleBeforeVanilla(context, hexColor);

        if (candleResult != InteractionResult.PASS) {
            return candleResult;
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult tryUseOnCandleBeforeVanilla(UseOnContext context, int hexColor) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof PearlCandleBlock pearlCandleBlock) {
            return useOnPearlCandle(context, pearlCandleBlock, state, pos, hexColor);
        }

        PearlCandleBlock matchingPearlCandle = pearlCandleForVanillaCandle(block);
        if (matchingPearlCandle == null) {
            return InteractionResult.PASS;
        }

        if (!PearlCandleBlock.canPearlLight(state)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS_SERVER;
        }

        BlockState pearlState = matchingPearlCandle.copyStateFromVanillaCandle(state, true);

        boolean changed = level.setBlock(
                pos,
                pearlState,
                Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
        );

        if (!changed) {
            return InteractionResult.FAIL;
        }

        setPearlCandleHex(level, pos, hexColor);

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        playUseEffects(level, player, pos, GameEvent.BLOCK_CHANGE);
        triggerPlacedBlockCriterion(player, pos, stack);
        damageStack(stack, player, context.getHand());

        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult useOnPearlCandle(
            UseOnContext context,
            PearlCandleBlock pearlCandleBlock,
            BlockState state,
            BlockPos pos,
            int hexColor
    ) {
        if (!PearlCandleBlock.canPearlLight(state)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS_SERVER;
        }

        BlockState finalState = state;

        /*
         * Normally Pearl Candles should only exist while lit.
         * But this keeps the item safe if an old/stale Pearl Candle somehow exists unlit.
         */
        if (state.hasProperty(CandleBlock.LIT) && !state.getValue(CandleBlock.LIT)) {
            finalState = state.setValue(CandleBlock.LIT, true);

            level.setBlock(
                    pos,
                    finalState,
                    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
            );
        }

        setPearlCandleHex(level, pos, hexColor);

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        playUseEffects(level, player, pos, GameEvent.BLOCK_CHANGE);
        damageStack(stack, player, context.getHand());

        return InteractionResult.SUCCESS_SERVER;
    }

    private static void setPearlCandleHex(Level level, BlockPos pos, int hexColor) {
        if (level.isClientSide()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof PearlCandleBlockEntity pearlCandleBe) {
            pearlCandleBe.setHexColor(hexColor);
        }
    }

    @Nullable
    private static PearlCandleBlock pearlCandleForVanillaCandle(Block block) {
        if (!(block instanceof CandleBlock)) {
            return null;
        }

        if (block == Blocks.CANDLE) return ModBlocks.PEARL_CANDLE.get();
        if (block == Blocks.WHITE_CANDLE) return ModBlocks.PEARL_WHITE_CANDLE.get();
        if (block == Blocks.ORANGE_CANDLE) return ModBlocks.PEARL_ORANGE_CANDLE.get();
        if (block == Blocks.MAGENTA_CANDLE) return ModBlocks.PEARL_MAGENTA_CANDLE.get();
        if (block == Blocks.LIGHT_BLUE_CANDLE) return ModBlocks.PEARL_LIGHT_BLUE_CANDLE.get();
        if (block == Blocks.YELLOW_CANDLE) return ModBlocks.PEARL_YELLOW_CANDLE.get();
        if (block == Blocks.LIME_CANDLE) return ModBlocks.PEARL_LIME_CANDLE.get();
        if (block == Blocks.PINK_CANDLE) return ModBlocks.PEARL_PINK_CANDLE.get();
        if (block == Blocks.GRAY_CANDLE) return ModBlocks.PEARL_GRAY_CANDLE.get();
        if (block == Blocks.LIGHT_GRAY_CANDLE) return ModBlocks.PEARL_LIGHT_GRAY_CANDLE.get();
        if (block == Blocks.CYAN_CANDLE) return ModBlocks.PEARL_CYAN_CANDLE.get();
        if (block == Blocks.PURPLE_CANDLE) return ModBlocks.PEARL_PURPLE_CANDLE.get();
        if (block == Blocks.BLUE_CANDLE) return ModBlocks.PEARL_BLUE_CANDLE.get();
        if (block == Blocks.BROWN_CANDLE) return ModBlocks.PEARL_BROWN_CANDLE.get();
        if (block == Blocks.GREEN_CANDLE) return ModBlocks.PEARL_GREEN_CANDLE.get();
        if (block == Blocks.RED_CANDLE) return ModBlocks.PEARL_RED_CANDLE.get();
        if (block == Blocks.BLACK_CANDLE) return ModBlocks.PEARL_BLACK_CANDLE.get();

        return null;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ensureDefaultColorComponent(stack);

        if (shouldOpenGui(level, player)) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                openGui(serverPlayer, hand, stack);
            }

            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        ItemStack stack = context.getItemInHand();

        ensureDefaultColorComponent(stack);

        BlockState modifiedState = clickedState.getToolModifiedState(
                context,
                ItemAbilities.FIRESTARTER_LIGHT,
                false
        );

        if (modifiedState != null) {
            return useVanillaFirestarterBehavior(context, modifiedState);
        }

        BlockPos placePos = clickedPos.relative(context.getClickedFace());
        int hexColor = getStoredHexOrDefault(stack);

        if (level.isClientSide()) {
            return previewClientPlacement(level, placePos, hexColor);
        }

        if (PearlPortalCreator.tryCreatePortal(level, placePos, hexColor, player, context.getClickedFace())) {
            playUseEffects(level, player, placePos, GameEvent.BLOCK_PLACE);
            triggerPlacedBlockCriterion(player, placePos, stack);
            damageStack(stack, player, context.getHand());
            return InteractionResult.SUCCESS_SERVER;
        }

        return placePearlFire(context, placePos, hexColor);
    }

    private InteractionResult previewClientPlacement(Level level, BlockPos placePos, int hexColor) {
        var optionalShape = PearlPortalFrame.findIgnitableShape(level, placePos);

        if (optionalShape.isPresent()) {
            optionalShape.get().forEachInteriorBlock(pos ->
                    PearlPlacementColorMemory.remember(level, pos, hexColor)
            );

            return InteractionResult.SUCCESS_SERVER;
        }

        return previewPearlFirePlacement(level, placePos, hexColor);
    }

    private InteractionResult previewPearlFirePlacement(Level level, BlockPos placePos, int hexColor) {
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        BlockState fireState = getCustomFireState(level, placePos);
        if (!fireState.canSurvive(level, placePos)) {
            return InteractionResult.FAIL;
        }

        PearlPlacementColorMemory.remember(level, placePos, hexColor);
        return InteractionResult.SUCCESS_SERVER;
    }

    private InteractionResult placePearlFire(UseOnContext context, BlockPos placePos, int hexColor) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        BlockState fireState = getCustomFireState(level, placePos);
        if (!fireState.canSurvive(level, placePos)) {
            return InteractionResult.FAIL;
        }

        boolean placed = level.setBlock(
                placePos,
                fireState,
                Block.UPDATE_NEIGHBORS
        );

        if (!placed) {
            return InteractionResult.FAIL;
        }

        setFreshlyPlacedPearlFireHex(level, placePos, hexColor);

        fireState.getBlock().setPlacedBy(level, placePos, fireState, player, stack);

        playUseEffects(level, player, placePos, GameEvent.BLOCK_PLACE);
        triggerPlacedBlockCriterion(player, placePos, stack);
        damageStack(stack, player, context.getHand());

        return InteractionResult.SUCCESS_SERVER;
    }

    private static void setFreshlyPlacedPearlFireHex(Level level, BlockPos pos, int hexColor) {
        if (level.isClientSide()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof PearlFireBlockEntity pearlFireBe) {
            pearlFireBe.setHexColor(hexColor);
        }
    }

    private static InteractionResult useVanillaFirestarterBehavior(UseOnContext context, BlockState modifiedState) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (!level.isClientSide()) {
            level.setBlock(
                    clickedPos,
                    modifiedState,
                    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
            );

            playUseEffects(level, player, clickedPos, GameEvent.BLOCK_CHANGE);
            damageStack(stack, player, context.getHand());
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    private static void playUseEffects(
            Level level,
            @Nullable Player player,
            BlockPos pos,
            Holder<GameEvent> gameEvent
    ) {
        level.playSound(
                null,
                pos,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.getRandom().nextFloat() * 0.4F + 0.8F
        );

        level.gameEvent(player, gameEvent, pos);
    }

    private static void triggerPlacedBlockCriterion(@Nullable Player player, BlockPos pos, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
        }
    }

    private static void damageStack(ItemStack stack, @Nullable Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer, slotForHand(hand));
        } else if (player != null) {
            stack.hurtAndBreak(1, player, slotForHand(hand));
        }
    }
}