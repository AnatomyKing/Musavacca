package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//?}
//? if >=26.3 {
/*import net.minecraft.core.Holder;
*///?}
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class ModSnifferDiggingLootProvider implements LootTableSubProvider {
    //? if >=26.3 {
    /*private final LootTableSubProvider.Context output;

    public ModSnifferDiggingLootProvider(LootTableSubProvider.Context output) {
        this.output = output;
    }
    *///?} else {
    public ModSnifferDiggingLootProvider(HolderLookup.Provider registries) {}
    //?}

    @Override
    //? if >=26.3 {
    /*public void run() {
    *///?} else {
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
    //?}
        output.accept(ModLootTables.SNIFFER_DIGGING_MUSAVACCA_PUP, LootTable.lootTable().withPool(LootPool.lootPool()
                //? if >=26.3 {
                /*.setRolls(Holder.direct(new ConstantValue(1)))
                *///?} else {
                .setRolls(ConstantValue.exactly(1.0F))
                //?}
                .when(LootItemRandomChanceCondition.randomChance(0.33333334F)).add(LootItem.lootTableItem(ModItems.MUSAVACCA_PUP.get()))));
    }
}
