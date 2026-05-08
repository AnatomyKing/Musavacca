// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/RightSideHudLayout.java
package space.anatomyuniverse.musavacca.bar;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class RightSideHudLayout {
    public static final int VANILLA_FOOD_RIGHT = 91;
    public static final int VANILLA_FOOD_BASE_Y_OFFSET = 49;
    public static final int ROW_SPACING = 10;

    private static final int HEARTS_PER_ROW = 10;
    private static final int MAX_VEHICLE_HEARTS = 30;

    private RightSideHudLayout() {
    }

    public static int getRightSideAnchorX(GuiGraphics graphics) {
        return graphics.guiWidth() / 2 + VANILLA_FOOD_RIGHT;
    }

    public static int getFirstFreeRightSideRowY(GuiGraphics graphics, Player player) {
        int y = graphics.guiHeight() - VANILLA_FOOD_BASE_Y_OFFSET;

        if (shouldReserveAirBubbleRow(player)) {
            y -= ROW_SPACING;
        }

        y -= getVisibleVehicleHeartRows(player) * ROW_SPACING;

        return y;
    }

    public static int getRowAbove(int y) {
        return y - ROW_SPACING;
    }

    public static boolean shouldReserveAirBubbleRow(Player player) {
        return player.isEyeInFluid(FluidTags.WATER)
                || player.getAirSupply() < player.getMaxAirSupply();
    }

    public static int getVisibleVehicleHeartRows(Player player) {
        Entity vehicle = player.getVehicle();

        if (!(vehicle instanceof LivingEntity livingVehicle)) {
            return 0;
        }

        if (!livingVehicle.isAlive()) {
            return 0;
        }

        int hearts = (int) Math.ceil(livingVehicle.getMaxHealth() / 2.0F);
        hearts = Math.max(0, Math.min(hearts, MAX_VEHICLE_HEARTS));

        return (int) Math.ceil((double) hearts / (double) HEARTS_PER_ROW);
    }
}