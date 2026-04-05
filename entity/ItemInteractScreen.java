// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/screen/ItemInteractScreen.java
package space.anatomyuniverse.musavacca.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.menu.ItemInteractMenu;

public class ItemInteractScreen extends AbstractContainerScreen<ItemInteractMenu> {

    private EditBox input;
    private String cachedValue = "";

    private static final int VIEWER_WIDTH = 132;
    private static final int VIEWER_HEIGHT = 132;
    private static final int VIEWER_PADDING = 4;
    private static final int PREVIEW_SCALE = 48;
    private static final float DRAG_SENSITIVITY = 2.0F;

    private int viewerX;
    private int viewerY;
    private boolean draggingViewer = false;

    /**
     * These are fed into the vanilla "follows mouse" renderer.
     * We update them from dragging, so the cow rotates like a normal preview model.
     */
    private float previewMouseX = 18.0F;
    private float previewMouseY = -12.0F;

    private Cow previewCow;

    public ItemInteractScreen(ItemInteractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 170;
    }

    @Override
    protected void init() {
        super.init();

        int inputWidth = 200;
        int inputHeight = 20;
        int inputX = this.leftPos + (this.imageWidth - inputWidth) / 2;
        int inputY = this.topPos + 6;

        this.input = this.addRenderableWidget(
                new EditBox(this.font, inputX, inputY, inputWidth, inputHeight, Component.literal("Input"))
        );

        this.input.setMaxLength(256);
        this.input.setCanLoseFocus(false);
        this.input.setFocused(true);
        this.input.setValue(this.cachedValue);
        this.input.setResponder(text -> this.cachedValue = text);
        this.input.moveCursorToEnd(false);

        this.setInitialFocus(this.input);

        this.viewerX = this.leftPos + (this.imageWidth - VIEWER_WIDTH) / 2;
        this.viewerY = inputY + inputHeight + 12;

        this.ensurePreviewCow();
    }

    @Override
    protected void setInitialFocus() {
        if (this.input != null) {
            this.setInitialFocus(this.input);
        } else {
            super.setInitialFocus();
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String text = this.input != null ? this.input.getValue() : this.cachedValue;
        super.resize(minecraft, width, height);

        this.cachedValue = text;
        if (this.input != null) {
            this.input.setValue(text);
            this.input.moveCursorToEnd(false);
        }

        int inputY = this.topPos + 6;
        this.viewerX = this.leftPos + (this.imageWidth - VIEWER_WIDTH) / 2;
        this.viewerY = inputY + 20 + 12;

        this.ensurePreviewCow();
    }

    private void ensurePreviewCow() {
        if (this.previewCow != null || this.minecraft == null || this.minecraft.level == null) {
            return;
        }

        Cow cow = EntityType.COW.create(this.minecraft.level);
        if (cow == null) {
            return;
        }

        cow.setNoAi(true);
        cow.setYRot(180.0F);
        cow.setYBodyRot(180.0F);
        cow.yRotO = cow.getYRot();
        cow.yBodyRotO = cow.yBodyRot;
        cow.setXRot(0.0F);
        cow.xRotO = cow.getXRot();

        this.previewCow = cow;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.input != null && this.input.canConsumeInput()) {
            if (this.input.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

            if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.input != null && this.input.canConsumeInput() && this.input.charTyped(codePoint, modifiers)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.input != null && this.input.isMouseOver(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (button == 0 && this.isInsideViewer(mouseX, mouseY)) {
            this.draggingViewer = true;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingViewer && button == 0) {
            this.previewMouseX += (float) (dragX * DRAG_SENSITIVITY);
            this.previewMouseY -= (float) (dragY * DRAG_SENSITIVITY);
            this.previewMouseY = Mth.clamp(this.previewMouseY, -60.0F, 60.0F);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingViewer) {
            this.draggingViewer = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isInsideViewer(double mouseX, double mouseY) {
        return mouseX >= this.viewerX
                && mouseX < this.viewerX + VIEWER_WIDTH
                && mouseY >= this.viewerY
                && mouseY < this.viewerY + VIEWER_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.renderViewerPanel(graphics);
        this.renderCowPreview(graphics);
    }

    private void renderViewerPanel(GuiGraphics graphics) {
        int x1 = this.viewerX;
        int y1 = this.viewerY;
        int x2 = this.viewerX + VIEWER_WIDTH;
        int y2 = this.viewerY + VIEWER_HEIGHT;

        graphics.fill(x1, y1, x2, y2, 0xFF101010);
        graphics.fill(x1, y1, x2, y1 + 1, 0xFF555555);
        graphics.fill(x1, y2 - 1, x2, y2, 0xFF555555);
        graphics.fill(x1, y1, x1 + 1, y2, 0xFF555555);
        graphics.fill(x2 - 1, y1, x2, y2, 0xFF555555);
    }

    private void renderCowPreview(GuiGraphics graphics) {
        this.ensurePreviewCow();
        if (this.previewCow == null) {
            return;
        }

        int x0 = this.viewerX + VIEWER_PADDING;
        int y0 = this.viewerY + VIEWER_PADDING;
        int x1 = this.viewerX + VIEWER_WIDTH - VIEWER_PADDING;
        int y1 = this.viewerY + VIEWER_HEIGHT - VIEWER_PADDING;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                this.previewCow,
                x0,
                y0,
                x1,
                y1,
                PREVIEW_SCALE,
                0.0F,
                this.previewMouseX,
                this.previewMouseY
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int labelX = (this.viewerX - this.leftPos) + 24;
        int labelY = (this.viewerY - this.topPos) + VIEWER_HEIGHT + 4;

        graphics.drawString(
                this.font,
                this.draggingViewer ? "dragging..." : "drag to rotate cow",
                labelX,
                labelY,
                0xA0A0A0,
                false
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}