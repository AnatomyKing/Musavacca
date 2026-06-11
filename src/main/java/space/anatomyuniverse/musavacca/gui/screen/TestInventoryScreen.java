package space.anatomyuniverse.musavacca.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.TestInventoryMenu;

public class TestInventoryScreen extends AbstractContainerScreen<TestInventoryMenu> {
    private static final int GUI_TEXTURE_SIZE = 256;

    private static final ResourceLocation BASE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/rotary_dialer/base.png");

    private static final ResourceLocation BASE_LETTERS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/rotary_dialer/base_letters.png");

    private static final ResourceLocation DISK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/rotary_dialer/disk.png");

    private static final ResourceLocation STOPPER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/rotary_dialer/overlay_stopper.png");

    private static final int DISK_TEXTURE_SIZE = 105;

    private static final float DIAL_AXIS_X = 129.0F;
    private static final float DIAL_AXIS_Y = 124.0F;

    private static final float DISK_PIVOT_X = 52.0F;
    private static final float DISK_PIVOT_Y = 52.0F;

    private static final int DISK_LOCAL_X = Math.round(DIAL_AXIS_X - DISK_PIVOT_X);
    private static final int DISK_LOCAL_Y = Math.round(DIAL_AXIS_Y - DISK_PIVOT_Y);

    private static final int MIDDLE_BUTTON_X = 117;
    private static final int MIDDLE_BUTTON_Y = 112;
    private static final int MIDDLE_BUTTON_WIDTH = 24;
    private static final int MIDDLE_BUTTON_HEIGHT = 25;

    private static final int DIAL_HOLE_SIZE = 14;
    private static final int DIAL_HOLE_PIXEL_COUNT = DIAL_HOLE_SIZE * DIAL_HOLE_SIZE;
    private static final int DIAL_HOLE_HALF_COVERED_COUNT = DIAL_HOLE_PIXEL_COUNT / 2;

    private static final float MIN_RETURN_ANGLE_RADIANS = 0.0001F;
    private static final float MAX_DRAG_STEP_RADIANS = 0.01F;
    private static final float RETURN_SPEED_RADIANS_PER_SECOND = 2.85F;

    private static final Hole[] DIAL_HOLES = new Hole[] {
            new Hole(8, 8),
            new Hole(32, 8),
            new Hole(58, 8),
            new Hole(83, 8),

            new Hole(8, 33),
            new Hole(83, 33),

            new Hole(8, 58),

            new Hole(8, 83),
            new Hole(33, 83),
            new Hole(58, 83)
    };

    private static final Rect[] STOPPER_RECTS = new Rect[] {
            new Rect(208, 131, 218, 140),
            new Rect(142, 140, 218, 152)
    };

    private float diskAngleRadians = 0.0F;
    private float previousDragMouseAngleRadians = 0.0F;

    private boolean draggingDisk = false;
    private boolean returningToStart = false;
    private boolean lettersBase = false;

    private Hole activeDragHole = null;
    private Hole returningHole = null;

    private ReturnReason returnReason = ReturnReason.NONE;
    private long lastReturnUpdateNanos = 0L;

    public TestInventoryScreen(TestInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = GUI_TEXTURE_SIZE;
        this.imageHeight = GUI_TEXTURE_SIZE;

        this.titleLabelY = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        this.updateReturnAnimation();

        int guiX = this.leftPos;
        int guiY = this.topPos;

        this.blitFullGuiTexture(graphics, this.lettersBase ? BASE_LETTERS_TEXTURE : BASE_TEXTURE, guiX, guiY);

        graphics.nextStratum();
        this.blitRotatingDisk(graphics, guiX, guiY);

        graphics.nextStratum();
        this.blitFullGuiTexture(graphics, STOPPER_TEXTURE, guiX, guiY);
    }

    private void blitRotatingDisk(GuiGraphics graphics, int guiX, int guiY) {
        float axisScreenX = guiX + DIAL_AXIS_X;
        float axisScreenY = guiY + DIAL_AXIS_Y;

        int diskScreenX = guiX + DISK_LOCAL_X;
        int diskScreenY = guiY + DISK_LOCAL_Y;

        graphics.pose().pushMatrix();

        graphics.pose().translate(axisScreenX, axisScreenY);
        graphics.pose().rotate(this.diskAngleRadians);
        graphics.pose().translate(-axisScreenX, -axisScreenY);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                DISK_TEXTURE,
                diskScreenX,
                diskScreenY,
                0.0F,
                0.0F,
                DISK_TEXTURE_SIZE,
                DISK_TEXTURE_SIZE,
                DISK_TEXTURE_SIZE,
                DISK_TEXTURE_SIZE
        );

