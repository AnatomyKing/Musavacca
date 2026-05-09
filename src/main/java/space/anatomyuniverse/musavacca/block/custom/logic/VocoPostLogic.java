// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoPostLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostReceptorHitboxes.HitPart;
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
        HitPart hitPart = VocoPostReceptorHitboxes.detectHitPart(pos, hit);

        if (!hitPart.isReceptor()) {
            return InteractionResult.PASS;
        }

        ReceptorPosition receptor = VocoPostBlock.receptorPosition(state);

        if (VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!VocoReceptorLogic.isCompletelyEmptyHanded(player)) {
            return InteractionResult.PASS;
        }

        if (!state.getValue(VocoPostBlock.LIT)) {
            if (!level.isClientSide()) {
                boolean lit = VocoReceptorLogic.lightReceptorWithBalance(
                        state,
                        level,
                        pos,
                        player,
                        VocoPostBlock.LIT
                );

                if (lit) {
                    VocoPostCandleLogic.refreshPortalAt(level, pos);
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoPostBlock.PORTAL)) {
            if (level.isClientSide()) {
                VocoReceptorLogic.showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
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
        HitPart hitPart = VocoPostReceptorHitboxes.detectHitPart(pos, hit);

        if (!hitPart.isReceptor()) {
            return InteractionResult.PASS;
        }

        ReceptorPosition receptor = VocoPostBlock.receptorPosition(state);

        if (VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        InteractionResult result = VocoReceptorLogic.handleReceptorHeldItemUse(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                VocoPostBlock.LIT,
                VocoPostBlock.PORTAL,
                receptor
        );

        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoPostCandleLogic.refreshPortalAt(level, pos);
        }

        return result;
    }
}