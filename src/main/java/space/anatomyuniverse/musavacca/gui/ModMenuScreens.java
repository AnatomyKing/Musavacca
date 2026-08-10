package space.anatomyuniverse.musavacca.gui;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.screen.*;

public final class ModMenuScreens {

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ITEM_INTERACT_MENU.get(), ItemInteractScreen::new);
        event.register(ModMenus.TEST_INVENTORY_MENU.get(), TestInventoryScreen::new);
        event.register(ModMenus.HEX_TELEPORT_MENU.get(), HexTeleportScreen::new);
        event.register(ModMenus.FLINT_AND_PEARL_MENU.get(), FlintAndPearlScreen::new);
    }

    private ModMenuScreens() {}
}
