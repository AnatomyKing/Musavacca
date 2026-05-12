// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableCandleHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableCandleHitboxes {
    private static final double VANILLA_CANDLE_SHAPE_HEIGHT = 6.0D;

    private static final Vec3[] CORNER_OFFSETS = {
            new Vec3(5.0D, 16.0D, -5.0D),
            new Vec3(-5.0D, 16.0D, -5.0D),
            new Vec3(5.0D, 16.0D, 5.0D),
            new Vec3(-5.0D, 16.0D, 5.0D)
    };

    private static final VocoHitboxes.Box[][] HIT_BOXES = buildHitBoxes();

    private VocoTableCandleHitboxes() {}

    public static Vec3 cornerOffset(ReceptorPosition receptor) {
        return CORNER_OFFSETS[receptor.id()];
    }

    public static VocoHitboxes.Box hitBox(ReceptorPosition receptor, int candleCount) {
        return HIT_BOXES[receptor.id()][clampCount(candleCount)];
    }

    public static double rayHitDistance(Vec3 start, Vec3 end, VocoHitboxes.Box box) {
        return VocoHitboxes.rayHitDistance(start, end, box);
    }

    private static VocoHitboxes.Box[][] buildHitBoxes() {
        VocoHitboxes.Box[][] result = new VocoHitboxes.Box[ReceptorPosition.COUNT][5];

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            Vec3 offset = cornerOffset(receptor);

            for (int count = 1; count <= 4; count++) {
                result[receptor.id()][count] = vanillaBounds(count).shifted(offset);
            }
        }

        return result;
    }

    private static VocoHitboxes.Box vanillaBounds(int candleCount) {
        return switch (clampCount(candleCount)) {
            case 1 -> new VocoHitboxes.Box(7.0D, 0.0D, 7.0D, 9.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 9.0D);
            case 2 -> new VocoHitboxes.Box(5.0D, 0.0D, 6.0D, 11.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 9.0D);
            case 3 -> new VocoHitboxes.Box(5.0D, 0.0D, 6.0D, 10.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 11.0D);
            case 4 -> new VocoHitboxes.Box(5.0D, 0.0D, 5.0D, 11.0D, VANILLA_CANDLE_SHAPE_HEIGHT, 10.0D);
            default -> throw new IllegalStateException("Unexpected candle count");
        };
    }

    private static int clampCount(int candleCount) {
        return Math.max(1, Math.min(4, candleCount));
    }
}