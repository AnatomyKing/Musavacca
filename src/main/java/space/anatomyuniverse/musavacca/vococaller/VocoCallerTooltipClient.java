package space.anatomyuniverse.musavacca.vococaller;

import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

public final class VocoCallerTooltipClient {

    private VocoCallerTooltipClient() {}

    public static void register(
            RegisterClientTooltipComponentFactoriesEvent event
    ) {
        event.register(
                VocoCallerBundleTooltip.class,
                tooltip ->
                        new VocoCallerClientBundleTooltip(
                                tooltip.contents()
                        )
        );
    }
}

