package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class PotassiumDropProtection {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(PotassiumDropProtection::onKey);
        NeoForge.EVENT_BUS.addListener(PotassiumDropProtection::onMousePressed);
        NeoForge.EVENT_BUS.addListener(PotassiumDropProtection::onMouseReleased);
    }

    private static void onKey(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;
        Slot slot = hovered(screen);
        if (slot == null || !slot.getItem().is(ModItems.POTASSIUM_THROWABLE_TOOL.get())) return;
        //? if >=1.21.9 {
        /*if (Minecraft.getInstance().options.keyDrop.matches(event.getKeyEvent())) event.setCanceled(true);
        *///?} else {
        if (Minecraft.getInstance().options.keyDrop.matches(event.getKeyCode(), event.getScanCode())) event.setCanceled(true);
        //?}
    }

    private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen && droppingOutside(screen, event.getMouseX(), event.getMouseY()))
            event.setCanceled(true);
    }

    private static void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen && droppingOutside(screen, event.getMouseX(), event.getMouseY()))
            event.setCanceled(true);
    }

    private static Slot hovered(AbstractContainerScreen<?> screen) {
        //? if >=26.1 {
        /*return screen.getHoveredSlot();
        *///?} else {
        return screen.getSlotUnderMouse();
        //?}
    }

    private static boolean droppingOutside(AbstractContainerScreen<?> screen, double x, double y) {
        if (!screen.getMenu().getCarried().is(ModItems.POTASSIUM_THROWABLE_TOOL.get())) return false;
        //? if >=26.1 {
        /*int left = screen.getLeftPos(), top = screen.getTopPos(), width = screen.getImageWidth(), height = screen.getImageHeight();
        *///?} else {
        int left = screen.getGuiLeft(), top = screen.getGuiTop(), width = screen.getXSize(), height = screen.getYSize();
        //?}
        if (x >= left && x < left + width && y >= top && y < top + height) return false;
        for (Slot slot : screen.getMenu().slots) {
            if (slot.isActive() && x >= left + slot.x - 1 && x < left + slot.x + 17 && y >= top + slot.y - 1 && y < top + slot.y + 17)
                return false;
        }
        return true;
    }

    private PotassiumDropProtection() {}
}
