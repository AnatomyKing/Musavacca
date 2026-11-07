package space.anatomyuniverse.musavacca.vococaller;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public final class VocoCallerTooltipClient {
    private VocoCallerTooltipClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(VocoCallerTooltipClient::registerTooltips);
    }

    private static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(VocoCallerBundleTooltip.class, tooltip -> new VocoCallerClientBundleTooltip(tooltip.contents()));
    }
}
