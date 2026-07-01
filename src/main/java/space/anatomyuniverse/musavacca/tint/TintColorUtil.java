package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.level.FoliageColor;

public final class TintColorUtil {
    public static final int NO_TINT = 0xFFFFFFFF;
    public static final int WHITE = 0xFFFFFF;

    public static final int UNSET_HEX = -1;
    public static final int HARD_HEX = 0xD5CD49;

    private static final int RGB_MASK = 0xFFFFFF;

    private TintColorUtil() {}

    public static int defaultFoliageItemTint() {
        return FoliageColor.FOLIAGE_DEFAULT;
    }

    public static int rgb(int rgb) {
        return rgb & RGB_MASK;
    }

    public static int opaqueRgb(int rgb) {
        return 0xFF000000 | rgb(rgb);
    }

    public static int normalizeHex(int hexColor) {
        return rgb(hexColor);
    }

    public static int normalizeHexOrUnset(int hexColor) {
        return hexColor == UNSET_HEX ? UNSET_HEX : normalizeHex(hexColor);
    }

    public static boolean isSetHex(int hexColor) {
        return hexColor != UNSET_HEX;
    }

    public static Integer nullableHex(int hexColor) {
        return isSetHex(hexColor) ? normalizeHex(hexColor) : null;
    }

    public static int defaultFoliageBlockTint() {
        return NO_TINT;
    }

    public static int defaultHexItemTint() {
        return rgb(HARD_HEX);
    }

    public static int defaultHexBlockTint() {
        return NO_TINT;
    }
}
