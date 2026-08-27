package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PearlPortalFrame {
    private PearlPortalFrame() {}

    public static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;

    public record Shape(
            Direction.Axis axis,
            BlockPos minCorner,
            int width,
            int height,
            Direction frontDirection,
            Direction upDirection,
            BlockPos exitAnchorPos
    ) {
        public Shape(Direction.Axis axis, BlockPos minCorner, int width, int height) {
            this(
                    axis,
                    minCorner,
                    width,
                    height,
                    PearlPortalFrame.defaultFrontDirection(axis),
                    PearlPortalFrame.defaultUpDirection(axis),
                    null
            );
        }

        public Shape(Direction.Axis axis, BlockPos minCorner, int width, int height, Direction frontDirection) {
            this(
                    axis,
                    minCorner,
                    width,
                    height,
                    frontDirection,
                    PearlPortalFrame.defaultUpDirection(axis),
                    null
            );
        }

        public Shape(
                Direction.Axis axis,
                BlockPos minCorner,
                int width,
                int height,
                Direction frontDirection,
                BlockPos exitAnchorPos
        ) {
            this(
                    axis,
                    minCorner,
                    width,
                    height,
                    frontDirection,
                    PearlPortalFrame.defaultUpDirection(axis),
                    exitAnchorPos
            );
        }

        public Shape {
            axis = normalizeAxis(axis);
            minCorner = (minCorner == null ? BlockPos.ZERO : minCorner).immutable();
            width = clamp(width, MIN_WIDTH, MAX_WIDTH);
            height = clamp(height, MIN_HEIGHT, MAX_HEIGHT);
            frontDirection = normalizeFrontDirection(axis, frontDirection);
            upDirection = normalizeUpDirection(axis, frontDirection, upDirection);
            exitAnchorPos = exitAnchorPos == null
                    ? defaultExitAnchor(axis, minCorner, width, height)
                    : exitAnchorPos.immutable();
        }

        public Shape withFrontDirection(Direction frontDirection) {
            Direction safeFront = normalizeFrontDirection(this.axis, frontDirection);
            Direction safeUp = normalizeUpDirection(this.axis, safeFront, this.upDirection);

            return new Shape(
                    this.axis,
                    this.minCorner,
                    this.width,
                    this.height,
                    safeFront,
                    safeUp,
                    this.exitAnchorPos
            );
        }

        public Shape withUpDirection(Direction upDirection) {
            return new Shape(
                    this.axis,
                    this.minCorner,
                    this.width,
                    this.height,
                    this.frontDirection,
                    upDirection,
                    this.exitAnchorPos
            );
        }

        public Shape withExitAnchor(BlockPos exitAnchorPos) {
            return new Shape(
                    this.axis,
                    this.minCorner,
                    this.width,
                    this.height,
                    this.frontDirection,
                    this.upDirection,
                    exitAnchorPos
            );
        }

        public boolean isFlat() {
            return this.axis == Direction.Axis.Y;
        }

        public boolean exitsUp() {
            return this.isFlat() && this.frontDirection == Direction.UP;
        }

        public Direction widthDirection() {
            return PearlPortalFrame.widthDirection(this.axis);
        }

        public Direction heightDirection() {
            return PearlPortalFrame.heightDirection(this.axis);
        }

        public Direction basisRightDirection() {
            if (!this.isFlat()) {
                return this.widthDirection();
            }

            return perpendicularRightOf(this.upDirection, this.frontDirection);
        }

        public Direction basisUpDirection() {
            return this.isFlat() ? this.upDirection : Direction.UP;
        }

        public int basisRightSize() {
            if (!this.isFlat()) {
                return this.width;
            }

            return this.sizeAlong(this.basisRightDirection());
        }

        public int basisUpSize() {
            if (!this.isFlat()) {
                return this.height;
            }

            return this.sizeAlong(this.basisUpDirection());
        }

        public Vec3 basisOrigin() {
            if (!this.isFlat()) {
                return Vec3.atLowerCornerOf(this.minCorner);
            }

            Vec3 origin = Vec3.atLowerCornerOf(this.minCorner);

            origin = shiftOriginForNegativeDirection(
                    origin,
                    this.basisRightDirection(),
                    this.basisRightSize()
            );

            origin = shiftOriginForNegativeDirection(
                    origin,
                    this.basisUpDirection(),
                    this.basisUpSize()
            );

            return origin;
        }

        private int sizeAlong(Direction direction) {
            return direction.getAxis() == Direction.Axis.X ? this.width : this.height;
        }

        public Direction backDirection() {
            return this.frontDirection.getOpposite();
        }

        public Vec3 center() {
            return Vec3.atLowerCornerOf(this.minCorner)
                    .add(directionVector(this.widthDirection()).scale(this.width / 2.0D))
                    .add(directionVector(this.heightDirection()).scale(this.height / 2.0D))
                    .add(planeCenterOffset(this.axis));
        }

        public Vec3 exitAnchorLandingPos() {
            return Vec3.atBottomCenterOf(this.exitAnchorPos).add(0.0D, 1.0D, 0.0D);
        }

        public Direction frontDirectionFromPosition(Vec3 position) {
            Vec3 center = this.center();

            return switch (this.axis) {
                case X -> position.z < center.z ? Direction.NORTH : Direction.SOUTH;
                case Z -> position.x < center.x ? Direction.WEST : Direction.EAST;
                case Y -> position.y < center.y ? Direction.DOWN : Direction.UP;
            };
        }

        public Direction upDirectionFromAnchor(BlockPos anchor) {
            if (!this.isFlat()) {
                return Direction.UP;
            }

            return nearestHorizontalDirection(Vec3.atCenterOf(anchor).subtract(this.center()))
                    .orElse(this.upDirection);
        }

        public Direction upDirectionFromReference(Vec3 reference) {
            if (!this.isFlat()) {
                return Direction.UP;
            }

            return nearestHorizontalDirection(reference.subtract(this.center()))
                    .orElse(this.upDirection);
        }

        public BlockPos closestFrameAnchor(Vec3 reference) {
            final BlockPos[] bestPos = {
                    defaultExitAnchor(this.axis, this.minCorner, this.width, this.height)
            };

            final double[] bestDistance = {Double.POSITIVE_INFINITY};

            this.forEachFrameBlock(pos -> {
                double distance = Vec3.atCenterOf(pos).distanceToSqr(reference);

                if (distance < bestDistance[0]) {
                    bestDistance[0] = distance;
                    bestPos[0] = pos.immutable();
                }
            });

            return bestPos[0];
        }

        public void forEachInteriorBlock(Consumer<BlockPos> consumer) {
            Direction widthDir = this.widthDirection();
            Direction heightDir = this.heightDirection();

            for (int x = 0; x < this.width; x++) {
                for (int h = 0; h < this.height; h++) {
                    consumer.accept(this.minCorner.relative(widthDir, x).relative(heightDir, h));
                }
            }
        }

        public void forEachFrameBlock(Consumer<BlockPos> consumer) {
            Direction widthDir = this.widthDirection();
            Direction heightDir = this.heightDirection();

            for (int x = 0; x < this.width; x++) {
                consumer.accept(this.minCorner.relative(widthDir, x).relative(heightDir, -1));
                consumer.accept(this.minCorner.relative(widthDir, x).relative(heightDir, this.height));
            }

            for (int h = 0; h < this.height; h++) {
                consumer.accept(this.minCorner.relative(widthDir, -1).relative(heightDir, h));
                consumer.accept(this.minCorner.relative(widthDir, this.width).relative(heightDir, h));
            }
        }
    }

    public static Optional<Shape> findIgnitableShape(BlockGetter level, BlockPos insidePos) {
        Optional<Shape> xShape = findShape(
                level,
                insidePos,
                Direction.Axis.X,
                PearlPortalFrame::canBecomePortalInterior
        );

        if (xShape.isPresent()) {
            return xShape;
        }

        Optional<Shape> zShape = findShape(
                level,
                insidePos,
                Direction.Axis.Z,
                PearlPortalFrame::canBecomePortalInterior
        );

        if (zShape.isPresent()) {
            return zShape;
        }

        return findShape(
                level,
                insidePos,
                Direction.Axis.Y,
                PearlPortalFrame::canBecomePortalInterior
        );
    }

    public static Optional<Shape> findExistingShape(BlockGetter level, BlockPos portalPos, Direction.Axis axis) {
        return findShape(level, portalPos, axis, state -> state.is(ModBlocks.PEARL_PORTAL.get()));
    }

    public static Direction widthDirection(Direction.Axis axis) {
        return switch (normalizeAxis(axis)) {
            case X -> Direction.EAST;
            case Z -> Direction.SOUTH;
            case Y -> Direction.EAST;
        };
    }

    public static Direction heightDirection(Direction.Axis axis) {
        return switch (normalizeAxis(axis)) {
            case X, Z -> Direction.UP;
            case Y -> Direction.SOUTH;
        };
    }

    public static Direction defaultFrontDirection(Direction.Axis axis) {
        return switch (normalizeAxis(axis)) {
            case X -> Direction.SOUTH;
            case Z -> Direction.EAST;
            case Y -> Direction.UP;
        };
    }

    public static Direction defaultUpDirection(Direction.Axis axis) {
        return normalizeAxis(axis) == Direction.Axis.Y ? Direction.NORTH : Direction.UP;
    }

    public static boolean isValidFrontDirection(Direction.Axis axis, Direction direction) {
        return direction != null && direction.getAxis() == normalAxis(axis);
    }

    public static boolean isValidUpDirection(Direction.Axis axis, Direction frontDirection, Direction upDirection) {
        axis = normalizeAxis(axis);

        if (axis != Direction.Axis.Y) {
            return upDirection == Direction.UP;
        }

        return upDirection != null
                && upDirection.getAxis().isHorizontal()
                && isValidFrontDirection(axis, frontDirection);
    }

    public static boolean isValidUpDirection(Direction.Axis axis, Direction upDirection) {
        return isValidUpDirection(axis, defaultFrontDirection(axis), upDirection);
    }

    public static Direction normalizeFrontDirection(Direction.Axis axis, Direction direction) {
        return isValidFrontDirection(axis, direction)
                ? direction
                : defaultFrontDirection(axis);
    }

    public static Direction normalizeUpDirection(Direction.Axis axis, Direction direction) {
        return normalizeUpDirection(axis, defaultFrontDirection(axis), direction);
    }

    public static Direction normalizeUpDirection(Direction.Axis axis, Direction frontDirection, Direction upDirection) {
        axis = normalizeAxis(axis);
        frontDirection = normalizeFrontDirection(axis, frontDirection);

        if (isValidUpDirection(axis, frontDirection, upDirection)) {
            return upDirection;
        }

        Direction fallback = defaultUpDirection(axis);

        if (isValidUpDirection(axis, frontDirection, fallback)) {
            return fallback;
        }

        return Direction.NORTH;
    }

    public static Direction.Axis normalAxis(Direction.Axis axis) {
        return switch (normalizeAxis(axis)) {
            case X -> Direction.Axis.Z;
            case Z -> Direction.Axis.X;
            case Y -> Direction.Axis.Y;
        };
    }

    public static Direction.Axis normalizeAxis(Direction.Axis axis) {
        return axis == null ? Direction.Axis.X : axis;
    }

    public static Optional<Direction> nearestHorizontalDirection(Vec3 vector) {
        if (vector == null) {
            return Optional.empty();
        }

        double absX = Math.abs(vector.x);
        double absZ = Math.abs(vector.z);

        if (absX <= 1.0E-6D && absZ <= 1.0E-6D) {
            return Optional.empty();
        }

        if (absX >= absZ) {
            return Optional.of(vector.x >= 0.0D ? Direction.EAST : Direction.WEST);
        }

        return Optional.of(vector.z >= 0.0D ? Direction.SOUTH : Direction.NORTH);
    }

    private static Optional<Shape> findShape(
            BlockGetter level,
            BlockPos insidePos,
            Direction.Axis axis,
            Predicate<BlockState> interiorPredicate
    ) {
        axis = normalizeAxis(axis);

        if (!isInterior(level, insidePos, interiorPredicate)) {
            return Optional.empty();
        }

        Direction widthDir = widthDirection(axis);
        Direction heightDir = heightDirection(axis);

        BlockPos minHeightCorner = scanToMin(level, insidePos, heightDir, interiorPredicate, MAX_HEIGHT);
        BlockPos minCorner = scanToMin(level, minHeightCorner, widthDir, interiorPredicate, MAX_WIDTH);

        int width = measure(level, minCorner, widthDir, interiorPredicate, MAX_WIDTH);
        int height = measure(level, minCorner, heightDir, interiorPredicate, MAX_HEIGHT);

        if (width < MIN_WIDTH || width > MAX_WIDTH || height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return Optional.empty();
        }

        Shape shape = new Shape(axis, minCorner, width, height);
        return isValidFrame(level, shape, interiorPredicate) ? Optional.of(shape) : Optional.empty();
    }

    private static BlockPos scanToMin(
            BlockGetter level,
            BlockPos start,
            Direction positiveDirection,
            Predicate<BlockState> interiorPredicate,
            int maxDistance
    ) {
        BlockPos current = start.immutable();

        for (int i = 1; i < maxDistance; i++) {
            BlockPos next = current.relative(positiveDirection, -1);

            if (!isInterior(level, next, interiorPredicate)) {
                break;
            }

            current = next;
        }

        return current;
    }

    private static int measure(
            BlockGetter level,
            BlockPos minCorner,
            Direction direction,
            Predicate<BlockState> interiorPredicate,
            int max
    ) {
        int size = 0;

        while (size <= max) {
            if (!isInterior(level, minCorner.relative(direction, size), interiorPredicate)) {
                break;
            }

            size++;
        }

        return size;
    }

    private static boolean isInterior(
            BlockGetter level,
            BlockPos pos,
            Predicate<BlockState> interiorPredicate
    ) {
        return interiorPredicate.test(level.getBlockState(pos));
    }

    private static boolean isValidFrame(
            BlockGetter level,
            Shape shape,
            Predicate<BlockState> interiorPredicate
    ) {
        Direction widthDir = shape.widthDirection();
        Direction heightDir = shape.heightDirection();

        for (int x = 0; x < shape.width(); x++) {
            if (!isPearlPortalFrame(level, shape.minCorner().relative(widthDir, x).relative(heightDir, -1))) {
                return false;
            }

            if (!isPearlPortalFrame(level, shape.minCorner().relative(widthDir, x).relative(heightDir, shape.height()))) {
                return false;
            }
        }

        for (int h = 0; h < shape.height(); h++) {
            if (!isPearlPortalFrame(level, shape.minCorner().relative(widthDir, -1).relative(heightDir, h))) {
                return false;
            }

            if (!isPearlPortalFrame(level, shape.minCorner().relative(widthDir, shape.width()).relative(heightDir, h))) {
                return false;
            }
        }

        for (int x = 0; x < shape.width(); x++) {
            for (int h = 0; h < shape.height(); h++) {
                BlockPos pos = shape.minCorner().relative(widthDir, x).relative(heightDir, h);

                if (!interiorPredicate.test(level.getBlockState(pos))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isPearlPortalFrame(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlockTags.PEARL_PORTAL_FRAME);
    }

    private static boolean canBecomePortalInterior(BlockState state) {
        return state.canBeReplaced()
                || state.is(ModBlocks.PEARL_FIRE.get())
                || state.is(ModBlocks.PEARL_PORTAL.get());
    }

    private static BlockPos defaultExitAnchor(
            Direction.Axis axis,
            BlockPos minCorner,
            int width,
            int height
    ) {
        Direction widthDir = widthDirection(axis);
        Direction heightDir = heightDirection(axis);

        return minCorner
                .relative(widthDir, Math.max(0, width / 2))
                .relative(heightDir, -1)
                .immutable();
    }

    private static Direction perpendicularRightOf(Direction upDirection, Direction frontDirection) {
        Vec3 right = cross(directionVector(upDirection), directionVector(frontDirection));
        return nearestDirection(right).filter(direction -> direction.getAxis().isHorizontal()).orElse(Direction.EAST);
    }

    private static Optional<Direction> nearestDirection(Vec3 vector) {
        if (vector == null) {
            return Optional.empty();
        }

        double absX = Math.abs(vector.x);
        double absY = Math.abs(vector.y);
        double absZ = Math.abs(vector.z);

        if (absX <= 1.0E-6D && absY <= 1.0E-6D && absZ <= 1.0E-6D) {
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

    private static Vec3 shiftOriginForNegativeDirection(Vec3 origin, Direction direction, int size) {
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            return origin.add(directionVector(direction.getOpposite()).scale(size));
        }

        return origin;
    }

    private static Vec3 planeCenterOffset(Direction.Axis axis) {
        return switch (normalizeAxis(axis)) {
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

    private static Vec3 directionVector(Direction direction) {
        return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}