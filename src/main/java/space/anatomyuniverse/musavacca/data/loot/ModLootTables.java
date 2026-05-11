// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/loot/ModLootTables.java
package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModLootTables {

    public static final ResourceKey<LootTable> VANILLA_SNIFFER_DIGGING = create(
            "minecraft",
            "gameplay/sniffer_digging"
    );

    public static final ResourceKey<LootTable> SNIFFER_DIGGING_MUSAVACCA_PUP = create(
            MusaCore.MOD_ID,
            "inject/sniffer_digging_musavacca_pup"
    );

    private ModLootTables() {}

    private static ResourceKey<LootTable> create(String namespace, String path) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(namespace, path)
        );
    }
}