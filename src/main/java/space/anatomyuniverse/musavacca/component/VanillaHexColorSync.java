package space.anatomyuniverse.musavacca.component;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import space.anatomyuniverse.musavacca.data.models.engine.tint.EngineTintRules;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;
import java.util.Map;

//? if >=1.21.4 {
import net.minecraft.world.item.component.CustomModelData;
//?}

/**
 * Mirrors Musavacca's named hex map into vanilla render components.
 *
 * This class is intentionally in component/, not tint/:
 * - HexColorComponent owns the custom data.
 * - ModelSets/EngineTintRules owns which items need pearl layer colors.
 * - space.anatomyuniverse.musavacca.tint stays stable runtime color math.
 */
public final class VanillaHexColorSync {
    private VanillaHexColorSync() {}

    public static void sync(ItemStack stack, Map<String, Integer> namedColors) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Integer first = HexColorComponent.first(namedColors);
        if (first == null) {
            clear(stack);
            return;
        }

        stack.set(DataComponents.DYED_COLOR, dyedColor(first));
        replaceCustomModelDataColors(stack, EngineTintRules.customModelDataColors(stack, namedColors));
    }


    private static DyedItemColor dyedColor(int rgb) {
        int color = TintColorUtil.rgb(rgb);

        try {
            return DyedItemColor.class
                    .getConstructor(int.class)
                    .newInstance(color);
        } catch (NoSuchMethodException ignored) {
            // 1.21.x mappings are not completely stable here.
            // Some targets expose DyedItemColor(int), others expose DyedItemColor(int, boolean).
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create vanilla DyedItemColor.", exception);
        }

        try {
            return DyedItemColor.class
                    .getConstructor(int.class, boolean.class)
                    .newInstance(color, false);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not create vanilla DyedItemColor.", exception);
        }
    }

    public static void clear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        stack.remove(DataComponents.DYED_COLOR);
        replaceCustomModelDataColors(stack, List.of());
    }

    private static void replaceCustomModelDataColors(ItemStack stack, List<Integer> colors) {
        //? if >=1.21.4 {
        CustomModelData old = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (old == null) {
            old = CustomModelData.EMPTY;
        }

        if (colors == null || colors.isEmpty()) {
            if (old.floats().isEmpty() && old.flags().isEmpty() && old.strings().isEmpty()) {
                stack.remove(DataComponents.CUSTOM_MODEL_DATA);
                return;
            }

            stack.set(
                    DataComponents.CUSTOM_MODEL_DATA,
                    new CustomModelData(old.floats(), old.flags(), old.strings(), List.of())
            );
            return;
        }

        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        old.floats(),
                        old.flags(),
                        old.strings(),
                        colors.stream().map(TintColorUtil::rgb).toList()
                )
        );
        //?}
    }
}
