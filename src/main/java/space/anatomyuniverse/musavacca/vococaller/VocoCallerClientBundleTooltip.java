package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.BundleContents;
import space.anatomyuniverse.musavacca.mixin.client.ClientBundleTooltipAccessor;

public final class VocoCallerClientBundleTooltip
        extends ClientBundleTooltip {

    private static final Component EMPTY_DESCRIPTION =
            Component.translatable(
                    "item.musavacca.banana_phone.empty.description"
            );

    public VocoCallerClientBundleTooltip(
            BundleContents contents
    ) {
        super(contents);
    }

    @Override
    public int getHeight(Font font) {
        Component vanilla = useVocoDescription();

        try {
            return super.getHeight(font);
        } finally {
            restoreDescription(vanilla);
        }
    }

    @Override
    public int getWidth(Font font) {
        Component vanilla = useVocoDescription();

        try {
            return super.getWidth(font);
        } finally {
            restoreDescription(vanilla);
        }
    }

    @Override
    public void renderImage(
            Font font,
            int x,
            int y,
            int width,
            int height,
            GuiGraphics graphics
    ) {
        Component vanilla = useVocoDescription();

        try {
            super.renderImage(
                    font,
                    x,
                    y,
                    width,
                    height,
                    graphics
            );
        } finally {
            restoreDescription(vanilla);
        }
    }

    private static Component useVocoDescription() {
        Component vanilla =
                ClientBundleTooltipAccessor
                        .musavacca$getEmptyDescription();

        ClientBundleTooltipAccessor
                .musavacca$setEmptyDescription(
                        EMPTY_DESCRIPTION
                );

        return vanilla;
    }

    private static void restoreDescription(
            Component vanilla
    ) {
        ClientBundleTooltipAccessor
                .musavacca$setEmptyDescription(
                        vanilla
                );
    }
}