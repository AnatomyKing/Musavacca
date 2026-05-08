// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoPearlPortalLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoReceptorBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;

public final class VocoPearlPortalLogic {
    private static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    private VocoPearlPortalLogic() {}

    public static void refreshReceptorBelowCandle(Level level, BlockPos candlePos) {
        if (level == null || candlePos == null || level.isClientSide()) {
            return;
        }

        refreshReceptorAt(level, candlePos.below());
    }

    public static void refreshReceptorAt(Level level, BlockPos receptorPos) {
        if (level == null || receptorPos == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(receptorPos);
        if (!(state.getBlock() instanceof VocoReceptorBlock)) {
            return;
        }

        BlockState updated = updateReceptorStateFromTop(level, receptorPos, state);

        if (updated != state) {
            level.setBlock(receptorPos, updated, UPDATE_FLAGS);
        }
    }

    public static BlockState updateReceptorStateFromTop(
            Level level,
            BlockPos receptorPos,
            BlockState receptorState
    ) {
        PortalInfo portalInfo = readPortalInfo(level, receptorPos, receptorState);

        BlockEntity be = level.getBlockEntity(receptorPos);
        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            if (portalInfo.active()) {
                receptorBe.setHexColor(portalInfo.hexColor());
            } else {
                receptorBe.clearHexColor();
            }
        }

        if (!receptorState.hasProperty(VocoReceptorBlock.PORTAL)) {
            return receptorState;
        }

        boolean wasPortal = receptorState.getValue(VocoReceptorBlock.PORTAL);
        boolean shouldBePortal = portalInfo.active();

        if (!wasPortal && shouldBePortal && level != null && !level.isClientSide()) {
            playPortalAppearSound(level, receptorPos);
        }

        return receptorState.setValue(VocoReceptorBlock.PORTAL, shouldBePortal);
    }

    private static void playPortalAppearSound(Level level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.BLOCKS,
                0.65F,
                1.25F
        );
    }

    private static PortalInfo readPortalInfo(
            Level level,
            BlockPos receptorPos,
            BlockState receptorState
    ) {
        if (!receptorState.hasProperty(VocoReceptorBlock.LIT)
                || !receptorState.getValue(VocoReceptorBlock.LIT)) {
            return PortalInfo.INACTIVE;
        }

        BlockPos candlePos = receptorPos.above();
        BlockState candleState = level.getBlockState(candlePos);

        if (!(candleState.getBlock() instanceof PearlCandleBlock)) {
            return PortalInfo.INACTIVE;
        }

        if (!candleState.hasProperty(BlockStateProperties.LIT)
                || !candleState.getValue(BlockStateProperties.LIT)) {
            return PortalInfo.INACTIVE;
        }

        if (candleState.hasProperty(BlockStateProperties.WATERLOGGED)
                && candleState.getValue(BlockStateProperties.WATERLOGGED)) {
            return PortalInfo.INACTIVE;
        }

        BlockEntity candleBe = level.getBlockEntity(candlePos);
        if (!(candleBe instanceof PearlCandleBlockEntity pearlCandleBe)) {
            return PortalInfo.INACTIVE;
        }

        if (!pearlCandleBe.hasHexColor()) {
            return PortalInfo.INACTIVE;
        }

        return new PortalInfo(true, pearlCandleBe.getHexColor());
    }

    private record PortalInfo(boolean active, int hexColor) {
        private static final PortalInfo INACTIVE =
                new PortalInfo(false, VocoReceptorBlockEntity.UNSET_HEX_COLOR);
    }
}