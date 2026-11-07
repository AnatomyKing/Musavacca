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
import space.anatomyuniverse.musavacca.item.ModItems;

public final class ModJungleTempleLootProvider implements LootTableSubProvider {
    //? if >=26.3 {
    /*private final LootTableSubProvider.Context output;

    public ModJungleTempleLootProvider(LootTableSubProvider.Context output) {
        this.output = output;
    }
    *///?} else {
    public ModJungleTempleLootProvider(HolderLookup.Provider registries) {}
    //?}

    @Override
    //? if >=26.3 {
    /*public void run() {
    *///?} else {
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
    //?}
        output.accept(ModLootTables.JUNGLE_TEMPLE_FRACTURED_POTASSIUM_TEMPLATE, LootTable.lootTable().withPool(LootPool.lootPool()
                //? if >=26.3 {
                /*.setRolls(Holder.direct(new ConstantValue(1)))
                *///?} else {
                .setRolls(ConstantValue.exactly(1.0F))
                //?}
                //? if >=26.3 {
                /*.setBonusRolls(Holder.direct(new net.minecraft.world.level.storage.loot.providers.number.floats.ConstantValue(0.0F)))
                *///?} else {
                .setBonusRolls(ConstantValue.exactly(0.0F))
                //?}
                .add(LootItem.lootTableItem(ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()))));
    }
}
