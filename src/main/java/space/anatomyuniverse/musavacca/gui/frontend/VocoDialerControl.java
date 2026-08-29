package space.anatomyuniverse.musavacca.gui.frontend;

//? if <1.21.6
//import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
//? if <1.21.6
////? if >=1.21.2
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.backend.VocoDialerBackend;

import java.util.function.IntConsumer;

public final class VocoDialerControl {
    private static final int BASE_WIDTH = 157;
    private static final int BASE_HEIGHT = 164;
    private static final int DISK_SIZE = 105;
    private static final int STOPPER_WIDTH = 76;
    private static final int STOPPER_HEIGHT = 21;
    private static final int NO_NIBBLE = -1;

    private static final ResourceLocation BASE_TEXTURE = guiTexture("base");
    private static final ResourceLocation BASE_LETTERS_TEXTURE = guiTexture("base_letters");
    private static final ResourceLocation DISK_TEXTURE = guiTexture("disk");
    private static final ResourceLocation STOPPER_BACK_TEXTURE = guiTexture("stopper_back");
    private static final ResourceLocation STOPPER_FRONT_TEXTURE = guiTexture("stopper_front");

    private static final float DIAL_AXIS_X = 78.0F;
    private static final float DIAL_AXIS_Y = 78.0F;
    private static final float DISK_PIVOT_X = 52.0F;
    private static final float DISK_PIVOT_Y = 52.0F;
    private static final int DISK_X = Math.round(DIAL_AXIS_X - DISK_PIVOT_X);
    private static final int DISK_Y = Math.round(DIAL_AXIS_Y - DISK_PIVOT_Y);
    private static final int STOPPER_X = 91;
    private static final int STOPPER_Y = 85;
    private static final float STOPPER_PIVOT_X = 65.0F;
    private static final float STOPPER_PIVOT_Y = 10.0F;
    private static final int DIAL_HOLE_SIZE = 14;
    private static final int DIAL_HOLE_HALF_COVERED_COUNT = DIAL_HOLE_SIZE * DIAL_HOLE_SIZE / 2;
    private static final float MIN_RETURN_ANGLE_RADIANS = 0.0001F;
    private static final float MAX_DRAG_STEP_RADIANS = 0.01F;
    private static final float RETURN_SPEED_RADIANS_PER_SECOND = 2.85F;
    private static final float STOPPER_MAX_SWING_ANGLE_RADIANS = -(float) (Math.PI / 2.0D);
    private static final long STOPPER_SWING_DURATION_NANOS = 280_000_000L;

    private static final Rect MIDDLE_BUTTON = new Rect(66, 66, 90, 91);
    private static final DialEntry[] DIAL_ENTRIES = {
            new DialEntry(new Hole(83, 33), 1, 10), new DialEntry(new Hole(83, 8), 2, 11),
            new DialEntry(new Hole(58, 8), 3, 12), new DialEntry(new Hole(32, 8), 4, 13),
            new DialEntry(new Hole(8, 8), 5, 14), new DialEntry(new Hole(8, 33), 6, 15),
            new DialEntry(new Hole(8, 58), 7, NO_NIBBLE), new DialEntry(new Hole(8, 83), 8, NO_NIBBLE),
            new DialEntry(new Hole(33, 83), 9, NO_NIBBLE), new DialEntry(new Hole(58, 83), 0, NO_NIBBLE)
    };
    private static final Rect[] STOPPER_RECTS = {new Rect(157, 85, 167, 94), new Rect(91, 94, 167, 106)};

    private final IntConsumer buttonSender;
    private float diskAngleRadians = 0.0F;
    private float previousDragMouseAngleRadians = 0.0F;
    private float stopperAngleRadians = 0.0F;
    private boolean stopperSwinging = false;
    private long stopperSwingStartNanos = 0L;
    private boolean draggingDisk = false;
    private boolean returningToStart = false;
    private boolean lettersBase = false;
    private DialEntry activeDialEntry = null;
    private DialEntry returningDialEntry = null;
    private ReturnReason returnReason = ReturnReason.NONE;
    private long lastReturnUpdateNanos = 0L;

    public VocoDialerControl(IntConsumer buttonSender) {
        this.buttonSender = buttonSender;
    }

    private static ResourceLocation guiTexture(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/rotary_dialer/" + fileName + ".png");
    }

    public void render(GuiGraphics graphics, int guiX, int guiY) {
        this.updateAnimations();
        this.blitStopper(graphics, STOPPER_BACK_TEXTURE, guiX, guiY);
        this.nextLayer(graphics);
        this.blitTexture(graphics, this.lettersBase ? BASE_LETTERS_TEXTURE : BASE_TEXTURE, guiX, guiY, BASE_WIDTH, BASE_HEIGHT);
        this.nextLayer(graphics);
        this.blitDisk(graphics, guiX, guiY);
        this.nextLayer(graphics);
        this.blitStopper(graphics, STOPPER_FRONT_TEXTURE, guiX, guiY);
    }

