package space.anatomyuniverse.musavacca.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;
import space.anatomyuniverse.musavacca.gui.menu.VocoSliderMenu;

import java.util.function.IntConsumer;

public class VocoSliderScreen extends AbstractContainerScreen<VocoSliderMenu> {

    public VocoSliderScreen(VocoSliderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 240;
        this.imageHeight = 132;

        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top = this.topPos;

        this.addRenderableWidget(
                new DegreeSlider(
                        left + 20,
                        top + 38,
                        200,
                        20,
                        "Horizontal / Yaw",
                        VocoReceptorBlockEntity.MIN_YAW_DEGREES,
                        VocoReceptorBlockEntity.MAX_YAW_DEGREES,
                        this.menu.getYawDegrees(),
                        degrees -> this.sendMenuButton(VocoSliderMenu.buttonIdForYaw(degrees))
                )
        );

        this.addRenderableWidget(
                new DegreeSlider(
                        left + 20,
                        top + 70,
                        200,
                        20,
                        "Vertical / Pitch",
                        VocoReceptorBlockEntity.MIN_PITCH_DEGREES,
                        VocoReceptorBlockEntity.MAX_PITCH_DEGREES,
                        this.menu.getPitchDegrees(),
                        degrees -> this.sendMenuButton(VocoSliderMenu.buttonIdForPitch(degrees))
                )
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), button -> this.onClose())
                        .bounds(left + 84, top + 100, 72, 20)
                        .build()
        );
    }

    private void sendMenuButton(int buttonId) {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.gameMode == null) {
            return;
        }

        if (this.menu.clickMenuButton(this.minecraft.player, buttonId)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF1B1B1B);
        graphics.fill(left + 1, top + 1, left + this.imageWidth - 1, top + this.imageHeight - 1, 0xFF2A2A2A);

        graphics.drawString(
                this.font,
                Component.literal("Voco Facing: " + this.menu.getReceptorDisplayName()),
                left + 20,
                top + 14,
                0xFFFFFF,
                false
        );
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

    private static class DegreeSlider extends AbstractSliderButton {
        private final String label;
        private final int minDegrees;
        private final int maxDegrees;
        private final IntConsumer onChanged;

        private DegreeSlider(
                int x,
                int y,
                int width,
                int height,
                String label,
                int minDegrees,
                int maxDegrees,
                int initialDegrees,
                IntConsumer onChanged
        ) {
            super(
                    x,
                    y,
                    width,
                    height,
                    Component.empty(),
                    toSliderValue(minDegrees, maxDegrees, initialDegrees)
            );

            this.label = label;
            this.minDegrees = minDegrees;
            this.maxDegrees = maxDegrees;
            this.onChanged = onChanged;

            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(this.label + ": " + this.getDegrees() + "°"));
        }

        @Override
        protected void applyValue() {
            this.updateMessage();
            this.onChanged.accept(this.getDegrees());
        }

        private int getDegrees() {
            int range = this.maxDegrees - this.minDegrees;
            return this.minDegrees + Math.round((float) this.value * range);
        }

        private static double toSliderValue(int minDegrees, int maxDegrees, int degrees) {
            int clamped = Math.max(minDegrees, Math.min(maxDegrees, degrees));
            return (double) (clamped - minDegrees) / (double) (maxDegrees - minDegrees);
        }
    }
}