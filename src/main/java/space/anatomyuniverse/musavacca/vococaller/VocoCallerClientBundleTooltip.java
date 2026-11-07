package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.client.gui.Font;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.component.BundleContents;
//? if >=1.21.2 {
import net.minecraft.network.chat.Component;
import space.anatomyuniverse.musavacca.mixin.client.ClientBundleTooltipAccessor;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
*///?}

public final class VocoCallerClientBundleTooltip extends ClientBundleTooltip {
    //? if >=1.21.2 {
    private static final Component EMPTY_DESCRIPTION = Component.translatable("item.musavacca.banana_phone.empty.description");

    //?} else {
    /*private final BundleContents contents;
    private static final ResourceLocation LEGACY_BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "container/bundle/background");
    private static final ResourceLocation LEGACY_SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("minecraft", "container/bundle/slot");
    private static final int TOOLTIP_WIDTH = 18 + 1 * 2;
    private static final int TOOLTIP_HEIGHT = 20 + 1 * 2 + 4;

    *///?}
    public VocoCallerClientBundleTooltip(BundleContents contents) {
        super(contents);
        //? if <1.21.2 {
        /*this.contents = contents;
        *///?}
    }

    @Override
    //? if >=1.21.2 {
    public int getHeight(Font font) {
    //?} else {
    /*public int getHeight() {
    *///?}
        //? if >=1.21.2 {
        Component vanilla = useVocoDescription();
        try {
            return super.getHeight(font);
        } finally {
            restoreDescription(vanilla);
        }
        //?} else {
        /*return TOOLTIP_HEIGHT;
        *///?}
    }

    @Override
    public int getWidth(Font font) {
        //? if >=1.21.2 {
        Component vanilla = useVocoDescription();
        try {
            return super.getWidth(font);
        } finally {
            restoreDescription(vanilla);
        }
        //?} else {
        /*return TOOLTIP_WIDTH;
        *///?}
    }

    @Override
    //? if >=26.1 {
    /*public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
    *///?} else if >=1.21.2 {
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics graphics) {
    //?} else {
    /*public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
    *///?}
        //? if >=1.21.2 {
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
        //?} else {
        /*graphics.blitSprite(LEGACY_BACKGROUND_SPRITE, x, y, TOOLTIP_WIDTH, 20 + 1 * 2);
        int slotX = x + 1;
        int slotY = y + 1;
        graphics.blitSprite(LEGACY_SLOT_SPRITE, slotX, slotY, 18, 20);
        ItemStack displayed = this.contents.itemCopyStream().findFirst().orElse(ItemStack.EMPTY);
        if (displayed.isEmpty()) return;
        ItemStack one = displayed.copyWithCount(1);
        graphics.renderItem(one, slotX + 1, slotY + 1);
        graphics.renderItemDecorations(font, one, slotX + 1, slotY + 1);
        *///?}
    }

    //? if >=1.21.2 {
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
