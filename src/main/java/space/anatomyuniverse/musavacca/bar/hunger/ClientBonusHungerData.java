package space.anatomyuniverse.musavacca.bar.hunger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class ClientBonusHungerData {
    private static int food = BonusHungerData.MAX_FOOD;
    private static float saturation = BonusHungerData.MAX_FOOD;
    private static boolean active = false;

    private ClientBonusHungerData() {}

    public static void set(int newFood, float newSaturation, boolean newActive) {
        food = Mth.clamp(newFood, 0, BonusHungerData.MAX_FOOD);
        saturation = Mth.clamp(newSaturation, 0.0F, food);
        active = newActive;
    }

    public static int getFood() {
        return Math.min(food, getCapacity());
    }

    public static float getSaturation() {
        return Math.min(saturation, getFood());
    }

    public static int getCapacity() {
        Player player = Minecraft.getInstance().player;
        return player == null ? 0 : BonusHungerEvents.getCapacity(player);
    }

    public static boolean isActive() {
        return active && getCapacity() > 0;
    }

    public static boolean needsFood() {
        return isActive() && getFood() < getCapacity();
    }
}
