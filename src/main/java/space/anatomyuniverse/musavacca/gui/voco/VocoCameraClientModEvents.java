package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
//? if >=1.21.6
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import space.anatomyuniverse.musavacca.MusaCore;

public final class VocoCameraClientModEvents {
    private static final ResourceLocation CAMERA_HUD_LAYER =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_camera_hud");

    private VocoCameraClientModEvents() {}

    public static void register(IEventBus modBus) {
        //? if >=1.21.6
        modBus.addListener(VocoCameraClientModEvents::registerClientPayloads);
        modBus.addListener(VocoCameraClientModEvents::registerGuiLayers);

        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onClientTickPre);
        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onClientTickPost);
        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onMouseButton);
        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onLogout);
        NeoForge.EVENT_BUS.addListener(VocoCameraClient::onRenderHand);
    }

    //? if >=1.21.6 {
    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                VocoCameraStartPayload.TYPE,
                (payload, context) -> VocoCameraClient.start(payload)
        );
    }
    //?}

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.CROSSHAIR,
                CAMERA_HUD_LAYER,
                VocoCameraClient::renderHud
        );
    }
}
