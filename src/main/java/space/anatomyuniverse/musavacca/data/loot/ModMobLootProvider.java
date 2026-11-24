package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
//?}
//? if >=26.2 {
/*import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
*///?} else if >=1.21.11 <26.2 {
/*import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
*///?} else {
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
//?}
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class ModMobLootProvider implements LootTableSubProvider {
    //? if >=26.3 {
    /*private final LootTableSubProvider.Context output;

    public ModMobLootProvider(LootTableSubProvider.Context output) {
        this.output = output;
    }
    *///?} else {
    private final HolderLookup.Provider registries;

    public ModMobLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }
    //?}

    @Override
    //? if >=26.3 {
    /*public void run() {
    *///?} else {
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
    //?}
        output.accept(ModLootTables.BANANA_COW, LootTable.lootTable()
                .withPool(createVacacaPool(BananaCow.PEEL_STAGE_DEFAULT, 0.60F))
                .withPool(createVacacaPool(BananaCow.PEEL_STAGE_SHEARED, 0.20F))
                .withPool(createVacacaPool(BananaCow.PEEL_STAGE_PEELD, 0.20F))
                .withPool(createLeatherPool()));
    }

    private LootPool.Builder createLeatherPool() {
        //? if >=26.3 {
        /*var count = ContextIntProviders.between(0, 2);
        var looting = EnchantedCountIncreaseFunction.lootingMultiplier(output.lookup(Registries.ENCHANTMENT), ContextFloatProviders.between(0.0F, 1.0F));
        *///?} else {
        var count = UniformGenerator.between(0.0F, 2.0F);
        var looting = EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F));
        //?}
        return singleRollPool().add(LootItem.lootTableItem(Items.LEATHER)
                .apply(SetItemCountFunction.setCount(count))
                .apply(looting));
    }

    private static LootPool.Builder createVacacaPool(int requiredPeelStage, float chance) {
        return singleRollPool()
                .when(peelStageIs(requiredPeelStage)).when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(ModItems.VACACA.get()));
    }

    private static LootPool.Builder singleRollPool() {
        //? if >=26.3 {
        /*return LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).setBonusRolls(ContextFloatProviders.exactly(0.0F));
        *///?} else {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).setBonusRolls(ConstantValue.exactly(0.0F));
        //?}
    }

    private static LootItemCondition.Builder peelStageIs(int requiredPeelStage) {
        CompoundTag expectedEntityData = new CompoundTag();
        expectedEntityData.putInt(BananaCow.TAG_PEEL_STAGE, requiredPeelStage);
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                EntityPredicate.Builder.entity().nbt(new NbtPredicate(expectedEntityData)));
    }
}
