// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/portal/PearlPortalFrame.java
package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
            Direction frontDirection
    ) {
        public Shape(Direction.Axis axis, BlockPos minCorner, int width, int height) {
            this(axis, minCorner, width, height, PearlPortalFrame.defaultFrontDirection(axis));
        }

        public Shape {
            axis = normalizeAxis(axis);
            minCorner = minCorner.immutable();
            width = clamp(width, MIN_WIDTH, MAX_WIDTH);
            height = clamp(height, MIN_HEIGHT, MAX_HEIGHT);
            frontDirection = normalizeFrontDirection(axis, frontDirection);
        }

        public Shape withFrontDirection(Direction frontDirection) {
            return new Shape(
                    this.axis,
                    this.minCorner,
                    this.width,
                    this.height,
                    frontDirection
            );
        }

        public Direction widthDirection() {
            return PearlPortalFrame.widthDirection(axis);
        }

        public Direction backDirection() {
            return this.frontDirection.getOpposite();
        }

        public Vec3 center() {
            if (axis == Direction.Axis.X) {
                return new Vec3(
                        minCorner.getX() + width / 2.0D,
                        minCorner.getY() + height / 2.0D,
                        minCorner.getZ() + 0.5D
                );
            }

            return new Vec3(
                    minCorner.getX() + 0.5D,
                    minCorner.getY() + height / 2.0D,
                    minCorner.getZ() + width / 2.0D
            );
        }

        public Direction frontDirectionFromPosition(Vec3 position) {
            Vec3 center = this.center();

            if (this.axis == Direction.Axis.X) {
                return position.z < center.z ? Direction.NORTH : Direction.SOUTH;
            }

            return position.x < center.x ? Direction.WEST : Direction.EAST;
        }

        public void forEachInteriorBlock(Consumer<BlockPos> consumer) {
            Direction widthDir = widthDirection();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    consumer.accept(minCorner.relative(widthDir, x).above(y));
                }
            }
        }
    }

    public static Optional<Shape> findIgnitableShape(BlockGetter level, BlockPos insidePos) {
        Optional<Shape> xShape = findShape(level, insidePos, Direction.Axis.X, PearlPortalFrame::canBecomePortalInterior);
        if (xShape.isPresent()) return xShape;

        return findShape(level, insidePos, Direction.Axis.Z, PearlPortalFrame::canBecomePortalInterior);
    }

    public static Optional<Shape> findExistingShape(BlockGetter level, BlockPos portalPos, Direction.Axis axis) {
        return findShape(level, portalPos, axis, state -> state.is(ModBlocks.PEARL_PORTAL.get()));
    }

    public static Direction widthDirection(Direction.Axis axis) {
        return normalizeAxis(axis) == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
    }

    public static Direction defaultFrontDirection(Direction.Axis axis) {
        return normalizeAxis(axis) == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
    }

    public static boolean isValidFrontDirection(Direction.Axis axis, Direction direction) {
        if (direction == null || direction.getAxis().isVertical()) {
            return false;
        }

        Direction.Axis normalAxis = normalAxis(axis);
        return direction.getAxis() == normalAxis;
    }

    public static Direction normalizeFrontDirection(Direction.Axis axis, Direction direction) {
        if (isValidFrontDirection(axis, direction)) {
            return direction;
        }

        return defaultFrontDirection(axis);
    }

    public static Direction.Axis normalAxis(Direction.Axis axis) {
        return normalizeAxis(axis) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

    public static Direction.Axis normalizeAxis(Direction.Axis axis) {
        return axis == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X;
    }

    private static Optional<Shape> findShape(
            BlockGetter level,
            BlockPos insidePos,
            Direction.Axis axis,
            Predicate<BlockState> interiorPredicate
    ) {
        if (!isInterior(level, insidePos, interiorPredicate)) {
            return Optional.empty();
        }

        Direction widthDir = widthDirection(axis);
        BlockPos bottom = scanToBottom(level, insidePos, interiorPredicate);
        BlockPos minCorner = scanToLeft(level, bottom, widthDir, interiorPredicate);

        int width = measureWidth(level, minCorner, widthDir, interiorPredicate);
        int height = measureHeight(level, minCorner, interiorPredicate);

        if (width < MIN_WIDTH || width > MAX_WIDTH || height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return Optional.empty();
        }

        Shape shape = new Shape(axis, minCorner, width, height);
        return isValidFrame(level, shape, interiorPredicate) ? Optional.of(shape) : Optional.empty();
    }

    private static BlockPos scanToBottom(
            BlockGetter level,
            BlockPos start,
            Predicate<BlockState> interiorPredicate
    ) {
        BlockPos current = start.immutable();

        for (int i = 1; i < MAX_HEIGHT; i++) {
            BlockPos next = current.below();
            if (!isInterior(level, next, interiorPredicate)) break;
            current = next;
        }

        return current;
    }

    private static BlockPos scanToLeft(
            BlockGetter level,
            BlockPos start,
            Direction widthDir,
            Predicate<BlockState> interiorPredicate
    ) {
        BlockPos current = start.immutable();

        for (int i = 1; i < MAX_WIDTH; i++) {
            BlockPos next = current.relative(widthDir, -1);
            if (!isInterior(level, next, interiorPredicate)) break;
            current = next;
        }

        return current;
    }

    private static int measureWidth(
            BlockGetter level,
            BlockPos minCorner,
            Direction widthDir,
            Predicate<BlockState> interiorPredicate
    ) {
        int width = 0;

        while (width <= MAX_WIDTH) {
            BlockPos pos = minCorner.relative(widthDir, width);
            if (!isInterior(level, pos, interiorPredicate)) break;
            width++;
        }

        return width;
    }

    private static int measureHeight(
            BlockGetter level,
            BlockPos minCorner,
            Predicate<BlockState> interiorPredicate
    ) {
        int height = 0;

        while (height <= MAX_HEIGHT) {
            BlockPos pos = minCorner.above(height);
            if (!isInterior(level, pos, interiorPredicate)) break;
            height++;
        }

        return height;
    }

    private static boolean isInterior(
            BlockGetter level,
            BlockPos pos,
            Predicate<BlockState> interiorPredicate
    ) {
        return interiorPredicate.test(level.getBlockState(pos));
    }

    private static boolean isValidFrame(BlockGetter level, Shape shape, Predicate<BlockState> interiorPredicate) {
        Direction widthDir = shape.widthDirection();

        for (int x = 0; x < shape.width; x++) {
            if (!isBananaPearlFrame(level, shape.minCorner.relative(widthDir, x).below())) return false;
            if (!isBananaPearlFrame(level, shape.minCorner.relative(widthDir, x).above(shape.height))) return false;
        }

        for (int y = 0; y < shape.height; y++) {
            if (!isBananaPearlFrame(level, shape.minCorner.relative(widthDir, -1).above(y))) return false;
            if (!isBananaPearlFrame(level, shape.minCorner.relative(widthDir, shape.width).above(y))) return false;
        }

        for (int x = 0; x < shape.width; x++) {
            for (int y = 0; y < shape.height; y++) {
                BlockState state = level.getBlockState(shape.minCorner.relative(widthDir, x).above(y));
                if (!interiorPredicate.test(state)) return false;
            }
        }

        return true;
    }

    private static boolean isBananaPearlFrame(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.BANANA_PEARL_BLOCK.get());
    }

    private static boolean canBecomePortalInterior(BlockState state) {
        return state.canBeReplaced()
                || state.is(ModBlocks.PEARL_FIRE.get());
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}