        graphics.pose().popMatrix();
    }

    private void blitFullGuiTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x,
                y,
                0.0F,
                0.0F,
                GUI_TEXTURE_SIZE,
                GUI_TEXTURE_SIZE,
                GUI_TEXTURE_SIZE,
                GUI_TEXTURE_SIZE
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMouseInsideMiddleButton(mouseX, mouseY)) {
            this.lettersBase = !this.lettersBase;
            this.cancelDialInteraction();
            return true;
        }

        if (button == 0 && !this.returningToStart) {
            Hole clickedHole = this.findDialHoleAtMouse(mouseX, mouseY);

            if (clickedHole != null) {
                this.draggingDisk = true;
                this.activeDragHole = clickedHole;
                this.returningHole = null;
                this.returnReason = ReturnReason.NONE;
                this.previousDragMouseAngleRadians = normalizeRadians(this.angleFromDialAxis(mouseX, mouseY));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.draggingDisk && this.activeDragHole != null) {
            float currentMouseAngleRadians = normalizeRadians(this.angleFromDialAxis(mouseX, mouseY));
            float clockwiseDeltaRadians = signedShortestAngleDelta(
                    this.previousDragMouseAngleRadians,
                    currentMouseAngleRadians
            );

            if (clockwiseDeltaRadians > 0.0F && this.applyClockwiseDragSafely(clockwiseDeltaRadians)) {
                return true;
            }

            this.previousDragMouseAngleRadians = currentMouseAngleRadians;
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingDisk) {
            if (this.diskAngleRadians > MIN_RETURN_ANGLE_RADIANS) {
                this.startReturningToStart(ReturnReason.RELEASED_EARLY);
            } else {
                this.stopDraggingWithoutReturn();
            }

            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        this.cancelDialInteraction();
        super.removed();
    }

    private void updateReturnAnimation() {
        if (!this.returningToStart) {
            return;
        }

        long now = System.nanoTime();

        if (this.lastReturnUpdateNanos == 0L) {
            this.lastReturnUpdateNanos = now;
            return;
        }

        float deltaSeconds = (now - this.lastReturnUpdateNanos) / 1_000_000_000.0F;
        this.lastReturnUpdateNanos = now;

        this.diskAngleRadians -= RETURN_SPEED_RADIANS_PER_SECOND * deltaSeconds;

        if (this.diskAngleRadians <= 0.0F) {
            this.diskAngleRadians = 0.0F;
            this.returningToStart = false;
            this.lastReturnUpdateNanos = 0L;

            ReturnReason finishedReason = this.returnReason;
            Hole finishedHole = this.returningHole;

            this.returnReason = ReturnReason.NONE;
            this.returningHole = null;

            this.onDialReturnedToStart(finishedReason, finishedHole);
        }
    }

    private void onDialReturnedToStart(ReturnReason reason, Hole hole) {
        if (reason == ReturnReason.HIT_STOPPER && hole != null) {
            /*
             * This is a real completed dial input.
             *
             * RELEASED_EARLY also returns visually, but does not count as input.
             * Later you can send your number/letter logic from here.
             */
        }
    }

    private void startReturningToStart(ReturnReason reason) {
        this.returningHole = this.activeDragHole;
        this.returnReason = reason;

        this.draggingDisk = false;
        this.activeDragHole = null;
        this.returningToStart = true;
        this.lastReturnUpdateNanos = 0L;
    }

    private void stopDraggingWithoutReturn() {
        this.draggingDisk = false;
        this.activeDragHole = null;
        this.previousDragMouseAngleRadians = 0.0F;
    }

    private void cancelDialInteraction() {
        this.draggingDisk = false;
        this.returningToStart = false;
        this.activeDragHole = null;
        this.returningHole = null;
        this.returnReason = ReturnReason.NONE;
        this.lastReturnUpdateNanos = 0L;
        this.previousDragMouseAngleRadians = 0.0F;
        this.diskAngleRadians = 0.0F;
    }

    private boolean isMouseInsideMiddleButton(double mouseX, double mouseY) {
        double localX = mouseX - this.leftPos;
        double localY = mouseY - this.topPos;

        return localX >= MIDDLE_BUTTON_X
                && localX < MIDDLE_BUTTON_X + MIDDLE_BUTTON_WIDTH
                && localY >= MIDDLE_BUTTON_Y
                && localY < MIDDLE_BUTTON_Y + MIDDLE_BUTTON_HEIGHT;
    }

    private Hole findDialHoleAtMouse(double mouseX, double mouseY) {
        DiskLocalPoint point = this.mouseToUnrotatedDiskLocal(mouseX, mouseY);

        if (point.x < 0.0D || point.x >= DISK_TEXTURE_SIZE || point.y < 0.0D || point.y >= DISK_TEXTURE_SIZE) {
            return null;
        }

        for (Hole hole : DIAL_HOLES) {
            if (hole.contains(point.x, point.y)) {
                return hole;
            }
        }

        return null;
    }

    private DiskLocalPoint mouseToUnrotatedDiskLocal(double mouseX, double mouseY) {
        double axisScreenX = this.getDialAxisScreenX();
        double axisScreenY = this.getDialAxisScreenY();

        double dx = mouseX - axisScreenX;
        double dy = mouseY - axisScreenY;

        double cos = Math.cos(-this.diskAngleRadians);
        double sin = Math.sin(-this.diskAngleRadians);

        double unrotatedDx = dx * cos - dy * sin;
        double unrotatedDy = dx * sin + dy * cos;

        double diskScreenX = this.leftPos + DISK_LOCAL_X;
        double diskScreenY = this.topPos + DISK_LOCAL_Y;

        return new DiskLocalPoint(
                axisScreenX + unrotatedDx - diskScreenX,
                axisScreenY + unrotatedDy - diskScreenY
        );
    }

    private boolean isActiveHoleHalfCoveredByStopper() {
        if (this.activeDragHole == null) {
            return false;
        }

        int coveredPixels = 0;

        for (int pixelY = 0; pixelY < DIAL_HOLE_SIZE; pixelY++) {
            for (int pixelX = 0; pixelX < DIAL_HOLE_SIZE; pixelX++) {
                double diskLocalX = this.activeDragHole.x + pixelX + 0.5D;
                double diskLocalY = this.activeDragHole.y + pixelY + 0.5D;

                GuiLocalPoint rotatedPoint = this.diskLocalToRotatedGuiLocal(diskLocalX, diskLocalY);

                if (this.isGuiLocalPointInsideStopper(rotatedPoint.x, rotatedPoint.y)) {
                    coveredPixels++;

                    if (coveredPixels >= DIAL_HOLE_HALF_COVERED_COUNT) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private GuiLocalPoint diskLocalToRotatedGuiLocal(double diskLocalX, double diskLocalY) {
        double unrotatedGuiX = DISK_LOCAL_X + diskLocalX;
        double unrotatedGuiY = DISK_LOCAL_Y + diskLocalY;

        double dx = unrotatedGuiX - DIAL_AXIS_X;
        double dy = unrotatedGuiY - DIAL_AXIS_Y;

        double cos = Math.cos(this.diskAngleRadians);
        double sin = Math.sin(this.diskAngleRadians);

        return new GuiLocalPoint(
                DIAL_AXIS_X + dx * cos - dy * sin,
                DIAL_AXIS_Y + dx * sin + dy * cos
        );
    }

    private boolean isGuiLocalPointInsideStopper(double guiLocalX, double guiLocalY) {
        for (Rect rect : STOPPER_RECTS) {
            if (rect.contains(guiLocalX, guiLocalY)) {
                return true;
            }
        }

        return false;
    }

    private float angleFromDialAxis(double mouseX, double mouseY) {
        double dx = mouseX - this.getDialAxisScreenX();
        double dy = mouseY - this.getDialAxisScreenY();

        return (float) Math.atan2(dy, dx);
    }

    private float getDialAxisScreenX() {
        return this.leftPos + DIAL_AXIS_X;
    }

    private float getDialAxisScreenY() {
        return this.topPos + DIAL_AXIS_Y;
    }

    private static float normalizeRadians(float angle) {
        float fullTurn = (float) (Math.PI * 2.0D);

        angle %= fullTurn;

        if (angle < 0.0F) {
            angle += fullTurn;
        }

        return angle;
    }

    private static float signedShortestAngleDelta(float previousAngle, float currentAngle) {
        float halfTurn = (float) Math.PI;
        float fullTurn = (float) (Math.PI * 2.0D);
        float delta = currentAngle - previousAngle;

        while (delta <= -halfTurn) {
            delta += fullTurn;
        }

        while (delta > halfTurn) {
            delta -= fullTurn;
        }

        return delta;
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

    private enum ReturnReason {
        NONE,
        RELEASED_EARLY,
        HIT_STOPPER
    }

    private record DiskLocalPoint(double x, double y) {}

    private record GuiLocalPoint(double x, double y) {}

    private record Rect(int minX, int minY, int maxX, int maxY) {
        private boolean contains(double pointX, double pointY) {
            return pointX >= this.minX
                    && pointX < this.maxX
                    && pointY >= this.minY
                    && pointY < this.maxY;
        }
    }

    private record Hole(int x, int y) {
        private boolean contains(double pointX, double pointY) {
            return pointX >= this.x
                    && pointX < this.x + DIAL_HOLE_SIZE
                    && pointY >= this.y
                    && pointY < this.y + DIAL_HOLE_SIZE;
        }
    }
}