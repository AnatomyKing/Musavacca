package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.level.FoliageColor;

public final class TintColorUtil {
    public static final int NO_TINT = 0xFFFFFFFF;

    private static final int RGB_MASK = 0xFFFFFF;

    private TintColorUtil() {}

    public static int defaultFoliageItemTint() {
        //? if >1.21.3 {
        return FoliageColor.FOLIAGE_DEFAULT;
        //?} else {
        /*return FoliageColor.getDefaultColor();
         *///?}
    }


    public static int rgb(int rgb) {
        return rgb & RGB_MASK;
    }

    public static int opaqueRgb(int rgb) {
        return 0xFF000000 | rgb(rgb);
    }

    public static int defaultHexBlockItemTint() {
        return 0xFFFFFF;
    }

    public static int defaultHexBlockTint() {
        return NO_TINT;
    }
}


