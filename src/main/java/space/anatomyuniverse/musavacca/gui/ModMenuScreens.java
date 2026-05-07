package space.anatomyuniverse.musavacca.gui;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.screen.ItemInteractScreen;
import space.anatomyuniverse.musavacca.gui.screen.TestInventoryScreen;
import space.anatomyuniverse.musavacca.gui.screen.VocoSliderScreen;

public final class ModMenuScreens {

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ITEM_INTERACT_MENU.get(), ItemInteractScreen::new);
        event.register(ModMenus.TEST_INVENTORY_MENU.get(), TestInventoryScreen::new);
        event.register(ModMenus.VOCO_SLIDER_MENU.get(), VocoSliderScreen::new);
    }

    private ModMenuScreens() {}
}