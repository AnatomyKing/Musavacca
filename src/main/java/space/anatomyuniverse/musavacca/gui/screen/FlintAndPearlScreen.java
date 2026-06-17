package space.anatomyuniverse.musavacca.gui.screen;

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
import space.anatomyuniverse.musavacca.gui.menu.FlintAndPearlMenu;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class FlintAndPearlScreen extends AbstractContainerScreen<FlintAndPearlMenu> {
    private static final int GUI_WIDTH = 275;
    private static final int GUI_HEIGHT = 256;

    private static final int PEARL_X = 5;
    private static final int PEARL_Y = 10;
    private static final int PEARL_WIDTH = 124;
    private static final int PEARL_HEIGHT = 156;
    private static final int PEARL_LAYER_COUNT = 13;

    private static final int HEX_BAR_X = 139;
    private static final int HEX_BAR_Y = 22;
    private static final int HEX_BAR_WIDTH = 78;
    private static final int HEX_BAR_HEIGHT = 38;

    private static final int HEX_SYMBOL_WIDTH = 7;
    private static final int HEX_SYMBOL_HEIGHT = 9;
    private static final int HEX_SYMBOL_START_X = 25;
    private static final int HEX_SYMBOL_Y = 11;
    private static final int HEX_SYMBOL_PRESSED_Y = 13;
    private static final int HEX_SYMBOL_SPACING = 8;
    private static final int HEX_SYMBOL_COUNT = 6;

    private static final long HEX_FLASH_PHASE_NANOS = 75_000_000L;
    private static final long HEX_LONG_CLICK_NANOS = 500_000_000L;
    private static final int HEX_SINGLE_FLASH_COUNT = 1;
    private static final int HEX_DOUBLE_FLASH_COUNT = 2;

    private static final int BAR_WIDTH = 115;
    private static final int BAR_HEIGHT = 38;

    private static final int STRIP_LOCAL_X = 10;
    private static final int STRIP_LOCAL_Y = 10;
    private static final int STRIP_WIDTH = 76;
    private static final int STRIP_HEIGHT = 13;

    private static final int VALUE_SYMBOL_WIDTH = 7;
    private static final int VALUE_SYMBOL_HEIGHT = 9;
    private static final int VALUE_SYMBOL_START_X = 88;
    private static final int VALUE_SYMBOL_Y = 12;
    private static final int VALUE_SYMBOL_SPACING = 8;
    private static final int VALUE_SYMBOL_COUNT = 3;

    private static final int SLIDER_WIDTH = 13;
    private static final int SLIDER_HEIGHT = 11;
    private static final int SLIDER_LOCAL_MIN_X = 4;
    private static final int SLIDER_LOCAL_MAX_X = 79;
    private static final int SLIDER_LOCAL_Y = 21;

    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 19;
    private static final int ARROW_LOCAL_X = BAR_WIDTH + 1;
    private static final int ARROW_UP_LOCAL_Y = -1;
    private static final int ARROW_DOWN_LOCAL_Y = 20;

    private static final long ARROW_REPEAT_DELAY_NANOS = 280_000_000L;
    private static final long ARROW_REPEAT_INTERVAL_NANOS = 35_000_000L;

    private static final ResourceLocation HEX_BAR_TEXTURE = guiTexture("hex_bar");
    private static final ResourceLocation HEX_BAR_PRESSED_TEXTURE = guiTexture("hex_bar_pressed");
    private static final ResourceLocation HEX_BAR_FLASH_TEXTURE = guiTexture("hex_bar_flash");
    private static final ResourceLocation HEX_BAR_PRESSED_FLASH_TEXTURE = guiTexture("hex_bar_pressed_flash");

    private static final ResourceLocation BAR_TEXTURE = guiTexture("bar");
    private static final ResourceLocation SLIDER_TEXTURE = guiTexture("slider");

    private static final ResourceLocation ARROW_UP_TEXTURE = guiTexture("arrow_up");
    private static final ResourceLocation ARROW_UP_PRESSED_TEXTURE = guiTexture("arrow_up_pressed");
    private static final ResourceLocation ARROW_DOWN_TEXTURE = guiTexture("arrow_down");
    private static final ResourceLocation ARROW_DOWN_PRESSED_TEXTURE = guiTexture("arrow_down_pressed");

    private static final ResourceLocation[] PEARL_TEXTURES = createPearlTextures("flint_and_pearl_");
    private static final ResourceLocation[] PEARL_PRESSED_TEXTURES = createPearlTextures("flint_and_pearl_pressed_");
    private static final ResourceLocation[] SYMBOL_TEXTURES = createSymbolTextures();
    private static final ArrowButton[] ARROW_BUTTONS = createArrowButtons();

    private float hue;
    private float saturation;
    private float value;

    private SliderId activeSlider = null;
    private ArrowButton pressedArrow = null;

    private boolean pearlPressed = false;
    private boolean hexBarPressed = false;
    private boolean hexFlashVisible = false;
    private boolean hexLongClickHandled = false;
    private boolean pressedArrowRepeating = false;

    private long hexBarPressStartNanos = 0L;
    private long hexFlashStartNanos = 0L;
    private int hexFlashPhaseCount = 0;

    private long pressedArrowLastUpdateNanos = 0L;
    private double sliderGrabOffsetX = SLIDER_WIDTH / 2.0D;

    public FlintAndPearlScreen(FlintAndPearlMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;

        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;

        HsvColor initial = rgbToHsv(menu.getHexColor());

        this.hue = initial.hue();
        this.saturation = initial.saturation();
        this.value = initial.value();
    }

    private static ResourceLocation guiTexture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "textures/gui/flintandpearl/" + fileName + ".png"
        );
    }

    private static ResourceLocation symbolTexture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "textures/gui/flintandpearl/symbols/" + fileName + ".png"
        );
    }

    private static ResourceLocation[] createPearlTextures(String prefix) {
        ResourceLocation[] textures = new ResourceLocation[PEARL_LAYER_COUNT];

        for (int layer = 0; layer < textures.length; layer++) {
            textures[layer] = guiTexture(prefix + layer);
        }

        return textures;
    }

    private static ResourceLocation[] createSymbolTextures() {
        ResourceLocation[] textures = new ResourceLocation[16];

        for (int value = 0; value < textures.length; value++) {
            textures[value] = symbolTexture(Integer.toHexString(value));
        }

        return textures;
    }

    private static ArrowButton[] createArrowButtons() {
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
        this.updatePressedArrow();
        this.updateHexBarHold();
        this.updateHexFlash();

        int guiX = this.leftPos;
        int guiY = this.topPos;
        int previewRgb = this.getPreviewRgb();

        this.blitPearl(graphics, guiX, guiY, previewRgb);
        this.blitHexBar(graphics, guiX, guiY);
        this.blitHexSymbols(graphics, guiX, guiY, previewRgb);

        for (SliderId slider : SliderId.values()) {
            this.renderStrip(graphics, guiX, guiY, slider, previewRgb);
            this.blitBar(graphics, guiX, guiY, slider);
            this.blitValueSymbols(graphics, guiX, guiY, slider);
            this.blitSlider(graphics, guiX, guiY, slider);
        }

        for (ArrowButton arrow : ARROW_BUTTONS) {
            this.blitArrow(graphics, guiX, guiY, arrow);
        }
    }

    private void blitPearl(GuiGraphics graphics, int guiX, int guiY, int rgb) {
        ResourceLocation[] textures = this.pearlPressed ? PEARL_PRESSED_TEXTURES : PEARL_TEXTURES;

        for (int layer = 0; layer < textures.length; layer++) {
            int tint = PearlFireTintSource.profileTint(
                    rgb,
                    layer,
                    PearlFireTintProfiles.FLINT_AND_PEARL
            );

            this.blitTintedTexture(
                    graphics,
                    textures[layer],
                    guiX + PEARL_X,
                    guiY + PEARL_Y,
                    PEARL_WIDTH,
                    PEARL_HEIGHT,
                    tint
            );
        }
    }

    private void blitHexBar(GuiGraphics graphics, int guiX, int guiY) {
        this.blitPlainTexture(
                graphics,
                this.getHexBarTexture(),
                guiX + HEX_BAR_X,
                guiY + HEX_BAR_Y,
                HEX_BAR_WIDTH,
                HEX_BAR_HEIGHT
        );
    }

    private ResourceLocation getHexBarTexture() {
        if (this.hexBarPressed) {
            return this.hexFlashVisible ? HEX_BAR_PRESSED_FLASH_TEXTURE : HEX_BAR_PRESSED_TEXTURE;
        }

        return this.hexFlashVisible ? HEX_BAR_FLASH_TEXTURE : HEX_BAR_TEXTURE;
    }

    private void blitHexSymbols(GuiGraphics graphics, int guiX, int guiY, int rgb) {
        String hex = toSixDigitHex(rgb);
        int symbolY = this.hexBarPressed ? HEX_SYMBOL_PRESSED_Y : HEX_SYMBOL_Y;

        for (int i = 0; i < HEX_SYMBOL_COUNT; i++) {
            this.blitSymbol(
                    graphics,
                    guiX + HEX_BAR_X + HEX_SYMBOL_START_X + (i * HEX_SYMBOL_SPACING),
                    guiY + HEX_BAR_Y + symbolY,
                    Character.digit(hex.charAt(i), 16),
                    HEX_SYMBOL_WIDTH,
                    HEX_SYMBOL_HEIGHT
            );
        }
    }

    private void blitValueSymbols(GuiGraphics graphics, int guiX, int guiY, SliderId slider) {
        String text = Integer.toString(this.getDisplayValue(slider));
        int count = Math.min(text.length(), VALUE_SYMBOL_COUNT);
        int startSlot = VALUE_SYMBOL_COUNT - count;
        int firstChar = text.length() - count;

        for (int i = 0; i < count; i++) {
            int slot = startSlot + i;
            int digit = Character.digit(text.charAt(firstChar + i), 10);

            this.blitSymbol(
                    graphics,
                    guiX + slider.x + VALUE_SYMBOL_START_X + (slot * VALUE_SYMBOL_SPACING),
                    guiY + slider.y + VALUE_SYMBOL_Y,
                    digit,
                    VALUE_SYMBOL_WIDTH,
                    VALUE_SYMBOL_HEIGHT
            );
        }
    }

    private void blitSymbol(GuiGraphics graphics, int x, int y, int value, int width, int height) {
        if (value < 0 || value >= SYMBOL_TEXTURES.length) {
            return;
        }

        this.blitPlainTexture(graphics, SYMBOL_TEXTURES[value], x, y, width, height);
    }

    private void renderStrip(GuiGraphics graphics, int guiX, int guiY, SliderId slider, int previewRgb) {
        int x = guiX + slider.stripX();
        int y = guiY + slider.stripY();

        for (int column = 0; column < STRIP_WIDTH; column++) {
            float t = column / (float) (STRIP_WIDTH - 1);

            graphics.fill(
                    x + column,
                    y,
                    x + column + 1,
                    y + STRIP_HEIGHT,
                    this.getStripColor(slider, t, previewRgb)
            );
        }
    }

    private int getStripColor(SliderId slider, float t, int rgb) {
        return switch (slider) {
            case HUE -> hsvToArgb(t, 1.0F, 1.0F);
            case SATURATION -> hsvToArgb(this.hue, t, this.value);
            case VALUE -> hsvToArgb(this.hue, this.saturation, t);
            case RED -> 0xFF000000 | packRgb(Math.round(t * 255.0F), green(rgb), blue(rgb));
            case GREEN -> 0xFF000000 | packRgb(red(rgb), Math.round(t * 255.0F), blue(rgb));
            case BLUE -> 0xFF000000 | packRgb(red(rgb), green(rgb), Math.round(t * 255.0F));
        };
    }

    private void blitBar(GuiGraphics graphics, int guiX, int guiY, SliderId slider) {
        this.blitPlainTexture(
                graphics,
                BAR_TEXTURE,
                guiX + slider.x,
                guiY + slider.y,
                BAR_WIDTH,
                BAR_HEIGHT
        );
    }

    private void blitSlider(GuiGraphics graphics, int guiX, int guiY, SliderId slider) {
        this.blitPlainTexture(
                graphics,
                SLIDER_TEXTURE,
                guiX + slider.sliderX(this.getSliderProgress(slider)),
                guiY + slider.sliderY(),
                SLIDER_WIDTH,
                SLIDER_HEIGHT
        );
    }

    private void blitArrow(GuiGraphics graphics, int guiX, int guiY, ArrowButton arrow) {
        this.blitPlainTexture(
                graphics,
                arrow.texture(arrow.equals(this.pressedArrow)),
                guiX + arrow.x(),
                guiY + arrow.y(),
                ARROW_WIDTH,
                ARROW_HEIGHT
        );
    }

    private void blitPlainTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height
    ) {
        this.blitColoredTexture(graphics, texture, x, y, width, height, 0xFFFFFFFF);
    }

    private void blitTintedTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int tint
    ) {
        int color = tint == TintColorUtil.NO_TINT
                ? 0xFFFFFFFF
                : 0xFF000000 | TintColorUtil.rgb(tint);

        this.blitColoredTexture(graphics, texture, x, y, width, height, color);
    }

    private void blitColoredTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height,
            int color
    ) {
        //? if >=1.21.6 {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x,
                y,
                0.0F,
                0.0F,
                width,
                height,
                width,
                height,
                color
        );
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
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (this.tryPressPearl(mouseX, mouseY)
                || this.tryPressHexBar(mouseX, mouseY)
                || this.tryPressArrow(mouseX, mouseY)) {
            return true;
        }

        for (SliderId slider : SliderId.values()) {
            if (this.tryStartSliderDrag(mouseX, mouseY, slider)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean tryPressPearl(double mouseX, double mouseY) {
        if (!this.contains(mouseX, mouseY, PEARL_X, PEARL_Y, PEARL_WIDTH, PEARL_HEIGHT)) {
            return false;
        }

        this.pearlPressed = true;
        this.randomizeColor();
        return true;
    }

    private void randomizeColor() {
        this.setColorFromRgb(java.util.concurrent.ThreadLocalRandom.current().nextInt(0x1000000));
    }

    private boolean tryPressHexBar(double mouseX, double mouseY) {
        if (!this.contains(mouseX, mouseY, HEX_BAR_X, HEX_BAR_Y, HEX_BAR_WIDTH, HEX_BAR_HEIGHT)) {
            return false;
        }

        this.hexBarPressed = true;
        this.hexBarPressStartNanos = System.nanoTime();
        this.hexLongClickHandled = false;
        return true;
    }

    private void updateHexBarHold() {
        if (!this.hexBarPressed || this.hexLongClickHandled || this.hexBarPressStartNanos == 0L) {
            return;
        }

        if (System.nanoTime() - this.hexBarPressStartNanos < HEX_LONG_CLICK_NANOS) {
            return;
        }

        this.hexLongClickHandled = true;

        if (this.tryInjectHexFromClipboard()) {
            this.startHexFlash(HEX_SINGLE_FLASH_COUNT);
        }
    }

    private void copyHexToClipboard() {
        if (this.minecraft == null) {
            return;
        }

        this.minecraft.keyboardHandler.setClipboard("#" + toSixDigitHex(this.getPreviewRgb()));
    }

    private boolean tryInjectHexFromClipboard() {
        if (this.minecraft == null) {
            return false;
        }

        String hex = normalizeClipboardHex(this.minecraft.keyboardHandler.getClipboard());

        if (hex == null) {
            return false;
        }

        this.setColorFromRgb(Integer.parseInt(hex, 16));
        return true;
    }

    private static String normalizeClipboardHex(String raw) {
        if (raw == null) {
            return null;
        }

        String value = raw.trim();

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

    private void setColorFromRgb(int rgb) {
        HsvColor hsv = rgbToHsv(rgb);

        this.hue = hsv.hue();
        this.saturation = hsv.saturation();
        this.value = hsv.value();
    }

    private void startHexFlash(int flashCount) {
        this.hexFlashPhaseCount = Math.max(0, flashCount * 2);

        if (this.hexFlashPhaseCount <= 0) {
            this.hexFlashStartNanos = 0L;
            this.hexFlashVisible = false;
            return;
        }

        this.hexFlashStartNanos = System.nanoTime();
        this.hexFlashVisible = true;
    }

    private void updateHexFlash() {
        if (this.hexFlashStartNanos == 0L || this.hexFlashPhaseCount <= 0) {
            this.hexFlashVisible = false;
            return;
        }

        int phase = (int) ((System.nanoTime() - this.hexFlashStartNanos) / HEX_FLASH_PHASE_NANOS);

        if (phase >= this.hexFlashPhaseCount) {
            this.hexFlashStartNanos = 0L;
            this.hexFlashPhaseCount = 0;
            this.hexFlashVisible = false;
            return;
        }

        this.hexFlashVisible = phase % 2 == 0;
    }

    private boolean tryPressArrow(double mouseX, double mouseY) {
        for (ArrowButton arrow : ARROW_BUTTONS) {
            if (this.contains(mouseX, mouseY, arrow.x(), arrow.y(), ARROW_WIDTH, ARROW_HEIGHT)) {
                this.pressedArrow = arrow;
                this.pressedArrowLastUpdateNanos = System.nanoTime();
                this.pressedArrowRepeating = false;
                this.applyArrowStep(arrow);
                return true;
            }
        }

        return false;
    }

    private boolean tryStartSliderDrag(double mouseX, double mouseY, SliderId slider) {
        float progress = this.getSliderProgress(slider);

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

    private boolean contains(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = this.leftPos + localX;
        int y = this.topPos + localY;

        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
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

        if (this.hexBarPressed) {
            this.updateHexBarHold();
        }

        boolean handled = this.pearlPressed
                || this.hexBarPressed
                || this.pressedArrow != null
                || this.activeSlider != null;

        if (this.hexBarPressed && !this.hexLongClickHandled) {
            this.copyHexToClipboard();
            this.startHexFlash(HEX_DOUBLE_FLASH_COUNT);
        }

        this.pearlPressed = false;

        this.hexBarPressed = false;
        this.hexBarPressStartNanos = 0L;
        this.hexLongClickHandled = false;

        this.activeSlider = null;
        this.clearPressedArrow();

        return handled || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        this.pearlPressed = false;

        this.hexBarPressed = false;
        this.hexLongClickHandled = false;
        this.hexBarPressStartNanos = 0L;

        this.hexFlashVisible = false;
        this.hexFlashStartNanos = 0L;
        this.hexFlashPhaseCount = 0;

        this.activeSlider = null;
        this.clearPressedArrow();

        super.removed();
    }

    private void updateSliderFromMouse(double mouseX) {
        if (this.activeSlider == null) {
            return;
        }

        double localSliderX = mouseX - this.leftPos - this.sliderGrabOffsetX;
        double clampedX = clamp(localSliderX, this.activeSlider.sliderMinX(), this.activeSlider.sliderMaxX());
        float progress = (float) ((clampedX - this.activeSlider.sliderMinX())
                / (double) (this.activeSlider.sliderMaxX() - this.activeSlider.sliderMinX()));

        this.setSliderProgress(this.activeSlider, progress);
    }

    private void updatePressedArrow() {
        if (this.pressedArrow == null) {
            return;
        }

        long now = System.nanoTime();
        long waitTime = this.pressedArrowRepeating ? ARROW_REPEAT_INTERVAL_NANOS : ARROW_REPEAT_DELAY_NANOS;

        if (now - this.pressedArrowLastUpdateNanos < waitTime) {
            return;
        }

        int guard = 0;

        do {
            this.applyArrowStep(this.pressedArrow);
            this.pressedArrowLastUpdateNanos += waitTime;
            this.pressedArrowRepeating = true;
            waitTime = ARROW_REPEAT_INTERVAL_NANOS;
            guard++;
        } while (now - this.pressedArrowLastUpdateNanos >= waitTime && guard < 20);
    }

    private void applyArrowStep(ArrowButton arrow) {
        float step = this.getArrowStep(arrow.slider());

        if (!arrow.up()) {
            step = -step;
        }

        this.setSliderProgress(arrow.slider(), this.getSliderProgress(arrow.slider()) + step);
    }

    private float getArrowStep(SliderId slider) {
        return switch (slider) {
            case HUE -> 1.0F / 360.0F;
            case SATURATION, VALUE -> 1.0F / 100.0F;
            case RED, GREEN, BLUE -> 1.0F / 255.0F;
        };
    }

    private void clearPressedArrow() {
        this.pressedArrow = null;
        this.pressedArrowLastUpdateNanos = 0L;
        this.pressedArrowRepeating = false;
    }

    private int getPreviewRgb() {
        return hsvToRgb(this.hue, this.saturation, this.value);
    }

    private int getDisplayValue(SliderId slider) {
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

    private float getSliderProgress(SliderId slider) {
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
            case HUE -> this.hue = progress;
            case SATURATION -> this.saturation = progress;
            case VALUE -> this.value = progress;
            case RED, GREEN, BLUE -> this.setRgbProgress(slider, progress);
        }
    }

    private void setRgbProgress(SliderId slider, float progress) {
        int rgb = this.getPreviewRgb();

        int red = red(rgb);
        int green = green(rgb);
        int blue = blue(rgb);
        int component = clamp255(Math.round(progress * 255.0F));

        switch (slider) {
            case RED -> red = component;
            case GREEN -> green = component;
            case BLUE -> blue = component;
            default -> {
            }
        }

        HsvColor hsv = rgbToHsv(packRgb(red, green, blue));

        this.hue = hsv.hue();
        this.saturation = hsv.saturation();
        this.value = hsv.value();
    }

    private static HsvColor rgbToHsv(int rgb) {
        rgb = TintColorUtil.rgb(rgb);

        float red = red(rgb) / 255.0F;
        float green = green(rgb) / 255.0F;
        float blue = blue(rgb) / 255.0F;

        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;

        float hue;

        if (delta <= 0.00001F) {
            hue = 0.0F;
        } else if (max == red) {
            hue = ((green - blue) / delta) / 6.0F;
        } else if (max == green) {
            hue = (((blue - red) / delta) + 2.0F) / 6.0F;
        } else {
            hue = (((red - green) / delta) + 4.0F) / 6.0F;
        }

        if (hue < 0.0F) {
            hue += 1.0F;
        }

        float saturation = max <= 0.0F ? 0.0F : delta / max;
        float value = max;

        return new HsvColor(clamp01(hue), clamp01(saturation), clamp01(value));
    }

    private static int hsvToArgb(float hue, float saturation, float value) {
        return 0xFF000000 | hsvToRgb(hue, saturation, value);
    }

    private static int hsvToRgb(float hue, float saturation, float value) {
        hue = clamp01(hue);
        saturation = clamp01(saturation);
        value = clamp01(value);

        float scaled = hue * 6.0F;
        int sector = Math.min(5, (int) Math.floor(scaled));
        float fraction = scaled - sector;

        float p = value * (1.0F - saturation);
        float q = value * (1.0F - (saturation * fraction));
        float t = value * (1.0F - (saturation * (1.0F - fraction)));

        return switch (sector) {
            case 0 -> packRgb(value, t, p);
            case 1 -> packRgb(q, value, p);
            case 2 -> packRgb(p, value, t);
            case 3 -> packRgb(p, q, value);
            case 4 -> packRgb(t, p, value);
            default -> packRgb(value, p, q);
        };
    }

    private static String toSixDigitHex(int rgb) {
        String hex = Integer.toHexString(TintColorUtil.rgb(rgb));

        return "000000".substring(hex.length()) + hex;
    }

    private static int packRgb(float red, float green, float blue) {
        return packRgb(
                clamp255(Math.round(red * 255.0F)),
                clamp255(Math.round(green * 255.0F)),
                clamp255(Math.round(blue * 255.0F))
        );
    }

    private static int packRgb(int red, int green, int blue) {
        return (clamp255(red) << 16)
                | (clamp255(green) << 8)
                | clamp255(blue);
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
        HUE(139, 64),
        SATURATION(0, 170),
        VALUE(0, 212),
        RED(139, 127),
        GREEN(139, 169),
        BLUE(139, 211);

        private final int x;
        private final int y;

        SliderId(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int stripX() {
            return this.x + STRIP_LOCAL_X;
        }

        private int stripY() {
            return this.y + STRIP_LOCAL_Y;
        }

        private int sliderMinX() {
            return this.x + SLIDER_LOCAL_MIN_X;
        }

        private int sliderMaxX() {
            return this.x + SLIDER_LOCAL_MAX_X;
        }

        private int sliderY() {
            return this.y + SLIDER_LOCAL_Y;
        }

        private int sliderX(float progress) {
            return Math.round(this.sliderMinX() + ((this.sliderMaxX() - this.sliderMinX()) * clamp01(progress)));
        }
    }

    private record ArrowButton(SliderId slider, boolean up) {
        private int x() {
            return this.slider.x + ARROW_LOCAL_X;
        }

        private int y() {
            return this.slider.y + (this.up ? ARROW_UP_LOCAL_Y : ARROW_DOWN_LOCAL_Y);
        }

        private ResourceLocation texture(boolean pressed) {
            if (this.up) {
                return pressed ? ARROW_UP_PRESSED_TEXTURE : ARROW_UP_TEXTURE;
            }

            return pressed ? ARROW_DOWN_PRESSED_TEXTURE : ARROW_DOWN_TEXTURE;
        }
    }

    private record HsvColor(float hue, float saturation, float value) {}
}