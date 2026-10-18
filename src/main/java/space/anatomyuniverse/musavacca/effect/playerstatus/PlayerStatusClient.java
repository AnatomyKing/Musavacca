package space.anatomyuniverse.musavacca.effect.playerstatus;

//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.gui.Gui;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

//? if >=1.21.11 {
/*import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
*///?} else if >=1.21.6 <1.21.11 {
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.client.renderer.RenderPipelines;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
//?} else if >=1.21.2 <1.21.6 {
/*import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
*///?} else {
/*import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
//?}
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.mojang.blaze3d.platform.InputConstants;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;
import space.anatomyuniverse.musavacca.gui.frontend.VocoCallerFrontend;

import java.util.UUID;

public final class PlayerStatusClient {

    private static final int FACE_SIZE = 8;

    private static final int HUD_ICON_X_OFFSET = 3;
    private static final int HUD_ICON_Y_OFFSET = 3;
    private static final int INVENTORY_ICON_Y_OFFSET = 7;

    private static final int FIRST_FACE_X_OFFSET = 1;
    private static final int FIRST_FACE_Y_OFFSET = 1;
    private static final int SECOND_FACE_X_OFFSET = 9;
    private static final int SECOND_FACE_Y_OFFSET = 9;

    private static final int BUTTON_WIDTH = 25;
    private static final int BUTTON_HEIGHT = 13;
    private static final int BUTTON_GAP = 3;

    private static final int ACCEPT_X_OFFSET = 56;
    private static final int BUTTON_Y_OFFSET = 15;

    private static final int RING_INTERVAL_TICKS = 24;
    private static final int SECOND_RING_TICK = 6;

    private static Screen buttonScreen;
    private static int buttonTextX;
    private static int buttonY;
    private static double mouseX;
    private static double mouseY;
    private static StatusButton pressedButton;
    private static int ringTick;
    private static UUID callerId;
    private static String callerName = "";

    private static final IClientMobEffectExtensions EXTENSIONS =
            new IClientMobEffectExtensions() {

                //? if >=26.2 {
                /*@Override
                public boolean extractHudIcon(MobEffectInstance instance, net.minecraft.client.gui.Hud hud,
                        GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
                    return renderPlayerFaces(graphics, x + HUD_ICON_X_OFFSET, y + HUD_ICON_Y_OFFSET,
                            ((color >>> 24) & 255) / 255.0F);
                }
                *///?} else if >=26.1 <26.2 {
                /*@Override
                public boolean renderGuiIcon(
                        MobEffectInstance instance,
                        Gui gui,
                        GuiGraphicsExtractor graphics,
                        int x,
                        int y,
                        float z,
                        float alpha
                ) {
                    return renderPlayerFaces(
                            graphics,
                            x + HUD_ICON_X_OFFSET,
                            y + HUD_ICON_Y_OFFSET,
                            alpha
                    );
                }
                *///?} else {
                @Override
                public boolean renderGuiIcon(
                        MobEffectInstance instance,
                        Gui gui,
                        GuiGraphics graphics,
                        int x,
                        int y,
                        float z,
                        float alpha
                ) {
                    return renderPlayerFaces(
                            graphics,
                            x + HUD_ICON_X_OFFSET,
                            y + HUD_ICON_Y_OFFSET,
                            alpha
                    );
                }
                //?}

                //? if >=26.2 {
                /*@Override
                public boolean extractInventoryIcon(MobEffectInstance instance, AbstractContainerScreen<?> screen,
                        GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
                    return renderPlayerFaces(graphics, x, y + INVENTORY_ICON_Y_OFFSET, ((color >>> 24) & 255) / 255.0F);
                }
                @Override
                public boolean extractInventoryText(MobEffectInstance instance, AbstractContainerScreen<?> screen,
                        GuiGraphicsExtractor graphics, int x, int y, int canvasWidth, int color) {
                    renderControls(screen, graphics, x, y);
                    return false;
                }
                *///?} else if >=26.1 <26.2 {
                /*@Override
                public boolean renderInventoryIcon(
                        MobEffectInstance instance,
                        AbstractContainerScreen<?> screen,
                        GuiGraphicsExtractor graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    return renderPlayerFaces(
                            graphics,
                            x,
                            y + INVENTORY_ICON_Y_OFFSET,
                            1.0F
                    );
                }

                @Override
                public boolean renderInventoryText(
                        MobEffectInstance instance,
                        AbstractContainerScreen<?> screen,
                        GuiGraphicsExtractor graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    renderControls(
                            screen,
                            graphics,
                            x,
                            y
                    );
                    return false;
                }
                *///?} else if >=1.21.2 <26.1 {
                @Override
                public boolean renderInventoryIcon(
                        MobEffectInstance instance,
                        AbstractContainerScreen<?> screen,
                        GuiGraphics graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    return renderPlayerFaces(
                            graphics,
                            x,
                            y + INVENTORY_ICON_Y_OFFSET,
                            1.0F
                    );
                }

                @Override
                public boolean renderInventoryText(
                        MobEffectInstance instance,
                        AbstractContainerScreen<?> screen,
                        GuiGraphics graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    renderControls(
                            screen,
                            graphics,
                            x,
                            y
                    );
                    return false;
                }
                //?} else {
                /*@Override
                public boolean renderInventoryIcon(
                        MobEffectInstance instance,
                        EffectRenderingInventoryScreen<?> screen,
                        GuiGraphics graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    return renderPlayerFaces(
                            graphics,
                            x,
                            y + INVENTORY_ICON_Y_OFFSET,
                            1.0F
                    );
                }

                @Override
                public boolean renderInventoryText(
                        MobEffectInstance instance,
                        EffectRenderingInventoryScreen<?> screen,
                        GuiGraphics graphics,
                        int x,
                        int y,
                        int blitOffset
                ) {
                    renderControls(
                            screen,
                            graphics,
                            x,
                            y
                    );
                    return false;
                }
                *///?}

            };

    private PlayerStatusClient() {}

    public static void register(IEventBus modBus) {
        //? if >=1.21.6 {

        modBus.addListener(PlayerStatusClient::registerClientPayloads);

        //?}
        NeoForge.EVENT_BUS.addListener(PlayerStatusClient::onLoggingOut);

        modBus.addListener(
                PlayerStatusClient::registerExtensions
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onScreenRenderPre
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onRenderInventoryMobEffects
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onMouseButtonPressed
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onMouseButtonReleased
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onClientTick
        );
    }

    private static void registerExtensions(
            RegisterClientExtensionsEvent event
    ) {
        event.registerMobEffect(
                EXTENSIONS,
                ModMobEffects.PLAYER_STATUS
        );
    }
    //? if >=1.21.6 {

    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                PlayerStatusCallerPayload.TYPE,
                (payload, context) -> setCaller(payload)
        );
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

    private static void onClientTick(
            ClientTickEvent.Post event
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || !minecraft.player.hasEffect(
                        ModMobEffects.PLAYER_STATUS
                )) {
            ringTick = 0;
            return;
        }

        if (minecraft.isPaused()) {
            return;
        }

        if (ringTick == 0 || ringTick == SECOND_RING_TICK) {
            minecraft.player.playSound(
                    SoundEvents.BELL_BLOCK,
                    1.0F,
                    ringTick == 0 ? 1.6F : 1.4F
            );
        }

        ringTick = (ringTick + 1) % RING_INTERVAL_TICKS;
    }

    private static boolean renderPlayerFaces(
            //? if >=26.1 {
            /*GuiGraphicsExtractor graphics,
            *///?} else {
            GuiGraphics graphics,
            //?}
            int iconX,
            int iconY,
            float alpha
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return false;
        }

        int firstX = iconX + FIRST_FACE_X_OFFSET;
        int firstY = iconY + FIRST_FACE_Y_OFFSET;
        int secondX = iconX + SECOND_FACE_X_OFFSET;
        int secondY = iconY + SECOND_FACE_Y_OFFSET;

        var connection = minecraft.getConnection();
        var caller = callerId != null && connection != null
                ? connection.getPlayerInfo(callerId)
                : null;
        var callerSkin = caller != null
                ? caller.getSkin()
                : DefaultPlayerSkin.get(
                        callerId != null ? callerId : PlayerStatusCallerPayload.CLEAR.callerId()
                );

        //? if >=26.1 {
        /*int alphaChannel = Math.max(
                0,
                Math.min(
                        255,
                        Math.round(alpha * 255.0F)
                )
        );

        int color =
                alphaChannel << 24
                        | 0x00FFFFFF;

        PlayerFaceExtractor.extractRenderState(
                graphics,
                callerSkin,
                firstX,
                firstY,
                FACE_SIZE,
                color
        *///?} else if >=1.21.2 <26.1 {
        int alphaChannel = Math.max(
                0,
                Math.min(
                        255,
                        Math.round(alpha * 255.0F)
                )
        );

        int color =
                alphaChannel << 24
                        | 0x00FFFFFF;

        PlayerFaceRenderer.draw(
                graphics,
                callerSkin,
                firstX,
                firstY,
                FACE_SIZE,
                color
        //?} else {
        /*graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                alpha
        *///?}
        );

        //? if >=26.1 {
        /*PlayerFaceExtractor.extractRenderState(
                graphics,
                minecraft.player.getSkin(),
        *///?} else if >=1.21.2 <26.1 {
        PlayerFaceRenderer.draw(
                graphics,
                minecraft.player.getSkin(),
        //?} else {
        /*PlayerFaceRenderer.draw(
                graphics,
                callerSkin.texture(),
                firstX,
                firstY,
                FACE_SIZE,
                true,
                false
        );

        PlayerFaceRenderer.draw(
                graphics,
                minecraft.player.getSkin().texture(),
        *///?}
                secondX,
                secondY,
                FACE_SIZE,
        //? if >=1.21.2 {
                color
        //?} else {
                /*true,
                false
        );

        graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        *///?}
        );

        return true;
    }

    private static void renderControls(
            Screen screen,
            //? if >=26.1 {
            /*GuiGraphicsExtractor graphics,
            *///?} else {
            GuiGraphics graphics,
            //?}
            int x,
            int y
    ) {
        if (!supportsControls(screen)) {
            return;
        }

        buttonScreen = screen;
        buttonTextX = x;
        buttonY = y + BUTTON_Y_OFFSET;

        for (StatusButton button : StatusButton.values()) {
            blit(
                    graphics,
                    button.currentTexture(
                            pressedButton == button
                    ),
                    buttonX(button),
                    buttonY,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT
            );
        }

        StatusButton hovered = buttonAt(
                mouseX,
                mouseY
        );

        if (hovered != null) {
            renderButtonTooltip(
                    graphics,
                    hovered
            );
        }
    }

    private static void renderButtonTooltip(
            //? if >=26.1 {
            /*GuiGraphicsExtractor graphics,
            *///?} else {
            GuiGraphics graphics,
            //?}
            StatusButton button
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        String message = callerName.isEmpty()
                ? "Incoming call"
                : callerName + " is calling you";
        var lines = java.util.List.of(
                Component.literal(message).getVisualOrderText(),
                Component.literal(button.tooltip).getVisualOrderText()
        );

        //? if >=1.21.6 {
        graphics.setTooltipForNextFrame(
        //?} else {
        /*graphics.renderTooltip(
        *///?}
                minecraft.font,
                lines,
                (int) mouseX,
                (int) mouseY
        );

    }

    private static void onScreenRenderPre(
            ScreenEvent.Render.Pre event
    ) {
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
        buttonScreen = null;

        Minecraft minecraft = Minecraft.getInstance();

        if (!supportsControls(event.getScreen())
                || minecraft.player == null
                || !minecraft.player.hasEffect(
                        ModMobEffects.PLAYER_STATUS
                )) {
            pressedButton = null;
        }
    }

    private static void onRenderInventoryMobEffects(
            ScreenEvent.RenderInventoryMobEffects event
    ) {
        if (!(event.getScreen() instanceof VocoCallerFrontend screen)) {
            return;
        }

        event.setCompact(false);
        event.setHorizontalOffset(
                screen.getEffectPanelX()
        );
    }

    private static void onMouseButtonPressed(
            ScreenEvent.MouseButtonPressed.Pre event
    ) {
        if (event.getButton()
                != InputConstants.MOUSE_BUTTON_LEFT
                || !hasActiveControls(
                        event.getScreen()
                )) {
            return;
        }

        StatusButton button = buttonAt(
                event.getMouseX(),
                event.getMouseY()
        );

        if (button == null) {
            return;
        }

        pressedButton = button;
        event.setCanceled(true);
    }

    private static void onMouseButtonReleased(
            ScreenEvent.MouseButtonReleased.Pre event
    ) {
        if (event.getButton()
                != InputConstants.MOUSE_BUTTON_LEFT
                || pressedButton == null) {
            return;
        }

        StatusButton button = pressedButton;
        pressedButton = null;

        if (!hasActiveControls(
                event.getScreen()
        )) {
            return;
        }

        event.setCanceled(true);

        if (button != buttonAt(
                event.getMouseX(),
                event.getMouseY()
        )) {
            return;
        }

        send(
                button.payload()
        );
    }

    private static boolean supportsControls(
            Screen screen
    ) {
        return screen instanceof InventoryScreen
                || screen instanceof VocoCallerFrontend;
    }

    private static boolean hasActiveControls(
            Screen screen
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        return supportsControls(screen)
                && buttonScreen == screen
                && minecraft.player != null
                && minecraft.player.hasEffect(
                        ModMobEffects.PLAYER_STATUS
                );
    }

    private static StatusButton buttonAt(
            double mouseX,
            double mouseY
    ) {
        for (StatusButton button : StatusButton.values()) {
            int x = buttonX(button);

            if (mouseX >= x
                    && mouseX < x + BUTTON_WIDTH
                    && mouseY >= buttonY
                    && mouseY < buttonY + BUTTON_HEIGHT) {
                return button;
            }
        }

        return null;
    }

    private static int buttonX(
            StatusButton button
    ) {
        return buttonTextX
                + button.xOffset;
    }

    private static void send(
            PlayerStatusActionPayload payload
    ) {
        //? if >=1.21.6 {
        ClientPacketDistributor.sendToServer(payload);
        //?} else {
        /*PacketDistributor.sendToServer(payload);
        *///?}
    }

    //? if >=1.21.11 {
    /*private static Identifier buttonTexture(
    *///?} else {
    private static ResourceLocation buttonTexture(
    //?}
            String name
    ) {
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/voco_caller/"
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/gui/voco_caller/"
        //?}
                                + name
                                + ".png");
    }

    private static void blit(
            //? if >=26.1 {
            /*GuiGraphicsExtractor graphics,
            Identifier texture,
            *///?} else if >=1.21.11 <26.1 {
            /*GuiGraphics graphics,
            Identifier texture,
            *///?} else {
            GuiGraphics graphics,
            ResourceLocation texture,
            //?}
            int x,
            int y,
            int width,
            int height
    ) {

        graphics.blit(
                //? if >=1.21.6 {
                RenderPipelines.GUI_TEXTURED,
                //?} else if >=1.21.2 <1.21.6 {
                /*RenderType::guiTextured,
                *///?}
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

    }

    private enum StatusButton {
        ACCEPT(
                ACCEPT_X_OFFSET,
                "accept",
                buttonTexture("accept"),
                buttonTexture("accept_pressed")
        ),
        CANCEL(
                ACCEPT_X_OFFSET
                        + BUTTON_WIDTH
                        + BUTTON_GAP,
                "cancel",
                buttonTexture("cancel"),
                buttonTexture("cancel_pressed")
        );

        private final int xOffset;
        private final String tooltip;
        //? if >=1.21.11 {
        /*private final Identifier normalTexture;
        private final Identifier pressedTexture;
        *///?} else {
        private final ResourceLocation normalTexture;
        private final ResourceLocation pressedTexture;
        //?}

        StatusButton(
                int xOffset,
                String tooltip,
                //? if >=1.21.11 {
                /*Identifier normalTexture,
                Identifier pressedTexture
                *///?} else {
                ResourceLocation normalTexture,
                ResourceLocation pressedTexture
                //?}
        ) {
            this.xOffset = xOffset;
            this.tooltip = tooltip;
            this.normalTexture = normalTexture;
            this.pressedTexture = pressedTexture;
        }

        //? if >=1.21.11 {
        /*private Identifier currentTexture(
        *///?} else {
        private ResourceLocation currentTexture(
        //?}
                boolean pressed
        ) {
            return pressed
                    ? this.pressedTexture
                    : this.normalTexture;
        }

        private PlayerStatusActionPayload payload() {
            return this == ACCEPT
                    ? PlayerStatusActionPayload.accept()
                    : PlayerStatusActionPayload.cancel();
        }
    }
}
