package space.anatomyuniverse.musavacca.gui.frontend;

//? if >=1.21.9 {
/*import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
*///?}
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else if >=1.21.2 <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///?}
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.InputConstants;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.gui.menu.payloads.FlintAndPearlColorPayload;
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlMenu;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public class FlintAndPearlFrontend extends AbstractContainerScreen<FlintAndPearlMenu> {
    private static final int HEX_TEXT_WIDTH = (6 - 1) * 8 + 7;
    private static final int HEX_TEXT_HEIGHT = 9 + (13 - 11);
    private static final int HEX_CARET_X = 25 + 6 * 8 - 1;
    private static final int VALUE_TEXT_WIDTH = (3 - 1) * 8 + 7;
    private static final int VALUE_TEXT_HEIGHT = 9 + 2;
    private static final int VALUE_CARET_X = 88 + 3 * 8 - 1;
    private static final int ARROW_X = 115 + 1;
    private static final Object PEARL_TARGET = new Object();
    private static final Object HEX_TARGET = new Object();
    //? if >=1.21.11 {
    /*private static final Identifier HEX_NORMAL = guiTexture("hex_bar");
    private static final Identifier HEX_PRESSED = guiTexture("hex_bar_pressed");
    private static final Identifier HEX_FLASH = guiTexture("hex_bar_flash");
    private static final Identifier HEX_PRESSED_FLASH = guiTexture("hex_bar_pressed_flash");
    private static final Identifier BAR_NORMAL = guiTexture("bar");
    private static final Identifier BAR_PRESSED = guiTexture("bar_pressed");
    private static final Identifier BAR_FLASH = guiTexture("bar_flash");
    private static final Identifier BAR_PRESSED_FLASH = guiTexture("bar_pressed_flash");
    private static final Identifier SLIDER = guiTexture("slider");
    private static final Identifier ARROW_UP = guiTexture("arrow_up");
    private static final Identifier ARROW_UP_PRESSED = guiTexture("arrow_up_pressed");
    private static final Identifier ARROW_DOWN = guiTexture("arrow_down");
    private static final Identifier ARROW_DOWN_PRESSED = guiTexture("arrow_down_pressed");
    private static final Identifier[] PEARL = textureArray("flint_and_pearl_", 13);
    private static final Identifier[] PEARL_PRESSED = textureArray("flint_and_pearl_pressed_", 13);
    private static final Identifier[] SYMBOLS = symbolArray();
    *///?} else {
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
    private static final ResourceLocation[] PEARL = textureArray("flint_and_pearl_", 13);
    private static final ResourceLocation[] PEARL_PRESSED = textureArray("flint_and_pearl_pressed_", 13);
    private static final ResourceLocation[] SYMBOLS = symbolArray();
    //?}
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
    private double sliderGrabOffsetX = 13 / 2.0D;
    public FlintAndPearlFrontend(FlintAndPearlMenu menu, Inventory playerInventory, Component title) {
        //? if >=26.1 {
        /*super(menu, playerInventory, title, 275, 256);
        *///?} else {
        super(menu, playerInventory, title);
        this.imageWidth = 275;
        this.imageHeight = 256;
        //?}
        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
        int initialRgb = MusavaccaTints.rgb(menu.getHexColor());
        this.applyColorFromRgb(initialRgb);
        this.lastSyncedRgb = initialRgb;
    }

    //? if >=1.21.11 {
    /*private static Identifier guiTexture(String name) {
        return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/" + name + ".png");
    *///?} else {
    private static ResourceLocation guiTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/" + name + ".png");
    //?}
    }

    //? if >=1.21.11 {
    /*private static Identifier symbolTexture(String name) {
        return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/symbols/" + name + ".png");
    *///?} else {
    private static ResourceLocation symbolTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/flintandpearl/symbols/" + name + ".png");
    //?}
    }

    //? if >=1.21.11 {
    /*private static Identifier[] textureArray(String prefix, int count) {
        Identifier[] textures = new Identifier[count];
    *///?} else {
    private static ResourceLocation[] textureArray(String prefix, int count) {
        ResourceLocation[] textures = new ResourceLocation[count];
    //?}
        for (int i = 0; i < count; i++) {
            textures[i] = guiTexture(prefix + i);
        }
        return textures;
    }

    //? if >=1.21.11 {
    /*private static Identifier[] symbolArray() {
        Identifier[] textures = new Identifier[16];
    *///?} else {
    private static ResourceLocation[] symbolArray() {
        ResourceLocation[] textures = new ResourceLocation[16];
    //?}
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

    //? if >=26.1 {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
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
    *///?}
    //? if <26.1 {
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
    //?}
    //? if >=26.1 {
    /*private void blitPearl(GuiGraphicsExtractor graphics, int guiX, int guiY, int rgb) {
    *///?} else {
    private void blitPearl(GuiGraphics graphics, int guiX, int guiY, int rgb) {
    //?}
        //? if >=1.21.11 {
        /*Identifier[] textures = this.isPressed(PEARL_TARGET) ? PEARL_PRESSED : PEARL;
        *///?} else {
        ResourceLocation[] textures = this.isPressed(PEARL_TARGET) ? PEARL_PRESSED : PEARL;
        //?}
        for (int layer = 0; layer < textures.length; layer++) {
            this.blitTinted(graphics, textures[layer], guiX + 5, guiY + 10, 124, 156,
                    PearlFireTintSource.profileTint(rgb, layer, PearlFireTintProfiles.FLINT_AND_PEARL));
        }
    }

    //? if >=26.1 {
    /*private void blitHex(GuiGraphicsExtractor graphics, int guiX, int guiY, int rgb) {
    *///?} else {
    private void blitHex(GuiGraphics graphics, int guiX, int guiY, int rgb) {
    //?}
        boolean pressed = this.isPressed(HEX_TARGET);
        boolean editing = this.isEditing(HEX_TARGET);
        int symbolY = guiY + 22 + (pressed ? 13 : 11);
        String hexText = editing ? this.editText : toSixDigitHex(rgb);
        this.blit(graphics, stateTexture(pressed, this.isFlashing(HEX_TARGET), HEX_NORMAL, HEX_PRESSED, HEX_FLASH, HEX_PRESSED_FLASH), guiX + 139, guiY + 22, 78,
                38);
        this.blitSymbols(graphics, guiX + 139 + 25, symbolY, hexText, 16, 6, editing);
        if (editing) {
            this.blitCaret(graphics, guiX + 139 + HEX_CARET_X, symbolY);
        }
    }

    //? if >=26.1 {
    /*private void blitSliderGroup(GuiGraphicsExtractor graphics, int guiX, int guiY, SliderId slider, int rgb) {
    *///?} else {
    private void blitSliderGroup(GuiGraphics graphics, int guiX, int guiY, SliderId slider, int rgb) {
    //?}
        int offsetY = this.barOffset(slider);
        int symbolY = guiY + slider.y + 12 + offsetY;
        boolean editing = this.isEditing(slider);
        String valueText = editing ? this.editText : Integer.toString(this.displayValue(slider));
        this.blitStrip(graphics, guiX + slider.stripX(), guiY + slider.stripY() + offsetY, slider, rgb);
        this.blit(graphics, stateTexture(this.isPressed(slider), this.isFlashing(slider), BAR_NORMAL, BAR_PRESSED, BAR_FLASH, BAR_PRESSED_FLASH),
                guiX + slider.x, guiY + slider.y, 115, 38);
        this.blitSymbols(graphics, guiX + slider.x + 88, symbolY, valueText, 10, 3, true);
        if (editing) {
            this.blitCaret(graphics, guiX + slider.x + VALUE_CARET_X, symbolY);
        }
        this.blit(graphics, SLIDER, guiX + slider.sliderX(this.sliderProgress(slider)), guiY + slider.sliderY() + offsetY, 13, 11);
    }

    //? if >=26.1 {
    /*private void blitCaret(GuiGraphicsExtractor graphics, int x, int y) {
    *///?} else {
    private void blitCaret(GuiGraphics graphics, int x, int y) {
    //?}
        if (!this.isCaretVisible()) return;
        graphics.fill(x, y + 0, x + 1, y + 0 + 9, 0xFF818181);
    }

    //? if >=26.1 {
    /*private void blitStrip(GuiGraphicsExtractor graphics, int x, int y, SliderId slider, int rgb) {
    *///?} else {
    private void blitStrip(GuiGraphics graphics, int x, int y, SliderId slider, int rgb) {
    //?}
        for (int column = 0; column < 76; column++) {
            float t = column / (float) (76 - 1);
            graphics.fill(x + column, y, x + column + 1, y + 13, this.stripColor(slider, t, rgb));
        }
    }

    //? if >=26.1 {
    /*private void blitSymbols(GuiGraphicsExtractor graphics, int x, int y, String text, int radix, int slots, boolean rightAligned) {
    *///?} else {
    private void blitSymbols(GuiGraphics graphics, int x, int y, String text, int radix, int slots, boolean rightAligned) {
    //?}
        int count = Math.min(text.length(), slots);
        int firstChar = text.length() - count;
        int firstSlot = rightAligned ? slots - count : 0;
        for (int i = 0; i < count; i++) {
            int value = Character.digit(text.charAt(firstChar + i), radix);
            if (value >= 0 && value < SYMBOLS.length) {
                this.blit(graphics, SYMBOLS[value], x + ((firstSlot + i) * 8), y, 7, 9);
            }
        }
    }

    //? if >=26.1 {
    /*private void blitArrow(GuiGraphicsExtractor graphics, int guiX, int guiY, ArrowButton arrow) {
    *///?} else {
    private void blitArrow(GuiGraphics graphics, int guiX, int guiY, ArrowButton arrow) {
    //?}
        this.blit(graphics, arrow.texture(this.isPressed(arrow)), guiX + arrow.x(), guiY + arrow.y(), 20, 19);
    }

    //? if >=1.21.11 {
    /*private static Identifier stateTexture(boolean pressed, boolean flashing, Identifier normal, Identifier pressedTexture, Identifier flash,
    *///?} else {
    private static ResourceLocation stateTexture(boolean pressed, boolean flashing, ResourceLocation normal, ResourceLocation pressedTexture,
    //?}
            //? if >=1.21.11 {
            /*Identifier pressedFlash) {
            *///?} else {
            ResourceLocation flash, ResourceLocation pressedFlash) {
            //?}
        if (pressed) return flashing ? pressedFlash : pressedTexture;
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

    //? if >=26.1 {
    /*private void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height) {
    *///?} else if >=1.21.11 <26.1 {
    /*private void blit(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height) {
    *///?} else {
    private void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
    //?}
        this.blitColored(graphics, texture, x, y, width, height, 0xFFFFFFFF);
    }

    //? if >=26.1 {
    /*private void blitTinted(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, int tint) {
    *///?} else if >=1.21.11 <26.1 {
    /*private void blitTinted(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height, int tint) {
    *///?} else {
    private void blitTinted(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int tint) {
    //?}
        this.blitColored(graphics, texture, x, y, width, height, tint == MusavaccaTints.NO_TINT ? 0xFFFFFFFF : 0xFF000000 | MusavaccaTints.rgb(tint));
    }

    //? if >=26.1 {
    /*private void blitColored(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, int color) {
    *///?} else if >=1.21.11 <26.1 {
    /*private void blitColored(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height, int color) {
    *///?} else {
    private void blitColored(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int color) {
    //?}
        //? if >=1.21.6 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, width, height, color);
        //?} else if >=1.21.2 <1.21.6 {
        /*graphics.blit(RenderType::guiTextured, texture, x, y, 0.0F, 0.0F, width, height, width, height, color);
        *///?} else {
        /*float alpha = ((color >> 24) & 0xFF) / 255.0F;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        graphics.setColor(red, green, blue, alpha);
        *///?}
        //? if <1.21.2 {
        /*graphics.blit(texture, x, y, 0.0F, 0.0F, width, height, width, height);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
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
        /*if (button != InputConstants.MOUSE_BUTTON_LEFT) return super.mouseClicked(event, doubleClick);
        *///?} else {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) return super.mouseClicked(mouseX, mouseY, button);
        //?}
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
            if (java.util.Objects.equals(this.lastTextClickTarget, textTarget)
                    && this.lastTextClickNanos != 0L
                    && now - this.lastTextClickNanos <= 250_000_000L) {
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
            this.press(mouseX, mouseY, HEX_TARGET, 139 + 25, 22 + 11, HEX_TEXT_WIDTH, HEX_TEXT_HEIGHT);
            return true;
        }
        if (textTarget instanceof SliderId slider) {
            this.press(mouseX, mouseY, slider, slider.x + 88, slider.y + 12, VALUE_TEXT_WIDTH, VALUE_TEXT_HEIGHT);
            return true;
        }
        if (this.press(mouseX, mouseY, PEARL_TARGET, 5, 10, 124, 156)) {
            this.randomizeColor();
            return true;
        }
        if (this.press(mouseX, mouseY, HEX_TARGET, 139, 22, 78, 38)) return true;
        for (ArrowButton arrow : ARROWS) {
            if (this.press(mouseX, mouseY, arrow, arrow.x(), arrow.y(), 20, 19)) {
                this.stepArrow(arrow);
                return true;
            }
        }
        for (SliderId slider : SliderId.values()) {
            if (this.tryStartSliderDrag(mouseX, mouseY, slider)) return true;
        }
        for (SliderId slider : SliderId.values()) {
            if (this.press(mouseX, mouseY, slider, slider.x, slider.y, 115, 38)) return true;
        }
        //? if >=1.21.9 {
        /*return super.mouseClicked(event, doubleClick);
        *///?} else {
        return super.mouseClicked(mouseX, mouseY, button);
        //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
    *///?}
    //? if >=1.21.9 {
        /*if (this.editingTarget == null) return super.keyPressed(event);
    *///?} else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.editingTarget == null) return super.keyPressed(keyCode, scanCode, modifiers);
    //?}
        if (keyCode == InputConstants.KEY_BACKSPACE) {
            if (!this.backspaceHeld) {
                this.backspaceHeld = true;
                this.backspaceRepeatMode = false;
                this.backspaceRepeatLastNanos = System.nanoTime();
                this.backspaceEditDigit();
            }
            return true;
        }
        if (keyCode == InputConstants.KEY_RETURN
                || keyCode == InputConstants.KEY_NUMPADENTER
                || keyCode == InputConstants.KEY_ESCAPE) {
            this.stopEditing();
            return true;
        }
        return true;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyReleased(KeyEvent event) {
        int keyCode = event.key();
    *///?}
    //? if >=1.21.9 {
    //?} else {
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    //?}
        if (keyCode == InputConstants.KEY_BACKSPACE && this.backspaceHeld) {
            this.clearBackspaceRepeat();
            return true;
        }
        //? if >=1.21.9 {
        /*return super.keyReleased(event);
        *///?} else {
        return super.keyReleased(keyCode, scanCode, modifiers);
        //?}
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
    *///?} else {
    public boolean charTyped(char codePoint, int modifiers) {
    //?}
        //? if >=1.21.9 {
        /*if (!Character.isBmpCodePoint(event.codepoint())) return super.charTyped(event);
        char codePoint = (char) event.codepoint();
        *///?}
        //? if >=1.21.9 {
        /*if (this.editingTarget == null) return super.charTyped(event);
        *///?} else {
        if (this.editingTarget == null) return super.charTyped(codePoint, modifiers);
        //?}
        int digit = Character.digit(codePoint, this.editRadix());
        //? if >=1.21.9 {
        /*if (digit < 0 || this.editText.length() >= this.editSymbolCount()) return true;
        *///?} else {
        if (digit < 0 || this.editText.length() >= this.editSymbolCount()) return true;
        //?}
        String candidate = this.editText + Character.forDigit(digit, this.editRadix());
        if (this.editingTarget instanceof SliderId slider
                && Integer.parseInt(candidate) > slider.max) {
            this.resetCaretBlink();
            //? if >=1.21.9 {
            /*return true;
            *///?} else {
            return true;
            //?}
        }
        this.editText = candidate;
        this.resetCaretBlink();
        this.applyEditText();
        //? if >=1.21.9 {
        /*return true;
        *///?} else {
        return true;
        //?}
    }

    private Object textTargetAt(double mouseX, double mouseY) {
        if (this.contains(mouseX, mouseY, 139 + 25, 22 + 11, HEX_TEXT_WIDTH, HEX_TEXT_HEIGHT)) return HEX_TARGET;
        for (SliderId slider : SliderId.values()) {
            if (this.contains(mouseX, mouseY, slider.x + 88, slider.y + 12, VALUE_TEXT_WIDTH, VALUE_TEXT_HEIGHT)) return slider;
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
        //? if >=26.3 {
        /*if (this.minecraft != null) {
            this.minecraft.onTextInputFocusChange(this, true);
        }
        *///?}
    }

    private void stopEditing() {
        //? if >=26.3 {
        /*if (this.editingTarget != null && this.minecraft != null) {
            this.minecraft.onTextInputFocusChange(this, false);
        }
        *///?}
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
        return this.editingTarget == HEX_TARGET ? 6 : 3;
    }

    private void applyEditText() {
        if (this.editText.isEmpty()) return;
        if (this.editingTarget == HEX_TARGET) {
            if (this.editText.length() == 6) {
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
        if (this.editingTarget == null || !this.backspaceHeld) return;
        long now = System.nanoTime();
        long wait = this.backspaceRepeatMode ? 35_000_000L : 280_000_000L;
        if (now - this.backspaceRepeatLastNanos < wait) return;
        int guard = 0;
        do {
            this.backspaceEditDigit();
            this.backspaceRepeatLastNanos += wait;
            this.backspaceRepeatMode = true;
            wait = 35_000_000L;
            guard++;
        } while (now - this.backspaceRepeatLastNanos >= wait
                && guard < this.editSymbolCount());
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
        return (System.nanoTime() - this.caretBlinkStartNanos) / 500_000_000L % 2L == 0L;
    }

    private boolean isEditing(Object target) {
        return java.util.Objects.equals(this.editingTarget, target);
    }

    private boolean press(double mouseX, double mouseY, Object target, int x, int y, int width, int height) {
        if (!this.contains(mouseX, mouseY, x, y, width, height)) return false;
        this.pressedTarget = target;
        this.pressStartNanos = System.nanoTime();
        this.repeatLastNanos = this.pressStartNanos;
        this.repeatMode = false;
        this.longClickHandled = false;
        return true;
    }

    private boolean tryStartSliderDrag(double mouseX, double mouseY, SliderId slider) {
        float progress = this.sliderProgress(slider);
        if (this.contains(mouseX, mouseY, slider.sliderX(progress), slider.sliderY(), 13, 11)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = mouseX - (this.leftPos + slider.sliderX(progress));
            return true;
        }
        int trackWidth = (slider.sliderMaxX() - slider.sliderMinX()) + 13;
        if (this.contains(mouseX, mouseY, slider.sliderMinX(), slider.sliderY(), trackWidth, 11)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = 13 / 2.0D;
            this.updateSliderFromMouse(mouseX);
            return true;
        }
        return false;
    }

    private void tickPressedTarget() {
        if (this.pressedTarget == PEARL_TARGET) {
            this.tickRepeat(220_000_000L, 115_000_000L, this::randomizeColor);
            return;
        }
        if (this.pressedTarget instanceof ArrowButton arrow) {
            this.tickRepeat(280_000_000L, 35_000_000L, () -> this.stepArrow(arrow));
            return;
        }
        if (this.pressedTarget == HEX_TARGET) {
            this.tickLongClick(500_000_000L, () -> {
                if (this.injectHexFromClipboard()) {
                    this.startFlash(HEX_TARGET, 1);
                }
            });
            return;
        }
        if (this.pressedTarget instanceof SliderId slider) {
            this.tickLongClick(500_000_000L, () -> {
                if (this.injectSliderFromClipboard(slider)) {
                    this.startFlash(slider, 1);
                }
            });
        }
    }

    private void tickRepeat(long delay, long interval, Runnable action) {
        long now = System.nanoTime();
        long wait = this.repeatMode ? interval : delay;
        if (now - this.repeatLastNanos < wait) return;
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
        if (this.longClickHandled || this.pressStartNanos == 0L) return;
        if (System.nanoTime() - this.pressStartNanos < delay) return;
        this.longClickHandled = true;
        action.run();
    }

    private void tickFlash() {
        if (this.flashTarget == null
                || this.flashStartNanos == 0L
                || this.flashPhaseCount <= 0) {
            this.flashVisible = false;
            return;
        }
        int phase = (int) ((System.nanoTime() - this.flashStartNanos) / 75_000_000L);
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
        float step = switch (arrow.slider()) { case HUE -> 1.0F / 360.0F; case SATURATION, VALUE -> 1.0F / 100.0F; case RED, GREEN, BLUE -> 1.0F / 255.0F; };
        this.setSliderProgress(arrow.slider(), this.sliderProgress(arrow.slider()) + (arrow.up() ? step : -step));
    }

    private boolean injectHexFromClipboard() {
        String hex = this.clipboardHex();
        if (hex == null) return false;
        this.setColorFromRgb(Integer.parseInt(hex, 16));
        return true;
    }

    private boolean injectSliderFromClipboard(SliderId slider) {
        Integer value = this.clipboardNumber(slider.max);
        if (value == null) return false;
        this.setDisplayValue(slider, value);
        return true;
    }

    private String clipboardHex() {
        if (this.minecraft == null) return null;
        String value = this.minecraft.keyboardHandler.getClipboard();
        if (value == null) return null;
        value = value.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        if (value.length() != 6) return null;
        for (int i = 0; i < value.length(); i++) {
            if (Character.digit(value.charAt(i), 16) < 0) return null;
        }
        return value;
    }

    private Integer clipboardNumber(int max) {
        if (this.minecraft == null) return null;
        String value = this.minecraft.keyboardHandler.getClipboard();
        if (value == null) return null;
        value = value.trim();
        if (value.isEmpty() || value.length() > 3) return null;
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return null;
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
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
    *///?} else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //?}
        //? if >=1.21.9 {
        /*double mouseX = event.x();

        int button = event.button();
        *///?}
        if (button == InputConstants.MOUSE_BUTTON_LEFT && this.activeSlider != null) {
            this.updateSliderFromMouse(mouseX);
            return true;
        }
        //? if >=1.21.9 {
        /*return super.mouseDragged(event, dragX, dragY);
        *///?} else {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
        if (button != InputConstants.MOUSE_BUTTON_LEFT) return super.mouseReleased(event);
        *///?} else {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) return super.mouseReleased(mouseX, mouseY, button);
        //?}
        if (this.pressedTarget == HEX_TARGET
                || this.pressedTarget instanceof SliderId) {
            this.tickPressedTarget();
        }
        Object releasedTarget = this.pressedTarget;
        boolean handled = releasedTarget != null || this.activeSlider != null;
        if (!this.longClickHandled) {
            if (releasedTarget == HEX_TARGET) {
                this.copyHex();
                this.startFlash(HEX_TARGET, 2);
            } else if (releasedTarget instanceof SliderId slider) {
                this.copySlider(slider);
                this.startFlash(slider, 2);
            }
        }
        this.activeSlider = null;
        this.clearPress();
        this.flushColorSync();
        //? if >=1.21.9 {
        /*return handled || super.mouseReleased(event);
        *///?} else {
        return handled || super.mouseReleased(mouseX, mouseY, button);
        //?}
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
        return this.isPressed(slider) ? 2 : 0;
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
            case RED, GREEN, BLUE ->
                    this.setSliderProgress(slider, displayValue / 255.0F);
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
            case RED, GREEN, BLUE ->
                    this.setRgbComponent(slider, progress);
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
        int rgb = MusavaccaTints.rgb(this.getPreviewRgb());
        this.menu.setHexColor(rgb);
        this.updateLocalHeldStack(rgb);
        this.pendingSyncRgb = rgb;
        this.flushColorSyncIfReady();
    }

    private void updateLocalHeldStack(int rgb) {
        ItemStack stack = this.getHeldStack();
        if (!stack.isEmpty()
                && stack.getItem() instanceof FlintAndPearlItem) {
            stack.set(ModDataComponents.HEX_COLOR.get(), MusavaccaTints.rgb(rgb));
        }
    }

    private ItemStack getHeldStack() {
        if (this.minecraft == null || this.minecraft.player == null) return ItemStack.EMPTY;
        return this.minecraft.player.getItemInHand(this.menu.getHand());
    }

    private void flushColorSyncIfReady() {
        if (this.pendingSyncRgb < 0
                || System.nanoTime() - this.lastSyncNanos
                < 50_000_000L) {
            return;
        }
        this.flushColorSync();
    }

    private void flushColorSync() {
        if (this.pendingSyncRgb < 0
                || this.pendingSyncRgb == this.lastSyncedRgb) {
            return;
        }
        this.lastSyncedRgb = this.pendingSyncRgb;
        this.lastSyncNanos = System.nanoTime();
        //? if >=1.21.6 {
        ClientPacketDistributor.sendToServer(new FlintAndPearlColorPayload(this.pendingSyncRgb));
        //?} else {
        /*PacketDistributor.sendToServer(new FlintAndPearlColorPayload(this.pendingSyncRgb));
        *///?}
    }

    private static HsvColor rgbToHsv(int rgb) {
        rgb = MusavaccaTints.rgb(rgb);
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
        String hex = Integer.toHexString(MusavaccaTints.rgb(rgb));
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

    //? if >=26.1 {
    /*@Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }
    *///?}
    //? if <26.1 {
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
    //?}
    //? if >=26.1 {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
    *///?}
    //? if <26.1 {
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    //?}
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
            return this.x + 10;
        }

        private int stripY() {
            return this.y + 10;
        }

        private int sliderMinX() {
            return this.x + 4;
        }

        private int sliderMaxX() {
            return this.x + 79;
        }

        private int sliderY() {
            return this.y + 21;
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
            return this.slider.y + (this.up ? (-1) : 20);
        }

        //? if >=1.21.11 {
        /*private Identifier texture(boolean pressed) {
        *///?} else {
        private ResourceLocation texture(boolean pressed) {
        //?}
            if (this.up) return pressed ? ARROW_UP_PRESSED : ARROW_UP;
            return pressed ? ARROW_DOWN_PRESSED : ARROW_DOWN;
        }
    }

    private record HsvColor(float hue, float saturation, float value) {
    }
}
