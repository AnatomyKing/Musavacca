package space.anatomyuniverse.musavacca.effect.playerstatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//? if <1.21.2 {
/*import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
*///?} else {
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//?}
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
//? if <1.21.6
////? if >=1.21.2
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
//? if <1.21.11 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;

//? if >=1.21.7
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//? if <1.21.7
//import net.neoforged.neoforge.network.PacketDistributor;

import org.lwjgl.glfw.GLFW;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;

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

    private static final int EXTEND_X_OFFSET = 56;
    private static final int BUTTON_Y_OFFSET = 15;

    private static Screen buttonScreen;
    private static int buttonTextX;
    private static int buttonY;
    private static double mouseX;
    private static double mouseY;
    private static StatusButton pressedButton;

    private static final IClientMobEffectExtensions EXTENSIONS =
            new IClientMobEffectExtensions() {
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

                //? if <1.21.2 {
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
                *///?} else {
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
                //?}
            };

    private PlayerStatusClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(
                PlayerStatusClient::registerExtensions
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onScreenRenderPre
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onMouseButtonPressed
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerStatusClient::onMouseButtonReleased
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

    private static boolean renderPlayerFaces(
            GuiGraphics graphics,
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

        //? if <1.21.2 {
        /*graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                alpha
        );

        PlayerFaceRenderer.draw(
                graphics,
                minecraft.player.getSkin().texture(),
                firstX,
                firstY,
                FACE_SIZE,
                true,
                false
        );

        PlayerFaceRenderer.draw(
                graphics,
                minecraft.player.getSkin().texture(),
                secondX,
                secondY,
                FACE_SIZE,
                true,
                false
        );

        graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
        *///?} else {
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
                minecraft.player.getSkin(),
                firstX,
                firstY,
                FACE_SIZE,
                color
        );

        PlayerFaceRenderer.draw(
                graphics,
                minecraft.player.getSkin(),
                secondX,
                secondY,
                FACE_SIZE,
                color
        );
        //?}

        return true;
    }

    private static void renderControls(
            Screen screen,
            GuiGraphics graphics,
            int x,
            int y
    ) {
        if (!(screen instanceof InventoryScreen)) {
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
            GuiGraphics graphics,
            StatusButton button
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        Component tooltip = Component.literal(
                minecraft.player.getGameProfile().getName()
                        + " "
                        + button.tooltip
        );

        //? if >=1.21.6 {
        graphics.setTooltipForNextFrame(
                minecraft.font,
                tooltip,
                (int) mouseX,
                (int) mouseY
        );
        //?} else {
        /*graphics.renderTooltip(
                minecraft.font,
                tooltip,
                (int) mouseX,
                (int) mouseY
        );
        *///?}
    }

    private static void onScreenRenderPre(
            ScreenEvent.Render.Pre event
    ) {
        mouseX = event.getMouseX();
        mouseY = event.getMouseY();
        buttonScreen = null;

        Minecraft minecraft = Minecraft.getInstance();

        if (!(event.getScreen() instanceof InventoryScreen)
                || minecraft.player == null
                || !minecraft.player.hasEffect(
                        ModMobEffects.PLAYER_STATUS
                )) {
            pressedButton = null;
        }
    }

    private static void onMouseButtonPressed(
            ScreenEvent.MouseButtonPressed.Pre event
    ) {
        if (event.getButton()
                != GLFW.GLFW_MOUSE_BUTTON_LEFT
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
                != GLFW.GLFW_MOUSE_BUTTON_LEFT
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

    private static boolean hasActiveControls(
            Screen screen
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        return screen instanceof InventoryScreen
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
        //? if >=1.21.7 {
        ClientPacketDistributor.sendToServer(payload);
        //?} else {
        /*PacketDistributor.sendToServer(payload);
        *///?}
    }

    private static /*? if <1.21.11 {*/ ResourceLocation
            /*?} else {*//*Identifier*//*?}*/ buttonTexture(
            String name
    ) {
        return /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/
                .fromNamespaceAndPath(
                        MusaCore.MOD_ID,
                        "textures/gui/voco_caller/"
                                + name
                                + ".png"
                );
    }

    private static void blit(
            GuiGraphics graphics,
            /*? if <1.21.11 {*/ ResourceLocation
            /*?} else {*//*Identifier*//*?}*/ texture,
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
        /*graphics.blit(
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

    private enum StatusButton {
        EXTEND(
                EXTEND_X_OFFSET,
                "accept",
                buttonTexture("accept"),
                buttonTexture("accept_pressed")
        ),
        CLEAR(
                EXTEND_X_OFFSET
                        + BUTTON_WIDTH
                        + BUTTON_GAP,
                "cancel",
                buttonTexture("cancel"),
                buttonTexture("cancel_pressed")
        );

        private final int xOffset;
        private final String tooltip;
        private final /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/ normalTexture;
        private final /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/ pressedTexture;

        StatusButton(
                int xOffset,
                String tooltip,
                /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/ normalTexture,
                /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/ pressedTexture
        ) {
            this.xOffset = xOffset;
            this.tooltip = tooltip;
            this.normalTexture = normalTexture;
            this.pressedTexture = pressedTexture;
        }

        private /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/ currentTexture(
                boolean pressed
        ) {
            return pressed
                    ? this.pressedTexture
                    : this.normalTexture;
        }

        private PlayerStatusActionPayload payload() {
            return this == EXTEND
                    ? PlayerStatusActionPayload.extend()
                    : PlayerStatusActionPayload.clear();
        }
    }
}
