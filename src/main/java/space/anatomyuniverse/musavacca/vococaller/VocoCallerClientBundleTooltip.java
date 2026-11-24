package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.component.BundleContents;
//? if >=1.21.2 {
import net.minecraft.network.chat.Component;
import space.anatomyuniverse.musavacca.mixin.client.ClientBundleTooltipAccessor;
//?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else if >=1.21.2 {
import net.minecraft.client.gui.GuiGraphics;
//?}

public final class VocoCallerClientBundleTooltip extends ClientBundleTooltip {
    //? if >=1.21.2 {
    private static final Component EMPTY_DESCRIPTION = Component.translatable("item.musavacca.banana_phone.empty.description");

    //?}
    public VocoCallerClientBundleTooltip(BundleContents contents) {
        super(contents);
    }

    //? if >=1.21.2 {
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
    //? if >=26.1 {
    /*public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
    *///?} else {
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics graphics) {
    //?}
        Component vanilla = useVocoDescription();
        try {
            //? if >=26.1 {
            /*super.extractImage(font, x, y, width, height, graphics);
            *///?} else {
            super.renderImage(font, x, y, width, height, graphics);
            //?}
        } finally {
            restoreDescription(vanilla);
        }
    }

    private static Component useVocoDescription() {
        Component vanilla = ClientBundleTooltipAccessor.musavacca$getEmptyDescription();
        ClientBundleTooltipAccessor.musavacca$setEmptyDescription(EMPTY_DESCRIPTION);
        return vanilla;
    }

    private static void restoreDescription(Component vanilla) {
        ClientBundleTooltipAccessor.musavacca$setEmptyDescription(vanilla);
    }
    //?}
}
