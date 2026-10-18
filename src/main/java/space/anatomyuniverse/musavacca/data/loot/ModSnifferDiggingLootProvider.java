package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;

import net.minecraft.core.Holder;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//?}

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.BiConsumer;

public final class ModSnifferDiggingLootProvider implements LootTableSubProvider {

    private static final float MUSAVACCA_PUP_DIG_CHANCE = 0.33333334F;

    //? if >=26.3 {
    /*private final LootTableSubProvider.Context output;
    public ModSnifferDiggingLootProvider(LootTableSubProvider.Context output) { this.output = output; }
    @Override
    public void run() {

        output.accept(
                ModLootTables.SNIFFER_DIGGING_MUSAVACCA_PUP,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(Holder.direct(new ConstantValue(1)))
                                        .when(LootItemRandomChanceCondition.randomChance(MUSAVACCA_PUP_DIG_CHANCE))
                                        .add(LootItem.lootTableItem(ModItems.MUSAVACCA_PUP.get()))
                        )
        );
    }
    *///?} else {
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
    //?}
}
