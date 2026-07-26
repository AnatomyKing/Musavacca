package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorVoxelShapes;
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

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape doorShape =
                super.getShape(
                        state,
                        level,
                        pos,
                        context
                );

        return MusavaccaPortalDoorVoxelShapes
                .outlineShape(
                        state,
                        doorShape
                );
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        /*
         * Call DoorBlock#getShape directly instead of using this
         * block's overridden getShape method.
         *
         * That preserves vanilla door collision without including
         * the non-solid portal selection panel.
         */
        VoxelShape doorShape =
                super.getShape(
                        state,
                        level,
                        pos,
                        context
                );

        return MusavaccaPortalDoorVoxelShapes
                .collisionShape(
                        doorShape
                );
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(
                level,
                pos,
                state,
                placer,
                stack
        );

        if (!level.isClientSide()) {
            BlockPos lowerPos =
                    lowerDoorPos(
                            state,
                            pos
                    );

            synchronizePearlState(
                    level,
                    lowerPos,
                    false
            );

            BlockState placedState =
                    level.getBlockState(lowerPos);

            boolean portal =
                    placedState.is(this)
                            && placedState.getValue(PORTAL);

            PearlSlotIgnition
                    .playPortalStateChangeSound(
                            level,
                            lowerPos,
                            false,
                            portal
                    );
        }
    }

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

        boolean wasPortal =
                lowerState.getValue(PORTAL);

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
            BlockState updatedLowerState =
                    level.getBlockState(lowerPos);

            boolean portalWasSheared =
                    wasPortal
                            && updatedLowerState.is(this)
                            && !updatedLowerState.getValue(LIT);

            if (
                    portalWasSheared
                            && level.getBlockEntity(lowerPos)
                            instanceof MusavaccaPortalDoorBlockEntity doorBe
            ) {
                doorBe.clearHexColor();
            }

            synchronizePearlState(
                    level,
                    lowerPos
            );
        }

        return result;
    }

    public static void synchronizePearlState(
            Level level,
            BlockPos lowerPos
    ) {
        synchronizePearlState(
                level,
                lowerPos,
                true
        );
    }

    public static void synchronizePearlState(
            Level level,
            BlockPos lowerPos,
            boolean playPortalSound
    ) {
        if (level.isClientSide()) {
            return;
        }

        BlockState lowerState =
                level.getBlockState(lowerPos);

        if (
                !(lowerState.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
                        || lowerState.getValue(HALF)
                        != DoubleBlockHalf.LOWER
        ) {
            return;
        }

        boolean wasPortal =
                lowerState.getValue(PORTAL);

        boolean lit =
                lowerState.getValue(LIT);

        boolean portal =
                lit
                        && hasAssignedHexColor(
                        level,
                        lowerPos
                );

        setVisualState(
                level,
                lowerPos,
                lit,
                portal
        );

        setVisualState(
                level,
                lowerPos.above(),
                lit,
                portal
        );

        if (playPortalSound) {
            PearlSlotIgnition
                    .playPortalStateChangeSound(
                            level,
                            lowerPos,
                            wasPortal,
                            portal
                    );
        }
    }

    private static void setVisualState(
            Level level,
            BlockPos pos,
            boolean lit,
            boolean portal
    ) {
        BlockState state =
                level.getBlockState(pos);

        if (
                !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
        ) {
            return;
        }

        boolean resolvedPortal =
                lit && portal;

        if (
                state.getValue(LIT) == lit
                        && state.getValue(PORTAL)
                        == resolvedPortal
        ) {
            return;
        }

        level.setBlock(
                pos,
                state
                        .setValue(LIT, lit)
                        .setValue(PORTAL, resolvedPortal),
                UPDATE_FLAGS
        );
    }

    private static boolean hasAssignedHexColor(
            LevelReader level,
            BlockPos lowerPos
    ) {
        return level.getBlockEntity(lowerPos)
                instanceof MusavaccaPortalDoorBlockEntity doorBe
                && doorBe.hasHexColor();
    }

    public static BlockPos lowerDoorPos(
            BlockState clickedState,
            BlockPos clickedPos
    ) {
        return clickedState.getValue(HALF)
                == DoubleBlockHalf.UPPER
                ? clickedPos.below()
                : clickedPos;
    }

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

        BlockPos lowerPos =
                lowerDoorPos(
                        updatedState,
                        pos
                );

        boolean portal =
                lit
                        && hasAssignedHexColor(
                        levelReader,
                        lowerPos
                );

        return updatedState
                .setValue(LIT, lit)
                .setValue(PORTAL, portal);
    }
}