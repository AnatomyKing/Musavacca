
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class VocoHitboxes {
    private static final double RAY_EPSILON = 1.0E-7D;

    private VocoHitboxes() {}

    public static Vec3 local16(BlockPos pos, BlockHitResult hit) {
        return local16(pos, hit.getLocation());
    }

    public static Vec3 local16(BlockPos pos, Vec3 worldPos) {
        return new Vec3(
                (worldPos.x - pos.getX()) * 16.0D,
                (worldPos.y - pos.getY()) * 16.0D,
                (worldPos.z - pos.getZ()) * 16.0D
        );
    }

    public static <T> T detect(List<Part<T>> parts, BlockPos pos, BlockHitResult hit, T fallback) {
        Vec3 local = local16(pos, hit);

        for (Part<T> part : parts) {
            if (part.box().contains(local)) {
                return part.value();
            }
        }

        return fallback;
    }

    public static VoxelShape shapeOf(List<? extends Part<?>> parts) {
        VoxelShape shape = Shapes.empty();

        for (Part<?> part : parts) {
            shape = Shapes.or(shape, part.box().toShape());
        }

        return shape;
    }

    public static double rayHitDistance(Vec3 start, Vec3 end, Box box) {
        Vec3 direction = end.subtract(start);

        double tMin = 0.0D;
        double tMax = 1.0D;

        double[] x = clipAxis(start.x, direction.x, box.minX(), box.maxX(), tMin, tMax);
        if (x == null) return Double.NaN;

        double[] y = clipAxis(start.y, direction.y, box.minY(), box.maxY(), x[0], x[1]);
        if (y == null) return Double.NaN;

        double[] z = clipAxis(start.z, direction.z, box.minZ(), box.maxZ(), y[0], y[1]);
        return z == null ? Double.NaN : z[0];
    }

    @Nullable
    private static double[] clipAxis(
            double start,
            double direction,
            double min,
            double max,
            double tMin,
            double tMax
    ) {
        if (Math.abs(direction) < RAY_EPSILON) {
            return start >= min && start <= max
                    ? new double[] {tMin, tMax}
                    : null;
        }

        double inv = 1.0D / direction;
        double t1 = (min - start) * inv;
        double t2 = (max - start) * inv;

        if (t1 > t2) {
            double swap = t1;
            t1 = t2;
            t2 = swap;
        }

        tMin = Math.max(tMin, t1);
        tMax = Math.min(tMax, t2);

        return tMin <= tMax ? new double[] {tMin, tMax} : null;
    }

    public record Part<T>(T value, Box box) {}

    public record Box(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        public boolean contains(Vec3 point) {
            return this.contains(point.x, point.y, point.z);
        }

        public boolean contains(double x, double y, double z) {
            return x >= this.minX && x <= this.maxX
                    && y >= this.minY && y <= this.maxY
                    && z >= this.minZ && z <= this.maxZ;
        }

        public Box shifted(Vec3 offset) {
            return new Box(
                    this.minX + offset.x,
                    this.minY + offset.y,
                    this.minZ + offset.z,
                    this.maxX + offset.x,
                    this.maxY + offset.y,
                    this.maxZ + offset.z
            );
        }

        public Vec3 centerScaled(double scale) {
            return new Vec3(
                    ((this.minX + this.maxX) * 0.5D) * scale,
                    ((this.minY + this.maxY) * 0.5D) * scale,
                    ((this.minZ + this.maxZ) * 0.5D) * scale
            );
        }

        public VoxelShape toShape() {
            return Block.box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
        }
    }
}

