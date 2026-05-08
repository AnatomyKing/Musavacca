// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/hunger/BonusHungerHud.java
package space.anatomyuniverse.musavacca.bar.hunger;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.bar.RightSideHudLayout;

public final class BonusHungerHud {
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;
    private static final int ICON_COUNT = 10;

    private static final long VANILLA_HUD_RANDOM_SEED_MULTIPLIER = 312871L;

    private static final RandomSource RANDOM = RandomSource.create();

    private static final ResourceLocation FOOD_EMPTY =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty");
    private static final ResourceLocation FOOD_HALF =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half");
    private static final ResourceLocation FOOD_FULL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full");

    private static final ResourceLocation FOOD_EMPTY_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER =
            ResourceLocation.fromNamespaceAndPath("minecraft", "hud/food_full_hunger");

    private BonusHungerHud() {
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

        if (!ClientBonusHungerData.isActive()) {
            return;
        }

        if (player.isSpectator() || player.getAbilities().instabuild) {
            return;
        }

        int food = ClientBonusHungerData.getFood();
        float saturation = ClientBonusHungerData.getSaturation();

        boolean hungerEffect = player.hasEffect(MobEffects.HUNGER);

        ResourceLocation emptySprite = hungerEffect ? FOOD_EMPTY_HUNGER : FOOD_EMPTY;
        ResourceLocation halfSprite = hungerEffect ? FOOD_HALF_HUNGER : FOOD_HALF;
        ResourceLocation fullSprite = hungerEffect ? FOOD_FULL_HUNGER : FOOD_FULL;

        int right = RightSideHudLayout.getRightSideAnchorX(graphics);
        int baseY = RightSideHudLayout.getFirstFreeRightSideRowY(graphics, player);

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

    private static boolean shouldJitter(int food, float saturation, int tickCount) {
        if (saturation > 0.0F) {
            return false;
        }

        int interval = food * 3 + 1;
        return interval > 0 && tickCount % interval == 0;
    }

    private static void drawFoodIcon(
            GuiGraphics graphics,
            int x,
            int y,
            int pointsInSlot,
            ResourceLocation emptySprite,
            ResourceLocation halfSprite,
            ResourceLocation fullSprite
    ) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, emptySprite, x, y, ICON_SIZE, ICON_SIZE);

        if (pointsInSlot >= 2) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, fullSprite, x, y, ICON_SIZE, ICON_SIZE);
        } else if (pointsInSlot == 1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, halfSprite, x, y, ICON_SIZE, ICON_SIZE);
        }
    }
}