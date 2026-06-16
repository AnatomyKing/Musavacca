package space.anatomyuniverse.musavacca.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.gui.menu.ItemInteractMenu;

public class ItemInteractScreen extends AbstractContainerScreen<ItemInteractMenu> {
    private static final String HEX = "0123456789ABCDEF";

    public ItemInteractScreen(ItemInteractMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 260;
        this.imageHeight = 170;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos + 10;
        int top = this.topPos + 54;

        this.addKeyboardRow("1234567890", left, top);
        this.addKeyboardRow("QWERTYUIOP", left, top + 22);
        this.addKeyboardRow("ASDFGHJKL", left + 11, top + 44);
        this.addKeyboardRow("ZXCVBNM", left + 33, top + 66);

        this.addRenderableWidget(
                Button.builder(Component.literal("Backspace"), button -> this.sendMenuButton(ItemInteractMenu.BUTTON_BACKSPACE))
                        .bounds(this.leftPos + 10, this.topPos + 138, 72, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Clear"), button -> this.sendMenuButton(ItemInteractMenu.BUTTON_CLEAR))
                        .bounds(this.leftPos + 86, this.topPos + 138, 50, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Default"), button -> this.sendMenuButton(ItemInteractMenu.BUTTON_DEFAULT))
                        .bounds(this.leftPos + 140, this.topPos + 138, 58, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Random"), button -> this.sendMenuButton(ItemInteractMenu.BUTTON_RANDOM))
                        .bounds(this.leftPos + 202, this.topPos + 138, 48, 20)
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
                        btn -> this.sendMenuButton(ItemInteractMenu.buttonIdForHexNibble(nibble))
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
        graphics.fill(left + 184, top + 12, left + 248, top + 76, 0xFFFFFFFF);
        graphics.fill(left + 188, top + 16, left + 244, top + 72, 0xFF000000 | this.menu.getDisplayedHexColor());

        String code = this.menu.getDisplayedCode();
        int cursor = this.menu.getCursor();

        for (int i = 0; i < 6; i++) {
            int boxX = left + 14 + i * 21;
            int boxY = top + 18;
            boolean activeCursor = (cursor == i);

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
        InteractionHand hand = this.menu.getHand();

        graphics.drawString(this.font, this.title, 10, 6, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.literal("#" + this.menu.getDisplayedCode()), 10, 46, 0xFFFFFF, false);
        graphics.drawString(this.font, Component.literal("Active keys: 0-9 and A-F"), 10, 58, 0xBFBFBF, false);
        graphics.drawString(this.font, Component.literal("Random chooses a server-side hex color."), 10, 70, 0xBFBFBF, false);
        graphics.drawString(
                this.font,
                Component.literal("Hand: " + (hand == InteractionHand.MAIN_HAND ? "Main Hand" : "Offhand")),
                184,
                82,
                0xD8D8D8,
                false
        );
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        int nibble = nibbleOf(codePoint);
        if (nibble >= 0) {
            this.sendMenuButton(ItemInteractMenu.buttonIdForHexNibble(nibble));
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259) { // backspace
            this.sendMenuButton(ItemInteractMenu.BUTTON_BACKSPACE);
            return true;
        }

        if (keyCode == 261) { // delete
            this.sendMenuButton(ItemInteractMenu.BUTTON_CLEAR);
            return true;
        }

        if (keyCode == 257 || keyCode == 335) { // enter / keypad enter
            this.onClose();
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