package space.anatomyuniverse.musavacca.data.models.unified;

import space.anatomyuniverse.musavacca.tint.TintColorUtil;

//? if >=1.21.4 {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.CustomModelDataSource;
import net.minecraft.client.color.item.Dye;
//?}

/**
 * Datagen-only helpers for vanilla item tint sources.
 *
 * Keep this OUT of space.anatomyuniverse.musavacca.tint on purpose:
 * the tint package is runtime color math/handlers only, while item JSON generation
 * belongs to the model engine.
 */
public final class VanillaItemTintSources {
    private VanillaItemTintSources() {}

    //? if >=1.21.4 {
    public static Object none() {
        return constant(TintColorUtil.WHITE);
    }

    public static Object constant(int rgb) {
        return new Constant(TintColorUtil.rgb(rgb));
    }

    public static Object dye(int defaultRgb) {
        return new Dye(TintColorUtil.rgb(defaultRgb));
    }

    public static Object customModelData(int index, int defaultRgb) {
        return new CustomModelDataSource(Math.max(0, index), TintColorUtil.rgb(defaultRgb));
    }
    //?}
}
