package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorHitboxes;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MusavaccaDoorTeleportEvent {

    private static final Map<Integer, Set<GlobalPos>> DOORS =
            new HashMap<>();

    private static final double EPSILON =
            1.0E-6D;

    private static final double PORTAL_EXIT_NUDGE =
            0.07D;

    private MusavaccaDoorTeleportEvent() {}

    public static void refresh(
            MusavaccaPortalDoorBlockEntity door
    ) {
        if (
                !(door.getLevel()
                        instanceof ServerLevel level)
        ) {
            return;
        }

        GlobalPos position =
                GlobalPos.of(
                        level.dimension(),
                        door.getBlockPos()
                );

        DOORS.values().forEach(
                positions ->
                        positions.remove(
                                position
                        )
        );

        DOORS.entrySet().removeIf(
                entry ->
                        entry.getValue()
                                .isEmpty()
        );

        if (!door.hasHexColor()) {
            return;
        }

        BlockState state =
                level.getBlockState(
                        door.getBlockPos()
                );

        if (
                !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
        ) {
            return;
        }

        DOORS.computeIfAbsent(
                door.getHexColor(),
                ignored ->
                        new HashSet<>()
        ).add(
                position
        );
    }

    @Nullable
    public static TeleportTransition getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos entryPos
    ) {
        Door source =
                findSourceDoor(
                        currentLevel,
                        entryPos
                );

        if (source == null) {
            return null;
        }

        Door target =
                findLinkedDoor(
                        source
                );

        if (target == null) {
            return null;
        }

        Transform transform =
                calculate(
                        entity,
                        source,
                        target
                );

        BlockPos targetTicketPos =
                BlockPos.containing(
                        transform.position()
                );

        TeleportTransition.PostTeleportTransition postTeleport =
                TeleportTransition
                        .PLAY_PORTAL_SOUND
                        .then(
                                teleportedEntity -> {
                                    teleportedEntity
                                            .setPortalCooldown();

                                    teleportedEntity
                                            .setDeltaMovement(
                                                    transform
                                                            .deltaMovement()
                                            );

                                    teleportedEntity
                                            .placePortalTicket(
                                                    targetTicketPos
                                            );
                                }
                        );

        return new TeleportTransition(
                target.level(),
                transform.position(),
                transform.deltaMovement(),
                transform.yRot(),
                transform.xRot(),
                Set.of(),
                postTeleport
        );
    }

    @Nullable
    private static Door findSourceDoor(
            ServerLevel level,
            BlockPos entryPos
    ) {
        BlockState entryState =
                level.getBlockState(
                        entryPos
                );

        if (
                !(entryState.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
        ) {
            return null;
        }

        BlockPos lowerPos =
                MusavaccaPortalDoorBlock
                        .lowerDoorPos(
                                entryState,
                                entryPos
                        );

        BlockState state =
                level.getBlockState(
                        lowerPos
                );

        if (!isActivePortalDoor(state)) {
            return null;
        }

        if (
                !(level.getBlockEntity(
                        lowerPos
                )
                        instanceof MusavaccaPortalDoorBlockEntity doorBe)
                        || !doorBe.hasHexColor()
        ) {
            return null;
        }

        return new Door(
                level,
                lowerPos,
                doorBe.getHexColor(),
                portalPlane(
                        lowerPos,
                        state
                )
        );
    }

    @Nullable
    private static Door findLinkedDoor(
            Door source
    ) {
        Set<GlobalPos> positions =
                DOORS.get(
                        source.hexColor()
                );

        if (
                positions == null
                        || positions.isEmpty()
        ) {
            return null;
        }

        GlobalPos sourcePosition =
                GlobalPos.of(
                        source.level()
                                .dimension(),
                        source.pos()
                );

        for (
                GlobalPos position :
                Set.copyOf(
                        positions
                )
        ) {
            if (
                    position.equals(
                            sourcePosition
                    )
            ) {
                continue;
            }

            ServerLevel level =
                    source.level()
                            .getServer()
                            .getLevel(
                                    position.dimension()
                            );

            if (level == null) {
                continue;
            }

            level.getChunkAt(
                    position.pos()
            );

            BlockState state =
                    level.getBlockState(
                            position.pos()
                    );

            if (!isActivePortalDoor(state)) {
                positions.remove(
                        position
                );

                continue;
            }

            if (
                    level.getBlockEntity(
                            position.pos()
                    )
                            instanceof MusavaccaPortalDoorBlockEntity doorBe
                            && doorBe.hasHexColor()
                            && doorBe.getHexColor()
                            == source.hexColor()
            ) {
                return new Door(
                        level,
                        position.pos(),
                        doorBe.getHexColor(),
                        portalPlane(
                                position.pos(),
                                state
                        )
                );
            }

            positions.remove(
                    position
            );
        }

        return null;
    }

    private static Transform calculate(
            Entity entity,
            Door source,
            Door target
    ) {

        Basis sourceBasis =
                basis(
                        source
                );

        Basis targetBasis =
                basis(
                        target
                );

        double entityHalfWidth =
                Math.max(
                        0.0D,
                        entity.getBbWidth()
                                * 0.5D
                );

        double entityHeight =
                Math.max(
                        0.0D,
                        entity.getBbHeight()
                );


        Vec3 sourceRelative =
                entity.position()
                        .subtract(
                                source.plane()
                                        .center()
                        );

        double sourceRight =
                sourceRelative.dot(
                        sourceBasis.right()
                );

        double sourceUp =
                entity.getY()
                        - source.pos().getY();

        double maximumRight =
                Math.max(
                        0.0D,
                        0.5D
                                - entityHalfWidth
                );

        double maximumUp =
                Math.max(
                        0.0D,
                        2.0D
                                - entityHeight
                );

        double targetRight =
                clamp(
                        -sourceRight,
                        -maximumRight,
                        maximumRight
                );

        double targetUp =
                clamp(
                        sourceUp,
                        0.0D,
                        maximumUp
                );

        Vec3 targetMovement =
                transformVector(
                        entity.getDeltaMovement(),
                        sourceBasis,
                        targetBasis
                );

        double targetFrontMovement =
                targetMovement.dot(
                        targetBasis.front()
                );

        double exitNudge =
                Math.abs(
                        targetFrontMovement
                )
                        >= EPSILON
                        ? Math.copySign(
                        PORTAL_EXIT_NUDGE,
                        targetFrontMovement
                )
                        : 0.0D;

        Vec3 targetPosition =
                new Vec3(
                        target.plane()
                                .center().x,
                        target.pos().getY()
                                + targetUp,
                        target.plane()
                                .center().z
                )
                        .add(
                                targetBasis.right()
                                        .scale(
                                                targetRight
                                        )
                        )
                        .add(
                                targetBasis.front()
                                        .scale(
                                                exitNudge
                                        )
                        );

        Vec3 targetLook =
                transformVector(
                        entity.getLookAngle(),
                        sourceBasis,
                        targetBasis
                );

        if (
                targetLook.lengthSqr()
                        >= EPSILON
        ) {
            targetLook =
                    targetLook.normalize();
        }

        return new Transform(
                targetPosition,
                targetMovement,
                yawFromLook(
                        targetLook
                ),
                pitchFromLook(
                        targetLook
                )
        );
    }


    private static Vec3 transformVector(
            Vec3 vector,
            Basis source,
            Basis target
    ) {
        double localRight =
                vector.dot(
                        source.right()
                );

        double localFront =
                vector.dot(
                        source.front()
                );

        return target.right()
                .scale(
                        -localRight
                )
                .add(
                        0.0D,
                        vector.y,
                        0.0D
                )
                .add(
                        target.front()
                                .scale(
                                        -localFront
                                )
                );
    }

    private static boolean isActivePortalDoor(
            BlockState state
    ) {
        return state.getBlock()
                instanceof MusavaccaPortalDoorBlock

                && state.getValue(
                MusavaccaPortalDoorBlock.PORTAL
        )

                && state.getValue(
                MusavaccaPortalDoorBlock.OPEN
        );
    }


    private static Basis basis(
            Door door
    ) {
        Direction normal =
                door.plane()
                        .normal();

        return new Basis(
                directionVector(
                        normal.getClockWise()
                ),
                directionVector(
                        normal
                )
        );
    }


    private static PortalPlane portalPlane(
            BlockPos pos,
            BlockState state
    ) {
        VoxelShape panel =
                MusavaccaPortalDoorHitboxes
                        .portalPanel(
                                state
                        );

        if (panel.isEmpty()) {
            throw new IllegalStateException(
                    "Active Musavacca portal door has no portal panel."
            );
        }

        AABB bounds =
                panel.bounds();

        double centerX =
                pos.getX()
                        + (
                        bounds.minX
                                + bounds.maxX
                )
                        * 0.5D;

        double centerZ =
                pos.getZ()
                        + (
                        bounds.minZ
                                + bounds.maxZ
                )
                        * 0.5D;

        return new PortalPlane(
                state.getValue(
                        MusavaccaPortalDoorBlock.FACING
                ),
                new Vec3(
                        centerX,
                        pos.getY(),
                        centerZ
                )
        );
    }

    private static Vec3 directionVector(
            Direction direction
    ) {
        return new Vec3(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
        );
    }

    private static float yawFromLook(
            Vec3 look
    ) {
        double yaw =
                Math.atan2(
                        look.z,
                        look.x
                )
                        * 180.0D
                        / Math.PI
                        - 90.0D;

        return wrapDegrees(
                (float) yaw
        );
    }

    private static float pitchFromLook(
            Vec3 look
    ) {
        double horizontal =
                Math.sqrt(
                        look.x
                                * look.x
                                + look.z
                                * look.z
                );

        double pitch =
                -Math.atan2(
                        look.y,
                        horizontal
                )
                        * 180.0D
                        / Math.PI;

        return clampPitch(
                (float) pitch
        );
    }

    private static float wrapDegrees(
            float value
    ) {
        value %= 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    private static float clampPitch(
            float value
    ) {
        return Math.max(
                -90.0F,
                Math.min(
                        90.0F,
                        value
                )
        );
    }

    private static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    private record PortalPlane(
            Direction normal,
            Vec3 center
    ) {}

    private record Basis(
            Vec3 right,
            Vec3 front
    ) {}

    private record Door(
            ServerLevel level,
            BlockPos pos,
            int hexColor,
            PortalPlane plane
    ) {}

    private record Transform(
            Vec3 position,
            Vec3 deltaMovement,
            float yRot,
            float xRot
    ) {}
}