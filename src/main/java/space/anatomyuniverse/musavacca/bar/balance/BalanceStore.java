package space.anatomyuniverse.musavacca.bar.balance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BalanceStore extends SavedData {
    public static final String STORAGE_ID = "musavacca_balance_store";

    private static final int MAX_BALANCE = Integer.MAX_VALUE;

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(
            BalanceStore::readUuid,
            UUID::toString
    );

    private static final Codec<Map<UUID, Integer>> BALANCES_CODEC =
            Codec.unboundedMap(UUID_CODEC, Codec.INT);

    private static final Codec<Map<String, UUID>> KNOWN_NAMES_CODEC =
            Codec.unboundedMap(Codec.STRING, UUID_CODEC);

    public static final Codec<BalanceStore> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BALANCES_CODEC.optionalFieldOf("balances", Map.of()).forGetter(store -> store.balances),
            KNOWN_NAMES_CODEC.optionalFieldOf("known_names", Map.of()).forGetter(store -> store.knownNames)
    ).apply(instance, BalanceStore::new));

    public static final SavedDataType<BalanceStore> TYPE =
            new SavedDataType<>(STORAGE_ID, BalanceStore::new, CODEC);

    private final HashMap<UUID, Integer> balances;
    private final HashMap<String, UUID> knownNames;

    public BalanceStore() {
        this.balances = new HashMap<>();
        this.knownNames = new HashMap<>();
    }

    private BalanceStore(Map<UUID, Integer> loadedBalances, Map<String, UUID> loadedKnownNames) {
        this.balances = new HashMap<>();
        this.knownNames = new HashMap<>();

        loadedBalances.forEach((playerUuid, balance) -> {
            if (playerUuid == null) {
                return;
            }

            int cleanBalance = clampBalance(balance);

            if (cleanBalance > 0) {
                this.balances.put(playerUuid, cleanBalance);
            }
        });

        loadedKnownNames.forEach((playerName, playerUuid) -> {
            if (playerUuid == null) {
                return;
            }

            String cleanName = normalizeName(playerName);

            if (!cleanName.isEmpty()) {
                this.knownNames.put(cleanName, playerUuid);
            }
        });
    }

    public static BalanceStore get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public void rememberPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }

        rememberPlayer(player.getUUID(), player.getGameProfile().getName());
    }

    public void rememberPlayer(UUID playerUuid, String playerName) {
        if (playerUuid == null) {
            return;
        }

        String cleanName = normalizeName(playerName);

        if (cleanName.isEmpty()) {
            return;
        }

        boolean removedOldNames = this.knownNames.entrySet().removeIf(entry ->
                entry.getValue().equals(playerUuid) && !entry.getKey().equals(cleanName)
        );

        UUID oldUuid = this.knownNames.put(cleanName, playerUuid);

        if (removedOldNames || !playerUuid.equals(oldUuid)) {
            this.setDirty();
        }
    }

    public Optional<UUID> findKnownUuid(String playerName) {
        String cleanName = normalizeName(playerName);

        if (cleanName.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.knownNames.get(cleanName));
    }

    public boolean containsBalance(UUID playerUuid) {
        return playerUuid != null && this.balances.containsKey(playerUuid);
    }

    public int getBalance(UUID playerUuid) {
        if (playerUuid == null) {
            return 0;
        }

        return this.balances.getOrDefault(playerUuid, 0);
    }

    public int setBalance(UUID playerUuid, int balance) {
        if (playerUuid == null) {
            return 0;
        }

        int oldBalance = getBalance(playerUuid);
        int newBalance = clampBalance(balance);

        if (newBalance <= 0) {
            this.balances.remove(playerUuid);
        } else {
            this.balances.put(playerUuid, newBalance);
        }

        if (oldBalance != newBalance) {
            this.setDirty();
        }

        return newBalance;
    }

    public int addBalance(UUID playerUuid, int amount) {
        if (playerUuid == null || amount <= 0) {
            return getBalance(playerUuid);
        }

        int oldBalance = getBalance(playerUuid);
        int newBalance = safeAdd(oldBalance, amount);

        if (oldBalance != newBalance) {
            this.balances.put(playerUuid, newBalance);
            this.setDirty();
        }

        return newBalance;
    }

    public boolean tryDeductBalance(UUID playerUuid, int amount) {
        if (playerUuid == null || amount <= 0) {
            return false;
        }

        int oldBalance = getBalance(playerUuid);

        if (oldBalance < amount) {
            return false;
        }

        setBalance(playerUuid, oldBalance - amount);
        return true;
    }

    public boolean hasBalance(UUID playerUuid, int amount) {
        return amount <= 0 || getBalance(playerUuid) >= amount;
    }

    private static int safeAdd(int balance, int amount) {
        if (amount <= 0) {
            return balance;
        }

        if (MAX_BALANCE - balance < amount) {
            return MAX_BALANCE;
        }

        return balance + amount;
    }

    private static int clampBalance(int balance) {
        return Math.max(0, balance);
    }

    private static String normalizeName(String playerName) {
        return playerName == null ? "" : playerName.trim().toLowerCase(Locale.ROOT);
    }

    private static DataResult<UUID> readUuid(String rawUuid) {
        try {
            return DataResult.success(UUID.fromString(rawUuid));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid balance UUID: " + rawUuid);
        }
    }
}