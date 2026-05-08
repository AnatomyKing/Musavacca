// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/gui/screen/HexTeleportScreen.java
package space.anatomyuniverse.musavacca.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.gui.menu.HexTeleportMenu;

public class HexTeleportScreen extends AbstractContainerScreen<HexTeleportMenu> {
    private static final String HEX = "0123456789ABCDEF";

    public HexTeleportScreen(HexTeleportMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 190;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos + 10;
        int top = this.topPos + 58;

        this.addKeyboardRow("1234567890", left, top);
        this.addKeyboardRow("QWERTYUIOP", left, top + 22);
        this.addKeyboardRow("ASDFGHJKL", left + 11, top + 44);
        this.addKeyboardRow("ZXCVBNM", left + 33, top + 66);

        this.addRenderableWidget(
                Button.builder(Component.literal("Teleport"), button -> this.sendMenuButton(HexTeleportMenu.BUTTON_TELEPORT))
                        .bounds(this.leftPos + 10, this.topPos + 138, 72, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Backspace"), button -> this.sendMenuButton(HexTeleportMenu.BUTTON_BACKSPACE))
                        .bounds(this.leftPos + 86, this.topPos + 138, 72, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Clear"), button -> this.sendMenuButton(HexTeleportMenu.BUTTON_CLEAR))
                        .bounds(this.leftPos + 162, this.topPos + 138, 50, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Default"), button -> this.sendMenuButton(HexTeleportMenu.BUTTON_DEFAULT))
                        .bounds(this.leftPos + 10, this.topPos + 162, 72, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), button -> this.onClose())
                        .bounds(this.leftPos + 86, this.topPos + 162, 72, 20)
                        .build()
        );
    }

    private void addKeyboardRow(String letters, int startX, int y) {
        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            int x = startX + i * 22;
            this.addRenderableWidget(this.makeKeyButton(x, y, c));
        }
    }

    private Button makeKeyButton(int x, int y, char key) {
        int nibble = nibbleOf(key);
        boolean active = nibble >= 0;

        Button button = Button.builder(
                        Component.literal(String.valueOf(key)),
                        btn -> this.sendMenuButton(HexTeleportMenu.buttonIdForHexNibble(nibble))
                )
                .bounds(x, y, 20, 20)
                .build();

        button.active = active;
        return button;
    }

    private void sendMenuButton(int buttonId) {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.gameMode == null) {
            return;
        }

        if (this.menu.clickMenuButton(this.minecraft.player, buttonId)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private static int nibbleOf(char c) {
        char upper = Character.toUpperCase(c);
        if (upper >= '0' && upper <= '9') {
            return upper - '0';
        }
        if (upper >= 'A' && upper <= 'F') {
            return 10 + (upper - 'A');
        }
        return -1;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF1B1B1B);
        graphics.fill(left + 1, top + 1, left + this.imageWidth - 1, top + this.imageHeight - 1, 0xFF2A2A2A);

        graphics.fill(left + 10, top + 10, left + 150, top + 44, 0x55101010);
        graphics.fill(left + 164, top + 12, left + 228, top + 76, 0xFFFFFFFF);
        graphics.fill(left + 168, top + 16, left + 224, top + 72, 0xFF000000 | this.menu.getDisplayedHexColor());

        String code = this.menu.getDisplayedCode();
        int cursor = this.menu.getCursor();

        for (int i = 0; i < 6; i++) {
            int boxX = left + 14 + i * 21;
            int boxY = top + 18;
            boolean activeCursor = cursor == i;

            graphics.fill(boxX, boxY, boxX + 18, boxY + 20, activeCursor ? 0xFFAAAA55 : 0xFF777777);
            graphics.fill(boxX + 1, boxY + 1, boxX + 17, boxY + 19, 0xFF202020);

            graphics.drawString(
                    this.font,
                    String.valueOf(code.charAt(i)),
                    boxX + 6,
                    boxY + 6,
                    0xFFFFFF,
                    false
            );
        }

        if (cursor == 6) {
            int boxX = left + 14 + 5 * 21;
            int boxY = top + 18;
            graphics.fill(boxX + 16, boxY + 2, boxX + 18, boxY + 18, 0xFFAAAA55);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 10, 6, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.literal("#" + this.menu.getDisplayedCode()), 10, 46, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.literal("Type any registered portal or Voco address."), 10, 58, 0xBFBFBF, false);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        int nibble = nibbleOf(codePoint);
        if (nibble >= 0) {
            this.sendMenuButton(HexTeleportMenu.buttonIdForHexNibble(nibble));
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259) {
            this.sendMenuButton(HexTeleportMenu.BUTTON_BACKSPACE);
            return true;
        }

        if (keyCode == 261) {
            this.sendMenuButton(HexTeleportMenu.BUTTON_CLEAR);
            return true;
        }

        if (keyCode == 257 || keyCode == 335) {
            this.sendMenuButton(HexTeleportMenu.BUTTON_TELEPORT);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}