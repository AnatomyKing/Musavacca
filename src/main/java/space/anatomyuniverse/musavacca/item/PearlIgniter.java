package space.anatomyuniverse.musavacca.item;

//? if >=26.2 {
/*import net.minecraft.advancements.triggers.CriteriaTriggers;
*///?} else {
import net.minecraft.advancements.CriteriaTriggers;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlMenu;
import space.anatomyuniverse.musavacca.portal.PearlPortalCreator;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlPlacementColorMemory;

public final class PearlIgniter {
    private static final Component TITLE = Component.literal("Pearl Fire Hex");

    private PearlIgniter() {}

    public static boolean is(ItemStack stack) {
        return stack.is(Items.FLINT_AND_STEEL) && Boolean.TRUE.equals(stack.get(ModDataComponents.PEARL_IGNITER.get()));
    }

    public static ItemStack create() {
        ItemStack stack = new ItemStack(Items.FLINT_AND_STEEL);
        ensureDefaultColorComponent(stack);
        configure(stack);
        return stack;
    }

    //? if >=26.1 {
    /*public static net.minecraft.world.item.ItemStackTemplate recipeResult() {
        return new net.minecraft.world.item.ItemStackTemplate(Items.FLINT_AND_STEEL.builtInRegistryHolder(), 1,
                configuration().set(ModDataComponents.HEX_COLOR.get(), MusavaccaTints.DEFAULT_TINT).build());
    }
    *///?} else {
    public static ItemStack recipeResult() {
        return create();
    }
    //?}

    public static void configure(ItemStack stack) {
        stack.applyComponents(configuration().build());
    }

    private static DataComponentPatch.Builder configuration() {
        var components = DataComponentPatch.builder().set(ModDataComponents.PEARL_IGNITER.get(), true).set(DataComponents.ITEM_NAME,
                Component.translatable("item.musavacca.flint_and_pearl"));
        //? if >=1.21.11 {
        /*components.set(DataComponents.ITEM_MODEL, net.minecraft.resources.Identifier.fromNamespaceAndPath("musavacca", "flint_and_pearl"));
        *///?} else if >=1.21.2 {
        components.set(DataComponents.ITEM_MODEL, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("musavacca", "flint_and_pearl"));
        //?}
        return components;
    }

    private static BlockState getCustomFireState(Level level, BlockPos pos) {
        return ModBlocks.PEARL_FIRE.get().getPlacementState(level, pos);
    }

    private static EquipmentSlot slotForHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    public static boolean shouldOpenGui(Level level, Player player) {
        return player.pick(player.blockInteractionRange(), 1.0F, false).getType() == HitResult.Type.MISS;
    }

    public static void openGui(ServerPlayer serverPlayer, InteractionHand hand, ItemStack stack) {
        int initialHex = getStoredHexOrDefault(stack);
        serverPlayer.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, openingPlayer) -> new FlintAndPearlMenu(containerId, playerInventory, hand, initialHex), TITLE), buf -> {
                    buf.writeBoolean(hand == InteractionHand.OFF_HAND);
                    buf.writeInt(initialHex);
                });
    }

    public static int getStoredHexOrDefault(ItemStack stack) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        int stored = savedHex == null ? MusavaccaTints.NO_TINT : MusavaccaTints.stored(savedHex);
        return MusavaccaTints.orDefault(stored);
    }

    public static void ensureDefaultColorComponent(ItemStack stack) {
        if (stack.get(ModDataComponents.HEX_COLOR.get()) == null) {
            stack.set(ModDataComponents.HEX_COLOR.get(), MusavaccaTints.DEFAULT_TINT);
        }
    }

    public static InteractionResult tryPickBlockColor(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
        Level level = context.getLevel();
        int hexColor = MusavaccaTints.blockEntityHexColor(level.getBlockEntity(context.getClickedPos()));
        if (!MusavaccaTints.hasTint(hexColor)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            stack.set(ModDataComponents.HEX_COLOR.get(), hexColor);
        }
        return successResult(level);
    }

    public static InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        ItemStack stack = context.getItemInHand();
        ensureDefaultColorComponent(stack);
        BlockState modifiedState = clickedState.getToolModifiedState(context, ItemAbilities.FIRESTARTER_LIGHT, false);
        if (modifiedState != null) return useVanillaFirestarterBehavior(context, modifiedState);
        BlockPos placePos = clickedPos.relative(context.getClickedFace());
        int hexColor = getStoredHexOrDefault(stack);
        if (level.isClientSide()) return previewClientPlacement(level, placePos, hexColor);
        if (PearlPortalCreator.tryCreatePortal(level, placePos, hexColor, player, context.getClickedFace())) {
            playUseEffects(level, player, placePos, GameEvent.BLOCK_PLACE);
            triggerPlacedBlockCriterion(player, placePos, stack);
            damageStack(stack, player, context.getHand());
            return successResult(level);
        }
        return placePearlFire(context, placePos, hexColor);
    }

    private static InteractionResult previewClientPlacement(Level level, BlockPos placePos, int hexColor) {
        var portalShape = PearlPortalFrame.findIgnitableShape(level, placePos);
        if (portalShape.isPresent()) {
            portalShape.get().forEachInteriorBlock(pos -> PearlPlacementColorMemory.remember(level, pos, hexColor));
            return successResult(level);
        }
        if (!level.getBlockState(placePos).canBeReplaced()) return InteractionResult.FAIL;
        BlockState fireState = getCustomFireState(level, placePos);
        if (!fireState.canSurvive(level, placePos)) return InteractionResult.FAIL;
        PearlPlacementColorMemory.remember(level, placePos, hexColor);
        return successResult(level);
    }

    private static InteractionResult placePearlFire(UseOnContext context, BlockPos placePos, int hexColor) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (!level.getBlockState(placePos).canBeReplaced()) return InteractionResult.FAIL;
        BlockState fireState = getCustomFireState(level, placePos);
        if (!fireState.canSurvive(level, placePos)) return InteractionResult.FAIL;
        boolean placed = level.setBlock(placePos, fireState, Block.UPDATE_NEIGHBORS);
        if (!placed) return InteractionResult.FAIL;
        setFreshlyPlacedPearlFireHex(level, placePos, hexColor);
        fireState.getBlock().setPlacedBy(level, placePos, fireState, player, stack);
        playUseEffects(level, player, placePos, GameEvent.BLOCK_PLACE);
        triggerPlacedBlockCriterion(player, placePos, stack);
        damageStack(stack, player, context.getHand());
        return successResult(level);
    }

    private static void setFreshlyPlacedPearlFireHex(Level level, BlockPos pos, int hexColor) {
        if (level.isClientSide()) return;
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
            level.setBlock(clickedPos, modifiedState, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
            playUseEffects(level, player, clickedPos, GameEvent.BLOCK_CHANGE);
            damageStack(stack, player, context.getHand());
        }
        return successResult(level);
    }

    private static void playUseEffects(Level level, @Nullable Player player, BlockPos pos, Holder<GameEvent> gameEvent) {
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.gameEvent(player, gameEvent, pos);
    }

    private static void triggerPlacedBlockCriterion(@Nullable Player player, BlockPos pos, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
        }
    }

    private static InteractionResult successResult(Level level) {
        //? if >=1.21.2 {
        return InteractionResult.SUCCESS_SERVER;
        //?} else {
        /*return InteractionResult.sidedSuccess(level.isClientSide());
        *///?}
    }

    private static void damageStack(ItemStack stack, @Nullable Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer, slotForHand(hand));
        } else if (player != null) {
            stack.hurtAndBreak(1, player, slotForHand(hand));
        }
    }
}
