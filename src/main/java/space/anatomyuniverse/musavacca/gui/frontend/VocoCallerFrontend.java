package space.anatomyuniverse.musavacca.gui.frontend;

//? if <1.21.6
//import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.backend.VocoCallerBackend;
import space.anatomyuniverse.musavacca.gui.menu.VocoCallerMenu;

public final class VocoCallerFrontend extends AbstractContainerScreen<VocoCallerMenu> {

    private static final int GUI_WIDTH = 286;
    private static final int GUI_HEIGHT = 206;
    private static final int PHONE_X = 0;
    private static final int PHONE_Y = 0;
    private static final int PHONE_WIDTH = 126;
    private static final int PHONE_HEIGHT = 206;
    private static final int BASE_X = 129;
    private static final int BASE_Y = 42;
    private static final int BASE_WIDTH = 157;
    private static final int BASE_HEIGHT = 164;
    private static final int BUTTON_BAR_X = 129;
    private static final int BUTTON_BAR_Y = 0;
    private static final int BUTTON_BAR_WIDTH = 157;
    private static final int BUTTON_BAR_HEIGHT = 39;
    private static final int BUTTON_Y = 8;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CURRENT_DIALED_X = 52;
    private static final int CURRENT_DIALED_Y = 22;
    private static final int RECENT_X = 23;
    private static final int SAVED_X = 76;
    private static final int LIST_Y = 59;
    private static final int ROW_HEIGHT = 9;
    private static final int SYMBOL_WIDTH = 5;
    private static final int SYMBOL_HEIGHT = 7;
    private static final int SYMBOL_STEP = 4;
    private static final int HEX_LENGTH = 6;
    private static final int HEX_CODE_WIDTH = SYMBOL_WIDTH + (HEX_LENGTH - 1) * SYMBOL_STEP;
    private static final int SELECTION_COUNT = VocoCallerBackend.ROW_COUNT * 2;
    private static final int RECENT_CARET_X = 19;
    private static final int SAVED_CARET_X = 72;
    private static final int CARET_WIDTH = 2;
    private static final int CARET_HEIGHT = 7;
    private static final int CARET_COLOR = 0xFFCFBF5F;
    private static final long CARET_BLINK_NANOS = 500_000_000L;
    private static final long ARROW_REPEAT_DELAY_NANOS = 280_000_000L;
    private static final long ARROW_REPEAT_INTERVAL_NANOS = 35_000_000L;
    private static final long DOUBLE_CLICK_NANOS = 300_000_000L;
    private static final long LONG_PRESS_NANOS = 650_000_000L;
    private static final ResourceLocation PHONE_SCREEN_TEXTURE = texture("phone_screen");
    private static final ResourceLocation BASE_TEXTURE = texture("base");
    private static final ResourceLocation BUTTON_BAR_TEXTURE = texture("button_bar");
    private static final ResourceLocation ARROW_LEFT_TEXTURE = texture("arrow_left");
    private static final ResourceLocation ARROW_LEFT_PRESSED_TEXTURE = texture("arrow_left_pressed");
    private static final ResourceLocation SPACE_BUTTON_TEXTURE = texture("space_button");
    private static final ResourceLocation SPACE_BUTTON_PRESSED_TEXTURE = texture("space_button_pressed");
    private static final ResourceLocation ARROW_RIGHT_TEXTURE = texture("arrow_right");
    private static final ResourceLocation ARROW_RIGHT_PRESSED_TEXTURE = texture("arrow_right_pressed");
    private static final ResourceLocation[] SYMBOL_TEXTURES = createSymbolTextures();
    private int selectedPosition = 0;

    /** Also controls the direction used when reordering Saved numbers. */
    private int lastNavigationDirection = 1;
    private long caretBlinkStartNanos = System.nanoTime();
    private CallerButton pressedButton = null;
    private long buttonRepeatLastNanos = 0L;
    private boolean buttonRepeatMode = false;

    /** Shared entry-press state for direct clicks and the middle button. */
    private int pressedEntryPosition = -1;
    private long entryPressStartNanos = 0L;
    private boolean entryPressConsumed = false;
    private int lastClickedEntryPosition = -1;
    private long lastEntryClickNanos = 0L;

