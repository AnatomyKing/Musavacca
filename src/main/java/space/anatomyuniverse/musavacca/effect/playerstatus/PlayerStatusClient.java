package space.anatomyuniverse.musavacca.effect.playerstatus;

//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
//? if >=26.2 {
/*import net.minecraft.client.gui.Hud;
*///?} else {
import net.minecraft.client.gui.Gui;
//?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >=26.1 {
/*import net.minecraft.client.gui.components.PlayerFaceExtractor;
*///?} else {
import net.minecraft.client.gui.components.PlayerFaceRenderer;
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//? if >=1.21.2 {
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?} else if <1.21.2 {
/*import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
*///?}
//? if >=1.21.2 <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///?}
import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
//?}
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.UUID;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;
import space.anatomyuniverse.musavacca.gui.screen.VocoCallerScreen;

public final class PlayerStatusClient {
    private static final int BUTTON_ROW_WIDTH = 25 * 2 + 3;
    private static Screen buttonScreen;
    private static int buttonsLeftX;
    private static int buttonY;
    private static double mouseX;
    private static double mouseY;
    private static StatusButton pressedButton;
    private static int ringTick;
    private static UUID callerId;
    private static String callerName = "";
    private static final IClientMobEffectExtensions EXTENSIONS = new IClientMobEffectExtensions() {
        @Override
        //? if >=26.2 {
        /*public boolean extractHudIcon(MobEffectInstance instance, Hud hud, GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        *///?} else if >=26.1 <26.2 {
        /*public boolean extractGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        *///?} else {
        public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
        //?}
            //? if >=26.1 {
            /*return renderPlayerFaces(graphics, x + 3, y + 3, ((color >>> 24) & 255) / 255.0F);
            *///?} else {
            return renderPlayerFaces(graphics, x + 3, y + 3, alpha);
            //?}
        }

        @Override
        //? if >=26.1 {
        /*public boolean extractInventoryIcon(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int x, int y,
                int width, int height, int color) {
        *///?} else if >=1.21.2 <26.1 {
        public boolean renderInventoryIcon(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
        //?} else {
        /*public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y,
                int blitOffset) {
        *///?}
            //? if >=26.1 {
            /*return renderPlayerFaces(graphics, x, y + 7, ((color >>> 24) & 255) / 255.0F);
            *///?} else {
            return renderPlayerFaces(graphics, x, y + 7, 1.0F);
            //?}
        }

        @Override
        //? if >=26.1 {
        /*public boolean extractInventoryText(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int x, int y,
                int canvasWidth, int color) {
        *///?} else if >=1.21.2 {
        public boolean renderInventoryText(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
        //?} else {
        /*public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y,
                int blitOffset) {
        *///?}
            //? if >=26.1 {
            /*return renderExpandedControls(instance, screen, graphics, x, y, canvasWidth, color);
            *///?} else if >=1.21.11 {
            /*int width = Minecraft.getInstance().font.width(instance.getEffect().value().getDisplayName());
            return renderExpandedControls(instance, screen, graphics, x, y, width, 0xFFFFFFFF);
            *///?} else {
            drawControls(screen, graphics, x + 56, y + 15);
            return false;
            //?}
        }
    };

    private PlayerStatusClient() {}

    public static void register(IEventBus modBus) {
        //? if >=1.21.6 {
        modBus.addListener(PlayerStatusClient::registerClientPayloads);
        //?}
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onLoggingOut);
        modBus.addListener(PlayerStatusClient::registerExtensions);
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onScreenRenderPre);
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onRenderInventoryMobEffects);
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onMouseButtonPressed);
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onMouseButtonReleased);
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onClientTick);
    }

    private static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(EXTENSIONS, ModMobEffects.PLAYER_STATUS);
    }

    //? if >=1.21.6 {
    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(PlayerStatusCallerPayload.TYPE, (payload, context) -> setCaller(payload));
    }

    //?}
    public static void setCaller(PlayerStatusCallerPayload payload) {
        callerId = payload.callerName().isEmpty() ? null : payload.callerId();
        callerName = payload.callerName();
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        setCaller(PlayerStatusCallerPayload.CLEAR);
        ringTick = 0;
        pressedButton = null;
        buttonScreen = null;
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !minecraft.player.hasEffect(ModMobEffects.PLAYER_STATUS)) {
            ringTick = 0;
            return;
        }
        if (minecraft.isPaused()) return;
        if (ringTick == 0 || ringTick == 6) {
            minecraft.player.playSound(SoundEvents.BELL_BLOCK, 1.0F, ringTick == 0 ? 1.6F : 1.4F);
        }
        ringTick = (ringTick + 1) % 24;
    }

    //? if >=26.1 {
    /*private static boolean renderPlayerFaces(GuiGraphicsExtractor graphics, int iconX, int iconY, float alpha) {
    *///?} else {
    private static boolean renderPlayerFaces(GuiGraphics graphics, int iconX, int iconY, float alpha) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return false;
        int firstX = iconX + 1;
        int firstY = iconY + 1;
        int secondX = iconX + 9;
        int secondY = iconY + 9;
        var connection = minecraft.getConnection();
        var caller = callerId != null && connection != null ? connection.getPlayerInfo(callerId) : null;
        var callerSkin = caller != null ? caller.getSkin() : DefaultPlayerSkin.get(callerId != null ? callerId : PlayerStatusCallerPayload.CLEAR.callerId());
        //? if >=1.21.2 {
        int alphaChannel = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
        int color = alphaChannel << 24 | 0x00FFFFFF;
        //?}
        //? if >=26.1 {
        /*PlayerFaceExtractor.extractRenderState(graphics, callerSkin, firstX, firstY, 8, color);
        PlayerFaceExtractor.extractRenderState(graphics, minecraft.player.getSkin(), secondX, secondY, 8, color);
        *///?} else if >=1.21.2 {
        PlayerFaceRenderer.draw(graphics, callerSkin, firstX, firstY, 8, color);
        PlayerFaceRenderer.draw(graphics, minecraft.player.getSkin(), secondX, secondY, 8, color);
        //?} else {
        /*graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        PlayerFaceRenderer.draw(graphics, callerSkin.texture(), firstX, firstY, 8, true, false);
        PlayerFaceRenderer.draw(graphics, minecraft.player.getSkin().texture(), secondX, secondY, 8, true, false);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
        return true;
    }

    //? if >=1.21.11 {
    /*//? if >=26.1 {
    /^private static boolean renderExpandedControls(MobEffectInstance instance, Screen screen, GuiGraphicsExtractor graphics, int x, int y, int availableWidth,
    ^///?} else {
    private static boolean renderExpandedControls(MobEffectInstance instance, Screen screen, GuiGraphics graphics, int x, int y, int availableWidth,
    //?}
            int textColor) {
        if (!supportsControls(screen)) return false;
        if (availableWidth < BUTTON_ROW_WIDTH) {
            pressedButton = null;
            return false;
        }
        //? if >=26.1 {
        /^graphics.text(Minecraft.getInstance().font, instance.getEffect().value().getDisplayName(), x, y, textColor);
        ^///?} else {
        graphics.drawString(Minecraft.getInstance().font, instance.getEffect().value().getDisplayName(), x, y, textColor);
        //?}
        drawControls(screen, graphics, x + (availableWidth - BUTTON_ROW_WIDTH) / 2, y + 11);
        return true;
    }

    *///?}
    //? if >=26.1 {
    /*private static void drawControls(Screen screen, GuiGraphicsExtractor graphics, int left, int top) {
    *///?} else {
    private static void drawControls(Screen screen, GuiGraphics graphics, int left, int top) {
    //?}
        buttonScreen = screen;
        buttonsLeftX = left;
        buttonY = top;
        for (StatusButton button : StatusButton.values()) {
            blit(graphics, button.currentTexture(pressedButton == button), buttonX(button), buttonY, 25, 13);
        }
        StatusButton hovered = buttonAt(mouseX, mouseY);
        if (hovered != null) renderButtonTooltip(graphics, hovered);
    }

    //? if >=26.1 {
    /*private static void renderButtonTooltip(GuiGraphicsExtractor graphics, StatusButton button) {
    *///?} else {
    private static void renderButtonTooltip(GuiGraphics graphics, StatusButton button) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        String message = callerName.isEmpty() ? "Incoming call" : callerName + " is calling you";
        var lines = List.of(Component.literal(message).getVisualOrderText(), Component.literal(button.tooltip).getVisualOrderText());
        //? if >=1.21.6 {
        graphics.setTooltipForNextFrame(minecraft.font, lines, (int) mouseX, (int) mouseY);
        //?} else {
        /*graphics.renderTooltip(minecraft.font, lines, (int) mouseX, (int) mouseY);
        *///?}
    }

    private static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
        buttonScreen = null;
        Minecraft minecraft = Minecraft.getInstance();
        if (!supportsControls(event.getScreen()) || minecraft.player == null || !minecraft.player.hasEffect(ModMobEffects.PLAYER_STATUS)) {
            pressedButton = null;
        }
    }

    private static void onRenderInventoryMobEffects(ScreenEvent.RenderInventoryMobEffects event) {
        if (!(event.getScreen() instanceof VocoCallerScreen screen)) return;
        event.setCompact(false);
        event.setHorizontalOffset(screen.getEffectPanelX());
    }

    private static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != InputConstants.MOUSE_BUTTON_LEFT || !hasActiveControls(event.getScreen())) {
            return;
        }
        StatusButton button = buttonAt(event.getMouseX(), event.getMouseY());
        if (button == null) return;
        pressedButton = button;
        event.setCanceled(true);
    }

    private static void onMouseButtonReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (event.getButton() != InputConstants.MOUSE_BUTTON_LEFT || pressedButton == null) {
            return;
        }
        StatusButton button = pressedButton;
        pressedButton = null;
        if (!hasActiveControls(event.getScreen())) return;
        event.setCanceled(true);
        if (button != buttonAt(event.getMouseX(), event.getMouseY())) return;
        send(button.payload());
    }

    private static boolean supportsControls(Screen screen) {
        return screen instanceof InventoryScreen || screen instanceof VocoCallerScreen;
    }

    private static boolean hasActiveControls(Screen screen) {
        Minecraft minecraft = Minecraft.getInstance();
        return supportsControls(screen) && buttonScreen == screen && minecraft.player != null && minecraft.player.hasEffect(ModMobEffects.PLAYER_STATUS);
    }

    private static StatusButton buttonAt(double mouseX, double mouseY) {
        for (StatusButton button : StatusButton.values()) {
            int x = buttonX(button);
            if (mouseX >= x && mouseX < x + 25 && mouseY >= buttonY && mouseY < buttonY + 13) {
                return button;
            }
        }
        return null;
    }

    private static int buttonX(StatusButton button) {
        return buttonsLeftX + button.xOffset;
    }

    private static void send(PlayerStatusActionPayload payload) {
        //? if >=1.21.6 {
        ClientPacketDistributor.sendToServer(payload);
        //?} else {
        /*PacketDistributor.sendToServer(payload);
        *///?}
    }

    //? if >=1.21.11 {
    /*private static Identifier buttonTexture(String name) {
        return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/voco_caller/" + name + ".png");
    *///?} else {
    private static ResourceLocation buttonTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/voco_caller/" + name + ".png");
    //?}
    }

    //? if >=26.1 {
    /*private static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height) {
    *///?} else if >=1.21.11 <26.1 {
    /*private static void blit(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height) {
    *///?} else {
    private static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
    //?}
        //? if >=1.21.6 {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, width, height, width, height);
        //?} else if >=1.21.2 <1.21.6 {
        /*graphics.blit(RenderType::guiTextured, texture, x, y, 0.0F, 0.0F, width, height, width, height);
        *///?} else {
        /*graphics.blit(texture, x, y, 0.0F, 0.0F, width, height, width, height);
        *///?}
    }

    private enum StatusButton {
        ACCEPT(0, "accept", buttonTexture("accept"), buttonTexture("accept_pressed")),
        CANCEL(25 + 3, "cancel", buttonTexture("cancel"), buttonTexture("cancel_pressed"));

        private final int xOffset;
        private final String tooltip;
        //? if >=1.21.11 {
        /*private final Identifier normalTexture;
        private final Identifier pressedTexture;

        StatusButton(int xOffset, String tooltip, Identifier normalTexture, Identifier pressedTexture) {
        *///?} else {
        private final ResourceLocation normalTexture;
        private final ResourceLocation pressedTexture;

        StatusButton(int xOffset, String tooltip, ResourceLocation normalTexture, ResourceLocation pressedTexture) {
        //?}
            this.xOffset = xOffset;
            this.tooltip = tooltip;
            this.normalTexture = normalTexture;
            this.pressedTexture = pressedTexture;
        }

        //? if >=1.21.11 {
        /*private Identifier currentTexture(boolean pressed) {
        *///?} else {
        private ResourceLocation currentTexture(boolean pressed) {
        //?}
            return pressed ? this.pressedTexture : this.normalTexture;
        }

        private PlayerStatusActionPayload payload() {
            return this == ACCEPT ? PlayerStatusActionPayload.accept() : PlayerStatusActionPayload.cancel();
        }
    }
}
