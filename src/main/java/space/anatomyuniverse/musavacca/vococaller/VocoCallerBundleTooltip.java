package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.BundleContents;

public record VocoCallerBundleTooltip(
        BundleContents contents
) implements TooltipComponent {}
