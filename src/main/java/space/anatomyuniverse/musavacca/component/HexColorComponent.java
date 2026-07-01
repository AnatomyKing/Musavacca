package space.anatomyuniverse.musavacca.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class HexColorComponent {
    public static final int UNSET = TintColorUtil.UNSET_HEX;

    public static final Codec<Map<String, Integer>> CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .xmap(HexColorComponent::normalize, HexColorComponent::normalize);

    private HexColorComponent() {}

    public static String cleanSlot(String slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Hex color slot cannot be null.");
        }

        String cleaned = slot.trim();
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("Hex color slot cannot be blank.");
        }

        return cleaned;
    }

    public static Map<String, Integer> normalize(Map<String, Integer> input) {
        if (input == null || input.isEmpty()) {
            return Map.of();
        }

        Map<String, Integer> sorted = new TreeMap<>();

        for (Map.Entry<String, Integer> entry : input.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            int rgb = TintColorUtil.normalizeHexOrUnset(entry.getValue());
            if (!TintColorUtil.isSetHex(rgb)) {
                continue;
            }

            sorted.put(cleanSlot(entry.getKey()), TintColorUtil.rgb(rgb));
        }

        if (sorted.isEmpty()) {
            return Map.of();
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(sorted));
    }

    /** Old method name kept so existing block entities keep compiling. */
    public static Map<String, Integer> normalizeMap(Map<String, Integer> input) {
        return normalize(input);
    }

    public static Map<String, Integer> single(String slot, int hexColor) {
        int rgb = TintColorUtil.normalizeHexOrUnset(hexColor);
        return TintColorUtil.isSetHex(rgb)
                ? Map.of(cleanSlot(slot), TintColorUtil.rgb(rgb))
                : Map.of();
    }

    public static Map<String, Integer> get(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Map.of();
        }

        return normalize(stack.get(ModDataComponents.HEX_COLOR.get()));
    }

    public static Map<String, Integer> get(DataComponentGetter input) {
        if (input == null) {
            return Map.of();
        }

        return normalize(input.get(ModDataComponents.HEX_COLOR.get()));
    }

    public static boolean has(ItemStack stack) {
        return !get(stack).isEmpty();
    }

    public static Integer getSlot(ItemStack stack, String slot) {
        return getSlot(get(stack), slot);
    }

    public static Integer getSlot(DataComponentGetter input, String slot) {
        return getSlot(get(input), slot);
    }

    public static Integer getSlot(Map<String, Integer> colors, String slot) {
        Integer color = normalize(colors).get(cleanSlot(slot));
        return color == null ? null : TintColorUtil.rgb(color);
    }

    public static int getSlotOrUnset(ItemStack stack, String slot) {
        Integer color = getSlot(stack, slot);
        return color == null ? UNSET : color;
    }

    public static int getSlotOrUnset(DataComponentGetter input, String slot) {
        Integer color = getSlot(input, slot);
        return color == null ? UNSET : color;
    }

    public static int getSlotOr(ItemStack stack, String slot, int fallbackHexColor) {
        Integer color = getSlot(stack, slot);
        return color == null ? TintColorUtil.rgb(fallbackHexColor) : color;
    }

    public static Integer first(ItemStack stack) {
        return first(get(stack));
    }

    public static Integer first(Map<String, Integer> colors) {
        for (Integer color : normalize(colors).values()) {
            if (color != null && TintColorUtil.isSetHex(color)) {
                return TintColorUtil.rgb(color);
            }
        }

        return null;
    }

    public static void set(ItemStack stack, Map<String, Integer> colors) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Map<String, Integer> normalized = normalize(colors);
        if (normalized.isEmpty()) {
            clear(stack);
            return;
        }

        stack.set(ModDataComponents.HEX_COLOR.get(), normalized);
        syncVanillaRenderColors(stack, normalized);
    }

    public static void clear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        stack.remove(ModDataComponents.HEX_COLOR.get());
        VanillaHexColorSync.clear(stack);
    }

    public static void setSlot(ItemStack stack, String slot, int hexColor) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int rgb = TintColorUtil.normalizeHexOrUnset(hexColor);
        if (!TintColorUtil.isSetHex(rgb)) {
            clearSlot(stack, slot);
            return;
        }

        Map<String, Integer> colors = new LinkedHashMap<>(get(stack));
        colors.put(cleanSlot(slot), TintColorUtil.rgb(rgb));
        set(stack, colors);
    }

    public static void clearSlot(ItemStack stack, String slot) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Map<String, Integer> colors = new LinkedHashMap<>(get(stack));
        colors.remove(cleanSlot(slot));
        set(stack, colors);
    }

    public static void syncVanillaRenderColors(ItemStack stack) {
        syncVanillaRenderColors(stack, get(stack));
    }

    public static void syncVanillaRenderColors(ItemStack stack, Map<String, Integer> colors) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Integer first = first(colors);
        if (first == null) {
            VanillaHexColorSync.clear(stack);
            return;
        }

        VanillaHexColorSync.sync(stack, colors);
    }

    /** Compatibility name for older call sites. */
    public static void syncVanillaDyedColorIfEquippable(ItemStack stack, int hexColor) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        VanillaHexColorSync.sync(stack, single("default", hexColor));
    }

    /** Compatibility name for older call sites. */
    public static void clearVanillaDyedColorIfEquippable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        VanillaHexColorSync.clear(stack);
    }
}
