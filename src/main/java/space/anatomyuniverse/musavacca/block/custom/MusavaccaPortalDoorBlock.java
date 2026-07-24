package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class MusavaccaPortalDoorBlock
        extends DoorBlock
        implements EntityBlock {

    public static final BooleanProperty LIT =
            BooleanProperty.create("lit");

    public static final BooleanProperty PORTAL =
            BooleanProperty.create("portal");

    private static final int UPDATE_FLAGS =
            Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    /*
     * The lower door half is used as the canonical pearl slot.
     *
     * The pearl drops from the vertical center of the complete
     * two-block-tall door and pops slightly upward.
     */
    private static final PearlSlotIgnition.Slot PEARL_SLOT =
            PearlSlotIgnition.Slot.of(
                    LIT,
                    PORTAL,
                    new Vec3(
                            0.5D,
                            1.0D,
                            0.5D
                    ),
                    new Vec3(
                            0.0D,
                            0.18D,
                            0.0D
                    )
            );

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

    /**
     * Only the lower half owns the block entity.
     *
     * The HEX_COLOR component from the placed door item
     * is stored inside this block entity.
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        if (
                state.getValue(HALF)
                        != DoubleBlockHalf.LOWER
        ) {
            return null;
        }

        return new MusavaccaPortalDoorBlockEntity(
                pos,
                state
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
     * Handles only Banana Pearl and shears interactions through
     * the shared PearlSlotIgnition system.
     *
     * Banana Pearl on unlit door:
     * - lights the door
     * - consumes one Banana Pearl
     * - automatically enables the portal
     *
     * Shears on lit door:
     * - extinguishes the door
     * - disables the portal
     * - drops one Banana Pearl
     * - damages the shears
     *
     * Shears on unlit door:
     * - charges it using balance through PearlSlotIgnition
     * - automatically enables the portal
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
        if (
                !stack.is(ModItems.BANANA_PEARL.get())
                        && !stack.is(Items.SHEARS)
        ) {
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

        BlockPos lowerPos =
                lowerDoorPos(
                        state,
                        pos
                );

        BlockState lowerState =
                level.getBlockState(lowerPos);

        if (!lowerState.is(this)) {
            return InteractionResult.PASS;
        }

        InteractionResult result =
                PearlSlotIgnition.handleHeldItemUse(
                        stack,
                        lowerState,
                        level,
                        lowerPos,
                        player,
                        hand,
                        PEARL_SLOT
                );

        if (
                result == InteractionResult.SUCCESS
                        && !level.isClientSide()
        ) {
            synchronizePearlState(
                    level,
                    lowerPos
            );
        }

        return result;
    }

    /**
     * PearlSlotIgnition changes the canonical lower half.
     * This copies that result to both halves and ensures:
     *
     *     PORTAL == LIT
     */
    private void synchronizePearlState(
            Level level,
            BlockPos lowerPos
    ) {
        BlockState lowerState =
                level.getBlockState(lowerPos);

        if (!lowerState.is(this)) {
            return;
        }

        boolean lit =
                lowerState.getValue(LIT);

        setVisualState(
                level,
                lowerPos,
                lit
        );

        setVisualState(
                level,
                lowerPos.above(),
                lit
        );
    }

    private void setVisualState(
            Level level,
            BlockPos pos,
            boolean lit
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
                        .setValue(PORTAL, lit),
                UPDATE_FLAGS
        );
    }

    /**
     * Returns the canonical lower-half position used by both
     * the pearl-slot logic and the block tint handler.
     */
    public static BlockPos lowerDoorPos(
            BlockState clickedState,
            BlockPos clickedPos
    ) {
        return clickedState.getValue(HALF)
                == DoubleBlockHalf.UPPER
                ? clickedPos.below()
                : clickedPos;
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

        if (
                !updatedState.is(this)
                        || direction.getAxis()
                        != Direction.Axis.Y
                        || !neighborState.is(this)
        ) {
            return updatedState;
        }

        if (
                updatedState.getValue(HALF)
                        == neighborState.getValue(HALF)
        ) {
            return updatedState;
        }

        boolean lit =
                neighborState.getValue(LIT);

        return updatedState
                .setValue(LIT, lit)
                .setValue(PORTAL, lit);
    }
}