    private void blitDisk(GuiGraphics graphics, int guiX, int guiY) {
        this.blitRotatedTexture(graphics, DISK_TEXTURE, guiX + DISK_X, guiY + DISK_Y, DISK_SIZE, DISK_SIZE,
                guiX + DIAL_AXIS_X, guiY + DIAL_AXIS_Y, this.diskAngleRadians);
    }

    private void blitStopper(GuiGraphics graphics, ResourceLocation texture, int guiX, int guiY) {
        this.blitRotatedTexture(graphics, texture, guiX + STOPPER_X, guiY + STOPPER_Y, STOPPER_WIDTH, STOPPER_HEIGHT,
                guiX + STOPPER_X + STOPPER_PIVOT_X, guiY + STOPPER_Y + STOPPER_PIVOT_Y, this.stopperAngleRadians);
    }

    private void blitRotatedTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                                    float axisX, float axisY, float angleRadians) {
        this.pushPose(graphics);
        this.translatePose(graphics, axisX, axisY);
        this.rotatePose(graphics, angleRadians);
        this.translatePose(graphics, -axisX, -axisY);
        this.blitTexture(graphics, texture, x, y, width, height);
        this.popPose(graphics);
    }

    private void blitTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
        //? if >=1.21.6 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, width, height);
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

    private void nextLayer(GuiGraphics graphics) {
        //? if >=1.21.6 {
        graphics.nextStratum();
        //?} else {
        /*graphics.pose();
        *///?}
    }

    private void pushPose(GuiGraphics graphics) {
        //? if >=1.21.6 {
        graphics.pose().pushMatrix();
        //?} else {
        /*graphics.pose().pushPose();
        *///?}
    }

    private void popPose(GuiGraphics graphics) {
        //? if >=1.21.6 {
        graphics.pose().popMatrix();
        //?} else {
        /*graphics.pose().popPose();
        *///?}
    }

    private void translatePose(GuiGraphics graphics, float x, float y) {
        //? if >=1.21.6 {
        graphics.pose().translate(x, y);
        //?} else {
        /*graphics.pose().translate(x, y, 0.0F);
        *///?}
    }

    private void rotatePose(GuiGraphics graphics, float radians) {
        //? if >=1.21.6 {
        graphics.pose().rotate(radians);
        //?} else {
        /*graphics.pose().mulPose(Axis.ZP.rotation(radians));
        *///?}
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int guiX, int guiY) {
        if (button != 0) return false;
        if (this.isMouseInsideRect(mouseX, mouseY, guiX, guiY, MIDDLE_BUTTON)) {
            this.lettersBase = !this.lettersBase;
            this.cancel();
            return true;
        }
        if (this.stopperSwinging || this.returningToStart || this.draggingDisk) return this.isInsideDialer(mouseX, mouseY, guiX, guiY);
        if (this.isMouseInsideStopper(mouseX, mouseY, guiX, guiY)) {
            this.startStopperSwing();
            return true;
        }
        DialEntry clickedEntry = this.findDialEntryAtMouse(mouseX, mouseY, guiX, guiY);
        if (clickedEntry == null) return false;
        this.draggingDisk = true;
        this.activeDialEntry = clickedEntry;
        this.returningDialEntry = null;
        this.returnReason = ReturnReason.NONE;
        this.previousDragMouseAngleRadians = normalizeRadians(this.angleFromDialAxis(mouseX, mouseY, guiX, guiY));
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, int guiX, int guiY) {
        if (button != 0 || !this.draggingDisk || this.activeDialEntry == null) return false;
        float currentMouseAngleRadians = normalizeRadians(this.angleFromDialAxis(mouseX, mouseY, guiX, guiY));
        float clockwiseDeltaRadians = signedShortestAngleDelta(this.previousDragMouseAngleRadians, currentMouseAngleRadians);
        if (clockwiseDeltaRadians > 0.0F && this.applyClockwiseDragSafely(clockwiseDeltaRadians)) return true;
        this.previousDragMouseAngleRadians = currentMouseAngleRadians;
        return true;
    }

    public boolean mouseReleased(int button) {
        if (button != 0 || !this.draggingDisk) return false;
        if (this.diskAngleRadians > MIN_RETURN_ANGLE_RADIANS) this.startReturningToStart(ReturnReason.RELEASED_EARLY);
        else this.stopDraggingWithoutReturn();
        return true;
    }

    private boolean isInsideDialer(double mouseX, double mouseY, int guiX, int guiY) {
        return mouseX >= guiX && mouseX < guiX + BASE_WIDTH && mouseY >= guiY && mouseY < guiY + BASE_HEIGHT;
    }

    private boolean applyClockwiseDragSafely(float totalDeltaRadians) {
        float remaining = totalDeltaRadians;
        while (remaining > 0.0F) {
            float step = Math.min(MAX_DRAG_STEP_RADIANS, remaining);
            float previousSafeAngle = this.diskAngleRadians;
            this.diskAngleRadians = normalizeRadians(this.diskAngleRadians + step);
            if (this.isActiveHoleHalfCoveredByStopper()) {
                this.diskAngleRadians = previousSafeAngle;
                this.startReturningToStart(ReturnReason.HIT_STOPPER);
                return true;
            }
            remaining -= step;
        }
        return false;
    }

    private void updateAnimations() {
        if (!this.returningToStart && !this.stopperSwinging) return;
        long now = System.nanoTime();
        this.updateReturnAnimation(now);
        this.updateStopperSwingAnimation(now);
    }

    private void updateReturnAnimation(long now) {
        if (!this.returningToStart) return;
        if (this.lastReturnUpdateNanos == 0L) {
            this.lastReturnUpdateNanos = now;
            return;
        }
        float deltaSeconds = (now - this.lastReturnUpdateNanos) / 1_000_000_000.0F;
        this.lastReturnUpdateNanos = now;
        this.diskAngleRadians -= RETURN_SPEED_RADIANS_PER_SECOND * deltaSeconds;
        if (this.diskAngleRadians > 0.0F) return;
        ReturnReason finishedReason = this.returnReason;
        DialEntry finishedEntry = this.returningDialEntry;
        this.diskAngleRadians = 0.0F;
        this.returningToStart = false;
        this.returnReason = ReturnReason.NONE;
        this.returningDialEntry = null;
        this.lastReturnUpdateNanos = 0L;
        this.onDialReturnedToStart(finishedReason, finishedEntry);
    }

    private void updateStopperSwingAnimation(long now) {
        if (!this.stopperSwinging) return;
        long elapsedNanos = now - this.stopperSwingStartNanos;
        if (elapsedNanos >= STOPPER_SWING_DURATION_NANOS) {
            this.stopperSwinging = false;
            this.stopperSwingStartNanos = 0L;
            this.stopperAngleRadians = 0.0F;
            this.buttonSender.accept(VocoDialerBackend.BUTTON_CLEAR);
            return;
        }
        float progress = elapsedNanos / (float) STOPPER_SWING_DURATION_NANOS;
        float swingProgress = progress < 0.5F ? easeInOutSine(progress * 2.0F) : 1.0F - easeInOutSine((progress - 0.5F) * 2.0F);
        this.stopperAngleRadians = swingProgress * STOPPER_MAX_SWING_ANGLE_RADIANS;
    }

    private void startStopperSwing() {
        this.stopperSwinging = true;
        this.stopperSwingStartNanos = System.nanoTime();
        this.stopperAngleRadians = 0.0F;
    }

    private void onDialReturnedToStart(ReturnReason reason, DialEntry entry) {
        if (reason != ReturnReason.HIT_STOPPER || entry == null) return;
        int nibble = entry.nibble(this.lettersBase);
        if (nibble >= 0) this.buttonSender.accept(nibble);
    }

    private void startReturningToStart(ReturnReason reason) {
        this.returningDialEntry = this.activeDialEntry;
        this.returnReason = reason;
        this.draggingDisk = false;
        this.activeDialEntry = null;
        this.returningToStart = true;
        this.lastReturnUpdateNanos = 0L;
    }

    private void stopDraggingWithoutReturn() {
        this.draggingDisk = false;
        this.activeDialEntry = null;
        this.previousDragMouseAngleRadians = 0.0F;
    }

    public void cancel() {
        this.draggingDisk = false;
        this.returningToStart = false;
        this.activeDialEntry = null;
        this.returningDialEntry = null;
        this.returnReason = ReturnReason.NONE;
        this.lastReturnUpdateNanos = 0L;
        this.previousDragMouseAngleRadians = 0.0F;
        this.diskAngleRadians = 0.0F;
        this.stopperSwinging = false;
        this.stopperSwingStartNanos = 0L;
        this.stopperAngleRadians = 0.0F;
    }

    private DialEntry findDialEntryAtMouse(double mouseX, double mouseY, int guiX, int guiY) {
        Point point = this.mouseToUnrotatedDiskLocal(mouseX, mouseY, guiX, guiY);
        if (point.x < 0.0D || point.x >= DISK_SIZE || point.y < 0.0D || point.y >= DISK_SIZE) return null;
        for (DialEntry entry : DIAL_ENTRIES) if (entry.hole.contains(point.x, point.y)) return entry;
        return null;
    }

    private Point mouseToUnrotatedDiskLocal(double mouseX, double mouseY, int guiX, int guiY) {
        double axisScreenX = guiX + DIAL_AXIS_X;
        double axisScreenY = guiY + DIAL_AXIS_Y;
        double dx = mouseX - axisScreenX;
        double dy = mouseY - axisScreenY;
        double cos = Math.cos(-this.diskAngleRadians);
        double sin = Math.sin(-this.diskAngleRadians);
        return new Point(axisScreenX + dx * cos - dy * sin - (guiX + DISK_X), axisScreenY + dx * sin + dy * cos - (guiY + DISK_Y));
    }

    private boolean isActiveHoleHalfCoveredByStopper() {
        if (this.activeDialEntry == null) return false;
        int coveredPixels = 0;
        double cos = Math.cos(this.diskAngleRadians);
        double sin = Math.sin(this.diskAngleRadians);
        Hole hole = this.activeDialEntry.hole;
        for (int pixelY = 0; pixelY < DIAL_HOLE_SIZE; pixelY++) {
            for (int pixelX = 0; pixelX < DIAL_HOLE_SIZE; pixelX++) {
                double unrotatedGuiX = DISK_X + hole.x + pixelX + 0.5D;
                double unrotatedGuiY = DISK_Y + hole.y + pixelY + 0.5D;
                double dx = unrotatedGuiX - DIAL_AXIS_X;
                double dy = unrotatedGuiY - DIAL_AXIS_Y;
                double rotatedGuiX = DIAL_AXIS_X + dx * cos - dy * sin;
                double rotatedGuiY = DIAL_AXIS_Y + dx * sin + dy * cos;
                if (this.isGuiLocalPointInsideStopper(rotatedGuiX, rotatedGuiY) && ++coveredPixels >= DIAL_HOLE_HALF_COVERED_COUNT) return true;
            }
        }
        return false;
    }

    private boolean isMouseInsideStopper(double mouseX, double mouseY, int guiX, int guiY) {
        return this.isGuiLocalPointInsideStopper(mouseX - guiX, mouseY - guiY);
    }

    private boolean isMouseInsideRect(double mouseX, double mouseY, int guiX, int guiY, Rect rect) {
        return rect.contains(mouseX - guiX, mouseY - guiY);
    }

    private boolean isGuiLocalPointInsideStopper(double guiLocalX, double guiLocalY) {
        for (Rect rect : STOPPER_RECTS) if (rect.contains(guiLocalX, guiLocalY)) return true;
        return false;
    }

    private float angleFromDialAxis(double mouseX, double mouseY, int guiX, int guiY) {
        return (float) Math.atan2(mouseY - (guiY + DIAL_AXIS_Y), mouseX - (guiX + DIAL_AXIS_X));
    }

    private static float easeInOutSine(float t) {
        return (float) (-(Math.cos(Math.PI * t) - 1.0D) / 2.0D);
    }

    private static float normalizeRadians(float angle) {
        float fullTurn = (float) (Math.PI * 2.0D);
        angle %= fullTurn;
        return angle < 0.0F ? angle + fullTurn : angle;
    }

    private static float signedShortestAngleDelta(float previousAngle, float currentAngle) {
        float halfTurn = (float) Math.PI;
        float fullTurn = (float) (Math.PI * 2.0D);
        float delta = currentAngle - previousAngle;
        while (delta <= -halfTurn) delta += fullTurn;
        while (delta > halfTurn) delta -= fullTurn;
        return delta;
    }

    private enum ReturnReason { NONE, RELEASED_EARLY, HIT_STOPPER }

    private record Point(double x, double y) {}

    private record Rect(int minX, int minY, int maxX, int maxY) {
        private boolean contains(double pointX, double pointY) {
            return pointX >= this.minX && pointX < this.maxX && pointY >= this.minY && pointY < this.maxY;
        }
    }

    private record Hole(int x, int y) {
        private boolean contains(double pointX, double pointY) {
            return pointX >= this.x && pointX < this.x + DIAL_HOLE_SIZE && pointY >= this.y && pointY < this.y + DIAL_HOLE_SIZE;
        }
    }

    private record DialEntry(Hole hole, int numberNibble, int letterNibble) {
        private int nibble(boolean lettersMode) {
            return lettersMode ? this.letterNibble : this.numberNibble;
        }
    }
}
