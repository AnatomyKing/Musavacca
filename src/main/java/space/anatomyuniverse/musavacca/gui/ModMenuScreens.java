// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/screen/ModMenuScreens.java
package space.anatomyuniverse.musavacca.gui;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.screen.ItemInteractScreen;
import space.anatomyuniverse.musavacca.gui.screen.TestInventoryScreen;

public final class ModMenuScreens {

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ITEM_INTERACT_MENU.get(), ItemInteractScreen::new);
        event.register(ModMenus.TEST_INVENTORY_MENU.get(), TestInventoryScreen::new);
    }

    private ModMenuScreens() {}
}