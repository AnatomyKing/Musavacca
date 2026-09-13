package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

//? if <1.21.2
//import net.minecraft.resources.ResourceLocation;

//? if >=1.21.2
import net.minecraft.network.chat.Component;

//? if >=1.21.2
import space.anatomyuniverse.musavacca.mixin.client.ClientBundleTooltipAccessor;

public final class VocoCallerClientBundleTooltip
        extends ClientBundleTooltip {

    private final BundleContents contents;

    //? if <1.21.2 {
    /*private static final ResourceLocation LEGACY_BACKGROUND_SPRITE =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "container/bundle/background"
            );

    private static final ResourceLocation LEGACY_SLOT_SPRITE =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "container/bundle/slot"
            );

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

    //? if >=1.21.2 {
    private static final Component EMPTY_DESCRIPTION =
            Component.translatable(
                    "item.musavacca.banana_phone.empty.description"
            );
    //?}

    public VocoCallerClientBundleTooltip(
            BundleContents contents
    ) {
        super(contents);

        this.contents = contents;
    }

    //? if <1.21.2 {
    /*@Override
    public int getHeight() {
        return TOOLTIP_HEIGHT;
    }

    @Override
    public int getWidth(
            Font font
    ) {
        return TOOLTIP_WIDTH;
    }

    @Override
    public void renderImage(
            Font font,
            int x,
            int y,
            GuiGraphics graphics
    ) {
        // Old 1.21.1 Bundle GUI, but collapsed to one physical SIM slot.
        graphics.blitSprite(
                LEGACY_BACKGROUND_SPRITE,
                x,
                y,
                TOOLTIP_WIDTH,
                SLOT_HEIGHT + BORDER * 2
        );

        int slotX = x + BORDER;
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
    }
    *///?}

    //? if >=1.21.2 {
    @Override
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
    }

    @Override
    public int getWidth(
            Font font
    ) {
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
        Component vanilla =
                useVocoDescription();

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
            restoreDescription(
                    vanilla
            );
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
    //?}
}