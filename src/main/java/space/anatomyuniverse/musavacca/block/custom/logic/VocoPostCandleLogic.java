// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoPostCandleLogic.java
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
    private static final ReceptorPosition POST_RECEPTOR = ReceptorPosition.NORTH_EAST;

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
        if (level == null || level.isClientSide()) {
            return;
        }

        refreshPortalAt(level, candlePos.below());
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
            level.setBlock(
                    postPos,
                    updated,
                    VocoReceptorLogic.UPDATE_FLAGS
            );
        }
    }

    public static BlockState updatePortalStateFromTop(
            Level level,
            BlockPos postPos,
            BlockState postState
    ) {
        PortalInfo portalInfo = readPortalInfo(level, postPos, postState);
        boolean shouldBePortal = portalInfo.active();

        BlockEntity be = level.getBlockEntity(postPos);
        if (be instanceof VocoPostBlockEntity postBe) {
            if (shouldBePortal) {
                shouldBePortal = postBe.setHexColor(portalInfo.hexColor());

                if (shouldBePortal && level instanceof ServerLevel serverLevel) {
                    shouldBePortal = VocoTeleportLogic.syncEndpoint(
                            serverLevel,
                            postPos,
                            POST_RECEPTOR,
                            true,
                            portalInfo.hexColor()
                    );
                }

                if (!shouldBePortal) {
                    postBe.clearHexColor();

                    if (level instanceof ServerLevel serverLevel) {
                        VocoTeleportLogic.syncEndpoint(
                                serverLevel,
                                postPos,
                                POST_RECEPTOR,
                                false,
                                VocoReceptorLogic.UNSET_HEX_COLOR
                        );
                    }
                }
            } else {
                postBe.clearHexColor();

                if (level instanceof ServerLevel serverLevel) {
                    VocoTeleportLogic.syncEndpoint(
                            serverLevel,
                            postPos,
                            POST_RECEPTOR,
                            false,
                            VocoReceptorLogic.UNSET_HEX_COLOR
                    );
                }
            }
        } else {
            shouldBePortal = false;
        }

        if (!postState.hasProperty(VocoPostBlock.PORTAL)) {
            return postState;
        }

        boolean wasPortal = postState.getValue(VocoPostBlock.PORTAL);

        if (wasPortal != shouldBePortal) {
            if (!wasPortal && shouldBePortal) {
                VocoReceptorLogic.playPortalAppearSound(level, postPos);
            } else if (wasPortal) {
                VocoReceptorLogic.playPortalDisappearSound(level, postPos);
            }
        }

        return postState.setValue(VocoPostBlock.PORTAL, shouldBePortal);
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

        if (!(level.getBlockEntity(candlePos) instanceof PearlCandleBlockEntity pearlCandleBe)
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