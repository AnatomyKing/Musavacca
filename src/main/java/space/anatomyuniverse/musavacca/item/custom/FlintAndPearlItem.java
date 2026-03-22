// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/FlintAndPearlItem.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import space.anatomyuniverse.musavacca.block.ModBlocks;

public class FlintAndPearlItem extends FlintAndSteelItem {
    public FlintAndPearlItem(Properties properties) {
        super(properties);
    }

    /**
     * Your custom hook.
     * Future subclasses can override this to place a different custom fire.
     */
    protected BlockState getCustomFireState(Level level, BlockPos pos) {
        return ModBlocks.PEARL_FIRE.get().getPlacementState(level, pos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // Keep vanilla-style FIRESTARTER_LIGHT support first
        BlockState modifiedState = clickedState.getToolModifiedState(
                context,
                ItemAbilities.FIRESTARTER_LIGHT,
                false
        );

        if (modifiedState == null) {
            BlockPos placePos = clickedPos.relative(context.getClickedFace());

            if (!level.getBlockState(placePos).isAir()) {
                return InteractionResult.FAIL;
            }

            BlockState fireState = getCustomFireState(level, placePos);

            if (!fireState.canSurvive(level, placePos)) {
                return InteractionResult.FAIL;
            }

            level.playSound(
                    player,
                    placePos,
                    SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.4F + 0.8F
            );

            level.setBlock(placePos, fireState, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, clickedPos);

            ItemStack stack = context.getItemInHand();
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, stack);
                stack.hurtAndBreak(
                        1,
                        serverPlayer,
                        LivingEntity.getSlotForHand(context.getHand())
                );
            }

            return InteractionResult.SUCCESS;
        } else {
            level.playSound(
                    player,
                    clickedPos,
                    SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.4F + 0.8F
            );

            level.setBlock(clickedPos, modifiedState, 11);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, clickedPos);

            if (player != null) {
                context.getItemInHand().hurtAndBreak(
                        1,
                        player,
                        LivingEntity.getSlotForHand(context.getHand())
                );
            }

            return InteractionResult.SUCCESS;
        }
    }
}