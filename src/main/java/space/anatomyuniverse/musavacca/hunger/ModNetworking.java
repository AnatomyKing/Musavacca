package space.anatomyuniverse.musavacca.hunger;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("musavacca").playToClient(
                BonusHungerSyncPayload.TYPE,
                BonusHungerSyncPayload.STREAM_CODEC
        );
    }
}