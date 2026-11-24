package space.anatomyuniverse.musavacca.vococaller;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import space.anatomyuniverse.musavacca.MusaCore;

@EventBusSubscriber(modid = MusaCore.MOD_ID, value = Dist.CLIENT)
public final class VocoCallerTooltipClient {
    private VocoCallerTooltipClient() {}

    @SubscribeEvent
    public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(VocoCallerBundleTooltip.class, tooltip -> new VocoCallerClientBundleTooltip(tooltip.contents()));
    }
}
