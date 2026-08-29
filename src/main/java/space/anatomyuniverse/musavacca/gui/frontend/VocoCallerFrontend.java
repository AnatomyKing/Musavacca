package space.anatomyuniverse.musavacca.gui.frontend;

//? if <1.21.6
////? if >=1.21.2
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
//? if >=1.21.7
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//? if <1.21.7
//import net.neoforged.neoforge.network.PacketDistributor;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.backend.VocoCallerBackend;
import space.anatomyuniverse.musavacca.gui.backend.VocoDialerBackend;
import space.anatomyuniverse.musavacca.gui.menu.VocoCallerMenu;
import space.anatomyuniverse.musavacca.gui.menu.payloads.VocoCallerStatePayload;

public final class VocoCallerFrontend
        extends AbstractContainerScreen<VocoCallerMenu> {

    private static final int GUI_WIDTH = 286;
    private static final int GUI_HEIGHT = 206;

    private static final int PHONE_X = 0;
    private static final int PHONE_Y = 0;
    private static final int PHONE_WIDTH = 126;
    private static final int PHONE_HEIGHT = 206;

    private static final int DIALER_X = 129;
    private static final int DIALER_Y = 42;

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

    private static final int SELECTION_COUNT =
            VocoCallerBackend.ROW_COUNT * 2;

    private static final int RECENT_CARET_X = 19;
    private static final int SAVED_CARET_X = 72;
    private static final int CARET_WIDTH = 2;
    private static final int CARET_HEIGHT = 7;
    private static final int CARET_COLOR = 0xFFCFBF5F;

    private static final long CARET_BLINK_NANOS =
            500_000_000L;

    private static final long ARROW_REPEAT_DELAY_NANOS =
            280_000_000L;

    private static final long ARROW_REPEAT_INTERVAL_NANOS =
            35_000_000L;

    private static final long DOUBLE_CLICK_NANOS =
            300_000_000L;

    private static final long LONG_PRESS_NANOS =
            650_000_000L;

    private static final ResourceLocation PHONE_SCREEN_TEXTURE =
            texture("phone_screen");

    private static final ResourceLocation BUTTON_BAR_TEXTURE =
            texture("button_bar");

    private static final ResourceLocation ARROW_LEFT_TEXTURE =
            texture("arrow_left");

    private static final ResourceLocation ARROW_LEFT_PRESSED_TEXTURE =
            texture("arrow_left_pressed");

    private static final ResourceLocation SPACE_BUTTON_TEXTURE =
            texture("space_button");

    private static final ResourceLocation SPACE_BUTTON_PRESSED_TEXTURE =
            texture("space_button_pressed");

    private static final ResourceLocation ARROW_RIGHT_TEXTURE =
            texture("arrow_right");

    private static final ResourceLocation ARROW_RIGHT_PRESSED_TEXTURE =
            texture("arrow_right_pressed");

    private static final ResourceLocation[] SYMBOL_TEXTURES =
            createSymbolTextures();

    private final VocoDialerControl dialer =
            new VocoDialerControl(
                    this::sendBackendButton
            );

    private int selectedPosition = 0;

    /**
     * Also controls the direction used when reordering Saved numbers.
     */
    private int lastNavigationDirection = 1;

    private long caretBlinkStartNanos =
            System.nanoTime();

    private CallerButton pressedButton = null;
    private long buttonRepeatLastNanos = 0L;
    private boolean buttonRepeatMode = false;

    private int spacePressPosition = -1;
    private long spacePressStartNanos = 0L;
    private boolean spacePressConsumed = false;

    private int pendingSpacePosition = -1;
    private long pendingSpaceClickNanos = 0L;

    private VocoCallerBackend.CallStateSnapshot
            pendingSpaceSnapshot = null;

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

        /*
         * Recent row zero remains the preferred starting position.
         *
         * If it is empty, immediately move the caret onto the first
         * populated Recent or Saved entry. This lets a phone with no
         * recent calls but at least one saved number be used instantly.
         */
        this.ensureCaretOnFilledEntry();
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        this.tickHeldArrow();
        this.tickSpaceButton();

        blit(
                graphics,
                PHONE_SCREEN_TEXTURE,
                this.leftPos + PHONE_X,
                this.topPos + PHONE_Y,
                PHONE_WIDTH,
                PHONE_HEIGHT
        );

        this.dialer.render(
                graphics,
                this.leftPos + DIALER_X,
                this.topPos + DIALER_Y
        );

        nextLayer(graphics);

        blit(
                graphics,
                BUTTON_BAR_TEXTURE,
                this.leftPos + BUTTON_BAR_X,
                this.topPos + BUTTON_BAR_Y,
                BUTTON_BAR_WIDTH,
                BUTTON_BAR_HEIGHT
        );

        nextLayer(graphics);

        for (CallerButton button : CallerButton.values()) {
            blit(
                    graphics,
                    button.texture(
                            this.pressedButton == button
                    ),
                    this.leftPos + button.x,
                    this.topPos + BUTTON_Y,
                    button.width,
                    BUTTON_HEIGHT
            );
        }
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        this.renderCurrentDialed(graphics);
        this.renderCallLists(graphics);
        this.renderSelectionCaret(graphics);
    }

    private void renderCurrentDialed(
            GuiGraphics graphics
    ) {
        this.drawHexCode(
                graphics,
                this.backend().getCurrentDialed(),
                CURRENT_DIALED_X,
                CURRENT_DIALED_Y
        );
    }

    private void renderCallLists(
            GuiGraphics graphics
    ) {
        for (
                int row = 0;
                row < VocoCallerBackend.ROW_COUNT;
                row++
        ) {
            int y =
                    LIST_Y
                            + row * ROW_HEIGHT;

            this.drawHexCode(
                    graphics,
                    this.backend().getRecentCall(row),
                    RECENT_X,
                    y
            );

            this.drawHexCode(
                    graphics,
                    this.backend().getSavedNumber(row),
                    SAVED_X,
                    y
            );
        }
    }

    private void drawHexCode(
            GuiGraphics graphics,
            String hexCode,
            int x,
            int y
    ) {
        if (hexCode == null) {
            return;
        }

        for (
                int index = 0;
                index < Math.min(
                        HEX_LENGTH,
                        hexCode.length()
                );
                index++
        ) {
            int symbol =
                    Character.digit(
                            hexCode.charAt(index),
                            16
                    );

            if (symbol >= 0) {
                blit(
                        graphics,
                        SYMBOL_TEXTURES[symbol],
                        x + index * SYMBOL_STEP,
                        y,
                        SYMBOL_WIDTH,
                        SYMBOL_HEIGHT
                );
            }
        }
    }

    private void renderSelectionCaret(
            GuiGraphics graphics
    ) {
        if (
                !this.hasEntry(this.selectedPosition)
                        || !this.isCaretVisible()
        ) {
            return;
        }

        int row =
                rowOf(this.selectedPosition);

        int x =
                isSavedPosition(this.selectedPosition)
                        ? SAVED_CARET_X
                        : RECENT_CARET_X;

        int y =
                LIST_Y
                        + row * ROW_HEIGHT;

        graphics.fill(
                x,
                y,
                x + CARET_WIDTH,
                y + CARET_HEIGHT,
                CARET_COLOR
        );
    }

    private boolean isCaretVisible() {
        return (
                (
                        System.nanoTime()
                                - this.caretBlinkStartNanos
                )
                        / CARET_BLINK_NANOS
                        & 1L
        ) == 0L;
    }

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

        for (
                CallerButton callerButton
                : CallerButton.values()
        ) {
            if (
                    !this.contains(
                            mouseX,
                            mouseY,
                            callerButton.x,
                            BUTTON_Y,
                            callerButton.width,
                            BUTTON_HEIGHT
                    )
            ) {
                continue;
            }

            this.startButtonPress(
                    callerButton
            );

            return true;
        }

        this.clearPendingSpaceClick();

        return this.dialer.mouseClicked(
                mouseX,
                mouseY,
                button,
                this.leftPos + DIALER_X,
                this.topPos + DIALER_Y
        )
                || super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        return this.dialer.mouseDragged(
                mouseX,
                mouseY,
                button,
                this.leftPos + DIALER_X,
                this.topPos + DIALER_Y
        )
                || super.mouseDragged(
                mouseX,
                mouseY,
                button,
                dragX,
                dragY
        );
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
            if (
                    this.pressedButton
                            == CallerButton.SPACE
            ) {
                this.finishSpacePress();
            }

            this.clearButtonPress();
            return true;
        }

        return this.dialer.mouseReleased(button)
                || super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    private void startButtonPress(
            CallerButton button
    ) {
        this.pressedButton = button;

        this.buttonRepeatLastNanos =
                System.nanoTime();

        this.buttonRepeatMode = false;

        if (button == CallerButton.SPACE) {
            this.beginSpacePress();
        } else {
            this.clearPendingSpaceClick();
            this.activateButton(button);
        }
    }

    private void activateButton(
            CallerButton button
    ) {
        switch (button) {
            case LEFT ->
                    this.moveSelection(-1);

            case SPACE -> {
            }

            case RIGHT ->
                    this.moveSelection(1);
        }
    }

    private void dialEntry(
            int position
    ) {
        String hexCode =
                this.hexCodeAt(position);

        if (hexCode == null) {
            return;
        }

        this.sendBackendButton(
                VocoDialerBackend.buttonForAddress(
                        VocoCallerBackend.parseHex(
                                hexCode
                        )
                )
        );
    }

    private String hexCodeAt(
            int position
    ) {
        if (!this.hasEntry(position)) {
            return null;
        }

        int row =
                rowOf(position);

        return isSavedPosition(position)
                ? this.backend()
                .getSavedNumber(row)
                : this.backend()
                .getRecentCall(row);
    }

    private void tickHeldArrow() {
        if (
                this.pressedButton
                        != CallerButton.LEFT
                        && this.pressedButton
                        != CallerButton.RIGHT
        ) {
            return;
        }

        long now =
                System.nanoTime();

        long wait =
                this.buttonRepeatMode
                        ? ARROW_REPEAT_INTERVAL_NANOS
                        : ARROW_REPEAT_DELAY_NANOS;

        if (
                now
                        - this.buttonRepeatLastNanos
                        < wait
        ) {
            return;
        }

        int guard = 0;

        do {
            this.activateButton(
                    this.pressedButton
            );

            this.buttonRepeatLastNanos +=
                    wait;

            this.buttonRepeatMode = true;
            wait = ARROW_REPEAT_INTERVAL_NANOS;
            guard++;
        } while (
                now
                        - this.buttonRepeatLastNanos
                        >= wait
                        && guard < 20
        );
    }

    private void beginSpacePress() {
        long now =
                System.nanoTime();

        this.spacePressPosition =
                this.hasEntry(
                        this.selectedPosition
                )
                        ? this.selectedPosition
                        : -1;

        this.spacePressStartNanos = now;
        this.spacePressConsumed = false;

        if (this.spacePressPosition < 0) {
            return;
        }

        if (
                this.pendingSpaceSnapshot == null
                        || now
                        - this.pendingSpaceClickNanos
                        > DOUBLE_CLICK_NANOS
        ) {
            this.clearPendingSpaceClick();
            return;
        }

        int originalPosition =
                this.pendingSpacePosition;

        VocoCallerBackend.CallStateSnapshot snapshot =
                this.pendingSpaceSnapshot;

        this.clearPendingSpaceClick();

        this.backend()
                .restoreCallState(snapshot);

        this.selectedPosition =
                originalPosition;

        this.spacePressPosition =
                originalPosition;

        this.spacePressConsumed = true;

        this.deleteEntry(
                originalPosition
        );
    }

    private void tickSpaceButton() {
        long now =
                System.nanoTime();

        if (
                this.pressedButton
                        == CallerButton.SPACE
                        && this.spacePressPosition >= 0
                        && !this.spacePressConsumed
                        && now
                        - this.spacePressStartNanos
                        >= LONG_PRESS_NANOS
        ) {
            this.clearPendingSpaceClick();
            this.spacePressConsumed = true;

            this.dialEntry(
                    this.spacePressPosition
            );
        }

        if (
                this.pendingSpaceSnapshot != null
                        && now
                        - this.pendingSpaceClickNanos
                        > DOUBLE_CLICK_NANOS
        ) {
            this.clearPendingSpaceClick();
        }
    }

    private void finishSpacePress() {
        if (
                this.spacePressPosition >= 0
                        && !this.spacePressConsumed
        ) {
            long now =
                    System.nanoTime();

            if (
                    now
                            - this.spacePressStartNanos
                            >= LONG_PRESS_NANOS
            ) {
                this.clearPendingSpaceClick();

                this.dialEntry(
                        this.spacePressPosition
                );
            } else {
                this.clearPendingSpaceClick();

                this.pendingSpacePosition =
                        this.spacePressPosition;

                this.pendingSpaceSnapshot =
                        this.backend()
                                .snapshotCallState();

                this.pendingSpaceClickNanos = now;

                this.activateSingleSpaceClick(
                        this.spacePressPosition
                );
            }
        }

        this.clearSpacePress();
    }

    private void activateSingleSpaceClick(
            int position
    ) {
        if (!this.hasEntry(position)) {
            return;
        }

        int row =
                rowOf(position);

        if (isSavedPosition(position)) {
            this.moveSavedEntry(row);
        } else {
            this.moveRecentEntryToSaved(row);
        }
    }

    private void clearPendingSpaceClick() {
        this.pendingSpacePosition = -1;
        this.pendingSpaceClickNanos = 0L;
        this.pendingSpaceSnapshot = null;
    }

    private void clearSpacePress() {
        this.spacePressPosition = -1;
        this.spacePressStartNanos = 0L;
        this.spacePressConsumed = false;
    }

    private void moveRecentEntryToSaved(
            int row
    ) {
        if (
                !this.backend()
                        .moveRecentCallToSavedTop(row)
        ) {
            return;
        }

        this.selectedPosition =
                positionOf(
                        0,
                        true
                );

        this.restartCaretBlink();
    }

    private void moveSavedEntry(
            int row
    ) {
        if (
                !this.backend()
                        .hasSavedNumber(row)
        ) {
            return;
        }

        int newRow =
                this.backend()
                        .moveSavedNumber(
                                row,
                                this.lastNavigationDirection
                        );

        this.selectedPosition =
                positionOf(
                        newRow,
                        true
                );

        this.restartCaretBlink();
    }

    private void deleteEntry(
            int position
    ) {
        int row =
                rowOf(position);

        if (isSavedPosition(position)) {
            this.backend()
                    .deleteSavedNumberToRecent(row);
        } else {
            this.backend()
                    .deleteRecentCall(row);
        }

        this.ensureCaretOnFilledEntry();
        this.restartCaretBlink();
    }

    private void ensureCaretOnFilledEntry() {
        if (
                this.hasEntry(
                        this.selectedPosition
                )
        ) {
            return;
        }

        int nextPosition =
                this.findNextFilledPosition(
                        this.selectedPosition,
                        this.lastNavigationDirection
                );

        if (nextPosition < 0) {
            nextPosition =
                    this.findNextFilledPosition(
                            this.selectedPosition,
                            -this.lastNavigationDirection
                    );
        }

        if (nextPosition >= 0) {
            this.selectedPosition =
                    nextPosition;
        }
    }

    private void clearButtonPress() {
        this.pressedButton = null;
        this.buttonRepeatLastNanos = 0L;
        this.buttonRepeatMode = false;
    }

    private void moveSelection(
            int direction
    ) {
        int step =
                Integer.compare(
                        direction,
                        0
                );

        if (step == 0) {
            return;
        }

        this.lastNavigationDirection =
                step;

        int nextPosition =
                this.findNextFilledPosition(
                        this.selectedPosition,
                        step
                );

        if (nextPosition < 0) {
            return;
        }

        this.selectedPosition =
                nextPosition;

        this.restartCaretBlink();
    }

    private int findNextFilledPosition(
            int fromPosition,
            int direction
    ) {
        int step =
                Integer.compare(
                        direction,
                        0
                );

        if (step == 0) {
            return -1;
        }

        int position =
                fromPosition;

        for (
                int checked = 0;
                checked < SELECTION_COUNT;
                checked++
        ) {
            position =
                    Math.floorMod(
                            position + step,
                            SELECTION_COUNT
                    );

            if (this.hasEntry(position)) {
                return position;
            }
        }

        return -1;
    }

    private void restartCaretBlink() {
        this.caretBlinkStartNanos =
                System.nanoTime();
    }

    private boolean hasEntry(
            int position
    ) {
        if (
                position < 0
                        || position >= SELECTION_COUNT
        ) {
            return false;
        }

        int row =
                rowOf(position);

        return isSavedPosition(position)
                ? this.backend()
                .hasSavedNumber(row)
                : this.backend()
                .hasRecentCall(row);
    }

    private static int rowOf(
            int position
    ) {
        return position / 2;
    }

    private static boolean isSavedPosition(
            int position
    ) {
        return (position & 1) != 0;
    }

    private static int positionOf(
            int row,
            boolean savedNumber
    ) {
        return row * 2
                + (
                savedNumber
                        ? 1
                        : 0
        );
    }

    private VocoCallerBackend backend() {
        return this.menu.getBackend();
    }

    private void sendBackendButton(
            int buttonId
    ) {
        if (
                !VocoDialerBackend.isKnownButton(
                        buttonId
                )
        ) {
            return;
        }

        if (
                this.minecraft == null
                        || this.minecraft.player == null
                        || this.minecraft.gameMode == null
        ) {
            return;
        }

        String before =
                this.backend()
                        .getCurrentDialed();

        boolean completesCall =
                buttonId
                        > VocoDialerBackend.BUTTON_CLEAR
                        || buttonId
                        >= VocoDialerBackend.BUTTON_HEX_0
                        && buttonId
                        <= VocoDialerBackend.BUTTON_HEX_F
                        && before != null
                        && before.length() == 5;

        if (
                !this.menu.clickMenuButton(
                        this.minecraft.player,
                        buttonId
                )
        ) {
            return;
        }

        /*
         * Only flush list edits when a call is about to let the server close
         * this menu. Normal list editing stays entirely local until onClose().
         */
        if (completesCall) {
            this.syncCallStateToServer();
        }

        this.minecraft.gameMode
                .handleInventoryButtonClick(
                        this.menu.containerId,
                        buttonId
                );
    }

    private void syncCallStateToServer() {
        VocoCallerStatePayload payload =
                new VocoCallerStatePayload(
                        this.menu.containerId,
                        this.menu.getPhoneHex(),
                        this.backend()
                                .copyRecentAddresses(),
                        this.backend()
                                .copySavedAddresses()
                );

        //? if >=1.21.7 {
        ClientPacketDistributor.sendToServer(
                payload
        );
        //?} else {
        /*PacketDistributor.sendToServer(
                payload
        );
        *///?}
    }

    public void setRecentCall(
            int row,
            String hexCode
    ) {
        this.backend()
                .setRecentCall(
                        row,
                        hexCode
                );

        this.ensureCaretOnFilledEntry();
    }

    public void setRecentCall(
            int row,
            int hexColor
    ) {
        this.backend()
                .setRecentCall(
                        row,
                        hexColor
                );

        this.ensureCaretOnFilledEntry();
    }

    public void clearRecentCall(
            int row
    ) {
        this.backend()
                .clearRecentCall(row);

        this.ensureCaretOnFilledEntry();
    }

    public void setSavedNumber(
            int row,
            String hexCode
    ) {
        this.backend()
                .setSavedNumber(
                        row,
                        hexCode
                );

        this.ensureCaretOnFilledEntry();
    }

    public void setSavedNumber(
            int row,
            int hexColor
    ) {
        this.backend()
                .setSavedNumber(
                        row,
                        hexColor
                );

        this.ensureCaretOnFilledEntry();
    }

    public void clearSavedNumber(
            int row
    ) {
        this.backend()
                .clearSavedNumber(row);

        this.ensureCaretOnFilledEntry();
    }

    @Override
    public void onClose() {
        this.syncCallStateToServer();
        super.onClose();
    }

    @Override
    public void removed() {
        this.clearSpacePress();
        this.clearPendingSpaceClick();
        this.clearButtonPress();
        this.dialer.cancel();
        this.backend().commitPendingRecentCall();
        super.removed();
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

    private static ResourceLocation[]
    createSymbolTextures() {
        ResourceLocation[] textures =
                new ResourceLocation[16];

        for (
                int value = 0;
                value < textures.length;
                value++
        ) {
            textures[value] =
                    texture(
                            "symbols/"
                                    + Character.forDigit(
                                    value,
                                    16
                            )
                    );
        }

        return textures;
    }

    private static ResourceLocation texture(
            String name
    ) {
        return ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "textures/gui/voco_caller/"
                        + name
                        + ".png"
        );
    }

    private static void blit(
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
        //?} else if >=1.21.2 {
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
        *///?} else {
        /*graphics.blit(texture, x, y, 0.0F, 0.0F, width, height, width, height);
        *///?}
    }

    private static void nextLayer(
            GuiGraphics graphics
    ) {
        //? if >=1.21.6
        graphics.nextStratum();
    }

    private enum CallerButton {
        LEFT(
                155,
                20
        ),
        SPACE(
                183,
                49
        ),
        RIGHT(
                240,
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
}


