package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.BiConsumer;

public final class ModSnifferDiggingLootProvider implements LootTableSubProvider {

    private static final float MUSAVACCA_PUP_DIG_CHANCE = 0.33333334F;

    public ModSnifferDiggingLootProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                ModLootTables.SNIFFER_DIGGING_MUSAVACCA_PUP,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .when(LootItemRandomChanceCondition.randomChance(MUSAVACCA_PUP_DIG_CHANCE))
                                        .add(LootItem.lootTableItem(ModItems.MUSAVACCA_PUP.get()))
                        )
        );
    }
}