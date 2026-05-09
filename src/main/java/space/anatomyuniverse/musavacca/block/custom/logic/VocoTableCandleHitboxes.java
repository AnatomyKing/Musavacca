// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableCandleHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableCandleHitboxes {
    private static final double VANILLA_CANDLE_SHAPE_HEIGHT = 6.0D;

    private static final Vec3[] CORNER_OFFSETS = {
            new Vec3(5.0D, 16.0D, -5.0D),
            new Vec3(-5.0D, 16.0D, -5.0D),
            new Vec3(5.0D, 16.0D, 5.0D),
            new Vec3(-5.0D, 16.0D, 5.0D)
    };

    private static final Box[][] HIT_BOXES = buildHitBoxes();

    private VocoTableCandleHitboxes() {}

    public static Box hitBox(ReceptorPosition receptor, int candleCount) {
        return HIT_BOXES[receptor.id()][clampCount(candleCount)];
    }

    public static double rayHitDistance(
            Vec3 start,
            Vec3 end,
            Box box
    ) {
        Vec3 direction = end.subtract(start);

        double tMin = 0.0D;
        double tMax = 1.0D;

        double[] resultX = clipAxis(start.x, direction.x, box.minX(), box.maxX(), tMin, tMax);
        if (resultX == null) return Double.NaN;
        tMin = resultX[0];
        tMax = resultX[1];

        double[] resultY = clipAxis(start.y, direction.y, box.minY(), box.maxY(), tMin, tMax);
        if (resultY == null) return Double.NaN;
        tMin = resultY[0];
        tMax = resultY[1];

        double[] resultZ = clipAxis(start.z, direction.z, box.minZ(), box.maxZ(), tMin, tMax);
        if (resultZ == null) return Double.NaN;

        return resultZ[0];
    }

    private static Box[][] buildHitBoxes() {
        Box[][] result = new Box[ReceptorPosition.COUNT][5];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            Vec3 cornerOffset = CORNER_OFFSETS[receptor.id()];

            for (int count = 1; count <= 4; count++) {
                Bounds vanilla = vanillaBounds(count);

                result[receptor.id()][count] = new Box(
                        vanilla.minX + cornerOffset.x,
                        vanilla.minY + cornerOffset.y,
                        vanilla.minZ + cornerOffset.z,
                        vanilla.maxX + cornerOffset.x,
                        vanilla.maxY + cornerOffset.y,
                        vanilla.maxZ + cornerOffset.z
                );
            }
        }

        return result;
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
        if (Math.abs(direction) < 1.0E-7D) {
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

        return tMin <= tMax
                ? new double[] {tMin, tMax}
                : null;
    }

    private static Bounds vanillaBounds(int candleCount) {
        return switch (clampCount(candleCount)) {
            case 1 -> new Bounds(7.0D, 0.0D, 7.0D, 9.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 9.0D);
            case 2 -> new Bounds(5.0D, 0.0D, 6.0D, 11.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 9.0D);
            case 3 -> new Bounds(5.0D, 0.0D, 6.0D, 10.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 11.0D);
            case 4 -> new Bounds(5.0D, 0.0D, 5.0D, 11.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 10.0D);
            default -> throw new IllegalStateException("Unexpected candle count");
        };
    }

    private static int clampCount(int candleCount) {
        return Math.max(1, Math.min(4, candleCount));
    }

    private record Bounds(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {}

    public record Box(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        public boolean contains(double x, double y, double z) {
            return x >= this.minX && x <= this.maxX
                    && y >= this.minY && y <= this.maxY
                    && z >= this.minZ && z <= this.maxZ;
        }
    }
}