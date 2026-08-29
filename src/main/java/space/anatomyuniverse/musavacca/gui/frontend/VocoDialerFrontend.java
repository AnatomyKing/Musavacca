package space.anatomyuniverse.musavacca.gui.frontend;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.gui.backend.VocoDialerBackend;
import space.anatomyuniverse.musavacca.gui.menu.VocoDialerMenu;

public class VocoDialerFrontend extends AbstractContainerScreen<VocoDialerMenu> {
    private static final int GUI_WIDTH = 157;
    private static final int GUI_HEIGHT = 164;

    private final VocoDialerControl dialer = new VocoDialerControl(this::sendBackendButton);

    public VocoDialerFrontend(VocoDialerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.dialer.render(graphics, this.leftPos, this.topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.dialer.mouseClicked(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.dialer.mouseDragged(mouseX, mouseY, button, this.leftPos, this.topPos) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.dialer.mouseReleased(button) || super.mouseReleased(mouseX, mouseY, button);
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
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Intentionally empty.
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}


