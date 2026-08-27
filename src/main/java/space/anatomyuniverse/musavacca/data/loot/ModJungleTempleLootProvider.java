package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.BiConsumer;

public final class ModJungleTempleLootProvider
        implements LootTableSubProvider {

    public ModJungleTempleLootProvider(
            HolderLookup.Provider registries
    ) {
    }

    @Override
    public void generate(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output
    ) {
        output.accept(
                ModLootTables.JUNGLE_TEMPLE_FRACTURED_POTASSIUM_TEMPLATE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(
                                                ConstantValue.exactly(1.0F)
                                        )
                                        .setBonusRolls(
                                                ConstantValue.exactly(0.0F)
                                        )
                                        .add(
                                                LootItem.lootTableItem(
                                                        ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()
                                                )
                                        )
                        )
        );
    }
}