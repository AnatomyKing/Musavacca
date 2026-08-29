package space.anatomyuniverse.musavacca.bar.balance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

//? if <1.21.5 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
*///?} else {
import net.minecraft.world.level.saveddata.SavedDataType;
//?}

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BalanceStore extends SavedData {
    public static final String STORAGE_ID = "musavacca_balance_store";

    private static final String BALANCES_TAG = "balances";
    private static final String KNOWN_NAMES_TAG = "known_names";
    private static final int MAX_BALANCE = Integer.MAX_VALUE;

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(
            BalanceStore::readUuid,
            UUID::toString
    );

    private static final Codec<Map<UUID, Integer>> BALANCES_CODEC =
            Codec.unboundedMap(UUID_CODEC, Codec.INT);

    private static final Codec<Map<String, UUID>> KNOWN_NAMES_CODEC =
            Codec.unboundedMap(Codec.STRING, UUID_CODEC);

    public static final Codec<BalanceStore> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BALANCES_CODEC
                            .optionalFieldOf(BALANCES_TAG, Map.of())
                            .forGetter(store -> store.balances),
                    KNOWN_NAMES_CODEC
                            .optionalFieldOf(KNOWN_NAMES_TAG, Map.of())
                            .forGetter(store -> store.knownNames)
            ).apply(instance, BalanceStore::new));

    //? if >=1.21.5 {
    public static final SavedDataType<BalanceStore> TYPE =
            new SavedDataType<>(STORAGE_ID, BalanceStore::new, CODEC);
    //?} else {
    /*private static final SavedData.Factory<BalanceStore> FACTORY =
            new SavedData.Factory<>(BalanceStore::new, BalanceStore::load);
    *///?}

    private final HashMap<UUID, Integer> balances;
    private final HashMap<String, UUID> knownNames;

    public BalanceStore() {
        this.balances = new HashMap<>();
        this.knownNames = new HashMap<>();
    }

    private BalanceStore(
            Map<UUID, Integer> loadedBalances,
            Map<String, UUID> loadedKnownNames
    ) {
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

    //? if <1.21.5 {
    /*private static BalanceStore load(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        Map<UUID, Integer> loadedBalances = new HashMap<>();
        Map<String, UUID> loadedKnownNames = new HashMap<>();

        CompoundTag balancesTag = tag.getCompound(BALANCES_TAG);

        for (String rawUuid : balancesTag.getAllKeys()) {
            UUID playerUuid = parseUuidOrNull(rawUuid);

            if (playerUuid != null) {
                loadedBalances.put(
                        playerUuid,
                        balancesTag.getInt(rawUuid)
                );
            }
        }

        CompoundTag knownNamesTag = tag.getCompound(KNOWN_NAMES_TAG);

        for (String playerName : knownNamesTag.getAllKeys()) {
            UUID playerUuid = parseUuidOrNull(
                    knownNamesTag.getString(playerName)
            );

            if (playerUuid != null) {
                loadedKnownNames.put(playerName, playerUuid);
            }
        }

        return new BalanceStore(
                loadedBalances,
                loadedKnownNames
        );
    }

    @Override
    public CompoundTag save(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        CompoundTag balancesTag = new CompoundTag();

        this.balances.forEach((playerUuid, balance) ->
                balancesTag.putInt(
                        playerUuid.toString(),
                        balance
                )
        );

        tag.put(BALANCES_TAG, balancesTag);

        CompoundTag knownNamesTag = new CompoundTag();

        this.knownNames.forEach((playerName, playerUuid) ->
                knownNamesTag.putString(
                        playerName,
                        playerUuid.toString()
                )
        );

        tag.put(KNOWN_NAMES_TAG, knownNamesTag);
        return tag;
    }
    *///?}

    public static BalanceStore get(MinecraftServer server) {
        //? if >=1.21.5 {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(TYPE);
        //?} else {
        /*return server.overworld()
                .getDataStorage()
                .computeIfAbsent(FACTORY, STORAGE_ID);
        *///?}
    }

    public void rememberPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }

        rememberPlayer(
                player.getUUID(),
                player.getGameProfile().getName()
        );
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
                entry.getValue().equals(playerUuid)
                        && !entry.getKey().equals(cleanName)
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

        return Optional.ofNullable(
                this.knownNames.get(cleanName)
        );
    }

    public boolean containsBalance(UUID playerUuid) {
        return playerUuid != null
                && this.balances.containsKey(playerUuid);
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

        if (newBalance == 0) {
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
        return playerName == null
                ? ""
                : playerName.trim().toLowerCase(Locale.ROOT);
    }

    private static UUID parseUuidOrNull(String rawUuid) {
        try {
            return UUID.fromString(rawUuid);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static DataResult<UUID> readUuid(String rawUuid) {
        UUID playerUuid = parseUuidOrNull(rawUuid);

        if (playerUuid != null) {
            return DataResult.success(playerUuid);
        }

        return DataResult.error(
                () -> "Invalid balance UUID: " + rawUuid
        );
    }
}
