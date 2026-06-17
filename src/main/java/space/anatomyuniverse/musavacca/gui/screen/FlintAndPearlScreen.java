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
    private static final int GUI_SIZE = 256;
    private static final int LAYER_COUNT = 13;

    private static final int BAR_WIDTH = 115;
    private static final int BAR_HEIGHT = 38;

    private static final int STRIP_LOCAL_X = 13;
    private static final int STRIP_LOCAL_Y = 10;
    private static final int STRIP_WIDTH = 76;
    private static final int STRIP_HEIGHT = 13;

    private static final int SLIDER_WIDTH = 13;
    private static final int SLIDER_HEIGHT = 11;
    private static final int SLIDER_LOCAL_MIN_X = 7;
    private static final int SLIDER_LOCAL_MAX_X = 82;
    private static final int SLIDER_LOCAL_Y = 21;

    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 19;
    private static final int ARROW_LOCAL_X = BAR_WIDTH + 1;
    private static final int ARROW_UP_LOCAL_Y = -1;
    private static final int ARROW_DOWN_LOCAL_Y = 20;

    private static final SliderBar HUE_BAR = new SliderBar(138, 87);
    private static final SliderBar SATURATION_BAR = new SliderBar(111, 157);
    private static final SliderBar VALUE_BAR = new SliderBar(70, 202);

    private static final ResourceLocation BAR_TEXTURE = guiTexture("bar");
    private static final ResourceLocation SLIDER_TEXTURE = guiTexture("slider");

    private static final ResourceLocation ARROW_UP_TEXTURE = guiTexture("arrow_up");
    private static final ResourceLocation ARROW_UP_PRESSED_TEXTURE = guiTexture("arrow_up_pressed");
    private static final ResourceLocation ARROW_DOWN_TEXTURE = guiTexture("arrow_down");
    private static final ResourceLocation ARROW_DOWN_PRESSED_TEXTURE = guiTexture("arrow_down_pressed");

    private static final ResourceLocation[] LAYER_TEXTURES = createLayerTextures();

    private static final ArrowButton[] CLICKABLE_ARROWS = new ArrowButton[] {
            ArrowButton.SATURATION_UP,
            ArrowButton.SATURATION_DOWN,
            ArrowButton.VALUE_UP,
            ArrowButton.VALUE_DOWN
    };

    private float hueProgress;
    private float saturationProgress;
    private float valueProgress;

    private ActiveSlider activeSlider = ActiveSlider.NONE;
    private ArrowButton pressedArrow = ArrowButton.NONE;

    private double sliderGrabOffsetX = SLIDER_WIDTH / 2.0D;

    public FlintAndPearlScreen(FlintAndPearlMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = GUI_SIZE;
        this.imageHeight = GUI_SIZE;

        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;

        HsvColor initialColor = rgbToHsv(menu.getHexColor());

        this.hueProgress = initialColor.hue();
        this.saturationProgress = initialColor.saturation();
        this.valueProgress = initialColor.value();
    }

    private static ResourceLocation guiTexture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "textures/gui/flintandpearl/" + fileName + ".png"
        );
    }

    private static ResourceLocation[] createLayerTextures() {
        ResourceLocation[] textures = new ResourceLocation[LAYER_COUNT];

        for (int layer = 0; layer < LAYER_COUNT; layer++) {
            textures[layer] = guiTexture("flint_and_pearl_" + layer);
        }

        return textures;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int guiX = this.leftPos;
        int guiY = this.topPos;
        int previewRgb = this.getPreviewRgb();

        this.blitPearlLayers(graphics, guiX, guiY, previewRgb);

        this.renderHueStrip(graphics, guiX + HUE_BAR.stripX(), guiY + HUE_BAR.stripY());
        this.renderSaturationStrip(graphics, guiX + SATURATION_BAR.stripX(), guiY + SATURATION_BAR.stripY());
        this.renderValueStrip(graphics, guiX + VALUE_BAR.stripX(), guiY + VALUE_BAR.stripY());

        this.blitBar(graphics, guiX, guiY, HUE_BAR);
        this.blitBar(graphics, guiX, guiY, SATURATION_BAR);
        this.blitBar(graphics, guiX, guiY, VALUE_BAR);

        this.blitSlider(graphics, guiX, guiY, HUE_BAR, this.hueProgress);
        this.blitSlider(graphics, guiX, guiY, SATURATION_BAR, this.saturationProgress);
        this.blitSlider(graphics, guiX, guiY, VALUE_BAR, this.valueProgress);

        this.blitArrow(graphics, guiX, guiY, ArrowButton.SATURATION_UP);
        this.blitArrow(graphics, guiX, guiY, ArrowButton.SATURATION_DOWN);
        this.blitArrow(graphics, guiX, guiY, ArrowButton.VALUE_UP);
        this.blitArrow(graphics, guiX, guiY, ArrowButton.VALUE_DOWN);
    }

    private void blitPearlLayers(GuiGraphics graphics, int guiX, int guiY, int baseRgb) {
        for (int layer = 0; layer < LAYER_TEXTURES.length; layer++) {
            int tint = PearlFireTintSource.profileTint(
                    baseRgb,
                    layer,
                    PearlFireTintProfiles.FLINT_AND_PEARL
            );

            this.blitTintedTexture(
                    graphics,
                    LAYER_TEXTURES[layer],
                    guiX,
                    guiY,
                    GUI_SIZE,
                    GUI_SIZE,
                    tint
            );
        }
    }

    private void renderHueStrip(GuiGraphics graphics, int x, int y) {
        for (int column = 0; column < STRIP_WIDTH; column++) {
            float hue = column / (float) (STRIP_WIDTH - 1);
            int color = hsvToArgb(hue, 1.0F, 1.0F);

            graphics.fill(
                    x + column,
                    y,
                    x + column + 1,
                    y + STRIP_HEIGHT,
                    color
            );
        }
    }

    private void renderSaturationStrip(GuiGraphics graphics, int x, int y) {
        for (int column = 0; column < STRIP_WIDTH; column++) {
            float saturation = column / (float) (STRIP_WIDTH - 1);
            int color = hsvToArgb(this.hueProgress, saturation, this.valueProgress);

            graphics.fill(
                    x + column,
                    y,
                    x + column + 1,
                    y + STRIP_HEIGHT,
                    color
            );
        }
    }

    private void renderValueStrip(GuiGraphics graphics, int x, int y) {
        for (int column = 0; column < STRIP_WIDTH; column++) {
            float value = column / (float) (STRIP_WIDTH - 1);
            int color = hsvToArgb(this.hueProgress, this.saturationProgress, value);

            graphics.fill(
                    x + column,
                    y,
                    x + column + 1,
                    y + STRIP_HEIGHT,
                    color
            );
        }
    }

    private void blitBar(GuiGraphics graphics, int guiX, int guiY, SliderBar bar) {
        this.blitPlainTexture(
                graphics,
                BAR_TEXTURE,
                guiX + bar.x(),
                guiY + bar.y(),
                BAR_WIDTH,
                BAR_HEIGHT
        );
    }

    private void blitSlider(GuiGraphics graphics, int guiX, int guiY, SliderBar bar, float progress) {
        this.blitPlainTexture(
                graphics,
                SLIDER_TEXTURE,
                guiX + bar.sliderX(progress),
                guiY + bar.sliderY(),
                SLIDER_WIDTH,
                SLIDER_HEIGHT
        );
    }

    private void blitArrow(GuiGraphics graphics, int guiX, int guiY, ArrowButton arrow) {
        ResourceLocation texture = arrow.texture(this.pressedArrow == arrow);

        this.blitPlainTexture(
                graphics,
                texture,
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
        if (button == 0 && this.tryPressArrow(mouseX, mouseY)) {
            return true;
        }

        if (button == 0 && this.tryStartSliderDrag(mouseX, mouseY, HUE_BAR, ActiveSlider.HUE)) {
            return true;
        }

        if (button == 0 && this.tryStartSliderDrag(mouseX, mouseY, SATURATION_BAR, ActiveSlider.SATURATION)) {
            return true;
        }

        if (button == 0 && this.tryStartSliderDrag(mouseX, mouseY, VALUE_BAR, ActiveSlider.VALUE)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean tryPressArrow(double mouseX, double mouseY) {
        for (ArrowButton arrow : CLICKABLE_ARROWS) {
            if (this.isMouseInsideArrow(mouseX, mouseY, arrow)) {
                this.pressedArrow = arrow;
                return true;
            }
        }

        return false;
    }

    private boolean tryStartSliderDrag(double mouseX, double mouseY, SliderBar bar, ActiveSlider slider) {
        float progress = this.getSliderProgress(slider);

        if (this.isMouseInsideSlider(mouseX, mouseY, bar, progress)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = mouseX - (this.leftPos + bar.sliderX(progress));
            return true;
        }

        if (this.isMouseInsideSliderTrack(mouseX, mouseY, bar)) {
            this.activeSlider = slider;
            this.sliderGrabOffsetX = SLIDER_WIDTH / 2.0D;
            this.updateSliderFromMouse(mouseX);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.activeSlider != ActiveSlider.NONE) {
            this.updateSliderFromMouse(mouseX);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.pressedArrow != ArrowButton.NONE) {
            this.pressedArrow = ArrowButton.NONE;
            return true;
        }

        if (button == 0 && this.activeSlider != ActiveSlider.NONE) {
            this.activeSlider = ActiveSlider.NONE;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        this.activeSlider = ActiveSlider.NONE;
        this.pressedArrow = ArrowButton.NONE;
        super.removed();
    }

    private void updateSliderFromMouse(double mouseX) {
        SliderBar bar = this.activeSlider.bar();

        if (bar == null) {
            return;
        }

        double localSliderX = mouseX - this.leftPos - this.sliderGrabOffsetX;
        double clampedX = clamp(localSliderX, bar.sliderMinX(), bar.sliderMaxX());
        float progress = (float) ((clampedX - bar.sliderMinX()) / (double) (bar.sliderMaxX() - bar.sliderMinX()));

        this.setSliderProgress(this.activeSlider, progress);
    }

    private boolean isMouseInsideSlider(double mouseX, double mouseY, SliderBar bar, float progress) {
        int sliderX = this.leftPos + bar.sliderX(progress);
        int sliderY = this.topPos + bar.sliderY();

        return mouseX >= sliderX
                && mouseX < sliderX + SLIDER_WIDTH
                && mouseY >= sliderY
                && mouseY < sliderY + SLIDER_HEIGHT;
    }

    private boolean isMouseInsideSliderTrack(double mouseX, double mouseY, SliderBar bar) {
        int trackX = this.leftPos + bar.sliderMinX();
        int trackY = this.topPos + bar.sliderY();
        int trackWidth = (bar.sliderMaxX() - bar.sliderMinX()) + SLIDER_WIDTH;

        return mouseX >= trackX
                && mouseX < trackX + trackWidth
                && mouseY >= trackY
                && mouseY < trackY + SLIDER_HEIGHT;
    }

    private boolean isMouseInsideArrow(double mouseX, double mouseY, ArrowButton arrow) {
        int arrowX = this.leftPos + arrow.x();
        int arrowY = this.topPos + arrow.y();

        return mouseX >= arrowX
                && mouseX < arrowX + ARROW_WIDTH
                && mouseY >= arrowY
                && mouseY < arrowY + ARROW_HEIGHT;
    }

    private int getPreviewRgb() {
        return hsvToRgb(this.hueProgress, this.saturationProgress, this.valueProgress);
    }

    private float getSliderProgress(ActiveSlider slider) {
        return switch (slider) {
            case HUE -> this.hueProgress;
            case SATURATION -> this.saturationProgress;
            case VALUE -> this.valueProgress;
            case NONE -> 0.0F;
        };
    }

    private void setSliderProgress(ActiveSlider slider, float progress) {
        progress = clamp01(progress);

        switch (slider) {
            case HUE -> this.hueProgress = progress;
            case SATURATION -> this.saturationProgress = progress;
            case VALUE -> this.valueProgress = progress;
            case NONE -> {
            }
        }
    }

    private static HsvColor rgbToHsv(int rgb) {
        rgb = TintColorUtil.rgb(rgb);

        float red = ((rgb >> 16) & 0xFF) / 255.0F;
        float green = ((rgb >> 8) & 0xFF) / 255.0F;
        float blue = (rgb & 0xFF) / 255.0F;

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

        float red;
        float green;
        float blue;

        switch (sector) {
            case 0 -> {
                red = value;
                green = t;
                blue = p;
            }
            case 1 -> {
                red = q;
                green = value;
                blue = p;
            }
            case 2 -> {
                red = p;
                green = value;
                blue = t;
            }
            case 3 -> {
                red = p;
                green = q;
                blue = value;
            }
            case 4 -> {
                red = t;
                green = p;
                blue = value;
            }
            default -> {
                red = value;
                green = p;
                blue = q;
            }
        }

        return (clamp255(Math.round(red * 255.0F)) << 16)
                | (clamp255(Math.round(green * 255.0F)) << 8)
                | clamp255(Math.round(blue * 255.0F));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Intentionally empty: this GUI is visual-only for now.
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private enum ActiveSlider {
        NONE(null),
        HUE(HUE_BAR),
        SATURATION(SATURATION_BAR),
        VALUE(VALUE_BAR);

        private final SliderBar bar;

        ActiveSlider(SliderBar bar) {
            this.bar = bar;
        }

        private SliderBar bar() {
            return this.bar;
        }
    }

    private enum ArrowButton {
        NONE(null, false),
        SATURATION_UP(SATURATION_BAR, true),
        SATURATION_DOWN(SATURATION_BAR, false),
        VALUE_UP(VALUE_BAR, true),
        VALUE_DOWN(VALUE_BAR, false);

        private final SliderBar bar;
        private final boolean up;

        ArrowButton(SliderBar bar, boolean up) {
            this.bar = bar;
            this.up = up;
        }

        private int x() {
            return this.bar.x() + ARROW_LOCAL_X;
        }

        private int y() {
            return this.bar.y() + (this.up ? ARROW_UP_LOCAL_Y : ARROW_DOWN_LOCAL_Y);
        }

        private ResourceLocation texture(boolean pressed) {
            if (this.up) {
                return pressed ? ARROW_UP_PRESSED_TEXTURE : ARROW_UP_TEXTURE;
            }

            return pressed ? ARROW_DOWN_PRESSED_TEXTURE : ARROW_DOWN_TEXTURE;
        }
    }

    private record SliderBar(int x, int y) {
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

    private record HsvColor(float hue, float saturation, float value) {}
}