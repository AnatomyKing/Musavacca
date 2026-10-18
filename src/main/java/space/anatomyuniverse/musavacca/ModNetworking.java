package space.anatomyuniverse.musavacca;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import space.anatomyuniverse.musavacca.bar.balance.BalanceSyncPayload;

//? if >=1.21.6 {
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerSyncPayload;
//?} else {
/*import space.anatomyuniverse.musavacca.bar.balance.ClientBalanceData;

import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerSyncPayload;

import space.anatomyuniverse.musavacca.bar.hunger.ClientBonusHungerData;
*///?}

import space.anatomyuniverse.musavacca.effect.playerstatus.PlayerStatusActionPayload;
import space.anatomyuniverse.musavacca.effect.playerstatus.PlayerStatusCallerPayload;
//? if <1.21.6 {
/*import space.anatomyuniverse.musavacca.effect.playerstatus.PlayerStatusClient;
*///?}

import space.anatomyuniverse.musavacca.gui.menu.payloads.FlintAndPearlColorPayload;
import space.anatomyuniverse.musavacca.gui.menu.payloads.VocoCallerStatePayload;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraSelectionPayload;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraStartPayload;
//? if <1.21.6 {
/*import space.anatomyuniverse.musavacca.gui.voco.VocoCameraClient;
*///?}

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(
                ModNetworking::registerPayloads
        );
    }

    private static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {

        event.registrar("musavacca")
                .playToClient(
                        PlayerStatusCallerPayload.TYPE,
                        //? if >=1.21.6 {
                        PlayerStatusCallerPayload.STREAM_CODEC
                        //?} else {
                        /*PlayerStatusCallerPayload.STREAM_CODEC,
                        (payload, context) -> PlayerStatusClient.setCaller(payload)
                        *///?}
                )
                .playToClient(
                        BonusHungerSyncPayload.TYPE,
                        //? if >=1.21.6 {
                        BonusHungerSyncPayload.STREAM_CODEC
                        //?} else {
                        /*BonusHungerSyncPayload.STREAM_CODEC,
                        (payload, context) ->
                                ClientBonusHungerData.set(
                                        payload.food(),
                                        payload.saturation(),
                                        payload.active()
                                )
                        *///?}
                )
                .playToClient(
                        BalanceSyncPayload.TYPE,
                        //? if >=1.21.6 {
                        BalanceSyncPayload.STREAM_CODEC
                        //?} else {
                        /*BalanceSyncPayload.STREAM_CODEC,
                        (payload, context) ->
                                ClientBalanceData.set(
                                        payload.balance(),
                                        payload.active()
                                )
                        *///?}
                )
                .playToClient(
                        VocoCameraStartPayload.TYPE,
                        //? if >=1.21.6 {
                        VocoCameraStartPayload.STREAM_CODEC
                        //?} else {
                        /*VocoCameraStartPayload.STREAM_CODEC,
                        (payload, context) ->
                                VocoCameraClient.start(
                                        payload
                                )
                        *///?}
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
                )
                .playToServer(
                        VocoCallerStatePayload.TYPE,
                        VocoCallerStatePayload.STREAM_CODEC,
                        VocoCallerStatePayload::handle
                )
                .playToServer(
                        PlayerStatusActionPayload.TYPE,
                        PlayerStatusActionPayload.STREAM_CODEC,
                        PlayerStatusActionPayload::handle
                );

    }
}
