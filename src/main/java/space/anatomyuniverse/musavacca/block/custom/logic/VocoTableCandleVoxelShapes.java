package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableCandleVoxelShapes {
    private static final double UNIT = 1.0D / 16.0D;

    private static final Vec3[] EMPTY_OFFSETS = new Vec3[0];

    private static final Vec3[] RENDER_TRANSLATIONS = buildRenderTranslations();
    private static final Vec3[][][] PARTICLE_OFFSETS = buildParticleOffsets();
    private static final VoxelShape[][] SHAPES = buildShapes();

    private VocoTableCandleVoxelShapes() {}

    public static Vec3 renderTranslation(ReceptorPosition receptor) {
        return RENDER_TRANSLATIONS[receptor.id()];
    }

    public static Vec3[] particleOffsets(ReceptorPosition receptor, int candleCount) {
        return candleCount <= 0
                ? EMPTY_OFFSETS
                : PARTICLE_OFFSETS[receptor.id()][clampCount(candleCount)];
    }

    public static Vec3 dropPosition(ReceptorPosition receptor, int candleCount) {
        return VocoTableCandleHitboxes.hitBox(receptor, candleCount).centerScaled(UNIT);
    }

    public static VoxelShape shape(ReceptorPosition receptor, int candleCount) {
        return candleCount <= 0
                ? Shapes.empty()
                : SHAPES[receptor.id()][clampCount(candleCount)];
    }

    private static Vec3[] buildRenderTranslations() {
        Vec3[] result = new Vec3[ReceptorPosition.COUNT];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            result[receptor.id()] = VocoTableCandleHitboxes.cornerOffset(receptor).scale(UNIT);
        }

        return result;
    }

    private static Vec3[][][] buildParticleOffsets() {
        Vec3[][][] result = new Vec3[ReceptorPosition.COUNT][5][];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            result[receptor.id()][0] = EMPTY_OFFSETS;

            for (int count = 1; count <= 4; count++) {
                result[receptor.id()][count] = shiftedParticleOffsets(receptor, count);
            }
        }

        return result;
    }

    private static Vec3[] shiftedParticleOffsets(ReceptorPosition receptor, int candleCount) {
        Vec3 offset = VocoTableCandleHitboxes.cornerOffset(receptor);
        Vec3[] vanilla = vanillaParticleOffsets(candleCount);
        Vec3[] result = new Vec3[vanilla.length];

        for (int i = 0; i < vanilla.length; i++) {
            result[i] = vanilla[i].add(offset).scale(UNIT);
        }

        return result;
    }

    private static VoxelShape[][] buildShapes() {
        VoxelShape[][] result = new VoxelShape[ReceptorPosition.COUNT][5];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            result[receptor.id()][0] = Shapes.empty();

            for (int count = 1; count <= 4; count++) {
                result[receptor.id()][count] = VocoTableCandleHitboxes
                        .hitBox(receptor, count)
                        .toShape();
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

    private static int clampCount(int candleCount) {
        return Math.max(1, Math.min(4, candleCount));
    }
}
