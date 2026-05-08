// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoReceptorLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoReceptorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class VocoReceptorLogic {
    private VocoReceptorLogic() {}

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

    public static InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player
    ) {
        ReceptorPosition receptor = ReceptorPosition.NORTH_EAST;

        if (VocoSharedBetweenTableAndReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoReceptorBlock.LIT)) {
            if (level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.showNeedsPearlMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoReceptorBlock.PORTAL)) {
            if (level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.showNeedsPortalMessage(player);
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
            InteractionHand hand
    ) {
        ReceptorPosition receptor = ReceptorPosition.NORTH_EAST;

        if (VocoSharedBetweenTableAndReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoReceptorBlock.LIT)) {
            return useUnlitReceptor(stack, state, level, pos, player);
        }

        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.depleteReceptorPearl(
                        stack,
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        VocoReceptorBlock.LIT,
                        VocoReceptorBlock.PORTAL,
                        receptor
                );

                refreshPortalAt(level, pos);
            }

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(VocoReceptorBlock.PORTAL)) {
            if (level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult useUnlitReceptor(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player
    ) {
        if (!stack.is(ModItems.BANANA_PEARL.get())) {
            if (level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.showNeedsPearlMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            VocoSharedBetweenTableAndReceptorLogic.lightReceptorWithPearl(
                    stack,
                    state,
                    level,
                    pos,
                    player,
                    VocoReceptorBlock.LIT
            );

            refreshPortalAt(level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    public static void refreshReceptorBelowCandle(Level level, BlockPos candlePos) {
        if (level == null || level.isClientSide()) {
            return;
        }

        refreshPortalAt(level, candlePos.below());
    }

    public static void refreshPortalAt(Level level, BlockPos receptorPos) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(receptorPos);
        if (!(state.getBlock() instanceof VocoReceptorBlock)) {
            return;
        }

        BlockState updated = updatePortalStateFromTop(level, receptorPos, state);

        if (updated != state) {
            level.setBlock(
                    receptorPos,
                    updated,
                    VocoSharedBetweenTableAndReceptorLogic.UPDATE_FLAGS
            );
        }
    }

    public static BlockState updatePortalStateFromTop(
            Level level,
            BlockPos receptorPos,
            BlockState receptorState
    ) {
        PortalInfo portalInfo = readPortalInfo(level, receptorPos, receptorState);
        boolean shouldBePortal = portalInfo.active();

        BlockEntity be = level.getBlockEntity(receptorPos);
        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            if (shouldBePortal) {
                shouldBePortal = receptorBe.setHexColor(portalInfo.hexColor());

                if (shouldBePortal && level instanceof ServerLevel serverLevel) {
                    shouldBePortal = VocoTeleportLogic.syncEndpoint(
                            serverLevel,
                            receptorPos,
                            ReceptorPosition.NORTH_EAST,
                            true,
                            portalInfo.hexColor()
                    );
                }

                if (!shouldBePortal) {
                    receptorBe.clearHexColor();

                    if (level instanceof ServerLevel serverLevel) {
                        VocoTeleportLogic.syncEndpoint(
                                serverLevel,
                                receptorPos,
                                ReceptorPosition.NORTH_EAST,
                                false,
                                VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR
                        );
                    }
                }
            } else {
                receptorBe.clearHexColor();

                if (level instanceof ServerLevel serverLevel) {
                    VocoTeleportLogic.syncEndpoint(
                            serverLevel,
                            receptorPos,
                            ReceptorPosition.NORTH_EAST,
                            false,
                            VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR
                    );
                }
            }
        } else {
            shouldBePortal = false;
        }

        if (!receptorState.hasProperty(VocoReceptorBlock.PORTAL)) {
            return receptorState;
        }

        boolean wasPortal = receptorState.getValue(VocoReceptorBlock.PORTAL);

        if (!wasPortal && shouldBePortal) {
            VocoSharedBetweenTableAndReceptorLogic.playPortalAppearSound(level, receptorPos);
        }

        return receptorState.setValue(VocoReceptorBlock.PORTAL, shouldBePortal);
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

        if (!(level.getBlockEntity(candlePos) instanceof PearlCandleBlockEntity pearlCandleBe)
                || !pearlCandleBe.hasHexColor()) {
            return PortalInfo.INACTIVE;
        }

        return new PortalInfo(true, pearlCandleBe.getHexColor());
    }

    private record PortalInfo(boolean active, int hexColor) {
        private static final PortalInfo INACTIVE =
                new PortalInfo(false, VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR);
    }
}