package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

//? if >=1.21.7
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//? if <1.21.7
//import net.neoforged.neoforge.network.PacketDistributor;

public final class VocoCameraClient {

    private static final int GOLD_FILTER = 0x18D7B84E;
    private static final int PIXEL_GRID = 0x0AE7C85B;
    private static final int PIXEL_GRID_STEP = 6;

    private static boolean active = false;

    private static VocoCameraStartPayload session;

    private static float originalYaw;
    private static float originalPitch;
    private static CameraType originalCameraType;

    private VocoCameraClient() {}

    public static boolean isActive() {
        return active;
    }

    public static void start(VocoCameraStartPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        if (active) {
            finish(FinishMessage.NONE);
        }

        session = payload;

        originalYaw = minecraft.player.getYRot();
        originalPitch = minecraft.player.getXRot();
        originalCameraType = minecraft.options.getCameraType();

        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        minecraft.setCameraEntity(minecraft.player);

        active = true;
    }

    public static void onClientTickPre(ClientTickEvent.Pre event) {
        if (!active) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            finish(FinishMessage.NONE);
            return;
        }

        minecraft.options.keyUp.setDown(false);
        minecraft.options.keyDown.setDown(false);
        minecraft.options.keyLeft.setDown(false);
        minecraft.options.keyRight.setDown(false);
        minecraft.options.keyJump.setDown(false);
        minecraft.options.keyShift.setDown(false);
        minecraft.options.keySprint.setDown(false);
    }

    public static void onClientTickPost(ClientTickEvent.Post event) {
        if (!active) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            finish(FinishMessage.NONE);
            return;
        }

        if (minecraft.screen != null) {
            finish(FinishMessage.NONE);
            return;
        }

        if (minecraft.getCameraEntity() != minecraft.player) {
            minecraft.setCameraEntity(minecraft.player);
        }

        if (minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
            minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }

    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (!active || event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            event.setCanceled(true);
            save(false);
            return;
        }

        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);
            save(true);
        }
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!active || !(event.getNewScreen() instanceof PauseScreen)) {
            return;
        }

        event.setCanceled(true);
        finish(FinishMessage.NONE);
    }

    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        finish(FinishMessage.NONE);
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (active) {
            event.setCanceled(true);
        }
    }

    public static void renderHud(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!active) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        // Subtle warm Voco/gold camera tint.
        graphics.fill(
                0,
                0,
                width,
                height,
                GOLD_FILTER
        );

        // Very light pixel-grid treatment. Cheap: O(width + height), not a
        // per-pixel or per-cell post-process.
        for (int x = 0; x < width; x += PIXEL_GRID_STEP) {
            graphics.fill(
                    x,
                    0,
                    Math.min(x + 1, width),
                    height,
                    PIXEL_GRID
            );
        }

        for (int y = 0; y < height; y += PIXEL_GRID_STEP) {
            graphics.fill(
                    0,
                    y,
                    width,
                    Math.min(y + 1, height),
                    PIXEL_GRID
            );
        }

        // No backing rectangle: this replaces the translucent black bar that
        // previously appeared above the hotbar.
        int centerX = width / 2;

        graphics.drawCenteredString(
                minecraft.font,
                "Voco view",
                centerX,
                8,
                0xFFE6C95D
        );

        graphics.drawCenteredString(
                minecraft.font,
                "Left-click: save  |  Right-click: default  |  Esc: cancel",
                centerX,
                20,
                0xFFD8C984
        );
    }

    private static void save(boolean resetToDefault) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!active || session == null || minecraft.player == null) {
            finish(FinishMessage.NONE);
            return;
        }

        float yaw = Mth.wrapDegrees(minecraft.player.getYRot());
        float pitch = Mth.clamp(minecraft.player.getXRot(), -90.0F, 90.0F);

        VocoCameraSelectionPayload payload = new VocoCameraSelectionPayload(
                session.pos(),
                session.receptorId(),
                yaw,
                pitch,
                resetToDefault
        );

        //? if >=1.21.7 {
        ClientPacketDistributor.sendToServer(payload);
        //?} else {
        /*PacketDistributor.sendToServer(payload);
        *///?}

        finish(resetToDefault ? FinishMessage.RESET : FinishMessage.SAVED);
    }

    private static void finish(FinishMessage finishMessage) {
        Minecraft minecraft = Minecraft.getInstance();

        if (active && minecraft.player != null) {
            minecraft.player.setYRot(originalYaw);
            minecraft.player.setXRot(originalPitch);
            minecraft.player.setYHeadRot(originalYaw);
            minecraft.player.setYBodyRot(originalYaw);
            minecraft.setCameraEntity(minecraft.player);

            if (originalCameraType != null) {
                minecraft.options.setCameraType(originalCameraType);
            }

            if (finishMessage == FinishMessage.SAVED) {
                minecraft.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Voco arrival position and view saved."),
                        true
                );
            } else if (finishMessage == FinishMessage.RESET) {
                minecraft.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Voco receptor restored to its default arrival."),
                        true
                );
            }
        }

        active = false;
        session = null;
        originalCameraType = null;
    }

    private enum FinishMessage {
        NONE,
        SAVED,
        RESET
    }
}
