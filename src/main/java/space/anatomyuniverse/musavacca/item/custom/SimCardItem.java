
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class SimCardItem extends Item {
    public SimCardItem(Properties properties) {
        super(properties);
    }

    public static boolean hasStoredHex(ItemStack stack) {
        return stack.get(ModDataComponents.HEX_COLOR.get()) != null;
    }

    public static boolean isClean(ItemStack stack) {
        return !hasStoredHex(stack);
    }

    public static int getStoredHexOrFallback(ItemStack stack, int fallbackHexColor) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        return TintColorUtil.rgb(savedHex != null ? savedHex : fallbackHexColor);
    }

    public static void setStoredHex(ItemStack stack, int hexColor) {
        stack.set(ModDataComponents.HEX_COLOR.get(), TintColorUtil.rgb(hexColor));
    }
}