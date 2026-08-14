package space.anatomyuniverse.musavacca.gui.frontend;

import com.mojang.blaze3d.platform.NativeImage;
//? if <1.21.6
//import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.VocoCallerMenu;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Objects;

public final class VocoCallerFrontend
        extends AbstractContainerScreen<VocoCallerMenu> {

    private static final int GUI_WIDTH = 157;
    private static final int GUI_HEIGHT = 164;

    private static final int BASE_WIDTH = 157;
    private static final int BASE_HEIGHT = 164;

    private static final int PHONE_SCREEN_WIDTH = 126;
    private static final int PHONE_SCREEN_HEIGHT = 206;

    private static final int BUTTON_BAR_WIDTH = 157;
    private static final int BUTTON_BAR_HEIGHT = 39;

    private static final int GAP = 3;

    private static final int PHONE_SCREEN_X =
            -PHONE_SCREEN_WIDTH - GAP;

    private static final int PHONE_SCREEN_Y =
            -BUTTON_BAR_HEIGHT - GAP;

    private static final int BUTTON_BAR_X = 0;

    private static final int BUTTON_BAR_Y =
            -BUTTON_BAR_HEIGHT - GAP;

    private static final int BUTTON_Y =
            BUTTON_BAR_Y + 8;

    private static final int BUTTON_HEIGHT = 20;

    private static final int ENTRY_COUNT_PER_COLUMN = 13;

    private static final int SELECTION_COUNT =
            ENTRY_COUNT_PER_COLUMN * 2;

    private static final int RECENT_CALLS_CARET_X =
            PHONE_SCREEN_X + 19;

    private static final int SAVED_NUMBERS_CARET_X =
            PHONE_SCREEN_X + 72;

    private static final int CARET_START_Y =
            PHONE_SCREEN_Y + 59;

    private static final int CARET_ROW_STEP = 9;

    private static final int CARET_WIDTH = 2;
    private static final int CARET_HEIGHT = 7;

    private static final int CARET_COLOR =
            0xFFCFBF5F;

    private static final long CARET_BLINK_PHASE_NANOS =
            500_000_000L;

    private static final long ARROW_REPEAT_DELAY_NANOS =
            280_000_000L;

    private static final long ARROW_REPEAT_INTERVAL_NANOS =
            35_000_000L;

    private static final int RECENT_CALLS_ENTRY_X =
            RECENT_CALLS_CARET_X + 4;

    private static final int SAVED_NUMBERS_ENTRY_X =
            SAVED_NUMBERS_CARET_X + 4;

    private static final int ENTRY_START_Y =
            CARET_START_Y;

    private static final int ENTRY_WIDTH = 25;
    private static final int ENTRY_HEIGHT = 7;

    private static final int SYMBOL_WIDTH = 5;
    private static final int SYMBOL_HEIGHT = 7;

    private static final int SYMBOL_STEP = 4;

    private static final int HEX_CODE_LENGTH = 6;

    /*
     * The complete two-column hex-code area is cached in ONE texture.
     *
     * Recent:
     * x = 0
     *
     * Saved:
     * x = 53
     *
     * 53 + 25 = 78
     *
     * 13 rows:
     *
     * (12 * 9) + 7 = 115
     */
    private static final int RECENT_TEXTURE_X = 0;

    private static final int SAVED_TEXTURE_X =
            SAVED_NUMBERS_ENTRY_X
                    - RECENT_CALLS_ENTRY_X;

    private static final int HEX_LIST_DRAW_WIDTH =
            SAVED_TEXTURE_X + ENTRY_WIDTH;

    private static final int HEX_LIST_DRAW_HEIGHT =
            ((ENTRY_COUNT_PER_COLUMN - 1)
                    * CARET_ROW_STEP)
                    + ENTRY_HEIGHT;

    /*
     * Keep the GPU texture power-of-two sized.
     *
     * Only the top-left 78x115 area is actually drawn.
     */
    private static final int HEX_LIST_TEXTURE_WIDTH = 128;
    private static final int HEX_LIST_TEXTURE_HEIGHT = 128;

    private static final int ALL_ROWS_DIRTY =
            (1 << ENTRY_COUNT_PER_COLUMN) - 1;

    private static final ResourceLocation BASE_TEXTURE =
            guiTexture("base");

    private static final ResourceLocation PHONE_SCREEN_TEXTURE =
            guiTexture("phone_screen");

    private static final ResourceLocation BUTTON_BAR_TEXTURE =
            guiTexture("button_bar");

    private static final ResourceLocation ARROW_LEFT_TEXTURE =
            guiTexture("arrow_left");

    private static final ResourceLocation ARROW_LEFT_PRESSED_TEXTURE =
            guiTexture("arrow_left_pressed");

    private static final ResourceLocation SPACE_BUTTON_TEXTURE =
            guiTexture("space_button");

    private static final ResourceLocation SPACE_BUTTON_PRESSED_TEXTURE =
            guiTexture("space_button_pressed");

    private static final ResourceLocation ARROW_RIGHT_TEXTURE =
            guiTexture("arrow_right");

    private static final ResourceLocation ARROW_RIGHT_PRESSED_TEXTURE =
            guiTexture("arrow_right_pressed");

    /*
     * Dynamic texture.
     *
     * It does NOT need to exist as a PNG resource.
     */
    private static final ResourceLocation HEX_LIST_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "voco_caller/hex_list"
            );

    /*
     * Static test data for now.
     *
     * Later these can be replaced at any moment through:
     *
     * setRecentCall(...)
     * setSavedNumber(...)
     *
     * Only changed rows are rebuilt.
     */
    private final String[] recentCalls = new String[] {
            "AB2DE9",
            "45AF10",
            "C8293D",
            "7E114B",
            "09FF42",
            "D481AC",
            "602BE7",
            "F19D30",
            "3480CE",
            "BDA512",
            "72C8F4",
            "E00391",
            "5FAC26"
    };

    private final String[] savedNumbers = new String[] {
            "12ABEF",
            "C041D8",
            "93E52A",
            "0F72BC",
            "DDA903",
            "48C617",
            "AE304F",
            "7591DB",
            "E6B820",
            "31FD95",
            "BC4701",
            "8A2EC3",
            "F0547D"
    };

    /*
     * These glyphs are created ONCE when the class loads.
     *
     * They are NOT reconstructed every frame.
     */
    private static final Glyph[] GLYPHS = new Glyph[] {
            Glyph.of(
                    "#####",
                    "#...#",
                    "#.#.#",
                    "#.#.#",
                    "#.#.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    ".###.",
                    "##.#.",
                    "#..#.",
                    "##.#.",
                    "##.##",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "###.#",
                    "#...#",
                    "#.###",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "###.#",
                    "#...#",
                    "###.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#.#.#",
                    "#.#.#",
                    "#...#",
                    "###.#",
                    "..#.#",
                    "..###"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.###",
                    "#...#",
                    "###.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.###",
                    "#...#",
                    "#.#.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "###.#",
                    ".#.##",
                    ".#.#.",
                    ".#.#.",
                    ".###."
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.#.#",
                    "#...#",
                    "#.#.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.#.#",
                    "#...#",
                    "###.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.#.#",
                    "#...#",
                    "#.#.#",
                    "#.#.#",
                    "#####"
            ),

            Glyph.of(
                    "####.",
                    "#..##",
                    "#.#.#",
                    "#..##",
                    "#.#.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.#.#",
                    "#.###",
                    "#.#.#",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "####.",
                    "#..##",
                    "#.#.#",
                    "#.#.#",
                    "#.#.#",
                    "#..##",
                    "####."
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.###",
                    "#..#.",
                    "#.###",
                    "#...#",
                    "#####"
            ),

            Glyph.of(
                    "#####",
                    "#...#",
                    "#.###",
                    "#..#.",
                    "#.##.",
                    "#.#..",
                    "###.."
            )
    };

    private CallerButton pressedButton = null;

    private int selectedPosition = 0;

    private long caretBlinkStartNanos =
            System.nanoTime();

    private long buttonRepeatLastNanos = 0L;

    private boolean buttonRepeatMode = false;

    /*
     * Cached GPU representation of ALL 26 hex codes.
     */
    private DynamicTexture hexListDynamicTexture = null;

    private NativeImage hexListPixels = null;

    /*
     * Bit 0 = row 0 dirty
     * Bit 1 = row 1 dirty
     * ...
     * Bit 12 = row 12 dirty
     *
     * This means changing one code only rebuilds that one 25x7 section.
     */
    private int recentDirtyRows =
            ALL_ROWS_DIRTY;

    private int savedDirtyRows =
            ALL_ROWS_DIRTY;

    public VocoCallerFrontend(
            VocoCallerMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(
                menu,
                playerInventory,
                title
        );

        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;

        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        this.ensureHexListTexture();
        this.uploadHexListTextureIfDirty();
    }

    private static ResourceLocation guiTexture(
            String fileName
    ) {
        return ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "textures/gui/voco_caller/"
                        + fileName
                        + ".png"
        );
    }

    /*
     * ============================================================
     * Dynamic list data
     * ============================================================
     */

    public void setRecentCall(
            int row,
            String hexCode
    ) {
        this.setHexEntry(
                this.recentCalls,
                true,
                row,
                hexCode
        );
    }

    public void setRecentCall(
            int row,
            int hexColor
    ) {
        this.setRecentCall(
                row,
                formatHexColor(hexColor)
        );
    }

    public void setSavedNumber(
            int row,
            String hexCode
    ) {
        this.setHexEntry(
                this.savedNumbers,
                false,
                row,
                hexCode
        );
    }

    public void setSavedNumber(
            int row,
            int hexColor
    ) {
        this.setSavedNumber(
                row,
                formatHexColor(hexColor)
        );
    }

    public void clearRecentCall(
            int row
    ) {
        this.setRecentCall(
                row,
                (String) null
        );
    }

    public void clearSavedNumber(
            int row
    ) {
        this.setSavedNumber(
                row,
                (String) null
        );
    }

    private void setHexEntry(
            String[] entries,
            boolean recent,
            int row,
            String hexCode
    ) {
        this.checkRow(row);

        String normalized =
                normalizeHexCode(hexCode);

        if (Objects.equals(
                entries[row],
                normalized
        )) {
            return;
        }

        entries[row] =
                normalized;

        if (recent) {
            this.recentDirtyRows |=
                    1 << row;
        } else {
            this.savedDirtyRows |=
                    1 << row;
        }
    }

    private void checkRow(
            int row
    ) {
        if (row < 0
                || row >= ENTRY_COUNT_PER_COLUMN) {
            throw new IndexOutOfBoundsException(
                    "Voco Caller row must be between 0 and "
                            + (ENTRY_COUNT_PER_COLUMN - 1)
                            + ", got "
                            + row
            );
        }
    }

    private static String normalizeHexCode(
            String hexCode
    ) {
        if (hexCode == null) {
            return null;
        }

        String value =
                hexCode.trim();

        if (value.startsWith("#")) {
            value =
                    value.substring(1);
        }

        if (value.length() != HEX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Hex code must contain exactly 6 hexadecimal digits: "
                            + hexCode
            );
        }

        for (int i = 0;
             i < HEX_CODE_LENGTH;
             i++) {

            if (Character.digit(
                    value.charAt(i),
                    16
            ) < 0) {
                throw new IllegalArgumentException(
                        "Invalid hexadecimal code: "
                                + hexCode
                );
            }
        }

        return value.toUpperCase(
                Locale.ROOT
        );
    }

    private static String formatHexColor(
            int hexColor
    ) {
        return String.format(
                Locale.ROOT,
                "%06X",
                hexColor & 0xFFFFFF
        );
    }

    /*
     * ============================================================
     * Dynamic texture cache
     * ============================================================
     */

    private void ensureHexListTexture() {
        if (this.hexListDynamicTexture != null) {
            return;
        }

        if (this.minecraft == null) {
            return;
        }

        //? if >=1.21.5 {
        this.hexListDynamicTexture =
                new DynamicTexture(
                        "Musavacca Voco Caller Hex List",
                        HEX_LIST_TEXTURE_WIDTH,
                        HEX_LIST_TEXTURE_HEIGHT,
                        false
                );
        //?} else {
        /*this.hexListDynamicTexture =
                new DynamicTexture(
                        HEX_LIST_TEXTURE_WIDTH,
                        HEX_LIST_TEXTURE_HEIGHT,
                        false
                );
        *///?}

        this.hexListPixels =
                this.hexListDynamicTexture
                        .getPixels();

        if (this.hexListPixels == null) {
            throw new IllegalStateException(
                    "Voco Caller dynamic hex-list texture has no pixel image"
            );
        }

        this.minecraft
                .getTextureManager()
                .register(
                        HEX_LIST_TEXTURE,
                        this.hexListDynamicTexture
                );

        this.clearWholeHexListTexture();

        this.recentDirtyRows =
                ALL_ROWS_DIRTY;

        this.savedDirtyRows =
                ALL_ROWS_DIRTY;
    }

    private void clearWholeHexListTexture() {
        if (this.hexListPixels == null) {
            return;
        }

        for (int y = 0;
             y < HEX_LIST_TEXTURE_HEIGHT;
             y++) {

            for (int x = 0;
                 x < HEX_LIST_TEXTURE_WIDTH;
                 x++) {

                this.hexListPixels.setPixel(
                        x,
                        y,
                        0x00000000
                );
            }
        }
    }

    private void uploadHexListTextureIfDirty() {
        if (this.recentDirtyRows == 0
                && this.savedDirtyRows == 0) {
            return;
        }

        this.ensureHexListTexture();

        if (this.hexListDynamicTexture == null
                || this.hexListPixels == null) {
            return;
        }

        int recentMask =
                this.recentDirtyRows;

        int savedMask =
                this.savedDirtyRows;

        this.recentDirtyRows = 0;
        this.savedDirtyRows = 0;

        this.updateDirtyColumn(
                recentMask,
                this.recentCalls,
                RECENT_TEXTURE_X
        );

        this.updateDirtyColumn(
                savedMask,
                this.savedNumbers,
                SAVED_TEXTURE_X
        );

        /*
         * ONE upload regardless of whether 1 row or all 26 rows changed.
         */
        this.hexListDynamicTexture
                .upload();
    }

    private void updateDirtyColumn(
            int dirtyRows,
            String[] entries,
            int columnX
    ) {
        if (this.hexListPixels == null
                || dirtyRows == 0) {
            return;
        }

        for (int row = 0;
             row < ENTRY_COUNT_PER_COLUMN;
             row++) {

            int bit =
                    1 << row;

            if ((dirtyRows & bit) == 0) {
                continue;
            }

            int y =
                    row * CARET_ROW_STEP;

            this.clearEntryPixels(
                    columnX,
                    y
            );

            String hexCode =
                    entries[row];

            if (hexCode == null) {
                continue;
            }

            this.drawHexCodeToTexture(
                    columnX,
                    y,
                    hexCode
            );
        }
    }

    private void clearEntryPixels(
            int x,
            int y
    ) {
        if (this.hexListPixels == null) {
            return;
        }

        for (int pixelY = 0;
             pixelY < ENTRY_HEIGHT;
             pixelY++) {

            for (int pixelX = 0;
                 pixelX < ENTRY_WIDTH;
                 pixelX++) {

                this.hexListPixels.setPixel(
                        x + pixelX,
                        y + pixelY,
                        0x00000000
                );
            }
        }
    }

    private void drawHexCodeToTexture(
            int x,
            int y,
            String hexCode
    ) {
        if (this.hexListPixels == null) {
            return;
        }

        int rgb =
                parseHexColor(
                        hexCode
                );

        int fillColor =
                0xFF000000 | rgb;

        int outlineColor =
                matchingOutlineColor(
                        rgb
                );

        /*
         * Fill all six glyph interiors first.
         *
         * Symbols overlap by one pixel, so outlines are intentionally
         * rendered in a second pass.
         */
        for (int i = 0;
             i < HEX_CODE_LENGTH;
             i++) {

            int glyphIndex =
                    Character.digit(
                            hexCode.charAt(i),
                            16
                    );

            if (glyphIndex < 0
                    || glyphIndex >= GLYPHS.length) {
                continue;
            }

            this.drawMaskToTexture(
                    x + i * SYMBOL_STEP,
                    y,
                    GLYPHS[glyphIndex]
                            .fillRows(),
                    fillColor
            );
        }

        /*
         * Matching outline last.
         */
        for (int i = 0;
             i < HEX_CODE_LENGTH;
             i++) {

            int glyphIndex =
                    Character.digit(
                            hexCode.charAt(i),
                            16
                    );

            if (glyphIndex < 0
                    || glyphIndex >= GLYPHS.length) {
                continue;
            }

            this.drawMaskToTexture(
                    x + i * SYMBOL_STEP,
                    y,
                    GLYPHS[glyphIndex]
                            .outlineRows(),
                    outlineColor
            );
        }
    }

    private void drawMaskToTexture(
            int x,
            int y,
            int[] rows,
            int color
    ) {
        if (this.hexListPixels == null) {
            return;
        }

        for (int row = 0;
             row < SYMBOL_HEIGHT;
             row++) {

            int mask =
                    rows[row];

            if (mask == 0) {
                continue;
            }

            for (int column = 0;
                 column < SYMBOL_WIDTH;
                 column++) {

                if ((mask & (1 << column)) == 0) {
                    continue;
                }

                this.hexListPixels.setPixel(
                        x + column,
                        y + row,
                        color
                );
            }
        }
    }

    /*
     * ============================================================
     * Rendering
     * ============================================================
     */

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        this.tickHeldArrow();

        int guiX =
                this.leftPos;

        int guiY =
                this.topPos;

        this.blitTexture(
                graphics,
                BASE_TEXTURE,
                guiX,
                guiY,
                BASE_WIDTH,
                BASE_HEIGHT
        );

        this.blitTexture(
                graphics,
                PHONE_SCREEN_TEXTURE,
                guiX + PHONE_SCREEN_X,
                guiY + PHONE_SCREEN_Y,
                PHONE_SCREEN_WIDTH,
                PHONE_SCREEN_HEIGHT
        );

        this.nextLayer(
                graphics
        );

        /*
         * ALL 26 dynamic hex codes:
         *
         * ONE render submission.
         */
        this.blitHexListTexture(
                graphics,
                guiX + RECENT_CALLS_ENTRY_X,
                guiY + ENTRY_START_Y
        );

        this.blitCaret(
                graphics,
                guiX,
                guiY
        );

        this.nextLayer(
                graphics
        );

        this.blitTexture(
                graphics,
                BUTTON_BAR_TEXTURE,
                guiX + BUTTON_BAR_X,
                guiY + BUTTON_BAR_Y,
                BUTTON_BAR_WIDTH,
                BUTTON_BAR_HEIGHT
        );

        this.nextLayer(
                graphics
        );

        for (CallerButton button
                : CallerButton.values()) {

            this.blitTexture(
                    graphics,
                    button.texture(
                            this.pressedButton
                                    == button
                    ),
                    guiX + button.x,
                    guiY + BUTTON_Y,
                    button.width,
                    BUTTON_HEIGHT
            );
        }
    }

    private void blitHexListTexture(
            GuiGraphics graphics,
            int x,
            int y
    ) {
        if (this.hexListDynamicTexture == null) {
            return;
        }

        //? if >=1.21.6 {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                HEX_LIST_TEXTURE,
                x,
                y,
                0.0F,
                0.0F,
                HEX_LIST_DRAW_WIDTH,
                HEX_LIST_DRAW_HEIGHT,
                HEX_LIST_TEXTURE_WIDTH,
                HEX_LIST_TEXTURE_HEIGHT
        );
        //?} else {
        /*graphics.blit(
                RenderType::guiTextured,
                HEX_LIST_TEXTURE,
                x,
                y,
                0.0F,
                0.0F,
                HEX_LIST_DRAW_WIDTH,
                HEX_LIST_DRAW_HEIGHT,
                HEX_LIST_TEXTURE_WIDTH,
                HEX_LIST_TEXTURE_HEIGHT
        );
        *///?}
    }

    private void blitCaret(
            GuiGraphics graphics,
            int guiX,
            int guiY
    ) {
        if (!this.isCaretVisible()) {
            return;
        }

        int row =
                this.selectedPosition / 2;

        boolean savedNumbers =
                (this.selectedPosition & 1)
                        != 0;

        int x =
                savedNumbers
                        ? SAVED_NUMBERS_CARET_X
                        : RECENT_CALLS_CARET_X;

        int y =
                CARET_START_Y
                        + row
                        * CARET_ROW_STEP;

        graphics.fill(
                guiX + x,
                guiY + y,
                guiX + x + CARET_WIDTH,
                guiY + y + CARET_HEIGHT,
                CARET_COLOR
        );
    }

    private boolean isCaretVisible() {
        long elapsed =
                System.nanoTime()
                        - this.caretBlinkStartNanos;

        long phase =
                elapsed
                        / CARET_BLINK_PHASE_NANOS;

        return (phase & 1L) == 0L;
    }

    /*
     * ============================================================
     * Buttons
     * ============================================================
     */

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0) {
            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        for (CallerButton callerButton
                : CallerButton.values()) {

            if (!this.contains(
                    mouseX,
                    mouseY,
                    callerButton.x,
                    BUTTON_Y,
                    callerButton.width,
                    BUTTON_HEIGHT
            )) {
                continue;
            }

            this.startButtonPress(
                    callerButton
            );

            return true;
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    private void startButtonPress(
            CallerButton button
    ) {
        this.pressedButton =
                button;

        this.buttonRepeatLastNanos =
                System.nanoTime();

        this.buttonRepeatMode =
                false;

        this.activateButton(
                button
        );
    }

    private void activateButton(
            CallerButton button
    ) {
        switch (button) {
            case LEFT ->
                    this.moveSelection(-1);

            case RIGHT ->
                    this.moveSelection(1);

            case SPACE -> {
                // Functionality will be added later.
            }
        }
    }

    private void tickHeldArrow() {
        if (this.pressedButton
                != CallerButton.LEFT
                && this.pressedButton
                != CallerButton.RIGHT) {
            return;
        }

        long now =
                System.nanoTime();

        long wait =
                this.buttonRepeatMode
                        ? ARROW_REPEAT_INTERVAL_NANOS
                        : ARROW_REPEAT_DELAY_NANOS;

        if (now
                - this.buttonRepeatLastNanos
                < wait) {
            return;
        }

        int guard = 0;

        do {
            this.activateButton(
                    this.pressedButton
            );

            this.buttonRepeatLastNanos +=
                    wait;

            this.buttonRepeatMode =
                    true;

            wait =
                    ARROW_REPEAT_INTERVAL_NANOS;

            guard++;
        } while (
                now
                        - this.buttonRepeatLastNanos
                        >= wait
                        && guard < 20
        );
    }

    private void moveSelection(
            int direction
    ) {
        this.selectedPosition =
                Math.floorMod(
                        this.selectedPosition
                                + direction,
                        SELECTION_COUNT
                );

        this.restartCaretBlink();
    }

    private void restartCaretBlink() {
        this.caretBlinkStartNanos =
                System.nanoTime();
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0) {
            return super.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            );
        }

        if (this.pressedButton != null) {
            this.clearButtonPress();

            return true;
        }

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    private void clearButtonPress() {
        this.pressedButton =
                null;

        this.buttonRepeatLastNanos =
                0L;

        this.buttonRepeatMode =
                false;
    }

    /*
     * ============================================================
     * Client ticking
     * ============================================================
     */

    @Override
    protected void containerTick() {
        super.containerTick();

        /*
         * GPU uploads deliberately happen here,
         * NOT while building the render frame.
         *
         * Multiple data changes between ticks collapse into one upload.
         */
        this.uploadHexListTextureIfDirty();
    }

    /*
     * ============================================================
     * Cleanup
     * ============================================================
     */

    @Override
    public void removed() {
        this.clearButtonPress();
        this.releaseHexListTexture();

        super.removed();
    }

    private void releaseHexListTexture() {
        if (this.hexListDynamicTexture == null) {
            return;
        }

        if (this.minecraft != null) {
            this.minecraft
                    .getTextureManager()
                    .release(
                            HEX_LIST_TEXTURE
                    );
        }

        this.hexListDynamicTexture =
                null;

        this.hexListPixels =
                null;

        this.recentDirtyRows =
                ALL_ROWS_DIRTY;

        this.savedDirtyRows =
                ALL_ROWS_DIRTY;
    }

    /*
     * ============================================================
     * Hex coloring
     * ============================================================
     */

    private static int matchingOutlineColor(
            int rgb
    ) {
        int red =
                (rgb >> 16) & 0xFF;

        int green =
                (rgb >> 8) & 0xFF;

        int blue =
                rgb & 0xFF;

        int luminance =
                (
                        red * 299
                                + green * 587
                                + blue * 114
                ) / 1000;

        if (luminance < 110) {
            red =
                    mix(
                            red,
                            255,
                            0.48F
                    );

            green =
                    mix(
                            green,
                            255,
                            0.48F
                    );

            blue =
                    mix(
                            blue,
                            255,
                            0.48F
                    );
        } else {
            red =
                    mix(
                            red,
                            0,
                            0.48F
                    );

            green =
                    mix(
                            green,
                            0,
                            0.48F
                    );

            blue =
                    mix(
                            blue,
                            0,
                            0.48F
                    );
        }

        return 0xFF000000
                | red << 16
                | green << 8
                | blue;
    }

    private static int mix(
            int from,
            int to,
            float amount
    ) {
        return Math.round(
                from
                        + (to - from)
                        * amount
        );
    }

    private static int parseHexColor(
            String hexCode
    ) {
        int color = 0;

        for (int i = 0;
             i < HEX_CODE_LENGTH;
             i++) {

            color =
                    color << 4
                            | Character.digit(
                            hexCode.charAt(i),
                            16
                    );
        }

        return color & 0xFFFFFF;
    }

    /*
     * ============================================================
     * Generic GUI helpers
     * ============================================================
     */

    private boolean contains(
            double mouseX,
            double mouseY,
            int localX,
            int localY,
            int width,
            int height
    ) {
        int x =
                this.leftPos
                        + localX;

        int y =
                this.topPos
                        + localY;

        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    private void blitTexture(
            GuiGraphics graphics,
            ResourceLocation texture,
            int x,
            int y,
            int width,
            int height
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
                height
        );
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

    private void nextLayer(
            GuiGraphics graphics
    ) {
        //? if >=1.21.6 {
        graphics.nextStratum();
        //?}
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        // Intentionally empty.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        this.renderTooltip(
                graphics,
                mouseX,
                mouseY
        );
    }

    /*
     * ============================================================
     * Buttons
     * ============================================================
     */

    private enum CallerButton {
        LEFT(
                26,
                20
        ),

        SPACE(
                54,
                49
        ),

        RIGHT(
                111,
                20
        );

        private final int x;
        private final int width;

        CallerButton(
                int x,
                int width
        ) {
            this.x = x;
            this.width = width;
        }

        private ResourceLocation texture(
                boolean pressed
        ) {
            return switch (this) {
                case LEFT ->
                        pressed
                                ? ARROW_LEFT_PRESSED_TEXTURE
                                : ARROW_LEFT_TEXTURE;

                case SPACE ->
                        pressed
                                ? SPACE_BUTTON_PRESSED_TEXTURE
                                : SPACE_BUTTON_TEXTURE;

                case RIGHT ->
                        pressed
                                ? ARROW_RIGHT_PRESSED_TEXTURE
                                : ARROW_RIGHT_TEXTURE;
            };
        }
    }

    /*
     * ============================================================
     * Pixel glyph
     * ============================================================
     *
     * This whole section runs only during class initialization.
     *
     * It does NOT run while rendering the GUI.
     */

    private record Glyph(
            int[] outlineRows,
            int[] fillRows
    ) {

        private static Glyph of(
                String... rows
        ) {
            int[] outlineRows =
                    new int[SYMBOL_HEIGHT];

            for (int y = 0;
                 y < SYMBOL_HEIGHT;
                 y++) {

                int mask = 0;

                for (int x = 0;
                     x < SYMBOL_WIDTH;
                     x++) {

                    if (rows[y].charAt(x) == '#') {
                        mask |=
                                1 << x;
                    }
                }

                outlineRows[y] =
                        mask;
            }

            return new Glyph(
                    outlineRows,
                    findInterior(
                            outlineRows
                    )
            );
        }

        private static int[] findInterior(
                int[] outlineRows
        ) {
            boolean[][] outside =
                    new boolean[
                            SYMBOL_HEIGHT
                            ][
                            SYMBOL_WIDTH
                            ];

            ArrayDeque<Integer> queue =
                    new ArrayDeque<>();

            for (int x = 0;
                 x < SYMBOL_WIDTH;
                 x++) {

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x,
                        0
                );

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x,
                        SYMBOL_HEIGHT - 1
                );
            }

            for (int y = 0;
                 y < SYMBOL_HEIGHT;
                 y++) {

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        0,
                        y
                );

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        SYMBOL_WIDTH - 1,
                        y
                );
            }

            while (!queue.isEmpty()) {
                int index =
                        queue.removeFirst();

                int x =
                        index
                                % SYMBOL_WIDTH;

                int y =
                        index
                                / SYMBOL_WIDTH;

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x - 1,
                        y
                );

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x + 1,
                        y
                );

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x,
                        y - 1
                );

                addOutside(
                        outlineRows,
                        outside,
                        queue,
                        x,
                        y + 1
                );
            }

            int[] fillRows =
                    new int[
                            SYMBOL_HEIGHT
                            ];

            for (int y = 0;
                 y < SYMBOL_HEIGHT;
                 y++) {

                int mask = 0;

                for (int x = 0;
                     x < SYMBOL_WIDTH;
                     x++) {

                    boolean outline =
                            (
                                    outlineRows[y]
                                            & (1 << x)
                            ) != 0;

                    if (!outline
                            && !outside[y][x]) {

                        mask |=
                                1 << x;
                    }
                }

                fillRows[y] =
                        mask;
            }

            return fillRows;
        }

        private static void addOutside(
                int[] outlineRows,
                boolean[][] outside,
                ArrayDeque<Integer> queue,
                int x,
                int y
        ) {
            if (x < 0
                    || x >= SYMBOL_WIDTH
                    || y < 0
                    || y >= SYMBOL_HEIGHT) {
                return;
            }

            if (outside[y][x]) {
                return;
            }

            if ((
                    outlineRows[y]
                            & (1 << x)
            ) != 0) {
                return;
            }

            outside[y][x] =
                    true;

            queue.addLast(
                    y * SYMBOL_WIDTH
                            + x
            );
        }
    }
}