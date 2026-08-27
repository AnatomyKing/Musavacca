package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class PearlPortalTransform {
    private PearlPortalTransform() {}

    private static final double EPSILON = 1.0E-6D;
    private static final double MIN_EXIT_CLEARANCE = 0.125D;
    private static final double EXTRA_EXIT_CLEARANCE = 0.05D;

    private static final double FLAT_DOWN_EXTRA_EXIT_CLEARANCE = 0.30D;

    private static final double FLAT_UP_MIN_LAUNCH_Y = 0.55D;
    private static final double FLAT_UP_MAX_LAUNCH_Y = 1.20D;
    private static final double FLAT_UP_MIN_HORIZONTAL_SPEED = 0.18D;
    private static final double FLAT_UP_MAX_HORIZONTAL_SPEED = 0.95D;

    public record Result(
            Vec3 position,
            Vec3 deltaMovement,
            float yRot,
            float xRot
    ) {}

    private record LocalVector(double right, double up, double front) {}

    private record AxisRange(double min, double max) {}

    private record TransformBasis(
            Direction rightDirection,
            Direction upDirection,
            Direction frontDirection,
            int rightSize,
            int upSize,
            Vec3 origin
    ) {
        Vec3 rightVector() {
            return directionVector(this.rightDirection);
        }

        Vec3 upVector() {
            return directionVector(this.upDirection);
        }

        Vec3 frontVector() {
            return directionVector(this.frontDirection);
        }

        Vec3 localPosition(Vec3 worldPosition) {
            Vec3 relative = worldPosition.subtract(this.origin);

            return new Vec3(
                    relative.dot(this.rightVector()),
                    relative.dot(this.upVector()),
                    relative.dot(this.frontVector())
            );
        }

        LocalVector localVector(Vec3 worldVector) {
            return new LocalVector(
                    worldVector.dot(this.rightVector()),
                    worldVector.dot(this.upVector()),
                    worldVector.dot(this.frontVector())
            );
        }

        Vec3 worldVector(double right, double up, double front) {
            return this.rightVector().scale(right)
                    .add(this.upVector().scale(up))
                    .add(this.frontVector().scale(front));
        }

        Vec3 worldPosition(double right, double up, double frontClearance, Direction.Axis planeAxis) {
            return this.origin
                    .add(this.rightVector().scale(right))
                    .add(this.upVector().scale(up))
                    .add(planeCenterOffset(planeAxis))
                    .add(this.frontVector().scale(frontClearance));
        }
    }

    public static Result calculate(
            Entity entity,
            PearlPortalResolver.ResolvedPortal sourcePortal,
            PearlPortalResolver.ResolvedPortal targetPortal
    ) {
        PearlPortalFrame.Shape sourceShape = sourcePortal.shape();
        PearlPortalFrame.Shape targetShape = targetPortal.shape();

        TransformBasis sourceBasis = basisOf(sourceShape);
        TransformBasis targetBasis = basisOf(targetShape);

        double rightSign = rightTransformSign(
                sourceBasis,
                targetBasis
        );

        double entityHalfWidth = Math.max(0.0D, entity.getBbWidth() * 0.5D);
        double entityHeight = Math.max(0.0D, entity.getBbHeight());

        Vec3 entityPos = entity.position();
        Vec3 entityDelta = entity.getDeltaMovement();

        Vec3 sourceLocalPos = sourceBasis.localPosition(entityPos);

        double targetRight = mapLocalAxis(
                sourceLocalPos.x,
                sourceShape,
                targetShape,
                sourceBasis.rightSize(),
                targetBasis.rightSize(),
                entityHalfWidth,
                entityHeight,
                false,
                rightSign < 0.0D
        );

        double targetUp = mapLocalAxis(
                sourceLocalPos.y,
                sourceShape,
                targetShape,
                sourceBasis.upSize(),
                targetBasis.upSize(),
                entityHalfWidth,
                entityHeight,
                true,
                false
        );

        int exitSide = chooseExitSide(sourceShape, sourceBasis, entityPos, entityDelta);

        double exitClearance = exitClearanceFor(targetShape, exitSide, entityHalfWidth);
        double targetFrontClearance = exitSide * exitClearance;

        Vec3 targetPos = targetBasis.worldPosition(
                targetRight,
                targetUp,
                targetFrontClearance,
                targetShape.axis()
        );

        LocalVector localDelta = sourceBasis.localVector(entityDelta);

        Vec3 targetDelta = targetBasis.worldVector(
                localDelta.right() * rightSign,
                localDelta.up(),
                -localDelta.front()
        );

        if (isFlatUpExit(targetShape, exitSide)) {
            targetDelta = makeFlatUpAnchorLaunchVelocity(
                    targetShape,
                    targetBasis,
                    targetPos,
                    localDelta,
                    targetDelta,
                    rightSign
            );
        } else if (sourceShape.exitsUp() && !targetShape.isFlat()) {
            targetDelta = makeStandingExitFromFlatUpVelocity(
                    targetShape,
                    targetPos,
                    localDelta,
                    targetDelta,
                    rightSign
            );
        }

        LocalVector localLook = sourceBasis.localVector(entity.getLookAngle());
        double lookRightSign = lookRightSign(
                sourceShape,
                targetShape,
                rightSign,
                exitSide
        );

        Vec3 transformedLook = targetBasis.worldVector(
                localLook.right() * lookRightSign,
                localLook.up(),
                -localLook.front()
        );

        if (transformedLook.lengthSqr() >= EPSILON) {
            transformedLook = transformedLook.normalize();
        }

        Vec3 playableLook = playableLook(
                sourceShape,
                targetShape,
                targetBasis,
                targetPos,
                transformedLook,
                localLook,
                lookRightSign,
                exitSide
        );

        float finalXRot = (sourceShape.isFlat() || targetShape.isFlat())
                ? clampPitch(entity.getXRot())
                : pitchFromLook(playableLook);

        return new Result(
                targetPos,
                targetDelta,
                yawFromLook(playableLook),
                finalXRot
        );
    }

    private static Vec3 makeFlatUpAnchorLaunchVelocity(
            PearlPortalFrame.Shape targetShape,
            TransformBasis targetBasis,
            Vec3 targetPos,
            LocalVector localDelta,
            Vec3 transformedDelta,
            double rightSign
    ) {
        Vec3 anchorForward = flatAnchorDirectionVector(targetShape, targetPos);
        Vec3 sideDirection = horizontalOnly(targetBasis.rightVector());

        if (sideDirection.lengthSqr() >= EPSILON) {
            sideDirection = sideDirection.normalize();
        } else {
            sideDirection = perpendicularHorizontal(anchorForward);
        }

        double sourceForwardSpeed = Math.abs(localDelta.front());
        double transformedHorizontalSpeed = horizontalOnly(transformedDelta).length();

        double distanceToAnchor = horizontalOnly(targetShape.exitAnchorLandingPos().subtract(targetPos)).length();
        double distanceBiasedSpeed = distanceToAnchor * 0.12D;

        double horizontalSpeed = clamp(
                Math.max(
                        Math.max(sourceForwardSpeed, transformedHorizontalSpeed) * 0.9D,
                        distanceBiasedSpeed
                ),
                FLAT_UP_MIN_HORIZONTAL_SPEED,
                FLAT_UP_MAX_HORIZONTAL_SPEED
        );

        double sideSpeed = clamp(
                localDelta.right() * rightSign * 0.35D,
                -horizontalSpeed * 0.45D,
                horizontalSpeed * 0.45D
        );

        Vec3 horizontalVelocity = anchorForward.scale(horizontalSpeed)
                .add(sideDirection.scale(sideSpeed));

        if (horizontalVelocity.lengthSqr() < EPSILON) {
            horizontalVelocity = anchorForward.scale(FLAT_UP_MIN_HORIZONTAL_SPEED);
        }

        double upwardSpeed = clamp(
                Math.max(Math.abs(localDelta.front()) * 0.55D, FLAT_UP_MIN_LAUNCH_Y),
                FLAT_UP_MIN_LAUNCH_Y,
                FLAT_UP_MAX_LAUNCH_Y
        );

        return new Vec3(
                horizontalVelocity.x,
                Math.max(transformedDelta.y, upwardSpeed),
                horizontalVelocity.z
        );
    }

    private static Vec3 makeStandingExitFromFlatUpVelocity(
            PearlPortalFrame.Shape targetShape,
            Vec3 targetPos,
            LocalVector localDelta,
            Vec3 transformedDelta,
            double rightSign
    ) {
        Vec3 forward = standingExitForward(targetShape, targetPos);
        Vec3 right = perpendicularHorizontal(forward);

        double forwardSpeed = Math.abs(localDelta.up());
        double sideSpeed = localDelta.right() * rightSign;

        Vec3 horizontalVelocity = forward.scale(forwardSpeed)
                .add(right.scale(sideSpeed));

        if (horizontalVelocity.lengthSqr() >= EPSILON) {
            return new Vec3(horizontalVelocity.x, 0.0D, horizontalVelocity.z);
        }

        Vec3 fallback = horizontalOnly(transformedDelta);

        if (fallback.lengthSqr() >= EPSILON) {
            return new Vec3(fallback.x, 0.0D, fallback.z);
        }

        return new Vec3(
                forward.x * FLAT_UP_MIN_HORIZONTAL_SPEED,
                0.0D,
                forward.z * FLAT_UP_MIN_HORIZONTAL_SPEED
        );
    }

    private static Vec3 playableLook(
            PearlPortalFrame.Shape sourceShape,
            PearlPortalFrame.Shape targetShape,
            TransformBasis targetBasis,
            Vec3 targetPos,
            Vec3 transformedLook,
            LocalVector localLook,
            double lookRightSign,
            int exitSide
    ) {
        if (sourceShape.exitsUp() && !targetShape.isFlat()) {
            Vec3 forward = standingExitForward(targetShape, targetPos);
            Vec3 right = perpendicularHorizontal(forward);

            Vec3 look = forward.scale(Math.max(0.45D, Math.abs(localLook.up())))
                    .add(right.scale(clamp(localLook.right() * lookRightSign, -0.85D, 0.85D)));

            if (look.lengthSqr() >= EPSILON) {
                return look.normalize();
            }

            return forward;
        }

        if (!targetShape.isFlat()) {
            if (transformedLook.lengthSqr() >= EPSILON) {
                return transformedLook;
            }

            return directionVector(targetShape.frontDirection());
        }

        if (isFlatUpExit(targetShape, exitSide)) {
            Vec3 forward = flatAnchorDirectionVector(targetShape, targetPos);
            Vec3 right = horizontalOnly(targetBasis.rightVector());

            if (right.lengthSqr() >= EPSILON) {
                right = right.normalize();
            } else {
                right = perpendicularHorizontal(forward);
            }

            Vec3 look = forward.scale(Math.max(0.45D, Math.abs(localLook.front())))
                    .add(right.scale(clamp(localLook.right() * lookRightSign, -0.45D, 0.45D)));

            if (look.lengthSqr() >= EPSILON) {
                return look.normalize();
            }

            return forward;
        }

        Vec3 horizontalLook = horizontalOnly(transformedLook);

        if (horizontalLook.lengthSqr() >= EPSILON) {
            return horizontalLook.normalize();
        }

        Vec3 fallback = horizontalOnly(targetBasis.upVector());

        if (fallback.lengthSqr() >= EPSILON) {
            return fallback.normalize();
        }

        return directionVector(Direction.NORTH);
    }

    private static TransformBasis basisOf(PearlPortalFrame.Shape shape) {
        if (!shape.isFlat()) {
            return new TransformBasis(
                    shape.widthDirection(),
                    Direction.UP,
                    shape.frontDirection(),
                    shape.width(),
                    shape.height(),
                    Vec3.atLowerCornerOf(shape.minCorner())
            );
        }

        Direction anchorDirection = flatAnchorDirection(shape);
        Direction virtualUpDirection = anchorDirection.getOpposite();
        Direction rightDirection = perpendicularRightOf(virtualUpDirection, shape.frontDirection());

        int rightSize = flatSizeAlong(shape, rightDirection);
        int upSize = flatSizeAlong(shape, virtualUpDirection);

        Vec3 origin = Vec3.atLowerCornerOf(shape.minCorner());
        origin = shiftOriginForNegativeDirection(origin, rightDirection, rightSize);
        origin = shiftOriginForNegativeDirection(origin, virtualUpDirection, upSize);

        return new TransformBasis(
                rightDirection,
                virtualUpDirection,
                shape.frontDirection(),
                rightSize,
                upSize,
                origin
        );
    }

    private static double rightTransformSign(
            TransformBasis sourceBasis,
            TransformBasis targetBasis
    ) {
        return -basisHandedness(sourceBasis)
                * basisHandedness(targetBasis);
    }

    private static double basisHandedness(
            TransformBasis basis
    ) {
        double handedness = cross(
                basis.rightVector(),
                basis.upVector()
        ).dot(basis.frontVector());

        return handedness < 0.0D
                ? -1.0D
                : 1.0D;
    }

    private static double lookRightSign(
            PearlPortalFrame.Shape sourceShape,
            PearlPortalFrame.Shape targetShape,
            double rightSign,
            int exitSide
    ) {
        return shouldMirrorCrossOrientationLook(sourceShape, targetShape, exitSide)
                ? -rightSign
                : rightSign;
    }

    private static boolean shouldMirrorCrossOrientationLook(
            PearlPortalFrame.Shape sourceShape,
            PearlPortalFrame.Shape targetShape,
            int exitSide
    ) {
        boolean standingBackToFlatUnderside =
                !sourceShape.isFlat()
                        && targetShape.isFlat()
                        && isBackSide(exitSide)
                        && isFlatDownExit(targetShape, exitSide);

        boolean flatTopToStandingFront =
                sourceShape.isFlat()
                        && !targetShape.isFlat()
                        && isFlatTopSide(sourceShape, exitSide)
                        && isFrontSide(exitSide);

        return standingBackToFlatUnderside || flatTopToStandingFront;
    }

    private static boolean isFrontSide(int exitSide) {
        return exitSide > 0;
    }

    private static boolean isBackSide(int exitSide) {
        return exitSide < 0;
    }

    private static boolean isFlatTopSide(PearlPortalFrame.Shape shape, int exitSide) {
        return shape.isFlat() && directionVector(shape.frontDirection()).scale(exitSide).y > 0.0D;
    }

    private static double mapLocalAxis(
            double sourceValue,
            PearlPortalFrame.Shape sourceShape,
            PearlPortalFrame.Shape targetShape,
            int sourceSize,
            int targetSize,
            double entityHalfWidth,
            double entityHeight,
            boolean upAxis,
            boolean reverseTargetAxis
    ) {
        AxisRange sourceRange = axisRange(sourceShape, sourceSize, entityHalfWidth, entityHeight, upAxis);
        AxisRange targetRange = axisRange(targetShape, targetSize, entityHalfWidth, entityHeight, upAxis);

        double mapped = mapRangeClamped(
                sourceValue,
                sourceRange.min(),
                sourceRange.max(),
                targetRange.min(),
                targetRange.max()
        );

        if (!reverseTargetAxis) {
            return mapped;
        }

        return targetRange.min()
                + targetRange.max()
                - mapped;
    }

    private static AxisRange axisRange(
            PearlPortalFrame.Shape shape,
            int size,
            double entityHalfWidth,
            double entityHeight,
            boolean upAxis
    ) {
        if (upAxis && !shape.isFlat()) {
            return new AxisRange(0.0D, Math.max(0.0D, size - entityHeight));
        }

        double min = Math.min(entityHalfWidth, size * 0.5D);
        double max = Math.max(min, size - entityHalfWidth);

        return new AxisRange(min, max);
    }

    private static int chooseExitSide(
            PearlPortalFrame.Shape sourceShape,
            TransformBasis sourceBasis,
            Vec3 entityPos,
            Vec3 entityDelta
    ) {
        Vec3 sourceFront = sourceBasis.frontVector();
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

    private static double exitClearanceFor(
            PearlPortalFrame.Shape targetShape,
            int exitSide,
            double entityHalfWidth
    ) {
        double clearance = Math.max(MIN_EXIT_CLEARANCE, entityHalfWidth + EXTRA_EXIT_CLEARANCE);

        if (isFlatDownExit(targetShape, exitSide)) {
            clearance += FLAT_DOWN_EXTRA_EXIT_CLEARANCE;
        }

        return clearance;
    }

    private static Vec3 flatAnchorDirectionVector(PearlPortalFrame.Shape targetShape, Vec3 targetPos) {
        Vec3 towardAnchor = horizontalOnly(targetShape.exitAnchorLandingPos().subtract(targetPos));

        if (towardAnchor.lengthSqr() >= EPSILON) {
            return towardAnchor.normalize();
        }

        Vec3 centerToAnchor = horizontalOnly(targetShape.exitAnchorLandingPos().subtract(targetShape.center()));

        if (centerToAnchor.lengthSqr() >= EPSILON) {
            return centerToAnchor.normalize();
        }

        return directionVector(flatAnchorDirection(targetShape));
    }

    private static Vec3 standingExitForward(PearlPortalFrame.Shape targetShape, Vec3 targetPos) {
        Vec3 normal = horizontalOnly(directionVector(targetShape.frontDirection()));

        if (normal.lengthSqr() < EPSILON) {
            return directionVector(Direction.NORTH);
        }

        normal = normal.normalize();

        double sideDot = horizontalOnly(targetPos.subtract(targetShape.center())).dot(normal);

        if (Math.abs(sideDot) >= EPSILON) {
            return normal.scale(sideDot >= 0.0D ? 1.0D : -1.0D).normalize();
        }

        return normal;
    }

    private static Direction flatAnchorDirection(PearlPortalFrame.Shape shape) {
        if (!shape.isFlat()) {
            return Direction.DOWN;
        }

        return PearlPortalFrame
                .nearestHorizontalDirection(Vec3.atCenterOf(shape.exitAnchorPos()).subtract(shape.center()))
                .orElseGet(() -> shape.upDirection().getAxis().isHorizontal()
                        ? shape.upDirection()
                        : PearlPortalFrame.defaultUpDirection(Direction.Axis.Y));
    }

    private static boolean isFlatUpExit(PearlPortalFrame.Shape shape, int exitSide) {
        return shape.isFlat() && directionVector(shape.frontDirection()).scale(exitSide).y > 0.0D;
    }

    private static boolean isFlatDownExit(PearlPortalFrame.Shape shape, int exitSide) {
        return shape.isFlat() && directionVector(shape.frontDirection()).scale(exitSide).y < 0.0D;
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

    private static int flatSizeAlong(PearlPortalFrame.Shape shape, Direction direction) {
        return direction.getAxis() == Direction.Axis.X ? shape.width() : shape.height();
    }

    private static Vec3 shiftOriginForNegativeDirection(Vec3 origin, Direction direction, int size) {
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            return origin.add(directionVector(direction.getOpposite()).scale(size));
        }

        return origin;
    }

    private static Direction perpendicularRightOf(Direction upDirection, Direction frontDirection) {
        Vec3 right = cross(directionVector(upDirection), directionVector(frontDirection));

        return nearestDirection(right)
                .filter(direction -> direction.getAxis().isHorizontal())
                .orElse(Direction.EAST);
    }

    private static Vec3 perpendicularHorizontal(Vec3 forward) {
        Vec3 flat = horizontalOnly(forward);

        if (flat.lengthSqr() < EPSILON) {
            return directionVector(Direction.NORTH);
        }

        flat = flat.normalize();

        return new Vec3(
                -flat.z,
                0.0D,
                flat.x
        );
    }

    private static Vec3 horizontalOnly(Vec3 vector) {
        return new Vec3(vector.x, 0.0D, vector.z);
    }

    private static Vec3 planeCenterOffset(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vec3(0.0D, 0.0D, 0.5D);
            case Z -> new Vec3(0.5D, 0.0D, 0.0D);
            case Y -> new Vec3(0.0D, 0.5D, 0.0D);
        };
    }

    private static Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    private static Optional<Direction> nearestDirection(Vec3 vector) {
        if (vector == null) {
            return Optional.empty();
        }

        double absX = Math.abs(vector.x);
        double absY = Math.abs(vector.y);
        double absZ = Math.abs(vector.z);

        if (absX <= EPSILON && absY <= EPSILON && absZ <= EPSILON) {
            return Optional.empty();
        }

        if (absX >= absY && absX >= absZ) {
            return Optional.of(vector.x >= 0.0D ? Direction.EAST : Direction.WEST);
        }

        if (absY >= absX && absY >= absZ) {
            return Optional.of(vector.y >= 0.0D ? Direction.UP : Direction.DOWN);
        }

        return Optional.of(vector.z >= 0.0D ? Direction.SOUTH : Direction.NORTH);
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