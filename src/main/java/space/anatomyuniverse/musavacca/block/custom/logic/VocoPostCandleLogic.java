package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;

public final class VocoPostCandleLogic {
    private VocoPostCandleLogic() {}

    public static void onPlace(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            refreshPortalAt(level, pos);
        }
    }

    public static BlockState updateShape(
            BlockState state,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.UP && levelReader instanceof Level level && !level.isClientSide()) {
            return updatePortalStateFromTop(level, pos, state);
        }

        return state;
    }

    public static void refreshPostBelowCandle(Level level, BlockPos candlePos) {
        if (level != null && !level.isClientSide()) {
            refreshPortalAt(level, candlePos.below());
        }
    }

    public static void refreshPortalAt(Level level, BlockPos postPos) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(postPos);
        if (!(state.getBlock() instanceof VocoPostBlock)) {
            return;
        }

        BlockState updated = updatePortalStateFromTop(level, postPos, state);
        if (updated != state) {
            level.setBlock(postPos, updated, VocoReceptorLogic.UPDATE_FLAGS);
        }
    }

    public static BlockState updatePortalStateFromTop(
            Level level,
            BlockPos postPos,
            BlockState postState
    ) {
        PortalInfo info = readPortalInfo(level, postPos, postState);
        boolean shouldBePortal = false;
        boolean queued = false;

        BlockEntity be = level.getBlockEntity(postPos);
        if (be instanceof VocoPostBlockEntity postBe) {
            if (info.active()) {
                shouldBePortal = postBe.setHexColor(info.hexColor());

                if (shouldBePortal && level instanceof ServerLevel serverLevel) {
                    VocoTeleportLogic.SyncResult result = VocoTeleportLogic.syncEndpointDetailed(
                            serverLevel,
                            postPos,
                            VocoPostBlock.receptorPosition(postState),
                            true,
                            info.hexColor()
                    );

                    shouldBePortal = result == VocoTeleportLogic.SyncResult.ACTIVE;
                    queued = result == VocoTeleportLogic.SyncResult.QUEUED;
                }

                if (!shouldBePortal && !queued) {
                    postBe.clearHexColor();
                }
            } else {
                postBe.clearHexColor();
            }
        }

        return applyPortalState(level, postPos, postState, shouldBePortal);
    }

    private static void removeEndpoint(Level level, BlockPos postPos, BlockState postState) {
        if (level instanceof ServerLevel serverLevel) {
            VocoTeleportLogic.syncEndpointDetailed(
                    serverLevel,
                    postPos,
                    VocoPostBlock.receptorPosition(postState),
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }
    }

    private static BlockState applyPortalState(
            Level level,
            BlockPos pos,
            BlockState state,
            boolean portal
    ) {
        if (!state.hasProperty(VocoPostBlock.PORTAL)) {
            return state;
        }

        boolean wasPortal = state.getValue(VocoPostBlock.PORTAL);

        if (wasPortal != portal) {
            if (portal) {
                VocoReceptorLogic.playPortalAppearSound(level, pos);
            } else {
                VocoReceptorLogic.playPortalDisappearSound(level, pos);
            }
        }

        return state.setValue(VocoPostBlock.PORTAL, portal);
    }

    private static PortalInfo readPortalInfo(
            Level level,
            BlockPos postPos,
            BlockState postState
    ) {
        if (!postState.hasProperty(VocoPostBlock.LIT)
                || !postState.getValue(VocoPostBlock.LIT)) {
            return PortalInfo.INACTIVE;
        }

        BlockPos candlePos = postPos.above();
        BlockState candleState = level.getBlockState(candlePos);

        if (!(candleState.getBlock() instanceof PearlCandleBlock)
                || !candleState.hasProperty(BlockStateProperties.LIT)
                || !candleState.getValue(BlockStateProperties.LIT)
                || candleState.hasProperty(BlockStateProperties.WATERLOGGED)
                && candleState.getValue(BlockStateProperties.WATERLOGGED)
                || !(level.getBlockEntity(candlePos) instanceof PearlCandleBlockEntity pearlCandleBe)
                || !pearlCandleBe.hasHexColor()) {
            return PortalInfo.INACTIVE;
        }

        return new PortalInfo(true, pearlCandleBe.getHexColor());
    }

    private record PortalInfo(boolean active, int hexColor) {
        private static final PortalInfo INACTIVE =
                new PortalInfo(false, VocoReceptorLogic.UNSET_HEX_COLOR);
    }
}
