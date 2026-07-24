package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.basuke.summon.BasukeSummon;

public final class VocoTableItemDisplayLogic {
    private VocoTableItemDisplayLogic() {}

    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event
    ) {
        Player player =
                event.getEntity();

        if (
                !player.isShiftKeyDown()
                        || event.getHand()
                        != InteractionHand.MAIN_HAND
        ) {
            return;
        }

        Level level =
                event.getLevel();

        BlockPos pos =
                event.getPos();

        if (
                !(level.getBlockEntity(pos)
                        instanceof VocoTableBlockEntity)
        ) {
            return;
        }

        ItemStack stack =
                event.getItemStack();

        if (stack.isEmpty()) {
            return;
        }

        if (
                !VocoTableItemDisplayHitboxes
                        .detectHitPart(
                                pos,
                                event.getHitVec()
                        )
                        .isItemDisplay()
        ) {
            return;
        }

        InteractionResult result =
                useItemOn(
                        stack,
                        level,
                        pos,
                        player,
                        event.getHand(),
                        true
                );

        event.setCancellationResult(result);
        event.setCanceled(true);
    }

    public static InteractionResult useWithoutItem(
            Level level,
            BlockPos pos,
            Player player,
            boolean bulk
    ) {
        if (
                !(level.getBlockEntity(pos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return InteractionResult.CONSUME;
        }

        boolean canRemove =
                tableBe.hasDisplayedItem();

        if (level.isClientSide()) {
            return canRemove
                    ? InteractionResult.SUCCESS
                    : InteractionResult.CONSUME;
        }

        return removeDisplayedItem(
                level,
                pos,
                player,
                tableBe,
                bulk
        )
                ? InteractionResult.SUCCESS
                : InteractionResult.CONSUME;
    }

    public static InteractionResult useItemOn(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            boolean bulk
    ) {
        if (
                hand == InteractionHand.OFF_HAND
                        || stack.isEmpty()
        ) {
            return InteractionResult.CONSUME;
        }

        if (
                !(level.getBlockEntity(pos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return InteractionResult.CONSUME;
        }

        TransferAction action =
                transferAction(
                        tableBe,
                        stack
                );

        if (level.isClientSide()) {
            return action == TransferAction.NONE
                    ? InteractionResult.CONSUME
                    : InteractionResult.SUCCESS;
        }

        boolean changed =
                switch (action) {
                    case INSERT ->
                            insertDisplayedItem(
                                    level,
                                    pos,
                                    player,
                                    tableBe,
                                    stack,
                                    bulk
                            );

                    case MERGE ->
                            mergeDisplayedItem(
                                    level,
                                    pos,
                                    player,
                                    tableBe,
                                    stack,
                                    bulk
                            );

                    case SWAP ->
                            swapDisplayedItem(
                                    level,
                                    pos,
                                    player,
                                    tableBe,
                                    stack,
                                    hand
                            );

                    case NONE -> false;
                };

        if (changed) {
            BasukeSummon.trySummonFromVocoTable(
                    level,
                    pos
            );
        }

        return changed
                ? InteractionResult.SUCCESS
                : InteractionResult.CONSUME;
    }

    private static TransferAction transferAction(
            VocoTableBlockEntity tableBe,
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return TransferAction.NONE;
        }

        ItemStack displayed =
                tableBe.getDisplayedItem();

        if (!tableBe.hasDisplayedItem()) {
            return TransferAction.INSERT;
        }

        if (tableBe.canMergeDisplayedItem(stack)) {
            return tableBe.getDisplayedItemCount()
                    < Integer.MAX_VALUE
                    ? TransferAction.MERGE
                    : TransferAction.NONE;
        }

        if (
                ItemStack.isSameItemSameComponents(
                        displayed,
                        stack
                )
        ) {
            return TransferAction.NONE;
        }

        return tableBe.getDisplayedItemCount()
                <= displayed.getMaxStackSize()
                ? TransferAction.SWAP
                : TransferAction.NONE;
    }

    private static boolean insertDisplayedItem(
            Level level,
            BlockPos pos,
            Player player,
            VocoTableBlockEntity tableBe,
            ItemStack stack,
            boolean bulk
    ) {
        int moved =
                tableBe.addDisplayedItems(
                        stack,
                        transferAmount(
                                stack,
                                bulk
                        )
                );

        if (moved <= 0) {
            return false;
        }

        shrinkHeldStack(
                player,
                stack,
                moved
        );

        playItemDisplaySound(
                level,
                pos,
                SoundEvents.ITEM_FRAME_ADD_ITEM
        );

        return true;
    }

    private static boolean mergeDisplayedItem(
            Level level,
            BlockPos pos,
            Player player,
            VocoTableBlockEntity tableBe,
            ItemStack stack,
            boolean bulk
    ) {
        int moved =
                tableBe.addDisplayedItems(
                        stack,
                        transferAmount(
                                stack,
                                bulk
                        )
                );

        if (moved <= 0) {
            return false;
        }

        shrinkHeldStack(
                player,
                stack,
                moved
        );

        playItemDisplaySound(
                level,
                pos,
                SoundEvents.ITEM_FRAME_ROTATE_ITEM
        );

        return true;
    }

    private static boolean swapDisplayedItem(
            Level level,
            BlockPos pos,
            Player player,
            VocoTableBlockEntity tableBe,
            ItemStack stack,
            InteractionHand hand
    ) {
        ItemStack displayed =
                tableBe.getDisplayedItem();

        int displayedCount =
                tableBe.getDisplayedItemCount();

        if (
                displayed.isEmpty()
                        || displayedCount <= 0
                        || displayedCount
                        > displayed.getMaxStackSize()
        ) {
            return false;
        }

        ItemStack removed =
                displayed.copyWithCount(
                        displayedCount
                );

        tableBe.setDisplayedItem(
                stack,
                stack.getCount()
        );

        player.setItemInHand(
                hand,
                removed
        );

        playItemDisplaySound(
                level,
                pos,
                SoundEvents.ITEM_FRAME_ROTATE_ITEM
        );

        return true;
    }

    private static boolean removeDisplayedItem(
            Level level,
            BlockPos pos,
            Player player,
            VocoTableBlockEntity tableBe,
            boolean bulk
    ) {
        ItemStack displayed =
                tableBe.getDisplayedItem();

        if (displayed.isEmpty()) {
            return false;
        }

        int requestedAmount =
                bulk
                        ? displayed.getMaxStackSize()
                        : 1;

        ItemStack removed =
                tableBe.removeDisplayedItems(
                        requestedAmount
                );

        if (removed.isEmpty()) {
            return false;
        }

        if (!player.addItem(removed)) {
            player.drop(
                    removed,
                    false
            );
        }

        playItemDisplaySound(
                level,
                pos,
                SoundEvents.ITEM_FRAME_REMOVE_ITEM
        );

        return true;
    }

    private static int transferAmount(
            ItemStack stack,
            boolean bulk
    ) {
        if (!bulk) {
            return 1;
        }

        return Math.min(
                stack.getCount(),
                stack.getMaxStackSize()
        );
    }

    private static void shrinkHeldStack(
            Player player,
            ItemStack stack,
            int amount
    ) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(amount);
        }
    }

    private static void playItemDisplaySound(
            Level level,
            BlockPos pos,
            SoundEvent sound
    ) {
        level.playSound(
                null,
                pos,
                sound,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }

    private enum TransferAction {
        NONE,
        INSERT,
        MERGE,
        SWAP
    }
}