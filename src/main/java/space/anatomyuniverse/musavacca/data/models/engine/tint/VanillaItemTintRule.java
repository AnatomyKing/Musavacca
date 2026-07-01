package space.anatomyuniverse.musavacca.data.models.engine.tint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Runtime mirror of a PearlTint item definition from ModelSets.
 *
 * No item targets live in space.anatomyuniverse.musavacca.tint anymore.
 * Add/change tintable items in ModelSets by attaching PearlTint.dynamic(...),
 * and this rule is collected automatically by EngineTintRules.
 */
public record VanillaItemTintRule(Item item, PearlTint pearlTint) {
    public boolean enabled() {
        return item != null && pearlTint != null && pearlTint.hasItemSources();
    }

    public boolean matches(ItemStack stack) {
        return enabled() && stack != null && stack.is(item);
    }

    public List<Integer> customModelDataColors(ItemStack stack, Map<String, Integer> namedColors) {
        if (!matches(stack)) {
            return List.of();
        }

        int baseRgb = resolveBaseRgb(namedColors);
        List<Integer> colors = new ArrayList<>(pearlTint.layerCount());

        for (int layer = 0; layer < pearlTint.layerCount(); layer++) {
            colors.add(TintColorUtil.rgb(PearlFireTintSource.profileTint(
                    baseRgb,
                    layer,
                    pearlTint.profile()
            )));
        }

        return List.copyOf(colors);
    }

    public int legacyItemTint(ItemStack stack, int tintIndex) {
        if (!matches(stack)) {
            return TintColorUtil.NO_TINT;
        }

        int layer = tintIndex - pearlTint.firstTintIndex();
        if (!pearlTint.profile().supports(layer)) {
            return TintColorUtil.NO_TINT;
        }

        int baseRgb = resolveBaseRgb(HexColorComponent.get(stack));
        return TintColorUtil.opaqueRgb(PearlFireTintSource.profileTint(
                baseRgb,
                layer,
                pearlTint.profile()
        ));
    }

    private int resolveBaseRgb(Map<String, Integer> namedColors) {
        Integer base = pearlTint.resolveItemHex(namedColors);
        return base == null ? pearlTint.fallbackRgb() : TintColorUtil.rgb(base);
    }
}
