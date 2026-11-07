package space.anatomyuniverse.musavacca.gui;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.screen.FlintAndPearlScreen;
import space.anatomyuniverse.musavacca.gui.screen.VocoCallerScreen;
import space.anatomyuniverse.musavacca.gui.screen.VocoDialerScreen;

public final class ModMenuEvents {
    private ModMenuEvents() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModMenuEvents::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuRegistries.VOCO_DIALER_MENU.get(), VocoDialerScreen::new);
        event.register(ModMenuRegistries.VOCO_CALLER_MENU.get(), VocoCallerScreen::new);
        event.register(ModMenuRegistries.FLINT_AND_PEARL_MENU.get(), FlintAndPearlScreen::new);
    }
}
