// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/FlintAndPearlItem.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.menu.ItemInteractMenu;
import space.anatomyuniverse.musavacca.tint.PearlFirePlacementColorMemory;
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
                                new ItemInteractMenu(containerId, playerInventory, hand, initialHex),
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

        if (modifiedState == null) {
            BlockPos placePos = clickedPos.relative(context.getClickedFace());

            if (!level.getBlockState(placePos).canBeReplaced()) {
                return InteractionResult.FAIL;
            }

            BlockState fireState = getCustomFireState(level, placePos);

            if (!fireState.canSurvive(level, placePos)) {
                return InteractionResult.FAIL;
            }

            if (level.isClientSide()) {
                PearlFirePlacementColorMemory.remember(placePos, getStoredHexOrDefault(stack));
                return InteractionResult.SUCCESS_SERVER;
            }

            boolean placed = level.setBlock(
                    placePos,
                    fireState,
                    Block.UPDATE_NEIGHBORS | Block.UPDATE_IMMEDIATE
            );

            if (!placed) {
                return InteractionResult.FAIL;
            }

            fireState.getBlock().setPlacedBy(level, placePos, fireState, player, stack);

            level.playSound(
                    null,
                    placePos,
                    SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.4F + 0.8F
            );

            level.gameEvent(player, GameEvent.BLOCK_PLACE, placePos);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, stack);
                stack.hurtAndBreak(1, serverPlayer, slotForHand(context.getHand()));
            }

            return InteractionResult.SUCCESS_SERVER;
        } else {
            if (!level.isClientSide()) {
                level.setBlock(
                        clickedPos,
                        modifiedState,
                        Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
                );

                level.playSound(
                        null,
                        clickedPos,
                        SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS,
                        1.0F,
                        level.getRandom().nextFloat() * 0.4F + 0.8F
                );

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, clickedPos);

                if (player instanceof ServerPlayer serverPlayer) {
                    stack.hurtAndBreak(1, serverPlayer, slotForHand(context.getHand()));
                } else if (player != null) {
                    stack.hurtAndBreak(1, player, slotForHand(context.getHand()));
                }
            }

            return InteractionResult.SUCCESS_SERVER;
        }
    }
}