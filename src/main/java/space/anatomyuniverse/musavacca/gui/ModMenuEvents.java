package space.anatomyuniverse.musavacca.gui;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import space.anatomyuniverse.musavacca.gui.frontend.*;

public final class ModMenuEvents {

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenuRegistries.VOCO_DIALER_MENU.get(), VocoDialerFrontend::new);
        event.register(ModMenuRegistries.VOCO_CALLER_MENU.get(), VocoCallerFrontend::new);
        event.register(ModMenuRegistries.FLINT_AND_PEARL_MENU.get(), FlintAndPearlFrontend::new);
    }

    private ModMenuEvents() {}
}


