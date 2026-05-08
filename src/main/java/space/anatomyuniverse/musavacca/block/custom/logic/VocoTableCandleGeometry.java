// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableCandleGeometry.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic.ReceptorPosition;

public final class VocoTableCandleGeometry {
    private static final double UNIT = 1.0D / 16.0D;

    private static final Vec3[] EMPTY_OFFSETS = new Vec3[0];

    private static final Vec3[] CORNER_OFFSETS = {
            new Vec3(5.0D, 16.0D, -5.0D),
            new Vec3(-5.0D, 16.0D, -5.0D),
            new Vec3(5.0D, 16.0D, 5.0D),
            new Vec3(-5.0D, 16.0D, 5.0D)
    };

    private static final Vec3[] RENDER_TRANSLATIONS = buildRenderTranslations();
    private static final Vec3[][][] PARTICLE_OFFSETS = buildParticleOffsets();
    private static final Box[][] HIT_BOXES = buildHitBoxes();
    private static final VoxelShape[][] SHAPES = buildShapes();

    private VocoTableCandleGeometry() {}

    public static Vec3 renderTranslation(ReceptorPosition receptor) {
        return RENDER_TRANSLATIONS[receptor.id()];
    }

    public static Vec3[] particleOffsets(ReceptorPosition receptor, int candleCount) {
        if (candleCount <= 0) {
            return EMPTY_OFFSETS;
        }

        return PARTICLE_OFFSETS[receptor.id()][clampCount(candleCount)];
    }

    public static Box hitBox(ReceptorPosition receptor, int candleCount) {
        return HIT_BOXES[receptor.id()][clampCount(candleCount)];
    }

    public static Vec3 dropPosition(ReceptorPosition receptor, int candleCount) {
        Box box = hitBox(receptor, candleCount);

        return new Vec3(
                ((box.minX() + box.maxX()) * 0.5D) * UNIT,
                ((box.minY() + box.maxY()) * 0.5D) * UNIT,
                ((box.minZ() + box.maxZ()) * 0.5D) * UNIT
        );
    }

    public static VoxelShape shape(ReceptorPosition receptor, int candleCount) {
        if (candleCount <= 0) {
            return Shapes.empty();
        }

        return SHAPES[receptor.id()][clampCount(candleCount)];
    }

    private static Vec3[] buildRenderTranslations() {
        Vec3[] result = new Vec3[ReceptorPosition.COUNT];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            Vec3 offset = CORNER_OFFSETS[receptor.id()];
            result[receptor.id()] = new Vec3(
                    offset.x * UNIT,
                    offset.y * UNIT,
                    offset.z * UNIT
            );
        }

        return result;
    }

    private static Vec3[][][] buildParticleOffsets() {
        Vec3[][][] result = new Vec3[ReceptorPosition.COUNT][5][];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            result[receptor.id()][0] = EMPTY_OFFSETS;

            for (int count = 1; count <= 4; count++) {
                result[receptor.id()][count] = shiftedAndScaledParticleOffsets(receptor, count);
            }
        }

        return result;
    }

    private static Vec3[] shiftedAndScaledParticleOffsets(ReceptorPosition receptor, int candleCount) {
        Vec3 cornerOffset = CORNER_OFFSETS[receptor.id()];
        Vec3[] vanilla = vanillaParticleOffsets(candleCount);
        Vec3[] result = new Vec3[vanilla.length];

        for (int i = 0; i < vanilla.length; i++) {
            result[i] = vanilla[i].add(cornerOffset).scale(UNIT);
        }

        return result;
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

    private static VoxelShape[][] buildShapes() {
        VoxelShape[][] result = new VoxelShape[ReceptorPosition.COUNT][5];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            result[receptor.id()][0] = Shapes.empty();

            for (int count = 1; count <= 4; count++) {
                result[receptor.id()][count] = HIT_BOXES[receptor.id()][count].toShape();
            }
        }

        return result;
    }

    private static Vec3[] vanillaParticleOffsets(int candleCount) {
        return switch (clampCount(candleCount)) {
            case 1 -> new Vec3[] {
                    new Vec3(8.0D, 8.0D, 8.0D)
            };

            case 2 -> new Vec3[] {
                    new Vec3(6.0D, 7.0D, 8.0D),
                    new Vec3(10.0D, 8.0D, 7.0D)
            };

            case 3 -> new Vec3[] {
                    new Vec3(8.0D, 5.0D, 10.0D),
                    new Vec3(6.0D, 7.0D, 8.0D),
                    new Vec3(9.0D, 8.0D, 7.0D)
            };

            case 4 -> new Vec3[] {
                    new Vec3(7.0D, 5.0D, 9.0D),
                    new Vec3(10.0D, 7.0D, 9.0D),
                    new Vec3(6.0D, 7.0D, 6.0D),
                    new Vec3(9.0D, 8.0D, 6.0D)
            };

            default -> EMPTY_OFFSETS;
        };
    }

    private static Bounds vanillaBounds(int candleCount) {
        return switch (clampCount(candleCount)) {
            case 1 -> new Bounds(7.0D, 0.0D, 7.0D, 9.0D, 7.0D, 9.0D);
            case 2 -> new Bounds(5.0D, 0.0D, 6.0D, 11.0D, 7.0D, 9.0D);
            case 3 -> new Bounds(5.0D, 0.0D, 6.0D, 10.0D, 7.0D, 11.0D);
            case 4 -> new Bounds(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 10.0D);
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

        public VoxelShape toShape() {
            return Block.box(
                    this.minX,
                    this.minY,
                    this.minZ,
                    this.maxX,
                    this.maxY,
                    this.maxZ
            );
        }
    }
}