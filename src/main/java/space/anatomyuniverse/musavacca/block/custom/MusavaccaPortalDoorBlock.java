// file: src/main/java/space/anatomyuniverse/musavacca/block/custom/MusavaccaPortalDoorBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

public final class MusavaccaPortalDoorBlock extends DoorBlock {
    public static final BooleanProperty LIT =
            BooleanProperty.create("lit");

    public static final BooleanProperty PORTAL =
            BooleanProperty.create("portal");

    private static final int UPDATE_FLAGS =
            Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    public MusavaccaPortalDoorBlock(
            BlockSetType blockSetType,
            Properties properties
    ) {
        super(blockSetType, properties);

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(LIT, false)
                        .setValue(PORTAL, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, PORTAL);
    }

    /**
     * Empty-hand shift-right-click test interaction.
     *
     * State order:
     * normal -> lit -> portal -> lit + portal -> normal
     */
    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (player.isShiftKeyDown()) {
            return cycleTestState(state, level, pos);
        }

        return super.useWithoutItem(
                state,
                level,
                pos,
                player,
                hit
        );
    }

    /**
     * Also allows the temporary test cycle while holding an item.
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (player.isShiftKeyDown()) {
            return cycleTestState(state, level, pos);
        }

        return super.useItemOn(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hit
        );
    }

    private InteractionResult cycleTestState(
            BlockState clickedState,
            Level level,
            BlockPos clickedPos
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos lowerPos =
                clickedState.getValue(HALF)
                        == DoubleBlockHalf.UPPER
                        ? clickedPos.below()
                        : clickedPos;

        BlockState lowerState =
                level.getBlockState(lowerPos);

        if (!lowerState.is(this)) {
            return InteractionResult.PASS;
        }

        boolean lit =
                lowerState.getValue(LIT);

        boolean portal =
                lowerState.getValue(PORTAL);

        boolean nextLit;
        boolean nextPortal;

        if (!lit && !portal) {
            nextLit = true;
            nextPortal = false;
        } else if (lit && !portal) {
            nextLit = false;
            nextPortal = true;
        } else if (!lit) {
            nextLit = true;
            nextPortal = true;
        } else {
            nextLit = false;
            nextPortal = false;
        }

        /*
         * Update both halves explicitly.
         * Their normal door properties remain untouched.
         */
        setVisualState(
                level,
                lowerPos.above(),
                nextLit,
                nextPortal
        );

        setVisualState(
                level,
                lowerPos,
                nextLit,
                nextPortal
        );

        return InteractionResult.SUCCESS;
    }

    private void setVisualState(
            Level level,
            BlockPos pos,
            boolean lit,
            boolean portal
    ) {
        BlockState state =
                level.getBlockState(pos);

        if (!state.is(this)) {
            return;
        }

        level.setBlock(
                pos,
                state
                        .setValue(LIT, lit)
                        .setValue(PORTAL, portal),
                UPDATE_FLAGS
        );
    }

    /**
     * Keeps the two custom properties synchronized between
     * the upper and lower door halves during normal neighbor updates.
     */
    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader levelReader,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        BlockState updatedState =
                super.updateShape(
                        state,
                        levelReader,
                        scheduledTickAccess,
                        pos,
                        direction,
                        neighborPos,
                        neighborState,
                        random
                );

        if (!updatedState.is(this)
                || direction.getAxis() != Direction.Axis.Y
                || !neighborState.is(this)) {
            return updatedState;
        }

        if (updatedState.getValue(HALF)
                == neighborState.getValue(HALF)) {
            return updatedState;
        }

        return updatedState
                .setValue(
                        LIT,
                        neighborState.getValue(LIT)
                )
                .setValue(
                        PORTAL,
                        neighborState.getValue(PORTAL)
                );
    }
}
