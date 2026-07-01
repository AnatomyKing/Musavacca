package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class SimCardItem extends Item {
    public static final String HEX_SLOT = "sim_card";
    public SimCardItem(Properties properties) {
        super(properties);
    }

    public static boolean hasStoredHex(ItemStack stack) {
        return HexColorComponent.has(stack);
    }

    public static boolean isClean(ItemStack stack) {
        return !hasStoredHex(stack);
    }

    public static int getStoredHexOrFallback(ItemStack stack, int fallbackHexColor) {
        return HexColorComponent.getSlotOr(stack, HEX_SLOT, fallbackHexColor);
    }

    public static void setStoredHex(ItemStack stack, int hexColor) {
        HexColorComponent.setSlot(stack, HEX_SLOT, hexColor);
    }
}
