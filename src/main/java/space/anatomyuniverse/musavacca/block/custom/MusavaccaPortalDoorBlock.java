// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/MusavaccaPortalDoorBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
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
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorHitboxes;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorVoxelShapes;
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

    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier effectApplier
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

        VoxelShape portalPanel =
                MusavaccaPortalDoorHitboxes
                        .portalPanel(
                                state
                        );

        if (portalPanel.isEmpty()) {
            return;
        }

        AABB portalBox =
                portalPanel
                        .bounds()
                        .move(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        );

        AABB entranceBox =
                entranceTriggerBox(
                        state,
                        portalBox
                );

        Vec3 movement =
                entity.getDeltaMovement();

        /*
         * Preserve the original swept movement test.
         *
         * This prevents a fast-moving entity from skipping
         * completely over the entrance region between ticks.
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

    private static AABB entranceTriggerBox(
            BlockState state,
            AABB portalBox
    ) {
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
         * We create an entrance corridor extending equally
         * to BOTH sides of that center.
         *
         * This deliberately does NOT depend on getDeltaMovement()
         * to determine which side is allowed to trigger.
         *
         * The movement sweep is used only to prevent fast entities
         * from skipping across the corridor between ticks.
         */
        if (
                facing.getAxis()
                        == Direction.Axis.X
        ) {
            return new AABB(
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
            );
        }

        return new AABB(
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
    public TeleportTransition getPortalDestination(
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
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
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
                !stack.is(
                        ModItems.BANANA_PEARL.get()
                )
                        && !stack.is(
                        Items.SHEARS
                )
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