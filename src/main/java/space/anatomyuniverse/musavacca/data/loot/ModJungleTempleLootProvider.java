package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;

import net.minecraft.core.Holder;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//?}

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.BiConsumer;

public final class ModJungleTempleLootProvider
        implements LootTableSubProvider {

    //? if >=26.3 {
    /*private final LootTableSubProvider.Context output;
    public ModJungleTempleLootProvider(LootTableSubProvider.Context output) { this.output = output; }
    @Override
    public void run() {

        output.accept(
                ModLootTables.JUNGLE_TEMPLE_FRACTURED_POTASSIUM_TEMPLATE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(
                                                Holder.direct(new ConstantValue(1))
                                        )
                                        .setBonusRolls(
                                                Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.floats.ConstantValue(0.0F))
                                        )
                                        .add(
                                                LootItem.lootTableItem(
                                                        ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()
                                                )
                                        )
                        )
        );
    }
    *///?} else {
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
    //?}
}
