// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/gui/ModMenuScreens.java
package space.anatomyuniverse.musavacca.gui;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.screen.HexTeleportScreen;
import space.anatomyuniverse.musavacca.gui.screen.ItemInteractScreen;
import space.anatomyuniverse.musavacca.gui.screen.TestInventoryScreen;
import space.anatomyuniverse.musavacca.gui.screen.VocoSliderScreen;

public final class ModMenuScreens {

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ITEM_INTERACT_MENU.get(), ItemInteractScreen::new);
        event.register(ModMenus.TEST_INVENTORY_MENU.get(), TestInventoryScreen::new);
        event.register(ModMenus.VOCO_SLIDER_MENU.get(), VocoSliderScreen::new);
        event.register(ModMenus.HEX_TELEPORT_MENU.get(), HexTeleportScreen::new);
    }

    private ModMenuScreens() {}
}