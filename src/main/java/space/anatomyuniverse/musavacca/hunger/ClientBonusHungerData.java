package space.anatomyuniverse.musavacca.hunger;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class ClientBonusHungerData {
    private static int food = BonusHungerData.MAX_FOOD;
    private static float saturation = BonusHungerData.MAX_FOOD;
    private static boolean active = false;

    private ClientBonusHungerData() {
    }

    public static void set(int newFood, float newSaturation, boolean newActive) {
        food = Mth.clamp(newFood, 0, BonusHungerData.MAX_FOOD);
        saturation = Mth.clamp(newSaturation, 0.0F, food);
        active = newActive;
    }

    public static int getFood() {
        return food;
    }

    public static float getSaturation() {
        return saturation;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean needsFood() {
        return active && food < BonusHungerData.MAX_FOOD;
    }

    public static boolean shouldJitter(Player player) {
        if (!active) {
            return false;
        }

        if (saturation > 0.0F) {
            return false;
        }

        int interval = food * 3 + 1;
        return interval > 0 && player.tickCount % interval == 0;
    }
}