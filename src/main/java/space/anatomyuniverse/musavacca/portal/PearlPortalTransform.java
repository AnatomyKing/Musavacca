// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/portal/PearlPortalTransform.java
package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class PearlPortalTransform {
    private PearlPortalTransform() {}

    private static final double EPSILON = 1.0E-6D;
    private static final double MIN_EXIT_CLEARANCE = 0.125D;
    private static final double EXTRA_EXIT_CLEARANCE = 0.05D;

    public record Result(
            Vec3 position,
            Vec3 deltaMovement,
            float yRot,
            float xRot
    ) {}

    private record LocalVector(double right, double up, double front) {}

    public static Result calculate(
            Entity entity,
            PearlPortalResolver.ResolvedPortal sourcePortal,
            PearlPortalResolver.ResolvedPortal targetPortal
    ) {
        PearlPortalFrame.Shape sourceShape = sourcePortal.shape();
        PearlPortalFrame.Shape targetShape = targetPortal.shape();

        double entityHalfWidth = Math.max(0.0D, entity.getBbWidth() * 0.5D);
        double entityHeight = Math.max(0.0D, entity.getBbHeight());

        Vec3 entityPos = entity.position();
        Vec3 entityDelta = entity.getDeltaMovement();

        double sourceWidthLocal = getWidthLocal(sourceShape, entityPos);
        double sourceHeightLocal = entityPos.y - sourceShape.minCorner().getY();

        double targetWidthLocal = mapSafeWidth(
                sourceWidthLocal,
                sourceShape.width(),
                targetShape.width(),
                entityHalfWidth
        );

        double targetHeightLocal = mapSafeHeight(
                sourceHeightLocal,
                sourceShape.height(),
                targetShape.height(),
                entityHeight
        );

        int exitSide = chooseTargetExitSide(sourceShape, entityPos, entityDelta);
        double exitClearance = Math.max(MIN_EXIT_CLEARANCE, entityHalfWidth + EXTRA_EXIT_CLEARANCE);

        Vec3 targetPos = makeTargetPosition(
                targetShape,
                targetWidthLocal,
                targetHeightLocal,
                exitSide,
                exitClearance
        );

        LocalVector localDelta = toLocalVector(entityDelta, sourceShape);
        Vec3 targetDelta = fromLocalVector(
                localDelta.right(),
                localDelta.up(),
                -localDelta.front(),
                targetShape
        );

        Vec3 look = entity.getLookAngle();
        LocalVector localLook = toLocalVector(look, sourceShape);
        Vec3 targetLook = fromLocalVector(
                localLook.right(),
                localLook.up(),
                -localLook.front(),
                targetShape
        ).normalize();

        if (targetLook.lengthSqr() < EPSILON) {
            targetLook = directionVector(targetShape.frontDirection());
        }

        float yRot = yawFromLook(targetLook);
        float xRot = pitchFromLook(targetLook);

        return new Result(targetPos, targetDelta, yRot, xRot);
    }

    private static double mapSafeWidth(
            double sourceValue,
            int sourceWidth,
            int targetWidth,
            double entityHalfWidth
    ) {
        double sourceMin = Math.min(entityHalfWidth, sourceWidth * 0.5D);
        double sourceMax = Math.max(sourceMin, sourceWidth - entityHalfWidth);

        double targetMin = Math.min(entityHalfWidth, targetWidth * 0.5D);
        double targetMax = Math.max(targetMin, targetWidth - entityHalfWidth);

        return mapRangeClamped(sourceValue, sourceMin, sourceMax, targetMin, targetMax);
    }

    private static double mapSafeHeight(
            double sourceValue,
            int sourceHeight,
            int targetHeight,
            double entityHeight
    ) {
        double sourceMin = 0.0D;
        double sourceMax = Math.max(sourceMin, sourceHeight - entityHeight);

        double targetMin = 0.0D;
        double targetMax = Math.max(targetMin, targetHeight - entityHeight);

        return mapRangeClamped(sourceValue, sourceMin, sourceMax, targetMin, targetMax);
    }

    private static double mapRangeClamped(
            double value,
            double sourceMin,
            double sourceMax,
            double targetMin,
            double targetMax
    ) {
        if (sourceMax <= sourceMin + EPSILON) {
            return (targetMin + targetMax) * 0.5D;
        }

        double clamped = clamp(value, sourceMin, sourceMax);
        double t = (clamped - sourceMin) / (sourceMax - sourceMin);

        return targetMin + (targetMax - targetMin) * t;
    }

    private static double getWidthLocal(PearlPortalFrame.Shape shape, Vec3 position) {
        if (shape.axis() == Direction.Axis.X) {
            return position.x - shape.minCorner().getX();
        }

        return position.z - shape.minCorner().getZ();
    }

    private static Vec3 makeTargetPosition(
            PearlPortalFrame.Shape targetShape,
            double targetWidthLocal,
            double targetHeightLocal,
            int exitSide,
            double exitClearance
    ) {
        Direction front = targetShape.frontDirection();
        double y = targetShape.minCorner().getY() + targetHeightLocal;

        if (targetShape.axis() == Direction.Axis.X) {
            return new Vec3(
                    targetShape.minCorner().getX() + targetWidthLocal,
                    y,
                    targetShape.minCorner().getZ() + 0.5D + front.getStepZ() * exitSide * exitClearance
            );
        }

        return new Vec3(
                targetShape.minCorner().getX() + 0.5D + front.getStepX() * exitSide * exitClearance,
                y,
                targetShape.minCorner().getZ() + targetWidthLocal
        );
    }

    private static int chooseTargetExitSide(
            PearlPortalFrame.Shape sourceShape,
            Vec3 entityPos,
            Vec3 entityDelta
    ) {
        Vec3 sourceFront = directionVector(sourceShape.frontDirection());
        double depth = entityPos.subtract(sourceShape.center()).dot(sourceFront);

        if (Math.abs(depth) > 0.05D) {
            return depth >= 0.0D ? 1 : -1;
        }

        double velocityFront = entityDelta.dot(sourceFront);

        if (Math.abs(velocityFront) > EPSILON) {
            return velocityFront <= 0.0D ? 1 : -1;
        }

        return 1;
    }

    private static LocalVector toLocalVector(Vec3 vector, PearlPortalFrame.Shape shape) {
        Vec3 right = directionVector(shape.widthDirection());
        Vec3 front = directionVector(shape.frontDirection());

        return new LocalVector(
                vector.dot(right),
                vector.y,
                vector.dot(front)
        );
    }

    private static Vec3 fromLocalVector(
            double rightAmount,
            double upAmount,
            double frontAmount,
            PearlPortalFrame.Shape shape
    ) {
        Vec3 right = directionVector(shape.widthDirection());
        Vec3 front = directionVector(shape.frontDirection());

        return right.scale(rightAmount)
                .add(0.0D, upAmount, 0.0D)
                .add(front.scale(frontAmount));
    }

    private static Vec3 directionVector(Direction direction) {
        return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    private static float yawFromLook(Vec3 look) {
        double yaw = Math.atan2(look.z, look.x) * 180.0D / Math.PI - 90.0D;
        return wrapDegrees((float) yaw);
    }

    private static float pitchFromLook(Vec3 look) {
        double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
        double pitch = -Math.atan2(look.y, horizontal) * 180.0D / Math.PI;
        return clampPitch((float) pitch);
    }

    private static float wrapDegrees(float value) {
        value %= 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    private static float clampPitch(float value) {
        return Math.max(-90.0F, Math.min(90.0F, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}