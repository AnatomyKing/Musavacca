// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/TintColorUtil.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.level.FoliageColor;

public final class TintColorUtil {
    private TintColorUtil() {}

    public static int defaultFoliageItemTint() {
        //? if <1.21.4 {
        /*return FoliageColor.getDefaultColor();
         *///?} else {
        return FoliageColor.FOLIAGE_DEFAULT;
        //?}
    }

    public static int opaqueRgb(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static int defaultHexBlockTint() {
        return 0xFFFFFFFF;
    }
}