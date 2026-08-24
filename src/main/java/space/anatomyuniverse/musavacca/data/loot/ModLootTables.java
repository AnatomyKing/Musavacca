// file: src/main/java/space/anatomyuniverse/musavacca/data/loot/ModLootTables.java
package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModLootTables {

    public static final ResourceKey<LootTable> BANANA_COW = create(
            MusaCore.MOD_ID,
            "entities/banana_cow"
    );

    /*
     * Vanilla loot tables targeted by Global Loot Modifiers.
     */

    public static final ResourceKey<LootTable> VANILLA_SNIFFER_DIGGING = create(
            "minecraft",
            "gameplay/sniffer_digging"
    );

    public static final ResourceKey<LootTable> VANILLA_JUNGLE_TEMPLE = create(
            "minecraft",
            "chests/jungle_temple"
    );

    /*
     * Musavacca injection loot tables.
     */

    public static final ResourceKey<LootTable> SNIFFER_DIGGING_MUSAVACCA_PUP = create(
            MusaCore.MOD_ID,
            "inject/sniffer_digging_musavacca_pup"
    );

    public static final ResourceKey<LootTable>
            JUNGLE_TEMPLE_FRACTURED_POTASSIUM_TEMPLATE = create(
            MusaCore.MOD_ID,
            "inject/jungle_temple_fractured_potassium_template"
    );

    private ModLootTables() {
    }

    private static ResourceKey<LootTable> create(
            String namespace,
            String path
    ) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(
                        namespace,
                        path
                )
        );
    }
}