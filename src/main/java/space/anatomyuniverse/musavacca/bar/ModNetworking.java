// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/ModNetworking.java
package space.anatomyuniverse.musavacca.bar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import space.anatomyuniverse.musavacca.bar.balance.BalanceSyncPayload;
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerSyncPayload;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("musavacca")
                .playToClient(
                        BonusHungerSyncPayload.TYPE,
                        BonusHungerSyncPayload.STREAM_CODEC
                )
                .playToClient(
                        BalanceSyncPayload.TYPE,
                        BalanceSyncPayload.STREAM_CODEC
                );
    }
}