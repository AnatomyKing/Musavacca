// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/loot/ModGlobalLootModifierProvider.java
package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.concurrent.CompletableFuture;

public final class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output,
                                         CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MusaCore.MOD_ID);
    }

    @Override
    protected void start() {
        add(
                "inject_musavacca_pup_into_sniffer_digging",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition
                                        .builder(ModLootTables.VANILLA_SNIFFER_DIGGING.location())
                                        .build()
                        },
                        ModLootTables.SNIFFER_DIGGING_MUSAVACCA_PUP
                )
        );
    }
}