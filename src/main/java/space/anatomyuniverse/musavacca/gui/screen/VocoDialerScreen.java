package space.anatomyuniverse.musavacca.gui.screen;

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
import space.anatomyuniverse.musavacca.gui.menu.VocoDialerMenu;

public class VocoDialerScreen extends AbstractContainerScreen<VocoDialerMenu> {
    private final VocoDialerControl dialer = new VocoDialerControl(this::sendMenuButton);

    public VocoDialerScreen(VocoDialerMenu menu, Inventory playerInventory, Component title) {
        //? if >=26.1 {
        /*super(menu, playerInventory, title, 157, 164);
        *///?} else {
        super(menu, playerInventory, title);
        this.imageWidth = 157;
        this.imageHeight = 164;
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
    *///?} else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //?}
        //? if >=1.21.9 {
        /*return this.dialer.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseClicked(event, doubleClick);
        *///?} else {
        return this.dialer.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseClicked(mouseX, mouseY, button);
        //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
    *///?} else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //?}
        //? if >=1.21.9 {
        /*double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        return this.dialer.mouseDragged(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseDragged(event, dragX, dragY);
        *///?} else {
        return this.dialer.mouseDragged(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
    *///?} else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
    //?}
        //? if >=1.21.9 {
        /*int button = event.button();
        return this.dialer.mouseReleased(button) || super.mouseReleased(event);
        *///?} else {
        return this.dialer.mouseReleased(button) || super.mouseReleased(mouseX, mouseY, button);
        //?}
    }

    private void sendMenuButton(int buttonId) {
        if (!VocoDialerMenu.isKnownButton(buttonId)) return;
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

    //? if <26.1 {
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    //?}
}
