package space.anatomyuniverse.musavacca.bar.balance;

//? if >=26.1 {
/*import net.minecraft.resources.Identifier;
import space.anatomyuniverse.musavacca.MusaCore;
*///?}
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import space.anatomyuniverse.musavacca.data.MusaSavedData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
//? if >=1.21.5 {
import net.minecraft.world.level.saveddata.SavedDataType;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
*///?}

public final class BalanceStore extends SavedData {
    public static final String STORAGE_ID = "balance";
    private static final String BALANCES_TAG = "balances";
    private static final String KNOWN_NAMES_TAG = "known_names";
    private static final int MAX_BALANCE = Integer.MAX_VALUE;
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(BalanceStore::readUuid, UUID::toString);
    private static final Codec<Map<UUID, Integer>> BALANCES_CODEC = Codec.unboundedMap(UUID_CODEC, Codec.INT);
    private static final Codec<Map<String, UUID>> KNOWN_NAMES_CODEC = Codec.unboundedMap(Codec.STRING, UUID_CODEC);
    public static final Codec<BalanceStore> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                                    BALANCES_CODEC.optionalFieldOf(BALANCES_TAG, Map.of()).forGetter(store -> store.balances),
                                    KNOWN_NAMES_CODEC.optionalFieldOf(KNOWN_NAMES_TAG, Map.of()).forGetter(store -> store.knownNames))
                            .apply(instance, BalanceStore::new));
    //? if >=26.1 {
    /*public static final SavedDataType<BalanceStore> TYPE =
            new SavedDataType<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, STORAGE_ID), BalanceStore::new, CODEC);
    *///?} else if >=1.21.5 <26.1 {
    public static final SavedDataType<BalanceStore> TYPE = new SavedDataType<>(STORAGE_ID, BalanceStore::new, CODEC);
    //?} else {
    /*private static final SavedData.Factory<BalanceStore> FACTORY = new SavedData.Factory<>(BalanceStore::new, BalanceStore::load);
    *///?}
    private final HashMap<UUID, Integer> balances;
    private final HashMap<String, UUID> knownNames;

    public BalanceStore() {
        this.balances = new HashMap<>();
        this.knownNames = new HashMap<>();
    }

    private BalanceStore(Map<UUID, Integer> loadedBalances, Map<String, UUID> loadedKnownNames) {
        this();
        loadedBalances.forEach((playerUuid, balance) -> {
            if (playerUuid == null) return;
            int cleanBalance = clampBalance(balance);
            if (cleanBalance > 0) {
                this.balances.put(playerUuid, cleanBalance);
            }
        });
        loadedKnownNames.forEach((playerName, playerUuid) -> {
            if (playerUuid == null) return;
            String cleanName = normalizeName(playerName);
            if (!cleanName.isEmpty()) {
                this.knownNames.put(cleanName, playerUuid);
            }
        });
    }

    //? if <1.21.5 {
    /*private static BalanceStore load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(NbtOps.INSTANCE, tag).result()
                .orElseThrow(() -> new IllegalStateException("Invalid Musavacca balance store"));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Tag saved = CODEC.encodeStart(NbtOps.INSTANCE, this).result()
                .orElseThrow(() -> new IllegalStateException("Cannot save Musavacca balance store"));
        if (saved instanceof CompoundTag compound) return compound;
        throw new IllegalStateException("Balance store codec returned an invalid tag");
    }
    *///?}

    public static BalanceStore get(MinecraftServer server) {
        //? if >=1.21.5 {
        return MusaSavedData.storage(server).computeIfAbsent(TYPE);
        //?} else {
        /*return MusaSavedData.storage(server).computeIfAbsent(FACTORY, STORAGE_ID);
        *///?}
    }

    public void rememberPlayer(ServerPlayer player) {
        if (player == null) return;
        rememberPlayer(player.getUUID(), player.getName().getString());
    }

    public void rememberPlayer(UUID playerUuid, String playerName) {
        if (playerUuid == null) return;
        String cleanName = normalizeName(playerName);
        if (cleanName.isEmpty()) return;
        boolean removedOldNames =
                this.knownNames.entrySet().removeIf(entry -> entry.getValue().equals(playerUuid) && !entry.getKey().equals(cleanName));
        UUID oldUuid = this.knownNames.put(cleanName, playerUuid);
        if (removedOldNames || !playerUuid.equals(oldUuid)) {
            this.setDirty();
        }
    }

    public Optional<UUID> findKnownUuid(String playerName) {
        String cleanName = normalizeName(playerName);
        if (cleanName.isEmpty()) return Optional.empty();
        return Optional.ofNullable(this.knownNames.get(cleanName));
    }

    public int getBalance(UUID playerUuid) {
        if (playerUuid == null) return 0;
        return this.balances.getOrDefault(playerUuid, 0);
    }

    public int setBalance(UUID playerUuid, int balance) {
        if (playerUuid == null) return 0;
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
        if (playerUuid == null || amount <= 0) return getBalance(playerUuid);
        int oldBalance = getBalance(playerUuid);
        int newBalance = safeAdd(oldBalance, amount);
        if (oldBalance != newBalance) {
            this.balances.put(playerUuid, newBalance);
            this.setDirty();
        }
        return newBalance;
    }

    public boolean tryDeductBalance(UUID playerUuid, int amount) {
        if (playerUuid == null || amount <= 0) return false;
        int oldBalance = getBalance(playerUuid);
        if (oldBalance < amount) return false;
        setBalance(playerUuid, oldBalance - amount);
        return true;
    }

    public boolean hasBalance(UUID playerUuid, int amount) {
        return amount <= 0 || getBalance(playerUuid) >= amount;
    }

    private static int safeAdd(int balance, int amount) {
        if (amount <= 0) return balance;
        if (MAX_BALANCE - balance < amount) return MAX_BALANCE;
        return balance + amount;
    }

    private static int clampBalance(int balance) {
        return Math.max(0, balance);
    }

    private static String normalizeName(String playerName) {
        return playerName == null ? "" : playerName.trim().toLowerCase(Locale.ROOT);
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
        if (playerUuid != null) return DataResult.success(playerUuid);
        return DataResult.error(() -> "Invalid balance UUID: " + rawUuid);
    }
}