    public VocoCallerFrontend(VocoCallerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.tickHeldArrow();
        this.tickHeldEntry();
        blit(graphics, PHONE_SCREEN_TEXTURE, this.leftPos + PHONE_X, this.topPos + PHONE_Y, PHONE_WIDTH, PHONE_HEIGHT);
        blit(graphics, BASE_TEXTURE, this.leftPos + BASE_X, this.topPos + BASE_Y, BASE_WIDTH, BASE_HEIGHT);
        nextLayer(graphics);
        blit(graphics, BUTTON_BAR_TEXTURE, this.leftPos + BUTTON_BAR_X, this.topPos + BUTTON_BAR_Y, BUTTON_BAR_WIDTH, BUTTON_BAR_HEIGHT);
        nextLayer(graphics);
        for (CallerButton button : CallerButton.values()) {
            blit(graphics, button.texture(this.pressedButton == button), this.leftPos + button.x, this.topPos + BUTTON_Y, button.width, BUTTON_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        this.renderCurrentDialed(graphics);
        this.renderCallLists(graphics);
        this.renderSelectionCaret(graphics);
    }

    private void renderCurrentDialed(GuiGraphics graphics) {
        this.drawHexCode(graphics, this.backend().getCurrentDialed(), CURRENT_DIALED_X, CURRENT_DIALED_Y);
    }

    private void renderCallLists(GuiGraphics graphics) {
        for (int row = 0; row < VocoCallerBackend.ROW_COUNT; row++) {
            int y = LIST_Y + row * ROW_HEIGHT;
            this.drawHexCode(graphics, this.backend().getRecentCall(row), RECENT_X, y);
            this.drawHexCode(graphics, this.backend().getSavedNumber(row), SAVED_X, y);
        }
    }

    private void drawHexCode(GuiGraphics graphics, String hexCode, int x, int y) {
        if (hexCode == null) {
            return;
        }
        for (int index = 0; index < Math.min(HEX_LENGTH, hexCode.length()); index++) {
            int symbol = Character.digit(hexCode.charAt(index), 16);
            if (symbol < 0) {
                continue;
            }
            blit(graphics, SYMBOL_TEXTURES[symbol], x + index * SYMBOL_STEP, y, SYMBOL_WIDTH, SYMBOL_HEIGHT);
        }
    }

    private void renderSelectionCaret(GuiGraphics graphics) {
        if (!this.hasEntry(this.selectedPosition)) {
            return;
        }
        if (!this.isCaretVisible()) {
            return;
        }
        int row = rowOf(this.selectedPosition);
        boolean savedNumber = isSavedPosition(this.selectedPosition);
        int x = savedNumber ? SAVED_CARET_X : RECENT_CARET_X;
        int y = LIST_Y + row * ROW_HEIGHT;
        graphics.fill(x, y, x + CARET_WIDTH, y + CARET_HEIGHT, CARET_COLOR);
    }

    private boolean isCaretVisible() {
        long elapsed = System.nanoTime() - this.caretBlinkStartNanos;
        return (elapsed / CARET_BLINK_NANOS & 1L) == 0L;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        int entryPosition = this.findEntryPosition(mouseX, mouseY);
        if (entryPosition >= 0) {
            this.selectPosition(entryPosition);
            this.beginEntryPress(entryPosition);
            return true;
        }
        for (CallerButton callerButton : CallerButton.values()) {
            if (!this.contains(mouseX, mouseY, callerButton.x, BUTTON_Y, callerButton.width, BUTTON_HEIGHT)) {
                continue;
            }
            this.startButtonPress(callerButton);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
        boolean handled = false;
        if (this.pressedEntryPosition >= 0) {
            this.clearEntryPress();
            handled = true;
        }
        if (this.pressedButton != null) {
            this.clearButtonPress();
            handled = true;
        }
        if (handled) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void startButtonPress(CallerButton button) {
        this.pressedButton = button;
        this.buttonRepeatLastNanos = System.nanoTime();
        this.buttonRepeatMode = false;
        this.activateButton(button);
    }

    private void activateButton(CallerButton button) {
        switch (button) {
            case LEFT -> this.moveSelection(-1);
            case SPACE -> this.beginEntryPress(this.selectedPosition);
            case RIGHT -> this.moveSelection(1);
        }
    }

    private void tickHeldArrow() {
        if (this.pressedButton != CallerButton.LEFT && this.pressedButton != CallerButton.RIGHT) {
            return;
        }
        long now = System.nanoTime();
        long wait = this.buttonRepeatMode ? ARROW_REPEAT_INTERVAL_NANOS : ARROW_REPEAT_DELAY_NANOS;
        if (now - this.buttonRepeatLastNanos < wait) {
            return;
        }
        int guard = 0;
        do {
            this.activateButton(this.pressedButton);
            this.buttonRepeatLastNanos += wait;
            this.buttonRepeatMode = true;
            wait = ARROW_REPEAT_INTERVAL_NANOS;
            guard++;
        } while (now - this.buttonRepeatLastNanos >= wait && guard < 20);
    }

    private void beginEntryPress(int position) {
        if (!this.hasEntry(position)) {
            return;
        }
        long now = System.nanoTime();
        this.pressedEntryPosition = position;
        this.entryPressStartNanos = now;
        this.entryPressConsumed = false;
        boolean doubleClick = position == this.lastClickedEntryPosition && now - this.lastEntryClickNanos <= DOUBLE_CLICK_NANOS;
        if (doubleClick) {
            this.clearLastEntryClick();
            this.entryPressConsumed = true;
            this.activateDoubleClick(position);
            return;
        }
        this.lastClickedEntryPosition = position;
        this.lastEntryClickNanos = now;
    }

    private void tickHeldEntry() {
        if (this.pressedEntryPosition < 0 || this.entryPressConsumed) {
            return;
        }
        long now = System.nanoTime();
        if (now - this.entryPressStartNanos < LONG_PRESS_NANOS) {
            return;
        }
        int position = this.pressedEntryPosition;
        this.entryPressConsumed = true;
        this.clearLastEntryClick();
        this.deleteEntry(position);
    }

    private void activateDoubleClick(int position) {
        int row = rowOf(position);
        boolean savedNumber = isSavedPosition(position);
        if (savedNumber) {
            this.moveSavedEntry(row);
            return;
        }
        this.moveRecentEntryToSaved(row);
    }

    private void moveRecentEntryToSaved(int row) {
        if (!this.backend().moveRecentCallToSavedTop(row)) {
            return;
        }
        this.selectedPosition = positionOf(0, true);
        this.restartCaretBlink();
    }

    private void moveSavedEntry(int row) {
        if (!this.backend().hasSavedNumber(row)) {
            return;
        }
        int newRow = this.backend().moveSavedNumber(row, this.lastNavigationDirection);
        this.selectedPosition = positionOf(newRow, true);
        this.restartCaretBlink();
    }

    private void deleteEntry(int position) {
        int row = rowOf(position);
        boolean savedNumber = isSavedPosition(position);
        if (savedNumber) {
            this.backend().deleteSavedNumberToRecent(row);
        } else {
            this.backend().deleteRecentCall(row);
        }
        this.ensureCaretOnFilledEntry();
        this.restartCaretBlink();
    }

    private void ensureCaretOnFilledEntry() {
        if (this.hasEntry(this.selectedPosition)) {
            return;
        }
        int nextPosition = this.findNextFilledPosition(this.selectedPosition, this.lastNavigationDirection);
        if (nextPosition >= 0) {
            this.selectedPosition = nextPosition;
            return;
        }
        nextPosition = this.findNextFilledPosition(this.selectedPosition, -this.lastNavigationDirection);
        if (nextPosition >= 0) {
            this.selectedPosition = nextPosition;
        }
    }

    private void clearEntryPress() {
        this.pressedEntryPosition = -1;
        this.entryPressStartNanos = 0L;
        this.entryPressConsumed = false;
    }

    private void clearLastEntryClick() {
        this.lastClickedEntryPosition = -1;
        this.lastEntryClickNanos = 0L;
    }

    private void clearButtonPress() {
        this.pressedButton = null;
        this.buttonRepeatLastNanos = 0L;
        this.buttonRepeatMode = false;
    }

    private void moveSelection(int direction) {
        int step = Integer.compare(direction, 0);
        if (step == 0) {
            return;
        }
        this.lastNavigationDirection = step;
        int nextPosition = this.findNextFilledPosition(this.selectedPosition, step);
        if (nextPosition < 0) {
            return;
        }
        this.selectedPosition = nextPosition;
        this.restartCaretBlink();
    }

    private int findNextFilledPosition(int fromPosition, int direction) {
        int step = Integer.compare(direction, 0);
        if (step == 0) {
            return -1;
        }
        int position = fromPosition;
        for (int checked = 0; checked < SELECTION_COUNT; checked++) {
            position = Math.floorMod(position + step, SELECTION_COUNT);
            if (this.hasEntry(position)) {
                return position;
            }
        }
        return -1;
    }

    private void selectPosition(int position) {
        if (position < 0 || position >= SELECTION_COUNT) {
            return;
        }
        if (!this.hasEntry(position)) {
            return;
        }
        this.selectedPosition = position;
        this.restartCaretBlink();
    }

    private void restartCaretBlink() {
        this.caretBlinkStartNanos = System.nanoTime();
    }

    private boolean hasEntry(int position) {
        if (position < 0 || position >= SELECTION_COUNT) {
            return false;
        }
        int row = rowOf(position);
        return isSavedPosition(position) ? this.backend().hasSavedNumber(row) : this.backend().hasRecentCall(row);
    }

    private int findEntryPosition(double mouseX, double mouseY) {
        for (int row = 0; row < VocoCallerBackend.ROW_COUNT; row++) {
            int y = LIST_Y + row * ROW_HEIGHT;
            if (this.backend().hasRecentCall(row) && this.contains(mouseX, mouseY, RECENT_X, y, HEX_CODE_WIDTH, SYMBOL_HEIGHT)) {
                return positionOf(row, false);
            }
            if (this.backend().hasSavedNumber(row) && this.contains(mouseX, mouseY, SAVED_X, y, HEX_CODE_WIDTH, SYMBOL_HEIGHT)) {
                return positionOf(row, true);
            }
        }
        return -1;
    }

    private static int rowOf(int position) {
        return position / 2;
    }

    private static boolean isSavedPosition(int position) {
        return (position & 1) != 0;
    }

    private static int positionOf(int row, boolean savedNumber) {
        return row * 2 + (savedNumber ? 1 : 0);
    }

    private VocoCallerBackend backend() {
        return this.menu.getBackend();
    }

    public void setCurrentDialed(String hexCode) {
        this.backend().setCurrentDialed(hexCode);
    }

    public void setCurrentDialed(int hexColor) {
        this.backend().setCurrentDialed(hexColor);
    }

    public void clearCurrentDialed() {
        this.backend().clearCurrentDialed();
    }

    public void setRecentCall(int row, String hexCode) {
        this.backend().setRecentCall(row, hexCode);
        this.ensureCaretOnFilledEntry();
    }

    public void setRecentCall(int row, int hexColor) {
        this.backend().setRecentCall(row, hexColor);
        this.ensureCaretOnFilledEntry();
    }

    public void clearRecentCall(int row) {
        this.backend().clearRecentCall(row);
        this.ensureCaretOnFilledEntry();
    }

    public void setSavedNumber(int row, String hexCode) {
        this.backend().setSavedNumber(row, hexCode);
        this.ensureCaretOnFilledEntry();
    }

    public void setSavedNumber(int row, int hexColor) {
        this.backend().setSavedNumber(row, hexColor);
        this.ensureCaretOnFilledEntry();
    }

    public void clearSavedNumber(int row) {
        this.backend().clearSavedNumber(row);
        this.ensureCaretOnFilledEntry();
    }

    @Override
    public void removed() {
        this.clearEntryPress();
        this.clearButtonPress();
        super.removed();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private boolean contains(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = this.leftPos + localX;
        int y = this.topPos + localY;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static ResourceLocation[] createSymbolTextures() {
        ResourceLocation[] textures = new ResourceLocation[16];
        for (int value = 0; value < textures.length; value++) {
            textures[value] = texture("symbols/" + Character.forDigit(value, 16));
        }
        return textures;
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/voco_caller/" + name + ".png");
    }

    private static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
        //? if >=1.21.6 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, width, height);
        //?} else {
        /*graphics.blit(
                RenderType::guiTextured,
                texture,
                x,
                y,
                0.0F,
                0.0F,
                width,
                height,
                width,
                height
        );
        *///?}
    }

    private static void nextLayer(GuiGraphics graphics) {
        //? if >=1.21.6
        graphics.nextStratum();
    }

    private enum CallerButton {
        LEFT(155, 20),
        SPACE(183, 49),
        RIGHT(240, 20);
        private final int x;
        private final int width;
        CallerButton(int x, int width) {
            this.x = x;
            this.width = width;
        }
        private ResourceLocation texture(boolean pressed) {
            return switch (this) {
                case LEFT -> pressed ? ARROW_LEFT_PRESSED_TEXTURE : ARROW_LEFT_TEXTURE;
                case SPACE -> pressed ? SPACE_BUTTON_PRESSED_TEXTURE : SPACE_BUTTON_TEXTURE;
                case RIGHT -> pressed ? ARROW_RIGHT_PRESSED_TEXTURE : ARROW_RIGHT_TEXTURE;
            };
        }
    }
}
