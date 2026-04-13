// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/PearlFireTintSource.java
package space.anatomyuniverse.musavacca.tint;

import java.util.Arrays;

public final class PearlFireTintSource {
    public static final int LAYER_COUNT = 32;

    private PearlFireTintSource() {}

    public static int blockTint(int baseRgb, int tintIndex) {
        if (tintIndex < 0 || tintIndex >= LAYER_COUNT) {
            return TintColorUtil.NO_TINT;
        }

        return TintColorUtil.opaqueRgb(baseRgb);
    }

    public static int itemTint(int baseRgb, int tintIndex) {
        if (tintIndex < 0 || tintIndex >= LAYER_COUNT) {
            return TintColorUtil.NO_TINT;
        }

        return TintColorUtil.rgb(baseRgb);
    }

    public static int rgbTint(int baseRgb, int tintIndex) {
        if (tintIndex < 0 || tintIndex >= LAYER_COUNT) {
            return TintColorUtil.NO_TINT;
        }

        return TintColorUtil.rgb(baseRgb);
    }

    public static int[] palette(int baseRgb) {
        int[] palette = new int[LAYER_COUNT];
        Arrays.fill(palette, TintColorUtil.rgb(baseRgb));
        return palette;
    }
}