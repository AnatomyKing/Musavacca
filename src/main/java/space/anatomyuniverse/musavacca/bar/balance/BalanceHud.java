// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/balance/BalanceHud.java
package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.bar.hunger.ClientBonusHungerData;

public final class BalanceHud {
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 1;

    private static final int VANILLA_FOOD_RIGHT = 91;
    private static final int BALANCE_BASE_Y_OFFSET = 49;
    private static final int ROW_SPACING = 10;

    private static final int TEXT_COLOR = 0xFF80FF20;
    private static final int SHADOW_COLOR = 0xFF000000;

    private static final ResourceLocation BALANCE_ICON =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/heart/absorbing_full");

    private BalanceHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.hideGui) {
            return;
        }

        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        if (!ClientBalanceData.isActive()) {
            return;
        }

        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }

        if (shouldHideForMountHealth(player)) {
            return;
        }

        int balance = ClientBalanceData.getBalance();
        String text = Integer.toString(balance);

        Font font = minecraft.font;

        int right = graphics.guiWidth() / 2 + VANILLA_FOOD_RIGHT;
        int y = getBalanceY(graphics, player);

        int textWidth = font.width(text);
        int totalWidth = textWidth + ICON_SPACING + ICON_SIZE;

        int x = right - totalWidth;
        int iconX = x + textWidth + ICON_SPACING;

        drawVanillaNumber(graphics, font, text, x, y);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BALANCE_ICON, iconX, y, ICON_SIZE, ICON_SIZE);
    }

    private static int getBalanceY(GuiGraphics graphics, Player player) {
        int y = graphics.guiHeight() - BALANCE_BASE_Y_OFFSET;

        if (shouldReserveAirBubbleRow(player)) {
            y -= ROW_SPACING;
        }

        if (ClientBonusHungerData.isActive()) {
            y -= ROW_SPACING;
        }

        return y;
    }

    private static boolean shouldReserveAirBubbleRow(Player player) {
        return player.isEyeInFluid(FluidTags.WATER)
                || player.getAirSupply() < player.getMaxAirSupply();
    }

    private static boolean shouldHideForMountHealth(Player player) {
        return player.getVehicle() instanceof LivingEntity vehicle
                && vehicle.isAlive()
                && vehicle.getMaxHealth() > 0.0F;
    }

    private static void drawVanillaNumber(GuiGraphics graphics, Font font, String text, int x, int y) {
        graphics.drawString(font, text, x + 1, y, SHADOW_COLOR, false);
        graphics.drawString(font, text, x - 1, y, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y + 1, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y - 1, SHADOW_COLOR, false);
        graphics.drawString(font, text, x, y, TEXT_COLOR, false);
    }
}