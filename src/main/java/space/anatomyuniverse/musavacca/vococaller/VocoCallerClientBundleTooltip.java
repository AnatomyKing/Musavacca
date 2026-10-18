package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.client.gui.Font;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

//? if >=1.21.2 {
import net.minecraft.network.chat.Component;

import space.anatomyuniverse.musavacca.mixin.client.ClientBundleTooltipAccessor;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

public final class VocoCallerClientBundleTooltip
        extends ClientBundleTooltip {

    private final BundleContents contents;

    //? if >=1.21.2 {
    private static final Component EMPTY_DESCRIPTION =
            Component.translatable(
                    "item.musavacca.banana_phone.empty.description"
            );
    //?} else {
    /*private static final ResourceLocation LEGACY_BACKGROUND_SPRITE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "container/bundle/background");

    private static final ResourceLocation LEGACY_SLOT_SPRITE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "container/bundle/slot");

    private static final int SLOT_WIDTH = 18;
    private static final int SLOT_HEIGHT = 20;

    private static final int BORDER = 1;
    private static final int BOTTOM_MARGIN = 4;

    private static final int TOOLTIP_WIDTH =
            SLOT_WIDTH + BORDER * 2;

    private static final int TOOLTIP_HEIGHT =
            SLOT_HEIGHT
                    + BORDER * 2
                    + BOTTOM_MARGIN;
    *///?}

    public VocoCallerClientBundleTooltip(
            BundleContents contents
    ) {
        super(contents);

        this.contents = contents;
    }

    @Override
    //? if >=1.21.2 {
    public int getHeight(
            Font font
    ) {
        Component vanilla =
                useVocoDescription();

        try {
            return super.getHeight(
                    font
            );
        } finally {
            restoreDescription(
                    vanilla
            );
        }
    //?} else {
    /*public int getHeight() {
        return TOOLTIP_HEIGHT;
    *///?}
    }

    @Override
    public int getWidth(
            Font font
    ) {
        //? if >=1.21.2 {
        Component vanilla =
                useVocoDescription();

        try {
            return super.getWidth(
                    font
            );
        } finally {
            restoreDescription(
                    vanilla
            );
        }
        //?} else {
        /*return TOOLTIP_WIDTH;
        *///?}
    }

    @Override

    //? if >=26.1 {
    /*public void extractImage(
    *///?} else {
    public void renderImage(
    //?}
            Font font,
            int x,
            int y,
    //? if >=26.1 {
            /*int width,
            int height,
            GuiGraphicsExtractor graphics
    ) {
        Component vanilla =
                useVocoDescription();
    *///?} else if >=1.21.2 <26.1 {
            int width,
            int height,
            GuiGraphics graphics
    ) {
        Component vanilla =
                useVocoDescription();
    //?} else {
            /*GuiGraphics graphics
    ) {
    *///?}

        //? if >=1.21.2 {
        try {
        //?} else {
        /*graphics.blitSprite(
                LEGACY_BACKGROUND_SPRITE,
                x,
                y,
                TOOLTIP_WIDTH,
                SLOT_HEIGHT + BORDER * 2
        );
        *///?}

        //? if >=26.1 {
            /*super.extractImage(
                    font,
                    x,
                    y,
                    width,
                    height,
                    graphics
            );
        } finally {
            restoreDescription(
                    vanilla
            );
        }
        *///?} else if >=1.21.2 <26.1 {
            super.renderImage(
                    font,
                    x,
                    y,
                    width,
                    height,
                    graphics
            );
        } finally {
            restoreDescription(
                    vanilla
            );
        }
        //?} else {
        /*int slotX = x + BORDER;
        int slotY = y + BORDER;

        graphics.blitSprite(
                LEGACY_SLOT_SPRITE,
                slotX,
                slotY,
                SLOT_WIDTH,
                SLOT_HEIGHT
        );

        ItemStack displayed =
                this.contents
                        .itemCopyStream()
                        .findFirst()
                        .orElse(ItemStack.EMPTY);

        if (displayed.isEmpty()) {
            return;
        }

        ItemStack one = displayed.copy();
        one.setCount(1);

        graphics.renderItem(
                one,
                slotX + 1,
                slotY + 1
        );

        graphics.renderItemDecorations(
                font,
                one,
                slotX + 1,
                slotY + 1
        );
        *///?}
    }

    //? if >=1.21.2 {
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

    //?}
}
