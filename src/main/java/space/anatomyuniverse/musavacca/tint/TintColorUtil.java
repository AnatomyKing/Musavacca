// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/TintColorUtil.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.level.FoliageColor;

public final class TintColorUtil {
    public static final int NO_TINT = 0xFFFFFFFF;
    public static final int INVISIBLE_TINT = 0x00000000;

    private static final int RGB_MASK = 0xFFFFFF;

    private TintColorUtil() {}

    public static int defaultFoliageItemTint() {
        //? if <1.21.4 {
        /*return FoliageColor.getDefaultColor();
         *///?} else {
        return FoliageColor.FOLIAGE_DEFAULT;
        //?}
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