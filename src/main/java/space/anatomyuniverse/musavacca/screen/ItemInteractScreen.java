package space.anatomyuniverse.musavacca.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.menu.ItemInteractMenu;

public class ItemInteractScreen extends AbstractContainerScreen<ItemInteractMenu> {

    private EditBox input;

    public ItemInteractScreen(ItemInteractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 24;
    }

    @Override
    protected void init() {
        super.init();

        this.input = new EditBox(
                this.font,
                this.leftPos,
                this.topPos,
                this.imageWidth,
                20,
                Component.literal("Input")
        );

        this.input.setMaxLength(256);
        this.input.setFocused(true);
        this.setInitialFocus(this.input);
        this.addRenderableWidget(this.input);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // nothing needed
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // nothing needed
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}