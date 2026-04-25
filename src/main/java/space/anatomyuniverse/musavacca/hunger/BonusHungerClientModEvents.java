package space.anatomyuniverse.musavacca.hunger;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import space.anatomyuniverse.musavacca.MusaCore;

public final class BonusHungerClientModEvents {
    private static final ResourceLocation BONUS_HUNGER_LAYER =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "bonus_hunger_layer");

    private BonusHungerClientModEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(BonusHungerClientModEvents::registerClientPayloads);
        modBus.addListener(BonusHungerClientModEvents::registerGuiLayers);
    }

    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                BonusHungerSyncPayload.TYPE,
                (payload, context) -> ClientBonusHungerData.set(
                        payload.food(),
                        payload.saturation(),
                        payload.active()
                )
        );
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.FOOD_LEVEL,
                BONUS_HUNGER_LAYER,
                BonusHungerHud::render
        );
    }
}