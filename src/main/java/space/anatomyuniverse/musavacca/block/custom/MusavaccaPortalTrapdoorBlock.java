package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalTrapdoorHitboxes;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalTrapdoorVoxelShapes;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalTrapdoorBlockEntity;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.trapdoor.MusavaccaTrapdoorTeleportEvent;
import space.anatomyuniverse.musavacca.trapdoor.MusavaccaTrapdoorTeleportNetwork;
import space.anatomyuniverse.musavacca.trapdoor.MusavaccaTrapdoorTeleportTransform;

public final class MusavaccaPortalTrapdoorBlock
        extends TrapDoorBlock
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
                            0.5D,
                            0.5D
                    ),
                    new Vec3(
                            0.0D,
                            0.18D,
                            0.0D
                    )
            );

    public MusavaccaPortalTrapdoorBlock(
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
        VoxelShape trapdoorShape =
                super.getShape(
                        state,
                        level,
                        pos,
                        context
                );

        return MusavaccaPortalTrapdoorVoxelShapes
                .outlineShape(
                        state,
                        trapdoorShape
                );
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape trapdoorShape =
                super.getShape(
                        state,
                        level,
                        pos,
                        context
                );

        return MusavaccaPortalTrapdoorVoxelShapes
                .collisionShape(
                        trapdoorShape
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
                !MusavaccaPortalTrapdoorHitboxes
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
        return MusavaccaPortalTrapdoorHitboxes.hasOpenPortal(state)
                ? entranceTriggerShape(state)
                : super.getEntityInsideCollisionShape(state, level, pos);
    }
    *///?}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new MusavaccaPortalTrapdoorBlockEntity(
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
        if (level.isClientSide()) {
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
                            instanceof MusavaccaPortalTrapdoorBlockEntity
            ) {
                tickEntranceNudge(
                        serverLevel,
                        tickPos,
                        tickState
                );
            }
        };
    }

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
                        || !MusavaccaPortalTrapdoorHitboxes
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
                        || !isApproachingPortalPlane(
                        entity,
                        state,
                        pos
                )
        ) {
            return;
        }

        entity.setAsInsidePortal(
                this,
                pos
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
                && level.getBlockEntity(pos) instanceof MusavaccaPortalTrapdoorBlockEntity blockEntity) {
            blockEntity.cleanupBeforeRemoval();
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    *///?}

    private void tickEntranceNudge(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        if (
                !state.is(
                        this
                )
                        || !MusavaccaPortalTrapdoorHitboxes
                        .hasOpenPortal(
                                state
                        )
        ) {
            return;
        }

        AABB entranceBox =
                entranceTriggerWorldBox(
                        state,
                        pos
                );

        if (entranceBox == null) {
            return;
        }

        /*
         * entityInside() only participates once Minecraft is already
         * evaluating the trapdoor block coordinate.
         *
         * This ticker extends the same PORTAL_ENTRANCE_NUDGE beyond the
         * block boundary, both ABOVE and BELOW the horizontal aperture.
         *
         * Unlike a standing door, a horizontal pane must also check that
         * an entity is actually moving toward the plane. That prevents an
         * entity merely standing inside the early-nudge corridor from being
         * teleported without crossing toward the portal.
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
            if (
                    !isApproachingPortalPlane(
                            entity,
                            state,
                            pos
                    )
            ) {
                continue;
            }

            entity.setAsInsidePortal(
                    this,
                    pos
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

    private static VoxelShape entranceTriggerShape(
            BlockState state
    ) {
        VoxelShape portalPanel =
                MusavaccaPortalTrapdoorHitboxes
                        .portalPanel(
                                state
                        );

        if (portalPanel.isEmpty()) {
            return Shapes.empty();
        }

        AABB portalBox =
                portalPanel.bounds();

        double entranceNudge =
                Math.max(
                        0.0D,
                        MusavaccaTrapdoorTeleportTransform
                                .PORTAL_ENTRANCE_NUDGE
                );

        double centerY =
                (
                        portalBox.minY
                                + portalBox.maxY
                )
                        * 0.5D;

        return Shapes.create(
                new AABB(
                        portalBox.minX
                                - PORTAL_TRIGGER_EPSILON,
                        centerY
                                - entranceNudge
                                - PORTAL_TRIGGER_EPSILON,
                        portalBox.minZ
                                - PORTAL_TRIGGER_EPSILON,
                        portalBox.maxX
                                + PORTAL_TRIGGER_EPSILON,
                        centerY
                                + entranceNudge
                                + PORTAL_TRIGGER_EPSILON,
                        portalBox.maxZ
                                + PORTAL_TRIGGER_EPSILON
                )
        );
    }

    private static boolean isApproachingPortalPlane(
            Entity entity,
            BlockState state,
            BlockPos pos
    ) {
        VoxelShape portalPanel =
                MusavaccaPortalTrapdoorHitboxes
                        .portalPanel(
                                state
                        );

        if (portalPanel.isEmpty()) {
            return false;
        }

        AABB portalBox =
                portalPanel
                        .bounds()
                        .move(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        );

        double planeY =
                (
                        portalBox.minY
                                + portalBox.maxY
                )
                        * 0.5D;

        AABB entityBox =
                entity.getBoundingBox();

        double verticalMovement =
                entity.getDeltaMovement().y;

        if (
                entityBox.maxY
                        < planeY
                        - PORTAL_TRIGGER_EPSILON
        ) {
            return verticalMovement
                    > PORTAL_TRIGGER_EPSILON;
        }

        if (
                entityBox.minY
                        > planeY
                        + PORTAL_TRIGGER_EPSILON
        ) {
            return verticalMovement
                    < -PORTAL_TRIGGER_EPSILON;
        }

        /*
         * The bounding box is already touching/straddling the real 2px
         * portal plane, so this is a genuine crossing even at tiny speed.
         */
        return true;
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
        return MusavaccaTrapdoorTeleportEvent
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

        synchronizePearlState(
                level,
                pos,
                false
        );

        BlockState placedState =
                level.getBlockState(
                        pos
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
                        pos,
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

        InteractionResult result =
                PearlSlotIgnition
                        .handleHeldItemUse(
                                stack,
                                state,
                                level,
                                pos,
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
                    pos
            );
        }

        return result;
    }

    public static void synchronizePearlState(
            Level level,
            BlockPos pos
    ) {
        synchronizePearlState(
                level,
                pos,
                true
        );
    }

    public static void synchronizePearlState(
            Level level,
            BlockPos pos,
            boolean playPortalSound
    ) {
        if (level.isClientSide()) {
            return;
        }

        BlockState state =
                level.getBlockState(
                        pos
                );

        if (
                !(state.getBlock()
                        instanceof MusavaccaPortalTrapdoorBlock)
        ) {
            return;
        }

        boolean wasPortal =
                state.getValue(
                        PORTAL
                );

        boolean lit =
                state.getValue(
                        LIT
                );

        boolean litPortal =
                hasAssignedHexColor(
                        level,
                        pos
                );

        boolean portal =
                lit
                        && litPortal;

        setVisualState(
                level,
                pos,
                lit,
                litPortal,
                portal
        );

        if (playPortalSound) {
            PearlSlotIgnition
                    .playPortalStateChangeSound(
                            level,
                            pos,
                            wasPortal,
                            portal
                    );
        }

        if (
                level.getBlockEntity(
                        pos
                )
                        instanceof MusavaccaPortalTrapdoorBlockEntity trapdoorBe
        ) {
            MusavaccaTrapdoorTeleportNetwork
                    .refresh(
                            trapdoorBe
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
                        instanceof MusavaccaPortalTrapdoorBlock)
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
            BlockPos pos
    ) {
        return level.getBlockEntity(
                pos
        )
                instanceof MusavaccaPortalTrapdoorBlockEntity trapdoorBe
                && trapdoorBe.hasHexColor();
    }
}
