package space.anatomyuniverse.musavacca.portal;

import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public final class PearlPortalServerEvents {
    private PearlPortalServerEvents() {}

    public static void onServerStopping(ServerStoppingEvent event) {
        PearlPortalNetwork.clear();
    }
}

