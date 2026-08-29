package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.21.6
import net.minecraft.client.renderer.RenderPipelines;
//? if <1.21.6
////? if >=1.21.2
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.bar.hunger.ClientBonusHungerData;

public final class BalanceHud {
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 1;
    private static final int TEXT_Y_OFFSET = 1;

    private static final int VANILLA_FOOD_RIGHT = 91;
    private static final int BALANCE_BASE_Y_OFFSET = 49;
    private static final int ROW_SPACING = 10;

    private static final int TEXT_COLOR = 0xFFD5C349;
    private static final int SHADOW_COLOR = 0xFF000000;

    private static final ResourceLocation[] PEARL_ICON_LAYERS = {
            sprite("hud/pearl_husk"),
            sprite("hud/pearl")
    };

    private BalanceHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (!shouldRender(minecraft, player)) {
            return;
        }

        Font font = minecraft.font;
        String text = Integer.toString(ClientBalanceData.getBalance());

        int textWidth = font.width(text);
        int totalWidth = textWidth + ICON_SPACING + ICON_SIZE;

        int right = graphics.guiWidth() / 2 + VANILLA_FOOD_RIGHT;
        int y = getY(graphics, player);

        int textX = right - totalWidth;
        int iconX = textX + textWidth + ICON_SPACING;

        drawOutlinedString(graphics, font, text, textX, y + TEXT_Y_OFFSET);
        drawSpriteLayers(graphics, PEARL_ICON_LAYERS, iconX, y);
    }

    private static boolean shouldRender(Minecraft minecraft, Player player) {
        return !minecraft.options.hideGui
                && player != null
                && ClientBalanceData.isActive()
                && !player.isSpectator()
                && !player.getAbilities().instabuild
                && !hasVisibleMountHealth(player);
    }

    private static int getY(GuiGraphics graphics, Player player) {
        int y = graphics.guiHeight() - BALANCE_BASE_Y_OFFSET;

        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) {
            y -= ROW_SPACING;
        }

        if (ClientBonusHungerData.isActive()) {
            y -= ROW_SPACING;
        }

        return y;
    }

    private static boolean hasVisibleMountHealth(Player player) {
        return player.getVehicle() instanceof LivingEntity vehicle
                && vehicle.isAlive()
                && vehicle.getMaxHealth() > 0.0F;
    }

    private static void drawSpriteLayers(GuiGraphics graphics, ResourceLocation[] sprites, int x, int y) {
        for (ResourceLocation sprite : sprites) {
            drawSprite(graphics, sprite, x, y);
        }
    }

    private static void drawSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y) {
        //? if >=1.21.6
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, ICON_SIZE, ICON_SIZE);
        //? if >=1.21.2 && <1.21.6
        //graphics.blitSprite(RenderType::guiTextured, sprite, x, y, ICON_SIZE, ICON_SIZE);
        //? if <1.21.2
        //graphics.blitSprite(sprite, x, y, ICON_SIZE, ICON_SIZE);
    }

    private static void drawOutlinedString(GuiGraphics graphics, Font font, String text, int x, int y) {
        graphics.drawString(font, text, x + 1, y, SHADOW_COLOR, false);
        graphics.drawString(font, text, x - 1, y, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y + 1, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y - 1, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y, TEXT_COLOR, false);
    }

    private static ResourceLocation sprite(String path) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path);
    }
}


