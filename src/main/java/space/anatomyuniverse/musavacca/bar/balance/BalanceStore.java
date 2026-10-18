package space.anatomyuniverse.musavacca.bar.balance;
//? if >=26.1 {
/*import java.nio.file.StandardCopyOption;

import java.nio.file.Path;

import java.nio.file.Files;

import java.nio.file.AtomicMoveNotSupportedException;

import java.io.IOException;

import space.anatomyuniverse.musavacca.MusaCore;

import net.minecraft.resources.Identifier;
*///?}

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

//? if >=1.21.5 {
import net.minecraft.world.level.saveddata.SavedDataType;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
*///?}

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

    //? if >=26.1 {
    /*public static final SavedDataType<BalanceStore> TYPE =
            new SavedDataType<>(

                    Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, STORAGE_ID),

                    BalanceStore::new, CODEC);
    *///?} else if >=1.21.5 <26.1 {
    public static final SavedDataType<BalanceStore> TYPE =
            new SavedDataType<>(

                    STORAGE_ID,

                    BalanceStore::new, CODEC);
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

    //? if >=26.1 {
    /*public static BalanceStore get(MinecraftServer server) {
        migrateLegacyData(server, STORAGE_ID);
    *///?} else if >=1.21.5 <26.1 {
    public static BalanceStore get(MinecraftServer server) {
        
    //?} else {
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

    public static BalanceStore get(MinecraftServer server) {
    *///?}

        return server.overworld()
                .getDataStorage()
                //? if >=1.21.5 {
                .computeIfAbsent(TYPE);
                //?} else {
                /*.computeIfAbsent(FACTORY, STORAGE_ID);
                *///?}

    }

    public void rememberPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }

        rememberPlayer(
                player.getUUID(),
                player.getName().getString()
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
    //? if >=26.1 {
    /*// Minecraft 26.1 stores mod data in namespaced paths. Retain the legacy file.
    private static final java.util.Map<Object, java.util.Set<String>> CHECKED = new java.util.WeakHashMap<>();

    private static synchronized void migrateLegacyData(MinecraftServer server, String name) {

        var storage = server.overworld().getDataStorage();
        var checked = CHECKED.computeIfAbsent(storage, ignored -> new java.util.HashSet<>());
        if (checked.contains(name)) return;
        var directory = ((space.anatomyuniverse.musavacca.mixin.SavedDataStorageAccessor) storage).musavacca$dataFolder();
        var target = net.minecraft.resources.Identifier.fromNamespaceAndPath("musavacca", name + ".dat").resolveAgainst(directory);
        try {
            copyLegacyData(directory.resolve(name + ".dat"), target);
            copyLegacyData(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                    .resolve("data").resolve(name + ".dat"), target);
        } catch (java.io.IOException error) {
            throw new java.io.UncheckedIOException("Cannot migrate Musavacca saved data " + name + "; original retained", error);
        }
        checked.add(name);

    }

    private static void copyLegacyData(Path legacy, Path target) throws IOException {
        if (Files.exists(target) || !Files.isRegularFile(legacy)) return;
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".migrating");
        try {
            Files.copy(legacy, temporary, StandardCopyOption.REPLACE_EXISTING);
            if (!Files.exists(target)) {
                try {
                    Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(temporary, target);
                }
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
    *///?}
}
