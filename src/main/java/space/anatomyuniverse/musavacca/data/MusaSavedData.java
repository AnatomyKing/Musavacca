package space.anatomyuniverse.musavacca.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import space.anatomyuniverse.musavacca.MusaCore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
//? if >=26.1 {
/*import net.minecraft.world.level.storage.SavedDataStorage;
*///?} else {
import net.minecraft.world.level.storage.DimensionDataStorage;
//?}
//? if >=1.21.5 <1.21.11 {
import net.minecraft.world.level.saveddata.SavedData;
//?}

public final class MusaSavedData {
    //? if >=26.1 {
    /*private static final Map<MinecraftServer, SavedDataStorage> STORAGES = new IdentityHashMap<>();
    *///?} else {
    private static final Map<MinecraftServer, DimensionDataStorage> STORAGES = new IdentityHashMap<>();
    //?}

    private MusaSavedData() {}

    //? if >=26.1 {
    /*public static SavedDataStorage storage(MinecraftServer server) {
    *///?} else {
    public static DimensionDataStorage storage(MinecraftServer server) {
    //?}
        return STORAGES.computeIfAbsent(server, MusaSavedData::create);
    }

    //? if >=26.1 {
    /*private static SavedDataStorage create(MinecraftServer server) {
    *///?} else {
    private static DimensionDataStorage create(MinecraftServer server) {
    //?}
        Path root = server.getWorldPath(LevelResource.ROOT);
        Path directory = root.resolve(MusaCore.MOD_ID);
        try {
            Files.createDirectories(directory);
        } catch (IOException error) {
            throw new UncheckedIOException("Cannot create Musavacca saved data directory", error);
        }
        //? if >=26.1 {
        /*return new SavedDataStorage(null, root, DataFixers.getDataFixer(), server.registryAccess());
        *///?} else if >=1.21.11 {
        /*return new DimensionDataStorage(null, directory, DataFixers.getDataFixer(), server.registryAccess());
        *///?} else if >=1.21.5 {
        return new DimensionDataStorage(new SavedData.Context(server.overworld()), directory, DataFixers.getDataFixer(), server.registryAccess());
        //?} else if >=1.21.2 {
        /*return new DimensionDataStorage(directory, DataFixers.getDataFixer(), server.registryAccess());
        *///?} else {
        /*return new DimensionDataStorage(directory.toFile(), DataFixers.getDataFixer(), server.registryAccess());
        *///?}
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSave(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel level) save(level);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) save(level);
    }

    private static void save(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (level != server.overworld()) return;
        var storage = STORAGES.get(server);
        if (storage == null) return;
        //? if >=1.21.2 {
        storage.saveAndJoin();
        //?} else {
        /*storage.save();
        *///?}
    }

    @SubscribeEvent
    public static void onStopped(ServerStoppedEvent event) {
        STORAGES.remove(event.getServer());
    }
}
