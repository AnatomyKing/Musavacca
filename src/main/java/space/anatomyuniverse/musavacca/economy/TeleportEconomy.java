package space.anatomyuniverse.musavacca.economy;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import space.anatomyuniverse.musavacca.bar.balance.BalanceApi;

import java.util.function.BooleanSupplier;

public final class TeleportEconomy {
    private TeleportEconomy() {}

    public static boolean tryTeleport(ServerPlayer player, int cost, BooleanSupplier teleport) {
        if (player == null || teleport == null) return false;

        int safeCost = Math.max(0, cost);
        if (safeCost > 0 && !BalanceApi.deductBalance(player, safeCost)) {
            showNeedsBalanceMessage(player, safeCost);
            return false;
        }

        boolean success = false;
        try {
            success = teleport.getAsBoolean();
            return success;
        } finally {
            if (!success && safeCost > 0) BalanceApi.addBalance(player, safeCost);
        }
    }

    public static boolean canAfford(ServerPlayer player, int cost) {
        if (player == null) return false;

        int safeCost = Math.max(0, cost);
        if (safeCost == 0 || BalanceApi.hasBalance(player, safeCost)) return true;

        showNeedsBalanceMessage(player, safeCost);
        return false;
    }

    public static boolean tryCharge(Entity entity, int cost) {
        int safeCost = Math.max(0, cost);

        if (safeCost == 0 || !(entity instanceof ServerPlayer player)) {
            return true;
        }

        if (BalanceApi.deductBalance(player, safeCost)) {
            return true;
        }

        showNeedsBalanceMessage(player, safeCost);
        return false;
    }

    private static void showNeedsBalanceMessage(ServerPlayer player, int cost) {
        //? if >=26.1 {
        /*player.sendOverlayMessage(Component.literal("You need " + cost + " balance to teleport."));
        *///?} else {
        player.displayClientMessage(Component.literal("You need " + cost + " balance to teleport."), true);
        //?}
    }
}
