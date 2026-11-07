package space.anatomyuniverse.musavacca.bar.hunger;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class BonusHungerHud {
    private static final RandomSource RANDOM = RandomSource.create();
    //? if >=1.21.11 {
    /*private static final Identifier FOOD_EMPTY = Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty");
    private static final Identifier FOOD_HALF = Identifier.fromNamespaceAndPath("minecraft", "hud/food_half");
    private static final Identifier FOOD_FULL = Identifier.fromNamespaceAndPath("minecraft", "hud/food_full");
    private static final Identifier FOOD_EMPTY_HUNGER = Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger");
    private static final Identifier FOOD_HALF_HUNGER = Identifier.fromNamespaceAndPath("minecraft", "hud/food_half_hunger");
    private static final Identifier FOOD_FULL_HUNGER = Identifier.fromNamespaceAndPath("minecraft", "hud/food_full_hunger");

    *///?} else {
    private static final ResourceLocation FOOD_EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty");
    private static final ResourceLocation FOOD_HALF = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half");
    private static final ResourceLocation FOOD_FULL = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full");
    private static final ResourceLocation FOOD_EMPTY_HUNGER = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER = ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full_hunger");

    //?}
    private BonusHungerHud() {}

    //? if >=26.1 {
    /*public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    *///?} else {
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        /*if (minecraft.gui.hud.isHidden()) return;
        *///?} else {
        if (minecraft.options.hideGui) return;
        //?}
        Player player = minecraft.player;
        if (player == null) return;
        if (!ClientBonusHungerData.isActive()) return;
        if (player.isSpectator() || player.getAbilities().instabuild) return;
        if (shouldHideForMountHealth(player)) return;
        int food = ClientBonusHungerData.getFood();
        float saturation = ClientBonusHungerData.getSaturation();
        boolean hungerEffect = player.hasEffect(MobEffects.HUNGER);
        var emptySprite = hungerEffect ? FOOD_EMPTY_HUNGER : FOOD_EMPTY;
        var halfSprite = hungerEffect ? FOOD_HALF_HUNGER : FOOD_HALF;
        var fullSprite = hungerEffect ? FOOD_FULL_HUNGER : FOOD_FULL;
        int right = graphics.guiWidth() / 2 + 91;
        int baseY = getBonusFoodY(graphics, player);
        boolean shouldJitter = shouldJitter(food, saturation, player.tickCount);
        RANDOM.setSeed((long) player.tickCount * 312871L);
        for (int slot = 0; slot < 10; slot++) {
            int x = right - slot * 8 - 9;
            int y = baseY;
            if (shouldJitter) {
                y += RANDOM.nextInt(3) - 1;
            }
            int pointsInSlot = food - slot * 2;
            drawFoodIcon(graphics, x, y, pointsInSlot, emptySprite, halfSprite, fullSprite);
        }
    }

    //? if >=26.1 {
    /*private static int getBonusFoodY(GuiGraphicsExtractor graphics, Player player) {
    *///?} else {
    private static int getBonusFoodY(GuiGraphics graphics, Player player) {
    //?}
        int y = graphics.guiHeight() - 49;
        if (shouldReserveAirBubbleRow(player)) {
            y -= 10;
        }
        return y;
    }

    private static boolean shouldReserveAirBubbleRow(Player player) {
        return player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply();
    }

    private static boolean shouldHideForMountHealth(Player player) {
        return player.getVehicle() instanceof LivingEntity vehicle && vehicle.isAlive() && vehicle.getMaxHealth() > 0.0F;
    }

    private static boolean shouldJitter(int food, float saturation, int tickCount) {
        if (saturation > 0.0F) return false;
        int interval = food * 3 + 1;
        return interval > 0 && tickCount % interval == 0;
    }

    //? if >=26.1 {
    /*private static void drawFoodIcon(GuiGraphicsExtractor graphics, int x, int y, int pointsInSlot, Identifier emptySprite, Identifier halfSprite,
            Identifier fullSprite) {
    *///?} else if >=1.21.11 {
    /*private static void drawFoodIcon(GuiGraphics graphics, int x, int y, int pointsInSlot, Identifier emptySprite, Identifier halfSprite,
            Identifier fullSprite) {
    *///?} else {
    private static void drawFoodIcon(GuiGraphics graphics, int x, int y, int pointsInSlot, ResourceLocation emptySprite, ResourceLocation halfSprite,
            ResourceLocation fullSprite) {
    //?}
        drawSprite(graphics, emptySprite, x, y);
        if (pointsInSlot >= 2) {
            drawSprite(graphics, fullSprite, x, y);
        } else if (pointsInSlot == 1) {
            drawSprite(graphics, halfSprite, x, y);
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
}
