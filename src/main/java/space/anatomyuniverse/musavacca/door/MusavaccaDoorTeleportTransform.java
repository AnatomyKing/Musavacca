package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MusavaccaDoorTeleportTransform {

    public static final double PORTAL_ENTRANCE_NUDGE =
            0.17D;

    public static final double PORTAL_EXIT_NUDGE =
            0.07D;

    private static final double EPSILON =
            1.0E-6D;

    private MusavaccaDoorTeleportTransform() {}

    public record Result(
            Vec3 position,
            Vec3 deltaMovement,
            float yRot,
            float xRot
    ) {}

    private record Basis(
            Vec3 right,
            Vec3 front
    ) {}

    public static Result calculate(
            Entity entity,
            MusavaccaDoorTeleportResolver.ResolvedDoor source,
            MusavaccaDoorTeleportResolver.ResolvedDoor target
    ) {
        Basis sourceBasis =
                basis(
                        source.facing()
                );

        Basis targetBasis =
                basis(
                        target.facing()
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
                                source.portalCenter()
                        );

        double sourceRight =
                sourceRelative.dot(
                        sourceBasis.right()
                );

        double sourceUp =
                entity.getY()
                        - source.pos()
                        .getY();

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
                        target.portalCenter().x,
                        target.pos().getY()
                                + targetUp,
                        target.portalCenter().z
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

        return new Result(
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

    private static Basis basis(
            Direction normal
    ) {
        return new Basis(
                directionVector(
                        normal.getClockWise()
                ),
                directionVector(
                        normal
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
}
