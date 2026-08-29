package space.anatomyuniverse.musavacca.bar.balance;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import space.anatomyuniverse.musavacca.bar.ModAttachments;

import java.util.Optional;
import java.util.UUID;

public final class BalanceApi {
    private BalanceApi() {
    }

    public static int getBalance(ServerPlayer player) {
        MinecraftServer server = getServer(player);

        if (server == null) {
            return 0;
        }

        return BalanceStore.get(server).getBalance(player.getUUID());
    }

    public static int getBalance(MinecraftServer server, UUID playerUuid) {
        if (server == null || playerUuid == null) {
            return 0;
        }

        return BalanceStore.get(server).getBalance(playerUuid);
    }

    public static int getBalance(MinecraftServer server, String playerName) {
        return findKnownUuid(server, playerName)
                .map(playerUuid -> getBalance(server, playerUuid))
                .orElse(0);
    }

    public static boolean addBalance(CommandSourceStack source, int amount) {
        ServerPlayer player = getSelf(source);
        return player != null && addBalance(player, amount);
    }

    public static boolean addBalance(ServerPlayer player, int amount) {
        MinecraftServer server = getServer(player);

        if (server == null || amount <= 0) {
            return false;
        }

        return addBalance(server, player.getUUID(), amount);
    }

    public static boolean addBalance(MinecraftServer server, UUID playerUuid, int amount) {
        if (server == null || playerUuid == null || amount <= 0) {
            return false;
        }

        BalanceStore store = BalanceStore.get(server);

        int oldBalance = store.getBalance(playerUuid);
        int newBalance = store.addBalance(playerUuid, amount);

        syncIfOnline(server, playerUuid, newBalance);
        return oldBalance != newBalance;
    }

    public static boolean addBalance(MinecraftServer server, String playerName, int amount) {
        return findKnownUuid(server, playerName)
                .map(playerUuid -> addBalance(server, playerUuid, amount))
                .orElse(false);
    }

    public static boolean deductBalance(CommandSourceStack source, int amount) {
        ServerPlayer player = getSelf(source);
        return player != null && deductBalance(player, amount);
    }

    public static boolean deductBalance(ServerPlayer player, int amount) {
        MinecraftServer server = getServer(player);

        if (server == null || amount <= 0) {
            return false;
        }

        return deductBalance(server, player.getUUID(), amount);
    }

    public static boolean deductBalance(MinecraftServer server, UUID playerUuid, int amount) {
        if (server == null || playerUuid == null || amount <= 0) {
            return false;
        }

        BalanceStore store = BalanceStore.get(server);
        boolean deducted = store.tryDeductBalance(playerUuid, amount);

        if (deducted) {
            syncIfOnline(server, playerUuid, store.getBalance(playerUuid));
        }

        return deducted;
    }

    public static boolean deductBalance(MinecraftServer server, String playerName, int amount) {
        return findKnownUuid(server, playerName)
                .map(playerUuid -> deductBalance(server, playerUuid, amount))
                .orElse(false);
    }

    public static boolean setBalance(ServerPlayer player, int balance) {
        MinecraftServer server = getServer(player);

        if (server == null) {
            return false;
        }

        return setBalance(server, player.getUUID(), balance);
    }

    public static boolean setBalance(MinecraftServer server, UUID playerUuid, int balance) {
        if (server == null || playerUuid == null) {
            return false;
        }

        BalanceStore store = BalanceStore.get(server);

        int oldBalance = store.getBalance(playerUuid);
        int newBalance = store.setBalance(playerUuid, balance);

        syncIfOnline(server, playerUuid, newBalance);
        return oldBalance != newBalance;
    }

    public static boolean setBalance(MinecraftServer server, String playerName, int balance) {
        return findKnownUuid(server, playerName)
                .map(playerUuid -> setBalance(server, playerUuid, balance))
                .orElse(false);
    }

    public static boolean hasBalance(ServerPlayer player, int amount) {
        MinecraftServer server = getServer(player);

        if (server == null || amount < 0) {
            return false;
        }

        return hasBalance(server, player.getUUID(), amount);
    }

    public static boolean hasBalance(MinecraftServer server, UUID playerUuid, int amount) {
        if (server == null || playerUuid == null || amount < 0) {
            return false;
        }

        return BalanceStore.get(server).hasBalance(playerUuid, amount);
    }

    public static boolean hasBalance(MinecraftServer server, String playerName, int amount) {
        return findKnownUuid(server, playerName)
                .map(playerUuid -> hasBalance(server, playerUuid, amount))
                .orElse(false);
    }

    public static void refreshPlayer(ServerPlayer player) {
        MinecraftServer server = getServer(player);

        if (server == null) {
            return;
        }

        BalanceStore store = BalanceStore.get(server);
        store.rememberPlayer(player);

        BalanceData oldAttachment = player.getData(ModAttachments.BALANCE);

        if (!store.containsBalance(player.getUUID()) && oldAttachment.getBalance() > 0) {
            store.setBalance(player.getUUID(), oldAttachment.getBalance());
        }

        sync(player);
    }

    public static void sync(ServerPlayer player) {
        MinecraftServer server = getServer(player);

        if (server == null) {
            return;
        }

        BalanceStore store = BalanceStore.get(server);
        syncPlayer(player, store.getBalance(player.getUUID()));
    }

    private static void syncIfOnline(MinecraftServer server, UUID playerUuid, int balance) {
        ServerPlayer player = getOnlinePlayer(server, playerUuid);

        if (player != null) {
            syncPlayer(player, balance);
        }
    }

    private static void syncPlayer(ServerPlayer player, int balance) {
        BalanceData data = player.getData(ModAttachments.BALANCE);
        data.setBalance(balance);
        player.setData(ModAttachments.BALANCE, data);

        PacketDistributor.sendToPlayer(
                player,
                new BalanceSyncPayload(data.getBalance(), data.isVisible())
        );
    }

    private static Optional<UUID> findKnownUuid(MinecraftServer server, String playerName) {
        if (server == null || playerName == null || playerName.isBlank()) {
            return Optional.empty();
        }

        ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(playerName);

        if (onlinePlayer != null) {
            BalanceStore.get(server).rememberPlayer(onlinePlayer);
            return Optional.of(onlinePlayer.getUUID());
        }

        return BalanceStore.get(server).findKnownUuid(playerName);
    }

    private static ServerPlayer getSelf(CommandSourceStack source) {
        if (source == null) {
            return null;
        }

        try {
            return source.getPlayerOrException();
        } catch (CommandSyntaxException ignored) {
            return null;
        }
    }

    private static ServerPlayer getOnlinePlayer(MinecraftServer server, UUID playerUuid) {
        if (server == null || playerUuid == null) {
            return null;
        }

        return server.getPlayerList().getPlayer(playerUuid);
    }

    private static MinecraftServer getServer(ServerPlayer player) {
        if (player == null) {
            return null;
        }

        return player.level().getServer();
    }
}
