package space.anatomyuniverse.musavacca.bar.hunger;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.minecraft.client.renderer.RenderPipelines;
*///?} else if >=1.21.6 <26.1 {
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.renderer.RenderPipelines;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
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
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;
    private static final int ICON_COUNT = 10;

    private static final int VANILLA_FOOD_RIGHT = 91;
    private static final int BONUS_FOOD_BASE_Y_OFFSET = 49;
    private static final int ROW_SPACING = 10;

    private static final long VANILLA_HUD_RANDOM_SEED_MULTIPLIER = 312871L;

    private static final RandomSource RANDOM = RandomSource.create();

    //? if >=1.21.11 {
    /*private static final Identifier FOOD_EMPTY =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty");
    private static final Identifier FOOD_HALF =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_half");
    private static final Identifier FOOD_FULL =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_full");
    *///?} else {
    private static final ResourceLocation FOOD_EMPTY =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty");
    private static final ResourceLocation FOOD_HALF =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half");
    private static final ResourceLocation FOOD_FULL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full");
    //?}

    //? if >=1.21.11 {
    /*private static final Identifier FOOD_EMPTY_HUNGER =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger");
    private static final Identifier FOOD_HALF_HUNGER =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_half_hunger");
    private static final Identifier FOOD_FULL_HUNGER =
            Identifier.fromNamespaceAndPath("minecraft", "hud/food_full_hunger");
    *///?} else {
    private static final ResourceLocation FOOD_EMPTY_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full_hunger");
    //?}

    private BonusHungerHud() {
    }

    //? if >=26.1 {
    /*public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
    *///?} else {
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    //?}
        Minecraft minecraft = Minecraft.getInstance();

        //? if >=26.2 {
        /*if (minecraft.gui.hud.isHidden()) {
        *///?} else {
        if (minecraft.options.hideGui) {
        //?}
            return;
        }

        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        if (!ClientBonusHungerData.isActive()) {
            return;
        }

        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }

        if (shouldHideForMountHealth(player)) {
            return;
        }

        int food = ClientBonusHungerData.getFood();
        float saturation = ClientBonusHungerData.getSaturation();

        boolean hungerEffect = player.hasEffect(MobEffects.HUNGER);

        //? if >=1.21.11 {
        /*Identifier emptySprite = hungerEffect ? FOOD_EMPTY_HUNGER : FOOD_EMPTY;
        Identifier halfSprite = hungerEffect ? FOOD_HALF_HUNGER : FOOD_HALF;
        Identifier fullSprite = hungerEffect ? FOOD_FULL_HUNGER : FOOD_FULL;
        *///?} else {
        ResourceLocation emptySprite = hungerEffect ? FOOD_EMPTY_HUNGER : FOOD_EMPTY;
        ResourceLocation halfSprite = hungerEffect ? FOOD_HALF_HUNGER : FOOD_HALF;
        ResourceLocation fullSprite = hungerEffect ? FOOD_FULL_HUNGER : FOOD_FULL;
        //?}

        int right = graphics.guiWidth() / 2 + VANILLA_FOOD_RIGHT;
        int baseY = getBonusFoodY(graphics, player);

        boolean shouldJitter = shouldJitter(food, saturation, player.tickCount);

        RANDOM.setSeed((long) player.tickCount * VANILLA_HUD_RANDOM_SEED_MULTIPLIER);

        for (int slot = 0; slot < ICON_COUNT; slot++) {
            int x = right - slot * ICON_SPACING - 9;
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
        int y = graphics.guiHeight() - BONUS_FOOD_BASE_Y_OFFSET;

        if (shouldReserveAirBubbleRow(player)) {
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

    private static boolean shouldJitter(int food, float saturation, int tickCount) {
        if (saturation > 0.0F) {
            return false;
        }

        int interval = food * 3 + 1;
        return interval > 0 && tickCount % interval == 0;
    }

    private static void drawFoodIcon(
            //? if >=26.1 {
            /*GuiGraphicsExtractor graphics,
            *///?} else {
            GuiGraphics graphics,
            //?}
            int x,
            int y,
            int pointsInSlot,
            //? if >=1.21.11 {
            /*Identifier emptySprite,
            Identifier halfSprite,
            Identifier fullSprite
            *///?} else {
            ResourceLocation emptySprite,
            ResourceLocation halfSprite,
            ResourceLocation fullSprite
            //?}
    ) {
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
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, ICON_SIZE, ICON_SIZE);

        //?} else if >=1.21.2 <1.21.6 {
        /*graphics.blitSprite(RenderType::guiTextured, sprite, x, y, ICON_SIZE, ICON_SIZE);
        *///?} else {
        /*graphics.blitSprite(sprite, x, y, ICON_SIZE, ICON_SIZE);
        *///?}
    }
}
