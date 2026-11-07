package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.bar.hunger.ClientBonusHungerData;

public final class BalanceHud {
    //? if >=1.21.11 {
    /*private static final Identifier[] PEARL_ICON_LAYERS = {
    *///?} else {
    private static final ResourceLocation[] PEARL_ICON_LAYERS = {
    //?}
            sprite("hud/pearl_husk"), sprite("hud/pearl") };

    private BalanceHud() {}

    //? if >=26.1 {
    /*public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    *///?} else {
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (!shouldRender(minecraft, player)) return;
        Font font = minecraft.font;
        String text = Integer.toString(ClientBalanceData.getBalance());
        int textWidth = font.width(text);
        int totalWidth = textWidth + 1 + 9;
        int right = graphics.guiWidth() / 2 + 91;
        int y = getY(graphics, player);
        int textX = right - totalWidth;
        int iconX = textX + textWidth + 1;
        drawOutlinedString(graphics, font, text, textX, y + 1);
        drawSpriteLayers(graphics, PEARL_ICON_LAYERS, iconX, y);
    }

    private static boolean shouldRender(Minecraft minecraft, Player player) {
        //? if >=26.2 {
        /*return !minecraft.gui.hud.isHidden()
        *///?} else {
        return !minecraft.options.hideGui
        //?}
                && player != null && ClientBalanceData.isActive() && !player.isSpectator() && !player.getAbilities().instabuild
                && !hasVisibleMountHealth(player);
    }

    //? if >=26.1 {
    /*private static int getY(GuiGraphicsExtractor graphics, Player player) {
    *///?} else {
    private static int getY(GuiGraphics graphics, Player player) {
    //?}
        int y = graphics.guiHeight() - 49;
        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) {
            y -= 10;
        }
        if (ClientBonusHungerData.isActive()) {
            y -= 10;
        }
        return y;
    }

    private static boolean hasVisibleMountHealth(Player player) {
        return player.getVehicle() instanceof LivingEntity vehicle && vehicle.isAlive() && vehicle.getMaxHealth() > 0.0F;
    }

    //? if >=26.1 {
    /*private static void drawSpriteLayers(GuiGraphicsExtractor graphics, Identifier[] sprites, int x, int y) {
    *///?} else if >=1.21.11 <26.1 {
    /*private static void drawSpriteLayers(GuiGraphics graphics, Identifier[] sprites, int x, int y) {
    *///?} else {
    private static void drawSpriteLayers(GuiGraphics graphics, ResourceLocation[] sprites, int x, int y) {
    //?}
        for (var sprite : sprites) {
            drawSprite(graphics, sprite, x, y);
        }
    }

    //? if >=26.1 {
    /*private static void drawSprite(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y) {
    *///?} else if >=1.21.11 <26.1 {
    /*private static void drawSprite(GuiGraphics graphics, Identifier sprite, int x, int y) {
    *///?} else {
    private static void drawSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y) {
    //?}
        //? if >=1.21.6 {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 9, 9);
        //?} else if >=1.21.2 <1.21.6 {
        /*graphics.blitSprite(RenderType::guiTextured, sprite, x, y, 9, 9);
        *///?} else {
        /*graphics.blitSprite(sprite, x, y, 9, 9);
        *///?}
    }

    //? if >=26.1 {
    /*private static void drawOutlinedString(GuiGraphicsExtractor graphics, Font font, String text, int x, int y) {
        graphics.text(font, text, x + 1, y, 0xFF000000, false);
        graphics.text(font, text, x - 1, y, 0xFF000000, false);
        graphics.text(font, text, x, y + 1, 0xFF000000, false);
        graphics.text(font, text, x, y - 1, 0xFF000000, false);
        graphics.text(font, text, x, y, 0xFFD5C349, false);
    *///?} else {
    private static void drawOutlinedString(GuiGraphics graphics, Font font, String text, int x, int y) {
        graphics.drawString(font, text, x + 1, y, 0xFF000000, false);
        graphics.drawString(font, text, x - 1, y, 0xFF000000, false);
        graphics.drawString(font, text, x, y + 1, 0xFF000000, false);
        graphics.drawString(font, text, x, y - 1, 0xFF000000, false);
        graphics.drawString(font, text, x, y, 0xFFD5C349, false);
    //?}
    }

    //? if >=1.21.11 {
    /*private static Identifier sprite(String path) {
        return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, path);
    *///?} else {
    private static ResourceLocation sprite(String path) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path);
    //?}
    }
}
