// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/loot/ModBlockLootProvider.java
package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
//? if <1.21.9 {
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
//?} else {
/*import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
*///?}
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.BreakBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.Set;

public final class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelfAll(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get(),
                ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(),

                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),

                ModBlocks.MUSAVACCA_PLANKS.get(),
                ModBlocks.MUSAVACCA_STAIRS.get(),
                ModBlocks.MUSAVACCA_FENCE.get(),
                ModBlocks.MUSAVACCA_FENCE_GATE.get(),
                ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get(),
                ModBlocks.MUSAVACCA_BUTTON.get(),

                ModBlocks.MUSAVACCA_LEAVES.get(),

                ModBlocks.HARD_HEX_BLOCK.get(),
                ModBlocks.VOCO_TABLE.get(),
                ModBlocks.VOCO_POST.get()
        );

        /*
         * A double slab must drop two slab items.
         */
        this.add(
                ModBlocks.MUSAVACCA_SLAB.get(),
                this.createSlabItemTable(
                        ModBlocks.MUSAVACCA_SLAB.get()
                )
        );

        /*
         * Only the lower half of a door should produce the door item.
         */
        this.add(
                ModBlocks.MUSAVACCA_DOOR.get(),
                this.createDoorTable(
                        ModBlocks.MUSAVACCA_DOOR.get()
                )
        );

        this.add(
                ModBlocks.MUSAVACCA_PORTAL_DOOR.get(),
                this.createDoorTable(
                        ModBlocks.MUSAVACCA_PORTAL_DOOR.get()
                )
        );

        pearlCandleDrops();

        silkTouchMusavaccaEggByAge(
                ModBlocks.MUSAVACCA_EGG.get()
        );

        silkTouchHexBlockWithAssignedHexColor(
                ModBlocks.HEX_BLOCK.get(),
                0xFFFFFF
        );

        this.add(
                ModBlocks.BANANA_PEARL_CHALICE.get(),
                this.createSingleItemTable(
                        ModItems.SMALL_BANANA_PEARL.get(),
                        ConstantValue.exactly(6.0F)
                )
        );

        dropOther(
                ModBlocks.SMALL_BANANA_PEARL_BLOCK.get(),
                ModItems.SMALL_BANANA_PEARL.get()
        );
    }

    private void dropSelfAll(Block... blocks) {
        for (Block block : blocks) {
            dropSelf(block);
        }
    }

    private void silkTouchMusavaccaEggByAge(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(
                                                ConstantValue.exactly(1.0F)
                                        )
                                        .add(
                                                eggDropEntry(
                                                        block,
                                                        0,
                                                        ModBlocks.UNRIPE_MUSAVACCA_EGG.get()
                                                )
                                        )
                                        .add(
                                                eggDropEntry(
                                                        block,
                                                        1,
                                                        ModBlocks.RIPENING_MUSAVACCA_EGG.get()
                                                )
                                        )
                                        .add(
                                                eggDropEntry(
                                                        block,
                                                        2,
                                                        ModBlocks.RIPE_MUSAVACCA_EGG.get()
                                                )
                                        )
                        )
        );
    }

    private LootItem.Builder<?> eggDropEntry(
            Block block,
            int age,
            Item item
    ) {
        return this.applyExplosionCondition(
                block,
                LootItem.lootTableItem(item)
                        .when(this.hasSilkTouch())
                        .when(
                                LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(block)
                                        .setProperties(
                                                StatePropertiesPredicate.Builder
                                                        .properties()
                                                        .hasProperty(
                                                                BreakBlock.AGE,
                                                                age
                                                        )
                                        )
                        )
        );
    }

    private void silkTouchHexBlockWithAssignedHexColor(
            Block block,
            int assignedHexColor
    ) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(
                                                ConstantValue.exactly(1.0F)
                                        )
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(
                                                                        this.hasSilkTouch()
                                                                )
                                                                .apply(
                                                                        //? if <1.21.9 {
                                                                        CopyComponentsFunction
                                                                                .copyComponents(
                                                                                        CopyComponentsFunction.Source.BLOCK_ENTITY
                                                                                )
                                                                                .include(
                                                                                        ModDataComponents.HEX_COLOR.get()
                                                                                )
                                                                        //?} else {
                                                                        /*CopyComponentsFunction
                                                                                .copyComponentsFromBlockEntity(
                                                                                        LootContext.BlockEntityTarget.BLOCK_ENTITY
                                                                                                .getParam()
                                                                                )
                                                                                .include(
                                                                                        ModDataComponents.HEX_COLOR.get()
                                                                                )
                                                                        *///?}
                                                                )
                                                )
                                        )
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(
                                                                        this.doesNotHaveSilkTouch()
                                                                )
                                                                .apply(
                                                                        SetComponentsFunction
                                                                                .setComponent(
                                                                                        ModDataComponents.HEX_COLOR.get(),
                                                                                        assignedHexColor
                                                                                )
                                                                )
                                                )
                                        )
                        )
        );
    }

    private void pearlCandleDrops() {
        for (var holder : ModBlocks.PEARL_CANDLES) {
            PearlCandleBlock pearlCandle = holder.get();

            vanillaCandleDrops(
                    pearlCandle,
                    pearlCandle.getVanillaCandleBlock()
            );
        }
    }

    private void vanillaCandleDrops(
            PearlCandleBlock pearlCandle,
            Block vanillaCandle
    ) {
        this.add(
                pearlCandle,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(
                                                ConstantValue.exactly(1.0F)
                                        )
                                        .add(
                                                candleDropEntry(
                                                        pearlCandle,
                                                        vanillaCandle,
                                                        1
                                                )
                                        )
                                        .add(
                                                candleDropEntry(
                                                        pearlCandle,
                                                        vanillaCandle,
                                                        2
                                                )
                                        )
                                        .add(
                                                candleDropEntry(
                                                        pearlCandle,
                                                        vanillaCandle,
                                                        3
                                                )
                                        )
                                        .add(
                                                candleDropEntry(
                                                        pearlCandle,
                                                        vanillaCandle,
                                                        4
                                                )
                                        )
                        )
        );
    }

    private LootItem.Builder<?> candleDropEntry(
            PearlCandleBlock pearlCandle,
            Block vanillaCandle,
            int candles
    ) {
        return this.applyExplosionCondition(
                pearlCandle,
                LootItem.lootTableItem(vanillaCandle)
                        .when(
                                LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(pearlCandle)
                                        .setProperties(
                                                StatePropertiesPredicate.Builder
                                                        .properties()
                                                        .hasProperty(
                                                                CandleBlock.CANDLES,
                                                                candles
                                                        )
                                        )
                        )
                        .apply(
                                SetItemCountFunction.setCount(
                                        ConstantValue.exactly(candles)
                                )
                        )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS
                .getEntries()
                .stream()
                .map(holder -> (Block) holder.get())
                .toList();
    }
}