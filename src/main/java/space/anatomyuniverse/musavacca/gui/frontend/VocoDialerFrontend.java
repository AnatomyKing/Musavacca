package space.anatomyuniverse.musavacca.gui.frontend;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.gui.backend.VocoDialerBackend;
import space.anatomyuniverse.musavacca.gui.menu.VocoDialerMenu;

public class VocoDialerFrontend extends AbstractContainerScreen<VocoDialerMenu> {
    private static final int GUI_WIDTH = 157;
    private static final int GUI_HEIGHT = 164;

    private final VocoDialerControl dialer = new VocoDialerControl(this::sendBackendButton);

    public VocoDialerFrontend(VocoDialerMenu menu, Inventory playerInventory, Component title) {
        //? if >=26.1 {
        /*super(menu, playerInventory, title, GUI_WIDTH, GUI_HEIGHT);
        *///?} else {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        //?}
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    //? if >=26.1 {
    /*public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
    *///?} else {
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    //?}
        this.dialer.render(graphics, this.leftPos, this.topPos);
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        return this.dialer.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseClicked(event, doubleClick);
    *///?} else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.dialer.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseClicked(mouseX, mouseY, button);
    //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        return this.dialer.mouseDragged(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseDragged(event, dragX, dragY);
    *///?} else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.dialer.mouseDragged(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        return this.dialer.mouseReleased(button) || super.mouseReleased(event);
    *///?} else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.dialer.mouseReleased(button) || super.mouseReleased(mouseX, mouseY, button);
    //?}
    }

    private void sendBackendButton(int buttonId) {
        if (!VocoDialerBackend.isKnownButton(buttonId)) return;
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.gameMode == null) return;
        if (!this.menu.clickMenuButton(this.minecraft.player, buttonId)) return;
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    @Override
    public void removed() {
        this.dialer.cancel();
        super.removed();
    }

    @Override
    //? if >=26.1 {
    /*protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    *///?} else {
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    //?}

    }

    @Override
    //? if >=26.1 {
    /*public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    *///?} else {
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    //?}
    }
}
