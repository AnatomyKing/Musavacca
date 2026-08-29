package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoPostLogic {
    private VocoPostLogic() {}

    public static InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        ReceptorPosition receptor = receptorHit(state, pos, hit);
        if (receptor == null) {
            return InteractionResult.PASS;
        }

        if (VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!VocoReceptorLogic.isCompletelyEmptyHanded(player)) {
            return InteractionResult.PASS;
        }

        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(
                        VocoPostBlock.LIT,
                        VocoPostBlock.PORTAL,
                        receptor
                );

        if (!PearlSlotIgnition.isLit(state, pearlSlot)) {
            if (
                    !level.isClientSide()
                            && PearlSlotIgnition.igniteFromBalance(
                            state,
                            level,
                            pos,
                            player,
                            pearlSlot
                    )
            ) {
                VocoPostCandleLogic.refreshPortalAt(level, pos);
            }

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoPostBlock.PORTAL) && level.isClientSide()) {
            VocoReceptorLogic.showNeedsPortalMessage(player);
        }

        return InteractionResult.SUCCESS;
    }

    public static InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (stack.isEmpty()) {
            //? if <1.21.2 {
            /*return InteractionResult.PASS;
            *///?} else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
            //?}
        }

        ReceptorPosition receptor = receptorHit(state, pos, hit);
        if (receptor == null) {
            return InteractionResult.PASS;
        }

        if (VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(
                        VocoPostBlock.LIT,
                        VocoPostBlock.PORTAL,
                        receptor
                );

        InteractionResult result =
                PearlSlotIgnition.handleHeldItemUse(
                        stack,
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        pearlSlot
                );

        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoPostCandleLogic.refreshPortalAt(level, pos);
        }

        return result;
    }

    @Nullable
    private static ReceptorPosition receptorHit(BlockState state, BlockPos pos, BlockHitResult hit) {
        return VocoPostReceptorHitboxes.detectHitPart(pos, hit).isReceptor()
                ? VocoPostBlock.receptorPosition(state)
                : null;
    }
}



