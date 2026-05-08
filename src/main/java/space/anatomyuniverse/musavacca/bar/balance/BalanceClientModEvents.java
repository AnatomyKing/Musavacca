// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/balance/BalanceClientModEvents.java
package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import space.anatomyuniverse.musavacca.MusaCore;

public final class BalanceClientModEvents {
    private static final ResourceLocation BALANCE_LAYER =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_layer");

    private BalanceClientModEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(BalanceClientModEvents::registerClientPayloads);
        modBus.addListener(BalanceClientModEvents::registerGuiLayers);
    }

    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                BalanceSyncPayload.TYPE,
                (payload, context) -> ClientBalanceData.set(
                        payload.balance(),
                        payload.active()
                )
        );
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.FOOD_LEVEL,
                BALANCE_LAYER,
                BalanceHud::render
        );
    }
}