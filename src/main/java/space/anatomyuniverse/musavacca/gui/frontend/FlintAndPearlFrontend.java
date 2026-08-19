package space.anatomyuniverse.musavacca.gui.frontend;

//? if <1.21.6
//import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
//? if >=1.21.7
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//? if <1.21.7
//import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.gui.menu.payloads.FlintAndPearlColorPayload;
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlMenu;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class FlintAndPearlFrontend extends AbstractContainerScreen<FlintAndPearlMenu> {
    private static final int GUI_WIDTH = 275;
    private static final int GUI_HEIGHT = 256;
    private static final int PEARL_X = 5;
    private static final int PEARL_Y = 10;
    private static final int PEARL_WIDTH = 124;
    private static final int PEARL_HEIGHT = 156;
    private static final int PEARL_LAYER_COUNT = 13;
    private static final int HEX_X = 139;
    private static final int HEX_Y = 22;
    private static final int HEX_WIDTH = 78;
    private static final int HEX_HEIGHT = 38;
    private static final int HEX_SYMBOL_X = 25;
    private static final int HEX_SYMBOL_Y = 11;
    private static final int HEX_SYMBOL_PRESSED_Y = 13;
    private static final int HEX_SYMBOL_COUNT = 6;
    private static final int BAR_WIDTH = 115;
    private static final int BAR_HEIGHT = 38;
    private static final int BAR_PRESSED_OFFSET_Y = 2;
    private static final int STRIP_X = 10;
    private static final int STRIP_Y = 10;
    private static final int STRIP_WIDTH = 76;
    private static final int STRIP_HEIGHT = 13;
    private static final int VALUE_X = 88;
    private static final int VALUE_Y = 12;
    private static final int VALUE_SYMBOL_COUNT = 3;
    private static final int SYMBOL_WIDTH = 7;
    private static final int SYMBOL_HEIGHT = 9;
    private static final int SYMBOL_SPACING = 8;
    private static final int HEX_TEXT_WIDTH = (HEX_SYMBOL_COUNT - 1) * SYMBOL_SPACING + SYMBOL_WIDTH;
    private static final int HEX_TEXT_HEIGHT = SYMBOL_HEIGHT + (HEX_SYMBOL_PRESSED_Y - HEX_SYMBOL_Y);
    private static final int HEX_CARET_X = HEX_SYMBOL_X + HEX_SYMBOL_COUNT * SYMBOL_SPACING - 1;
    private static final int VALUE_TEXT_WIDTH = (VALUE_SYMBOL_COUNT - 1) * SYMBOL_SPACING + SYMBOL_WIDTH;
    private static final int VALUE_TEXT_HEIGHT = SYMBOL_HEIGHT + BAR_PRESSED_OFFSET_Y;
    private static final int VALUE_CARET_X = VALUE_X + VALUE_SYMBOL_COUNT * SYMBOL_SPACING - 1;
    private static final int CARET_WIDTH = 1;
    private static final int CARET_HEIGHT = 9;
    private static final int CARET_OFFSET_Y = 0;
    private static final int CARET_COLOR = 0xFF818181;
    private static final int SLIDER_WIDTH = 13;
    private static final int SLIDER_HEIGHT = 11;
    private static final int SLIDER_MIN_X = 4;
    private static final int SLIDER_MAX_X = 79;
    private static final int SLIDER_Y = 21;
    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 19;
    private static final int ARROW_X = BAR_WIDTH + 1;
    private static final int ARROW_UP_Y = -1;
    private static final int ARROW_DOWN_Y = 20;
    private static final long FLASH_PHASE_NANOS = 75_000_000L;
    private static final long LONG_CLICK_NANOS = 500_000_000L;
    private static final long COLOR_SYNC_INTERVAL_NANOS = 50_000_000L;
    private static final long EDIT_DOUBLE_CLICK_NANOS = 250_000_000L;
    private static final long CARET_BLINK_NANOS = 500_000_000L;
    private static final long BACKSPACE_REPEAT_DELAY_NANOS = 280_000_000L;
    private static final long BACKSPACE_REPEAT_INTERVAL_NANOS = 35_000_000L;
    private static final long PEARL_REPEAT_DELAY_NANOS = 220_000_000L;
    private static final long PEARL_REPEAT_INTERVAL_NANOS = 115_000_000L;
    private static final long ARROW_REPEAT_DELAY_NANOS = 280_000_000L;
    private static final long ARROW_REPEAT_INTERVAL_NANOS = 35_000_000L;
    private static final int SINGLE_FLASH = 1;
    private static final int DOUBLE_FLASH = 2;
    private static final Object PEARL_TARGET = new Object();
    private static final Object HEX_TARGET = new Object();
    private static final ResourceLocation HEX_NORMAL = guiTexture("hex_bar");
    private static final ResourceLocation HEX_PRESSED = guiTexture("hex_bar_pressed");
    private static final ResourceLocation HEX_FLASH = guiTexture("hex_bar_flash");
    private static final ResourceLocation HEX_PRESSED_FLASH = guiTexture("hex_bar_pressed_flash");
    private static final ResourceLocation BAR_NORMAL = guiTexture("bar");
    private static final ResourceLocation BAR_PRESSED = guiTexture("bar_pressed");
    private static final ResourceLocation BAR_FLASH = guiTexture("bar_flash");
    private static final ResourceLocation BAR_PRESSED_FLASH = guiTexture("bar_pressed_flash");
    private static final ResourceLocation SLIDER = guiTexture("slider");
    private static final ResourceLocation ARROW_UP = guiTexture("arrow_up");
    private static final ResourceLocation ARROW_UP_PRESSED = guiTexture("arrow_up_pressed");
    private static final ResourceLocation ARROW_DOWN = guiTexture("arrow_down");
    private static final ResourceLocation ARROW_DOWN_PRESSED = guiTexture("arrow_down_pressed");
    private static final ResourceLocation[] PEARL = textureArray("flint_and_pearl_", PEARL_LAYER_COUNT);
    private static final ResourceLocation[] PEARL_PRESSED = textureArray("flint_and_pearl_pressed_", PEARL_LAYER_COUNT);
    private static final ResourceLocation[] SYMBOLS = symbolArray();
    private static final ArrowButton[] ARROWS = arrows();
    private float hue;
    private float saturation;
    private float value;
    private SliderId activeSlider = null;
    private Object pressedTarget = null;
    private Object flashTarget = null;
    private Object editingTarget = null;
    private Object lastTextClickTarget = null;
    private boolean repeatMode = false;
    private boolean longClickHandled = false;
    private boolean flashVisible = false;
    private boolean backspaceHeld = false;
    private boolean backspaceRepeatMode = false;
    private long pressStartNanos = 0L;
    private long repeatLastNanos = 0L;
    private long flashStartNanos = 0L;
    private long lastSyncNanos = 0L;
    private long lastTextClickNanos = 0L;
    private long caretBlinkStartNanos = 0L;
    private long backspaceRepeatLastNanos = 0L;
    private int flashPhaseCount = 0;
    private int lastSyncedRgb = -1;
    private int pendingSyncRgb = -1;
    private String editText = "";
    private double sliderGrabOffsetX = SLIDER_WIDTH / 2.0D;

    public FlintAndPearlFrontend(FlintAndPearlMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
        int initialRgb = TintColorUtil.rgb(menu.getHexColor());
        this.applyColorFromRgb(initialRgb);
        this.lastSyncedRgb = initialRgb;
    }

    private static ResourceLocation guiTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/" + name + ".png");
    }

    private static ResourceLocation symbolTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/symbols/" + name + ".png");
    }

    private static ResourceLocation[] textureArray(String prefix, int count) {
        ResourceLocation[] textures = new ResourceLocation[count];
        for (int i = 0; i < count; i++) {
            textures[i] = guiTexture(prefix + i);
        }
        return textures;
    }

    private static ResourceLocation[] symbolArray() {
        ResourceLocation[] textures = new ResourceLocation[16];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = symbolTexture(Integer.toHexString(i));
        }
        return textures;
    }

    private static ArrowButton[] arrows() {
        ArrowButton[] buttons = new ArrowButton[SliderId.values().length * 2];
        int index = 0;
        for (SliderId slider : SliderId.values()) {
            buttons[index++] = new ArrowButton(slider, true);
            buttons[index++] = new ArrowButton(slider, false);
        }
        return buttons;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.tickPressedTarget();
        this.tickBackspaceRepeat();
        this.tickFlash();
        this.flushColorSyncIfReady();
        int guiX = this.leftPos;
        int guiY = this.topPos;
        int rgb = this.getPreviewRgb();
        this.blitPearl(graphics, guiX, guiY, rgb);
        this.blitHex(graphics, guiX, guiY, rgb);
        for (SliderId slider : SliderId.values()) {
            this.blitSliderGroup(graphics, guiX, guiY, slider, rgb);
        }
        for (ArrowButton arrow : ARROWS) {
            this.blitArrow(graphics, guiX, guiY, arrow);
        }
    }

    private void blitPearl(GuiGraphics graphics, int guiX, int guiY, int rgb) {
        ResourceLocation[] textures = this.isPressed(PEARL_TARGET) ? PEARL_PRESSED : PEARL;
        for (int layer = 0; layer < textures.length; layer++) {
            this.blitTinted(graphics, textures[layer], guiX + PEARL_X, guiY + PEARL_Y, PEARL_WIDTH, PEARL_HEIGHT, PearlFireTintSource.profileTint(rgb, layer, PearlFireTintProfiles.FLINT_AND_PEARL));
        }
    }

    private void blitHex(GuiGraphics graphics, int guiX, int guiY, int rgb) {
        boolean pressed = this.isPressed(HEX_TARGET);
        boolean editing = this.isEditing(HEX_TARGET);
        int symbolY = guiY + HEX_Y + (pressed ? HEX_SYMBOL_PRESSED_Y : HEX_SYMBOL_Y);
        String hexText = editing ? this.editText : toSixDigitHex(rgb);
        this.blit(graphics, stateTexture(pressed, this.isFlashing(HEX_TARGET), HEX_NORMAL, HEX_PRESSED, HEX_FLASH, HEX_PRESSED_FLASH), guiX + HEX_X, guiY + HEX_Y, HEX_WIDTH, HEX_HEIGHT);
        this.blitSymbols(graphics, guiX + HEX_X + HEX_SYMBOL_X, symbolY, hexText, 16, HEX_SYMBOL_COUNT, editing);
        if (editing) {
            this.blitCaret(graphics, guiX + HEX_X + HEX_CARET_X, symbolY);
        }
    }

    private void blitSliderGroup(GuiGraphics graphics, int guiX, int guiY, SliderId slider, int rgb) {
        int offsetY = this.barOffset(slider);
        int symbolY = guiY + slider.y + VALUE_Y + offsetY;
        boolean editing = this.isEditing(slider);
        String valueText = editing ? this.editText : Integer.toString(this.displayValue(slider));
        this.blitStrip(graphics, guiX + slider.stripX(), guiY + slider.stripY() + offsetY, slider, rgb);
        this.blit(graphics, stateTexture(this.isPressed(slider), this.isFlashing(slider), BAR_NORMAL, BAR_PRESSED, BAR_FLASH, BAR_PRESSED_FLASH), guiX + slider.x, guiY + slider.y, BAR_WIDTH, BAR_HEIGHT);
        this.blitSymbols(graphics, guiX + slider.x + VALUE_X, symbolY, valueText, 10, VALUE_SYMBOL_COUNT, true);
        if (editing) {
            this.blitCaret(graphics, guiX + slider.x + VALUE_CARET_X, symbolY);
        }
        this.blit(graphics, SLIDER, guiX + slider.sliderX(this.sliderProgress(slider)), guiY + slider.sliderY() + offsetY, SLIDER_WIDTH, SLIDER_HEIGHT);
    }

    private void blitCaret(GuiGraphics graphics, int x, int y) {
        if (!this.isCaretVisible()) {
            return;
        }
        graphics.fill(x, y + CARET_OFFSET_Y, x + CARET_WIDTH, y + CARET_OFFSET_Y + CARET_HEIGHT, CARET_COLOR);
    }

    private void blitStrip(GuiGraphics graphics, int x, int y, SliderId slider, int rgb) {
        for (int column = 0; column < STRIP_WIDTH; column++) {
            float t = column / (float) (STRIP_WIDTH - 1);
            graphics.fill(x + column, y, x + column + 1, y + STRIP_HEIGHT, this.stripColor(slider, t, rgb));
        }
    }

    private void blitSymbols(GuiGraphics graphics, int x, int y, String text, int radix, int slots, boolean rightAligned) {
        int count = Math.min(text.length(), slots);
        int firstChar = text.length() - count;
        int firstSlot = rightAligned ? slots - count : 0;
        for (int i = 0; i < count; i++) {
            int value = Character.digit(text.charAt(firstChar + i), radix);
            if (value >= 0 && value < SYMBOLS.length) {
                this.blit(graphics, SYMBOLS[value], x + ((firstSlot + i) * SYMBOL_SPACING), y, SYMBOL_WIDTH, SYMBOL_HEIGHT);
            }
        }
    }

    private void blitArrow(GuiGraphics graphics, int guiX, int guiY, ArrowButton arrow) {
        this.blit(graphics, arrow.texture(this.isPressed(arrow)), guiX + arrow.x(), guiY + arrow.y(), ARROW_WIDTH, ARROW_HEIGHT);
    }

    private static ResourceLocation stateTexture(boolean pressed, boolean flashing, ResourceLocation normal, ResourceLocation pressedTexture, ResourceLocation flash, ResourceLocation pressedFlash) {
        if (pressed) {
            return flashing ? pressedFlash : pressedTexture;
        }
        return flashing ? flash : normal;
    }

    private int stripColor(SliderId slider, float t, int rgb) {
        return switch (slider) {
            case HUE -> hsvToArgb(t, 1.0F, 1.0F);
            case SATURATION -> hsvToArgb(this.hue, t, this.value);
            case VALUE -> hsvToArgb(this.hue, this.saturation, t);
            case RED -> 0xFF000000 | packRgb(Math.round(t * 255.0F), green(rgb), blue(rgb));
            case GREEN -> 0xFF000000 | packRgb(red(rgb), Math.round(t * 255.0F), blue(rgb));
            case BLUE -> 0xFF000000 | packRgb(red(rgb), green(rgb), Math.round(t * 255.0F));
        };
    }

    private void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
        this.blitColored(graphics, texture, x, y, width, height, 0xFFFFFFFF);
    }

    private void blitTinted(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int tint) {
        this.blitColored(graphics, texture, x, y, width, height, tint == TintColorUtil.NO_TINT ? 0xFFFFFFFF : 0xFF000000 | TintColorUtil.rgb(tint));
    }

    private void blitColored(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int color) {
        //? if >=1.21.6 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, width, height, color);
        //?} else {
        /*float alpha = ((color >> 24) & 0xFF) / 255.0F;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        graphics.setColor(red, green, blue, alpha);
        graphics.blit(
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
        graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
        *///?}
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        Object textTarget = this.textTargetAt(mouseX, mouseY);
        if (this.editingTarget != null) {
            if (java.util.Objects.equals(this.editingTarget, textTarget)) {
                this.resetCaretBlink();
                return true;
            }
            this.stopEditing();
        }
        if (textTarget != null) {
            long now = System.nanoTime();
            if (java.util.Objects.equals(this.lastTextClickTarget, textTarget) && this.lastTextClickNanos != 0L && now - this.lastTextClickNanos <= EDIT_DOUBLE_CLICK_NANOS) {
                this.clearLastTextClick();
                this.startEditing(textTarget);
                return true;
            }
            this.lastTextClickTarget = textTarget;
            this.lastTextClickNanos = now;
        } else {
            this.clearLastTextClick();
        }
        if (textTarget == HEX_TARGET) {
            this.press(mouseX, mouseY, HEX_TARGET, HEX_X + HEX_SYMBOL_X, HEX_Y + HEX_SYMBOL_Y, HEX_TEXT_WIDTH, HEX_TEXT_HEIGHT);
            return true;
        }
        if (textTarget instanceof SliderId slider) {
            this.press(mouseX, mouseY, slider, slider.x + VALUE_X, slider.y + VALUE_Y, VALUE_TEXT_WIDTH, VALUE_TEXT_HEIGHT);
            return true;
        }
        if (this.press(mouseX, mouseY, PEARL_TARGET, PEARL_X, PEARL_Y, PEARL_WIDTH, PEARL_HEIGHT)) {
            this.randomizeColor();
            return true;
        }
        if (this.press(mouseX, mouseY, HEX_TARGET, HEX_X, HEX_Y, HEX_WIDTH, HEX_HEIGHT)) {
            return true;
        }
        for (ArrowButton arrow : ARROWS) {
            if (this.press(mouseX, mouseY, arrow, arrow.x(), arrow.y(), ARROW_WIDTH, ARROW_HEIGHT)) {
                this.stepArrow(arrow);
                return true;
            }
        }
        for (SliderId slider : SliderId.values()) {
            if (this.tryStartSliderDrag(mouseX, mouseY, slider)) {
                return true;
            }
        }
        for (SliderId slider : SliderId.values()) {
            if (this.press(mouseX, mouseY, slider, slider.x, slider.y, BAR_WIDTH, BAR_HEIGHT)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.editingTarget == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!this.backspaceHeld) {
                this.backspaceHeld = true;
                this.backspaceRepeatMode = false;
                this.backspaceRepeatLastNanos = System.nanoTime();
                this.backspaceEditDigit();
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.stopEditing();
            return true;
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && this.backspaceHeld) {
            this.clearBackspaceRepeat();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.editingTarget == null) {
            return super.charTyped(codePoint, modifiers);
        }
        int digit = Character.digit(codePoint, this.editRadix());
        if (digit < 0 || this.editText.length() >= this.editSymbolCount()) {
            return true;
        }
        String candidate = this.editText + Character.forDigit(digit, this.editRadix());
        if (this.editingTarget instanceof SliderId slider && Integer.parseInt(candidate) > slider.max) {
            this.resetCaretBlink();
            return true;
        }
        this.editText = candidate;
        this.resetCaretBlink();
        this.applyEditText();
        return true;
    }

    private Object textTargetAt(double mouseX, double mouseY) {
        if (this.contains(mouseX, mouseY, HEX_X + HEX_SYMBOL_X, HEX_Y + HEX_SYMBOL_Y, HEX_TEXT_WIDTH, HEX_TEXT_HEIGHT)) {
            return HEX_TARGET;
        }
        for (SliderId slider : SliderId.values()) {
            if (this.contains(mouseX, mouseY, slider.x + VALUE_X, slider.y + VALUE_Y, VALUE_TEXT_WIDTH, VALUE_TEXT_HEIGHT)) {
                return slider;
            }
        }
        return null;
    }

    private void startEditing(Object target) {
        this.editingTarget = target;
        if (target == HEX_TARGET) {
            this.editText = toSixDigitHex(this.getPreviewRgb());
        } else if (target instanceof SliderId slider) {
            this.editText = Integer.toString(this.displayValue(slider));
        } else {
            this.stopEditing();
            return;
        }
        this.clearPress();
        this.resetCaretBlink();
    }

    private void stopEditing() {
        this.editingTarget = null;
        this.editText = "";
        this.caretBlinkStartNanos = 0L;
        this.clearLastTextClick();
        this.clearBackspaceRepeat();
    }

    private void clearLastTextClick() {
        this.lastTextClickTarget = null;
        this.lastTextClickNanos = 0L;
    }

    private int editRadix() {
        return this.editingTarget == HEX_TARGET ? 16 : 10;
    }

    private int editSymbolCount() {
        return this.editingTarget == HEX_TARGET ? HEX_SYMBOL_COUNT : VALUE_SYMBOL_COUNT;
    }

    private void applyEditText() {
        if (this.editText.isEmpty()) {
            return;
        }
        if (this.editingTarget == HEX_TARGET) {
            if (this.editText.length() == HEX_SYMBOL_COUNT) {
                this.setColorFromRgb(Integer.parseInt(this.editText, 16));
            }
            return;
        }
        if (this.editingTarget instanceof SliderId slider) {
            int editedValue = Integer.parseInt(this.editText);
            if (editedValue <= slider.max) {
                this.setDisplayValue(slider, editedValue);
            }
        }
    }

    private void backspaceEditDigit() {
        if (!this.editText.isEmpty()) {
            this.editText = this.editText.substring(0, this.editText.length() - 1);
            this.applyEditText();
        }
        this.resetCaretBlink();
    }

    private void tickBackspaceRepeat() {
        if (this.editingTarget == null || !this.backspaceHeld) {
            return;
        }
        long now = System.nanoTime();
        long wait = this.backspaceRepeatMode ? BACKSPACE_REPEAT_INTERVAL_NANOS : BACKSPACE_REPEAT_DELAY_NANOS;
        if (now - this.backspaceRepeatLastNanos < wait) {
            return;
        }
        int guard = 0;
        do {
            this.backspaceEditDigit();
            this.backspaceRepeatLastNanos += wait;
            this.backspaceRepeatMode = true;
            wait = BACKSPACE_REPEAT_INTERVAL_NANOS;
            guard++;
        } while (now - this.backspaceRepeatLastNanos >= wait && guard < this.editSymbolCount());
    }

    private void clearBackspaceRepeat() {
        this.backspaceHeld = false;
        this.backspaceRepeatMode = false;
        this.backspaceRepeatLastNanos = 0L;
    }

    private void resetCaretBlink() {
        this.caretBlinkStartNanos = System.nanoTime();
    }

    private boolean isCaretVisible() {
        return (System.nanoTime() - this.caretBlinkStartNanos) / CARET_BLINK_NANOS % 2L == 0L;
    }

    private boolean isEditing(Object target) {
        return java.util.Objects.equals(this.editingTarget, target);
    }

    private boolean press(double mouseX, double mouseY, Object target, int x, int y, int width, int height) {
        if (!this.contains(mouseX, mouseY, x, y, width, height)) {
            return false;
        }
        this.pressedTarget = target;
        this.pressStartNanos = System.nanoTime();
        this.repeatLastNanos = this.pressStartNanos;
        this.repeatMode = false;
        this.longClickHandled = false;
        return true;
    }

    private boolean tryStartSliderDrag(double mouseX, double mouseY, SliderId slider) {
        float progress = this.sliderProgress(slider);
        if (this.contains(mouseX, mouseY, slider.sliderX(progress), slider.sliderY(), SLIDER_WIDTH, SLIDER_HEIGHT)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = mouseX - (this.leftPos + slider.sliderX(progress));
            return true;
        }
        int trackWidth = (slider.sliderMaxX() - slider.sliderMinX()) + SLIDER_WIDTH;
        if (this.contains(mouseX, mouseY, slider.sliderMinX(), slider.sliderY(), trackWidth, SLIDER_HEIGHT)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = SLIDER_WIDTH / 2.0D;
            this.updateSliderFromMouse(mouseX);
            return true;
        }
        return false;
    }

    private void tickPressedTarget() {
        if (this.pressedTarget == PEARL_TARGET) {
            this.tickRepeat(PEARL_REPEAT_DELAY_NANOS, PEARL_REPEAT_INTERVAL_NANOS, this::randomizeColor);
            return;
        }
        if (this.pressedTarget instanceof ArrowButton arrow) {
            this.tickRepeat(ARROW_REPEAT_DELAY_NANOS, ARROW_REPEAT_INTERVAL_NANOS, () -> this.stepArrow(arrow));
            return;
        }
        if (this.pressedTarget == HEX_TARGET) {
            this.tickLongClick(LONG_CLICK_NANOS, () -> {
                if (this.injectHexFromClipboard()) {
                    this.startFlash(HEX_TARGET, SINGLE_FLASH);
                }
            });
            return;
        }
        if (this.pressedTarget instanceof SliderId slider) {
            this.tickLongClick(LONG_CLICK_NANOS, () -> {
                if (this.injectSliderFromClipboard(slider)) {
                    this.startFlash(slider, SINGLE_FLASH);
                }
            });
        }
    }

    private void tickRepeat(long delay, long interval, Runnable action) {
        long now = System.nanoTime();
        long wait = this.repeatMode ? interval : delay;
        if (now - this.repeatLastNanos < wait) {
            return;
        }
        int guard = 0;
        do {
            action.run();
            this.repeatLastNanos += wait;
            this.repeatMode = true;
            wait = interval;
            guard++;
        } while (now - this.repeatLastNanos >= wait && guard < 20);
    }

    private void tickLongClick(long delay, Runnable action) {
        if (this.longClickHandled || this.pressStartNanos == 0L) {
            return;
        }
        if (System.nanoTime() - this.pressStartNanos < delay) {
            return;
        }
        this.longClickHandled = true;
        action.run();
    }

    private void tickFlash() {
        if (this.flashTarget == null || this.flashStartNanos == 0L || this.flashPhaseCount <= 0) {
            this.flashVisible = false;
            return;
        }
        int phase = (int) ((System.nanoTime() - this.flashStartNanos) / FLASH_PHASE_NANOS);
        if (phase >= this.flashPhaseCount) {
            this.clearFlash();
            return;
        }
        this.flashVisible = phase % 2 == 0;
    }

    private void startFlash(Object target, int flashes) {
        this.flashTarget = target;
        this.flashPhaseCount = Math.max(0, flashes * 2);
        if (this.flashPhaseCount <= 0) {
            this.clearFlash();
            return;
        }
        this.flashStartNanos = System.nanoTime();
        this.flashVisible = true;
    }

    private void clearFlash() {
        this.flashTarget = null;
        this.flashStartNanos = 0L;
        this.flashPhaseCount = 0;
        this.flashVisible = false;
    }

    private void randomizeColor() {
        this.setColorFromRgb(java.util.concurrent.ThreadLocalRandom.current().nextInt(0x1000000));
    }

    private void stepArrow(ArrowButton arrow) {
        float step = switch (arrow.slider()) {
            case HUE -> 1.0F / 360.0F;
            case SATURATION, VALUE -> 1.0F / 100.0F;
            case RED, GREEN, BLUE -> 1.0F / 255.0F;
        };
        this.setSliderProgress(arrow.slider(), this.sliderProgress(arrow.slider()) + (arrow.up() ? step : -step));
    }

    private boolean injectHexFromClipboard() {
        String hex = this.clipboardHex();
        if (hex == null) {
            return false;
        }
        this.setColorFromRgb(Integer.parseInt(hex, 16));
        return true;
    }

    private boolean injectSliderFromClipboard(SliderId slider) {
        Integer value = this.clipboardNumber(slider.max);
        if (value == null) {
            return false;
        }
        this.setDisplayValue(slider, value);
        return true;
    }

    private String clipboardHex() {
        if (this.minecraft == null) {
            return null;
        }
        String value = this.minecraft.keyboardHandler.getClipboard();
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        if (value.length() != 6) {
            return null;
        }
        for (int i = 0; i < value.length(); i++) {
            if (Character.digit(value.charAt(i), 16) < 0) {
                return null;
            }
        }
        return value;
    }

    private Integer clipboardNumber(int max) {
        if (this.minecraft == null) {
            return null;
        }
        String value = this.minecraft.keyboardHandler.getClipboard();
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty() || value.length() > VALUE_SYMBOL_COUNT) {
            return null;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return null;
            }
        }
        int number = Integer.parseInt(value);
        return number <= max ? number : null;
    }

    private void copyHex() {
        if (this.minecraft != null) {
            this.minecraft.keyboardHandler.setClipboard("#" + toSixDigitHex(this.getPreviewRgb()));
        }
    }

    private void copySlider(SliderId slider) {
        if (this.minecraft != null) {
            this.minecraft.keyboardHandler.setClipboard(Integer.toString(this.displayValue(slider)));
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.activeSlider != null) {
            this.updateSliderFromMouse(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
        if (this.pressedTarget == HEX_TARGET || this.pressedTarget instanceof SliderId) {
            this.tickPressedTarget();
        }
        Object releasedTarget = this.pressedTarget;
        boolean handled = releasedTarget != null || this.activeSlider != null;
        if (!this.longClickHandled) {
            if (releasedTarget == HEX_TARGET) {
                this.copyHex();
                this.startFlash(HEX_TARGET, DOUBLE_FLASH);
            } else if (releasedTarget instanceof SliderId slider) {
                this.copySlider(slider);
                this.startFlash(slider, DOUBLE_FLASH);
            }
        }
        this.activeSlider = null;
        this.clearPress();
        this.flushColorSync();
        return handled || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        this.activeSlider = null;
        this.stopEditing();
        this.clearPress();
        this.clearFlash();
        this.flushColorSync();
        super.removed();
    }

    private void clearPress() {
        this.pressedTarget = null;
        this.pressStartNanos = 0L;
        this.repeatLastNanos = 0L;
        this.repeatMode = false;
        this.longClickHandled = false;
    }

    private void updateSliderFromMouse(double mouseX) {
        double sliderX = mouseX - this.leftPos - this.sliderGrabOffsetX;
        double clampedX = clamp(sliderX, this.activeSlider.sliderMinX(), this.activeSlider.sliderMaxX());
        double range = this.activeSlider.sliderMaxX() - this.activeSlider.sliderMinX();
        this.setSliderProgress(this.activeSlider, (float) ((clampedX - this.activeSlider.sliderMinX()) / range));
    }

    private boolean contains(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = this.leftPos + localX;
        int y = this.topPos + localY;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean isPressed(Object target) {
        return java.util.Objects.equals(this.pressedTarget, target);
    }

    private boolean isFlashing(Object target) {
        return this.isEditing(target) || (this.flashVisible && java.util.Objects.equals(this.flashTarget, target));
    }

    private int barOffset(SliderId slider) {
        return this.isPressed(slider) ? BAR_PRESSED_OFFSET_Y : 0;
    }

    private int getPreviewRgb() {
        return hsvToRgb(this.hue, this.saturation, this.value);
    }

    private int displayValue(SliderId slider) {
        int rgb = this.getPreviewRgb();
        return switch (slider) {
            case HUE -> clampInt(Math.round(this.hue * 360.0F), 0, 360);
            case SATURATION -> clampInt(Math.round(this.saturation * 100.0F), 0, 100);
            case VALUE -> clampInt(Math.round(this.value * 100.0F), 0, 100);
            case RED -> red(rgb);
            case GREEN -> green(rgb);
            case BLUE -> blue(rgb);
        };
    }

    private void setDisplayValue(SliderId slider, int displayValue) {
        displayValue = clampInt(displayValue, 0, slider.max);
        switch (slider) {
            case HUE -> {
                this.hue = displayValue / 360.0F;
                this.queueColorSync();
            }
            case SATURATION -> {
                this.saturation = displayValue / 100.0F;
                this.queueColorSync();
            }
            case VALUE -> {
                this.value = displayValue / 100.0F;
                this.queueColorSync();
            }
            case RED, GREEN, BLUE -> this.setSliderProgress(slider, displayValue / 255.0F);
        }
    }

    private float sliderProgress(SliderId slider) {
        int rgb = this.getPreviewRgb();
        return switch (slider) {
            case HUE -> this.hue;
            case SATURATION -> this.saturation;
            case VALUE -> this.value;
            case RED -> red(rgb) / 255.0F;
            case GREEN -> green(rgb) / 255.0F;
            case BLUE -> blue(rgb) / 255.0F;
        };
    }

    private void setSliderProgress(SliderId slider, float progress) {
        progress = clamp01(progress);
        switch (slider) {
            case HUE -> {
                this.hue = progress;
                this.queueColorSync();
            }
            case SATURATION -> {
                this.saturation = progress;
                this.queueColorSync();
            }
            case VALUE -> {
                this.value = progress;
                this.queueColorSync();
            }
            case RED, GREEN, BLUE -> this.setRgbComponent(slider, progress);
        }
    }

    private void setRgbComponent(SliderId slider, float progress) {
        int rgb = this.getPreviewRgb();
        int r = red(rgb);
        int g = green(rgb);
        int b = blue(rgb);
        int component = clamp255(Math.round(progress * 255.0F));
        switch (slider) {
            case RED -> r = component;
            case GREEN -> g = component;
            case BLUE -> b = component;
            default -> {
            }
        }
        this.setColorFromRgb(packRgb(r, g, b));
    }

    private void setColorFromRgb(int rgb) {
        this.applyColorFromRgb(rgb);
        this.queueColorSync();
    }

    private void applyColorFromRgb(int rgb) {
        HsvColor hsv = rgbToHsv(rgb);
        this.hue = hsv.hue();
        this.saturation = hsv.saturation();
        this.value = hsv.value();
    }

    private void queueColorSync() {
        int rgb = TintColorUtil.rgb(this.getPreviewRgb());
        this.menu.setHexColor(rgb);
        this.updateLocalHeldStack(rgb);
        this.pendingSyncRgb = rgb;
        this.flushColorSyncIfReady();
    }

    private void updateLocalHeldStack(int rgb) {
        ItemStack stack = this.getHeldStack();
        if (!stack.isEmpty() && stack.getItem() instanceof FlintAndPearlItem) {
            stack.set(ModDataComponents.HEX_COLOR.get(), TintColorUtil.rgb(rgb));
        }
    }

    private ItemStack getHeldStack() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return ItemStack.EMPTY;
        }
        return this.minecraft.player.getItemInHand(this.menu.getHand());
    }

    private void flushColorSyncIfReady() {
        if (this.pendingSyncRgb < 0 || System.nanoTime() - this.lastSyncNanos < COLOR_SYNC_INTERVAL_NANOS) {
            return;
        }
        this.flushColorSync();
    }

    private void flushColorSync() {
        if (this.pendingSyncRgb < 0 || this.pendingSyncRgb == this.lastSyncedRgb) {
            return;
        }
        this.lastSyncedRgb = this.pendingSyncRgb;
        this.lastSyncNanos = System.nanoTime();
        //? if >=1.21.7 {
        ClientPacketDistributor.sendToServer(new FlintAndPearlColorPayload(this.pendingSyncRgb));
        //?} else {
        /*PacketDistributor.sendToServer(
                new FlintAndPearlColorPayload(
                        this.pendingSyncRgb
                )
        );
        *///?}
    }

    private static HsvColor rgbToHsv(int rgb) {
        rgb = TintColorUtil.rgb(rgb);
        float r = red(rgb) / 255.0F;
        float g = green(rgb) / 255.0F;
        float b = blue(rgb) / 255.0F;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float h = 0.0F;
        if (delta > 0.00001F) {
            if (max == r) {
                h = ((g - b) / delta) / 6.0F;
            } else if (max == g) {
                h = (((b - r) / delta) + 2.0F) / 6.0F;
            } else {
                h = (((r - g) / delta) + 4.0F) / 6.0F;
            }
        }
        if (h < 0.0F) {
            h += 1.0F;
        }
        return new HsvColor(clamp01(h), max <= 0.0F ? 0.0F : clamp01(delta / max), clamp01(max));
    }

    private static int hsvToArgb(float h, float s, float v) {
        return 0xFF000000 | hsvToRgb(h, s, v);
    }

    private static int hsvToRgb(float h, float s, float v) {
        h = clamp01(h);
        s = clamp01(s);
        v = clamp01(v);
        float scaled = h * 6.0F;
        int sector = Math.min(5, (int) Math.floor(scaled));
        float f = scaled - sector;
        float p = v * (1.0F - s);
        float q = v * (1.0F - (s * f));
        float t = v * (1.0F - (s * (1.0F - f)));
        return switch (sector) {
            case 0 -> packRgb(v, t, p);
            case 1 -> packRgb(q, v, p);
            case 2 -> packRgb(p, v, t);
            case 3 -> packRgb(p, q, v);
            case 4 -> packRgb(t, p, v);
            default -> packRgb(v, p, q);
        };
    }

    private static String toSixDigitHex(int rgb) {
        String hex = Integer.toHexString(TintColorUtil.rgb(rgb));
        return "000000".substring(hex.length()) + hex;
    }

    private static int packRgb(float r, float g, float b) {
        return packRgb(Math.round(r * 255.0F), Math.round(g * 255.0F), Math.round(b * 255.0F));
    }

    private static int packRgb(int r, int g, int b) {
        return (clamp255(r) << 16) | (clamp255(g) << 8) | clamp255(b);
    }

    private static int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private static int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private static int blue(int rgb) {
        return rgb & 0xFF;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static int clamp255(int value) {
        return clampInt(value, 0, 255);
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
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

    private enum SliderId {
        HUE(139, 64, 360),
        SATURATION(0, 170, 100),
        VALUE(0, 212, 100),
        RED(139, 127, 255),
        GREEN(139, 169, 255),
        BLUE(139, 211, 255);
        private final int x;
        private final int y;
        private final int max;
        SliderId(int x, int y, int max) {
            this.x = x;
            this.y = y;
            this.max = max;
        }
        private int stripX() {
            return this.x + STRIP_X;
        }
        private int stripY() {
            return this.y + STRIP_Y;
        }
        private int sliderMinX() {
            return this.x + SLIDER_MIN_X;
        }
        private int sliderMaxX() {
            return this.x + SLIDER_MAX_X;
        }
        private int sliderY() {
            return this.y + SLIDER_Y;
        }
        private int sliderX(float progress) {
            return Math.round(this.sliderMinX() + ((this.sliderMaxX() - this.sliderMinX()) * clamp01(progress)));
        }
    }

    private record ArrowButton(SliderId slider, boolean up) {
        private int x() {
            return this.slider.x + ARROW_X;
        }
        private int y() {
            return this.slider.y + (this.up ? ARROW_UP_Y : ARROW_DOWN_Y);
        }
        private ResourceLocation texture(boolean pressed) {
            if (this.up) {
                return pressed ? ARROW_UP_PRESSED : ARROW_UP;
            }
            return pressed ? ARROW_DOWN_PRESSED : ARROW_DOWN;
        }
    }

    private record HsvColor(float hue, float saturation, float value) {} }