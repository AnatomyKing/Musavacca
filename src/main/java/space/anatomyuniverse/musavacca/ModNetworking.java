package space.anatomyuniverse.musavacca;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import space.anatomyuniverse.musavacca.bar.balance.BalanceSyncPayload;
//? if <1.21.6
//import space.anatomyuniverse.musavacca.bar.balance.ClientBalanceData;
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerSyncPayload;
//? if <1.21.6
//import space.anatomyuniverse.musavacca.bar.hunger.ClientBonusHungerData;
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlColorPayload;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraSelectionPayload;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraStartPayload;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        //? if >=1.21.6 {
        event.registrar("musavacca")
                .playToClient(
                        BonusHungerSyncPayload.TYPE,
                        BonusHungerSyncPayload.STREAM_CODEC
                )
                .playToClient(
                        BalanceSyncPayload.TYPE,
                        BalanceSyncPayload.STREAM_CODEC
                )
                .playToClient(
                        VocoCameraStartPayload.TYPE,
                        VocoCameraStartPayload.STREAM_CODEC
                )
                .playToServer(
                        VocoCameraSelectionPayload.TYPE,
                        VocoCameraSelectionPayload.STREAM_CODEC,
                        VocoCameraSelectionPayload::handle
                )
                .playToServer(
                        FlintAndPearlColorPayload.TYPE,
                        FlintAndPearlColorPayload.STREAM_CODEC,
                        FlintAndPearlColorPayload::handle
                );
        //?} else {
        /*event.registrar("musavacca")
                .playToClient(
                        BonusHungerSyncPayload.TYPE,
                        BonusHungerSyncPayload.STREAM_CODEC,
                        (payload, context) -> ClientBonusHungerData.set(
                                payload.food(),
                                payload.saturation(),
                                payload.active()
                        )
                )
                .playToClient(
                        BalanceSyncPayload.TYPE,
                        BalanceSyncPayload.STREAM_CODEC,
                        (payload, context) -> ClientBalanceData.set(
                                payload.balance(),
                                payload.active()
                        )
                )
                .playToServer(
                        FlintAndPearlColorPayload.TYPE,
                        FlintAndPearlColorPayload.STREAM_CODEC,
                        FlintAndPearlColorPayload::handle
                );
        *///?}
    }
}
