package space.anatomyuniverse.musavacca.trapdoor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MusavaccaTrapdoorTeleportTransform {
    public static final double PORTAL_ENTRANCE_NUDGE = -0.07D;
    public static final double PORTAL_EXIT_NUDGE = 0.17D;

    private MusavaccaTrapdoorTeleportTransform() {}

    public record Result(Vec3 position, Vec3 deltaMovement, float yRot, float xRot) {}

    public static Result calculate(Entity entity, MusavaccaTrapdoorTeleport.ResolvedTrapdoor source,
            MusavaccaTrapdoorTeleport.ResolvedTrapdoor target) {
        double entityHalfWidth = Math.max(0.0D, entity.getBbWidth() * 0.5D);
        Vec3 sourceRelative = entity.position().subtract(source.portalCenter());
        double maximumOffset = Math.max(0.0D, 0.5D - entityHalfWidth);
        double targetOffsetX = clamp(sourceRelative.x, -maximumOffset, maximumOffset);
        double targetOffsetZ = clamp(sourceRelative.z, -maximumOffset, maximumOffset);
        boolean enteredFromBelow = enteredFromBelow(entity, source.portalCenter().y);
        double exitClearance = Math.max(PORTAL_EXIT_NUDGE, entityHalfWidth + 0.05D);
        double targetY = enteredFromBelow ? target.portalCenter().y + exitClearance : target.portalCenter().y - exitClearance - 0.60D;
        Vec3 targetPosition = new Vec3(target.portalCenter().x + targetOffsetX, targetY, target.portalCenter().z + targetOffsetZ);
        Vec3 sourceMovement = entity.getDeltaMovement();
        Vec3 targetMovement = sourceMovement;
        Vec3 playableLook = entity.getLookAngle();
        if (enteredFromBelow) {
            Vec3 launchDirection = undersideLaunchDirection(entity, sourceRelative);
            targetMovement = makeUndersideLaunchVelocity(sourceMovement, launchDirection);
            playableLook = horizontalLookOrFallback(targetMovement, launchDirection);
        }
        return new Result(targetPosition, targetMovement, yawFromLook(playableLook), clampPitch(entity.getXRot()));
    }

    public static boolean enteredFromBelow(Entity entity, double portalPlaneY) {
        double verticalMovement = entity.getDeltaMovement().y;
        if (verticalMovement > 1.0E-6D) {
            return true;
        }
        if (verticalMovement < -1.0E-6D) {
            return false;
        }
        return entity.getY() < portalPlaneY;
    }

    private static Vec3 undersideLaunchDirection(Entity entity, Vec3 sourceRelative) {
        Vec3 movementDirection = horizontalOnly(entity.getDeltaMovement());
        if (movementDirection.lengthSqr() >= 1.0E-4D) {
            return movementDirection.normalize();
        }
        Vec3 lookDirection = horizontalOnly(entity.getLookAngle());
        if (lookDirection.lengthSqr() >= 1.0E-4D) {
            return lookDirection.normalize();
        }
        Vec3 entryOffsetDirection = horizontalOnly(sourceRelative);
        if (entryOffsetDirection.lengthSqr() >= 1.0E-4D) {
            return entryOffsetDirection.normalize();
        }
        return new Vec3(0.0D, 0.0D, -1.0D);
    }

    private static Vec3 makeUndersideLaunchVelocity(Vec3 sourceMovement, Vec3 launchDirection) {
        Vec3 sourceHorizontalMovement = horizontalOnly(sourceMovement);
        double sourceVerticalSpeed = Math.abs(sourceMovement.y);
        double sourceHorizontalSpeed = sourceHorizontalMovement.length();
        double horizontalSpeed = clamp(Math.max(sourceVerticalSpeed, sourceHorizontalSpeed) * 0.9D, 0.18D, 0.95D);
        Vec3 safeLaunchDirection = horizontalOnly(launchDirection);
        if (safeLaunchDirection.lengthSqr() < 1.0E-6D) {
            safeLaunchDirection = new Vec3(0.0D, 0.0D, -1.0D);
        } else {
            safeLaunchDirection = safeLaunchDirection.normalize();
        }
        Vec3 horizontalVelocity = safeLaunchDirection.scale(horizontalSpeed);
        double upwardSpeed = clamp(Math.max(sourceVerticalSpeed * 0.55D, 0.55D), 0.55D, 1.20D);
        return new Vec3(horizontalVelocity.x, Math.max(sourceMovement.y, upwardSpeed), horizontalVelocity.z);
    }

    private static Vec3 horizontalLookOrFallback(Vec3 movement, Vec3 fallback) {
        Vec3 horizontal = horizontalOnly(movement);
        if (horizontal.lengthSqr() >= 1.0E-6D) {
            return horizontal.normalize();
        }
        Vec3 horizontalFallback = horizontalOnly(fallback);
        if (horizontalFallback.lengthSqr() >= 1.0E-6D) {
            return horizontalFallback.normalize();
        }
        return new Vec3(0.0D, 0.0D, -1.0D);
    }

    private static Vec3 horizontalOnly(Vec3 vector) {
        return new Vec3(vector.x, 0.0D, vector.z);
    }

    private static float yawFromLook(Vec3 look) {
        double yaw = Math.atan2(look.z, look.x) * 180.0D / Math.PI - 90.0D;
        return wrapDegrees((float) yaw);
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
