package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2
//import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
//? if >=1.21.5
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
//? if <1.21.2
//import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
//? if >=1.21.2
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//? if <1.21.2 {
/*import net.minecraft.world.level.portal.DimensionTransition;
*///?} else {
import net.minecraft.world.level.portal.TeleportTransition;
//?}
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorHitboxes;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorVoxelShapes;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.door.MusavaccaDoorTeleportEvent;
import space.anatomyuniverse.musavacca.door.MusavaccaDoorTeleportNetwork;
import space.anatomyuniverse.musavacca.door.MusavaccaDoorTeleportTransform;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class MusavaccaPortalDoorBlock
        extends DoorBlock
        implements EntityBlock, Portal {

    public static final BooleanProperty LIT =
            BooleanProperty.create(
                    "lit"
            );

    public static final BooleanProperty LIT_PORTAL =
            BooleanProperty.create(
                    "lit_portal"
            );

    public static final BooleanProperty PORTAL =
            BooleanProperty.create(
                    "portal"
            );

    private static final int UPDATE_FLAGS =
            Block.UPDATE_ALL
                    | Block.UPDATE_IMMEDIATE;

    private static final double PORTAL_TRIGGER_EPSILON =
            1.0D / 1024.0D;

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
        super(
                blockSetType,
                properties
        );

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(
                                LIT,
                                false
                        )
                        .setValue(
                                LIT_PORTAL,
                                false
                        )
                        .setValue(
                                PORTAL,
                                false
                        )
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        if (
                state.getValue(
                        HALF
                )
                        != DoubleBlockHalf.LOWER
        ) {
            return null;
        }

        return new MusavaccaPortalDoorBlockEntity(
                pos,
                state
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity>
    BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        if (
                level.isClientSide()
                        || state.getValue(
                        HALF
                )
                        != DoubleBlockHalf.LOWER
        ) {
            return null;
        }

        return (
                tickLevel,
                tickPos,
                tickState,
                blockEntity
        ) -> {
            if (
                    tickLevel
                            instanceof ServerLevel serverLevel
                            && blockEntity
                            instanceof MusavaccaPortalDoorBlockEntity
            ) {
                tickEntranceNudge(
                        serverLevel,
                        tickPos,
                        tickState
                );
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<
                    Block,
                    BlockState
                    > builder
    ) {
        super.createBlockStateDefinition(
                builder
        );

        builder.add(
                LIT,
                LIT_PORTAL,
                PORTAL
        );
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

    //? if >=1.21.5 {
    @Override
    protected VoxelShape getEntityInsideCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Entity entity
    ) {
        if (
                !MusavaccaPortalDoorHitboxes
                        .hasOpenPortal(
                                state
                        )
        ) {
            return super
                    .getEntityInsideCollisionShape(
                            state,
                            level,
                            pos,
                            entity
                    );
        }

        return entranceTriggerShape(
                state
        );
    }
    //?} else if >=1.21.2 {
    /*@Override
    protected VoxelShape getEntityInsideCollisionShape(
            BlockState state,
            Level level,
            BlockPos pos
    ) {
        return MusavaccaPortalDoorHitboxes.hasOpenPortal(state)
                ? entranceTriggerShape(state)
                : super.getEntityInsideCollisionShape(state, level, pos);
    }
    *///?}

    //? if >=1.21.5 {
    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier effectApplier
    ) {
        this.handleEntityInside(state, level, pos, entity);
    }
    //?} else {
    /*@Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        this.handleEntityInside(state, level, pos, entity);
    }
    *///?}

    private void handleEntityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (
                !entity.canUsePortal(
                        false
                )
                        || !MusavaccaPortalDoorHitboxes
                        .hasOpenPortal(
                                state
                        )
        ) {
            return;
        }

        VoxelShape entranceShape =
                entranceTriggerShape(
                        state
                );

        if (entranceShape.isEmpty()) {
            return;
        }

        AABB entranceBox =
                entranceShape
                        .bounds()
                        .move(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        );

        Vec3 movement =
                entity.getDeltaMovement();

        /*
         * Preserve the swept movement test.
         *
         * The server-side entrance ticker provides the early
         * PORTAL_ENTRANCE_NUDGE from both sides of the portal.
         *
         * This swept test remains important for fast entities
         * that can move across the entrance corridor between
         * two entity-inside checks.
         */
        AABB sweptEntityBox =
                entity.getBoundingBox()
                        .expandTowards(
                                -movement.x,
                                -movement.y,
                                -movement.z
                        );

        if (
                !entranceBox.intersects(
                        sweptEntityBox
                )
        ) {
            return;
        }

        entity.setAsInsidePortal(
                this,
                lowerDoorPos(
                        state,
                        pos
                )
        );
    }

    //? if <1.21.5 {
    /*@Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (state.getBlock() != newState.getBlock()
                && level.getBlockEntity(pos) instanceof MusavaccaPortalDoorBlockEntity blockEntity) {
            blockEntity.cleanupBeforeRemoval();
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    *///?}

    private void tickEntranceNudge(
            ServerLevel level,
            BlockPos lowerPos,
            BlockState lowerState
    ) {
        if (
                !lowerState.is(
                        this
                )
                        || lowerState.getValue(
                        HALF
                )
                        != DoubleBlockHalf.LOWER
                        || !MusavaccaPortalDoorHitboxes
                        .hasOpenPortal(
                                lowerState
                        )
        ) {
            return;
        }

        AABB lowerEntranceBox =
                entranceTriggerWorldBox(
                        lowerState,
                        lowerPos
                );

        BlockPos upperPos =
                lowerPos.above();

        BlockState upperState =
                level.getBlockState(
                        upperPos
                );

        AABB upperEntranceBox =
                null;

        if (
                upperState.is(
                        this
                )
                        && upperState.getValue(
                        HALF
                )
                        == DoubleBlockHalf.UPPER
                        && MusavaccaPortalDoorHitboxes
                        .hasOpenPortal(
                                upperState
                        )
        ) {
            upperEntranceBox =
                    entranceTriggerWorldBox(
                            upperState,
                            upperPos
                    );
        }

        AABB entranceBox =
                combineEntranceBoxes(
                        lowerEntranceBox,
                        upperEntranceBox
                );

        if (entranceBox == null) {
            return;
        }

        /*
         * entityInside() can only participate once Minecraft
         * is already evaluating the door's block coordinate.
         *
         * This small server-side check supplies the missing
         * part of PORTAL_ENTRANCE_NUDGE that extends into the
         * neighboring block.
         *
         * The exact same entranceTriggerShape() is used here,
         * so NORTH/SOUTH and EAST/WEST receive the same nudge.
         */
        for (
                Entity entity
                : level.getEntities(
                (Entity) null,
                entranceBox,
                entity -> entity
                        .canUsePortal(
                                false
                        )
        )
        ) {
            entity.setAsInsidePortal(
                    this,
                    lowerPos
            );
        }
    }

    @Nullable
    private static AABB entranceTriggerWorldBox(
            BlockState state,
            BlockPos pos
    ) {
        VoxelShape shape =
                entranceTriggerShape(
                        state
                );

        if (shape.isEmpty()) {
            return null;
        }

        return shape
                .bounds()
                .move(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                );
    }

    @Nullable
    private static AABB combineEntranceBoxes(
            @Nullable AABB first,
            @Nullable AABB second
    ) {
        if (first == null) {
            return second;
        }

        if (second == null) {
            return first;
        }

        return new AABB(
                Math.min(
                        first.minX,
                        second.minX
                ),
                Math.min(
                        first.minY,
                        second.minY
                ),
                Math.min(
                        first.minZ,
                        second.minZ
                ),
                Math.max(
                        first.maxX,
                        second.maxX
                ),
                Math.max(
                        first.maxY,
                        second.maxY
                ),
                Math.max(
                        first.maxZ,
                        second.maxZ
                )
        );
    }

    private static VoxelShape entranceTriggerShape(
            BlockState state
    ) {
        VoxelShape portalPanel =
                MusavaccaPortalDoorHitboxes
                        .portalPanel(
                                state
                        );

        if (portalPanel.isEmpty()) {
            return Shapes.empty();
        }

        AABB portalBox =
                portalPanel.bounds();

        Direction facing =
                MusavaccaPortalDoorHitboxes
                        .portalFacing(
                                state
                        );

        double entranceNudge =
                Math.max(
                        0.0D,
                        MusavaccaDoorTeleportTransform
                                .PORTAL_ENTRANCE_NUDGE
                );

        double centerX =
                (
                        portalBox.minX
                                + portalBox.maxX
                )
                        * 0.5D;

        double centerZ =
                (
                        portalBox.minZ
                                + portalBox.maxZ
                )
                        * 0.5D;

        /*
         * PORTAL_ENTRANCE_NUDGE is measured from the CENTER
         * of the 2px portal panel.
         *
         * The shape extends by exactly the same distance
         * to BOTH sides of that center.
         *
         * This is deliberately independent of entity movement
         * direction. Movement is only used by entityInside()
         * for the swept fast-crossing test.
         */
        if (
                facing.getAxis()
                        == Direction.Axis.X
        ) {
            return Shapes.create(
                    new AABB(
                            centerX
                                    - entranceNudge
                                    - PORTAL_TRIGGER_EPSILON,
                            portalBox.minY
                                    - PORTAL_TRIGGER_EPSILON,
                            portalBox.minZ
                                    - PORTAL_TRIGGER_EPSILON,
                            centerX
                                    + entranceNudge
                                    + PORTAL_TRIGGER_EPSILON,
                            portalBox.maxY
                                    + PORTAL_TRIGGER_EPSILON,
                            portalBox.maxZ
                                    + PORTAL_TRIGGER_EPSILON
                    )
            );
        }

        return Shapes.create(
                new AABB(
                        portalBox.minX
                                - PORTAL_TRIGGER_EPSILON,
                        portalBox.minY
                                - PORTAL_TRIGGER_EPSILON,
                        centerZ
                                - entranceNudge
                                - PORTAL_TRIGGER_EPSILON,
                        portalBox.maxX
                                + PORTAL_TRIGGER_EPSILON,
                        portalBox.maxY
                                + PORTAL_TRIGGER_EPSILON,
                        centerZ
                                + entranceNudge
                                + PORTAL_TRIGGER_EPSILON
                )
        );
    }

    @Override
    public int getPortalTransitionTime(
            ServerLevel level,
            Entity entity
    ) {
        return 0;
    }

    @Nullable
    @Override
    public
    //? if <1.21.2 {
    /*DimensionTransition
    *///?} else {
    TeleportTransition
    //?}
    getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos entryPos
    ) {
        return MusavaccaDoorTeleportEvent
                .getPortalDestination(
                        currentLevel,
                        entity,
                        entryPos
                );
    }

    @Override
    public Transition getLocalTransition() {
        return Transition.CONFUSION;
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

        if (level.isClientSide()) {
            return;
        }

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
                level.getBlockState(
                        lowerPos
                );

        boolean portal =
                placedState.is(
                        this
                )
                        && placedState.getValue(
                        PORTAL
                );

        PearlSlotIgnition
                .playPortalStateChangeSound(
                        level,
                        lowerPos,
                        false,
                        portal
                );
    }

    //? if <1.21.2 {
    /*@Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!isPearlSlotItem(stack)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        return InteractionResultCompat.forItemUse(
                this.handlePearlSlotItem(stack, state, level, pos, player, hand)
        );
    }
    *///?} else {
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
        if (!isPearlSlotItem(stack)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        return this.handlePearlSlotItem(stack, state, level, pos, player, hand);
    }
    //?}

    private static boolean isPearlSlotItem(ItemStack stack) {
        return stack.is(ModItems.BANANA_PEARL.get()) || stack.is(Items.SHEARS);
    }

    private InteractionResult handlePearlSlotItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand
    ) {

        BlockPos lowerPos =
                lowerDoorPos(
                        state,
                        pos
                );

        BlockState lowerState =
                level.getBlockState(
                        lowerPos
                );

        if (!lowerState.is(this)) {
            return InteractionResult.PASS;
        }

        InteractionResult result =
                PearlSlotIgnition
                        .handleHeldItemUse(
                                stack,
                                lowerState,
                                level,
                                lowerPos,
                                player,
                                hand,
                                PEARL_SLOT
                        );

        if (
                result
                        == InteractionResult.SUCCESS
                        && !level.isClientSide()
        ) {
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
                level.getBlockState(
                        lowerPos
                );

        if (
                !(lowerState.getBlock()
                        instanceof
                        MusavaccaPortalDoorBlock)
                        || lowerState.getValue(
                        HALF
                )
                        != DoubleBlockHalf.LOWER
        ) {
            return;
        }

        boolean wasPortal =
                lowerState.getValue(
                        PORTAL
                );

        boolean lit =
                lowerState.getValue(
                        LIT
                );

        boolean litPortal =
                hasAssignedHexColor(
                        level,
                        lowerPos
                );

        boolean portal =
                lit
                        && litPortal;

        setVisualState(
                level,
                lowerPos,
                lit,
                litPortal,
                portal
        );

        setVisualState(
                level,
                lowerPos.above(),
                lit,
                litPortal,
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

        if (
                level.getBlockEntity(
                        lowerPos
                )
                        instanceof
                        MusavaccaPortalDoorBlockEntity doorBe
        ) {
            MusavaccaDoorTeleportNetwork
                    .refresh(
                            doorBe
                    );
        }
    }

    private static void setVisualState(
            Level level,
            BlockPos pos,
            boolean lit,
            boolean litPortal,
            boolean portal
    ) {
        BlockState state =
                level.getBlockState(
                        pos
                );

        if (
                !(state.getBlock()
                        instanceof
                        MusavaccaPortalDoorBlock)
        ) {
            return;
        }

        boolean resolvedPortal =
                lit
                        && litPortal
                        && portal;

        if (
                state.getValue(
                        LIT
                )
                        == lit
                        && state.getValue(
                        LIT_PORTAL
                )
                        == litPortal
                        && state.getValue(
                        PORTAL
                )
                        == resolvedPortal
        ) {
            return;
        }

        level.setBlock(
                pos,
                state
                        .setValue(
                                LIT,
                                lit
                        )
                        .setValue(
                                LIT_PORTAL,
                                litPortal
                        )
                        .setValue(
                                PORTAL,
                                resolvedPortal
                        ),
                UPDATE_FLAGS
        );
    }

    private static boolean hasAssignedHexColor(
            LevelReader level,
            BlockPos lowerPos
    ) {
        return level.getBlockEntity(
                lowerPos
        )
                instanceof
                MusavaccaPortalDoorBlockEntity doorBe
                && doorBe.hasHexColor();
    }

    public static BlockPos lowerDoorPos(
            BlockState clickedState,
            BlockPos clickedPos
    ) {
        return clickedState.getValue(
                HALF
        )
                == DoubleBlockHalf.UPPER
                ? clickedPos.below()
                : clickedPos;
    }

    //? if <1.21.2 {
    /*@Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockState updatedState = super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
        return this.synchronizeOtherHalf(updatedState, level, pos, direction, neighborState);
    }
    *///?} else {
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

        return this.synchronizeOtherHalf(updatedState, levelReader, pos, direction, neighborState);
    }
    //?}

    private BlockState synchronizeOtherHalf(
            BlockState updatedState,
            LevelReader levelReader,
            BlockPos pos,
            Direction direction,
            BlockState neighborState
    ) {
        if (
                !updatedState.is(
                        this
                )
                        || direction.getAxis()
                        != Direction.Axis.Y
                        || !neighborState.is(
                        this
                )
        ) {
            return updatedState;
        }

        if (
                updatedState.getValue(
                        HALF
                )
                        == neighborState.getValue(
                        HALF
                )
        ) {
            return updatedState;
        }

        boolean lit =
                neighborState.getValue(
                        LIT
                );

        BlockPos lowerPos =
                lowerDoorPos(
                        updatedState,
                        pos
                );

        boolean litPortal =
                hasAssignedHexColor(
                        levelReader,
                        lowerPos
                );

        boolean portal =
                lit
                        && litPortal;

        return updatedState
                .setValue(
                        LIT,
                        lit
                )
                .setValue(
                        LIT_PORTAL,
                        litPortal
                )
                .setValue(
                        PORTAL,
                        portal
                );
    }
}


