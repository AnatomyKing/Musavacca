package space.anatomyuniverse.musavacca.gui.voco;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoDustRide;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

public final class VocoCameraClient {
    private static LocalPlayer rider;
    private static Entity checkedCamera;
    private static boolean cancelSent;
    private static VocoCameraStartPayload session;
    private static float originalYaw;
    private static float originalPitch;
    private static CameraType originalCameraType;

    private VocoCameraClient() {}

    public static boolean isActive() {
        return session != null || rider != null;
    }

    public static void start(VocoCameraStartPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || rider != null) return;
        if (session != null) finish();
        session = payload;
        originalYaw = minecraft.player.getYRot();
        originalPitch = minecraft.player.getXRot();
        enterFirstPerson(minecraft);
    }

    private static void syncRide(Minecraft minecraft) {
        Entity camera = minecraft.getCameraEntity();
        if (camera == checkedCamera && (rider == null || rider == minecraft.player)) return;
        checkedCamera = camera;
        if (minecraft.player == null || !VocoDustRide.isCamera(camera, minecraft.player)) {
            if (rider != null) finish();
            return;
        }
        if (rider == minecraft.player) return;
        if (isActive()) finish();
        rider = minecraft.player;
        camera.refreshDimensions();
        enterFirstPerson(minecraft);
    }

    private static void enterFirstPerson(Minecraft minecraft) {
        originalCameraType = minecraft.options.getCameraType();
        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        if (session != null) minecraft.setCameraEntity(minecraft.player);
    }

    public static void onCameraSize(EntityEvent.Size event) {
        if (rider == null || event.getEntity() != checkedCamera) return;
        float eye = event.getPose() == Pose.CROUCHING ? VocoDustRide.TRAIL_EYE_HEIGHT : rider.getEyeHeight();
        event.setNewSize(event.getNewSize().withEyeHeight(eye));
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        syncRide(Minecraft.getInstance());
        if (rider == null) return;
        event.setYaw(rider.getYRot());
        event.setPitch(rider.getXRot());
    }

    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        if (rider != null) event.setCanceled(true);
    }

    public static void onClientTickPre(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        syncRide(minecraft);
        if (!isActive()) return;
        if (minecraft.player == null) {
            finish();
            return;
        }
        minecraft.options.keyUp.setDown(false);
        minecraft.options.keyDown.setDown(false);
        minecraft.options.keyLeft.setDown(false);
        minecraft.options.keyRight.setDown(false);
        minecraft.options.keyJump.setDown(false);
        minecraft.options.keyShift.setDown(false);
        minecraft.options.keySprint.setDown(false);
        if (rider != null) {
            minecraft.options.keyAttack.setDown(false);
            minecraft.options.keyUse.setDown(false);
            for (var key : minecraft.options.keyHotbarSlots) while (key.consumeClick()) {}
        }
    }

    public static void onClientTickPost(ClientTickEvent.Post event) {
        if (!isActive()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            finish();
            return;
        }
        //? if >=26.2 {
        /*if (minecraft.gui.screen() != null) {
        *///?} else {
        if (minecraft.screen != null) {
        //?}
            cancel();
            return;
        }
        if (rider != null && rider != minecraft.player) {
            finish();
            return;
        }
        if (session != null && minecraft.getCameraEntity() != minecraft.player) {
            minecraft.setCameraEntity(minecraft.player);
        }
        if (minecraft.options.getCameraType() != CameraType.FIRST_PERSON) {
            minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }

    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != InputConstants.PRESS) return;
        if (rider != null) {
            event.setCanceled(true);
            return;
        }
        if (session == null) return;
        int button = event.getButton();
        if (button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_RIGHT) {
            event.setCanceled(true);
            save(button == InputConstants.MOUSE_BUTTON_RIGHT);
        }
    }

    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (rider == null) return;
        event.setSwingHand(false);
        event.setCanceled(true);
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!isActive() || !(event.getNewScreen() instanceof PauseScreen)) return;
        event.setCanceled(true);
        cancel();
    }

    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        checkedCamera = null;
        finish();
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (isActive()) {
            event.setCanceled(true);
        }
    }

    //? if >=26.1 {
    /*public static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    *///?} else {
    public static void renderHud(GuiGraphics graphics, DeltaTracker deltaTracker) {
    //?}
        if (!isActive()) return;
        Minecraft minecraft = Minecraft.getInstance();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        graphics.fill(0, 0, width, height, 0x18D7B84E);
        for (int x = 0; x < width; x += 6) {
            graphics.fill(x, 0, x + 1, height, 0x0AE7C85B);
        }
        for (int y = 0; y < height; y += 6) {
            graphics.fill(0, y, width, y + 1, 0x0AE7C85B);
        }
        int centerX = width / 2;
        String title = rider == null ? "Voco view" : "Pearl Current";
        String controls = rider == null ? "Left-click: save  |  Right-click: default  |  Esc: cancel" : "Esc: cancel ride";
        //? if >=26.1 {
        /*graphics.centeredText(minecraft.font, title, centerX, 8, 0xFFE6C95D);
        graphics.centeredText(minecraft.font, controls, centerX, 20, 0xFFD8C984);
        *///?} else {
        graphics.drawCenteredString(minecraft.font, title, centerX, 8, 0xFFE6C95D);
        graphics.drawCenteredString(minecraft.font, controls, centerX, 20, 0xFFD8C984);
        //?}
    }

    private static void save(boolean resetToDefault) {
        Minecraft minecraft = Minecraft.getInstance();
        if (session == null || minecraft.player == null) {
            finish();
            return;
        }
        float yaw = Mth.wrapDegrees(minecraft.player.getYRot());
        float pitch = Mth.clamp(minecraft.player.getXRot(), -90.0F, 90.0F);
        minecraft.player.connection.send(new VocoCameraSelectionPayload(session.pos(), session.receptorId(), yaw, pitch, resetToDefault));
        Component message =
                Component.literal(
                        resetToDefault ? "Voco receptor restored to its default arrival." : "Voco arrival position and view saved.");
        //? if >=26.1 {
        /*minecraft.player.sendOverlayMessage(message);
        *///?} else {
        minecraft.player.displayClientMessage(message, true);
        //?}
        finish();
    }

    private static void cancel() {
        if (rider == null) {
            finish();
        } else if (!cancelSent) {
            cancelSent = true;
            rider.connection.send(VocoDustRideCancelPayload.INSTANCE);
        }
    }

    private static void finish() {
        Minecraft minecraft = Minecraft.getInstance();
        if (session != null && minecraft.player != null) {
            minecraft.player.setYRot(originalYaw);
            minecraft.player.setXRot(originalPitch);
            minecraft.player.setYHeadRot(originalYaw);
            minecraft.player.setYBodyRot(originalYaw);
        }
        if (originalCameraType != null) minecraft.options.setCameraType(originalCameraType);
        rider = null;
        cancelSent = false;
        session = null;
        originalCameraType = null;
    }
}
