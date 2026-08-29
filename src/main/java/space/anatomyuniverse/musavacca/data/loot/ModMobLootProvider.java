package space.anatomyuniverse.musavacca.data.loot;

//? if <1.21.11 {
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
//?} else {
/*import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
*///?}
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.BiConsumer;

public final class ModMobLootProvider implements LootTableSubProvider {

    private static final float INTACT_VACACA_CHANCE = 0.60F;
    private static final float EXPOSED_VACACA_CHANCE = 0.20F;

    public ModMobLootProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output
    ) {
        output.accept(
                ModLootTables.BANANA_COW,
                LootTable.lootTable()

                        .withPool(
                                createVacacaPool(
                                        BananaCow.PEEL_STAGE_DEFAULT,
                                        INTACT_VACACA_CHANCE
                                )
                        )

                        .withPool(
                                createVacacaPool(
                                        BananaCow.PEEL_STAGE_SHEARED,
                                        EXPOSED_VACACA_CHANCE
                                )
                        )

                        .withPool(
                                createVacacaPool(
                                        BananaCow.PEEL_STAGE_PEELD,
                                        EXPOSED_VACACA_CHANCE
                                )
                        )
        );
    }

    private static LootPool.Builder createVacacaPool(
            int requiredPeelStage,
            float chance
    ) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .setBonusRolls(ConstantValue.exactly(0.0F))
                .when(peelStageIs(requiredPeelStage))
                .when(
                        LootItemRandomChanceCondition.randomChance(
                                chance
                        )
                )
                .add(
                        LootItem.lootTableItem(
                                ModItems.VACACA.get()
                        )
                );
    }

    private static LootItemCondition.Builder peelStageIs(
            int requiredPeelStage
    ) {
        CompoundTag expectedEntityData = new CompoundTag();

        expectedEntityData.putInt(
                BananaCow.TAG_PEEL_STAGE,
                requiredPeelStage
        );

        return LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.THIS,
                EntityPredicate.Builder.entity()
                        .nbt(
                                new NbtPredicate(
                                        expectedEntityData
                                )
                        )
        );
    }
}
