package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
*///?} else {
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//?}
//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
*///?} else {
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
//?}
//? if >=26.2 {
/*import net.minecraft.advancements.predicates.StatePropertiesPredicate;
*///?} else if >=1.21.11 <26.2 {
/*import net.minecraft.advancements.criterion.StatePropertiesPredicate;
*///?} else {
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
//?}
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.BreakBlock;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.ModItems;
import java.util.Set;

public final class ModBlockLootProvider extends BlockLootSubProvider {
    //? if >=26.3 {
    /*public ModBlockLootProvider(net.minecraft.data.loot.LootTableSubProvider.Context registries) {
    *///?} else {
    public ModBlockLootProvider(HolderLookup.Provider registries) {
    //?}
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelfAll(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get(),
                ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.BANANA_PEARL_MASCARON.get(),
                ModBlocks.BANANA_PEARL_STAIRS.get(),
                ModBlocks.BANANA_PEARL_BRICK_STAIRS.get(),
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.MUSAVACCA_PLANKS.get(),
                ModBlocks.GILDED_MUSAVACCA_PLANKS.get(),
                ModBlocks.MUSAVACCA_STAIRS.get(),
                ModBlocks.MUSAVACCA_FENCE.get(),
                ModBlocks.MUSAVACCA_FENCE_GATE.get(),
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get(),
                ModBlocks.MUSAVACCA_BUTTON.get(),
                ModBlocks.HARD_HEX_BLOCK.get(),
                ModBlocks.VOCO_POST.get()
        );

        this.add(ModBlocks.MUSAVACCA_LEAVES.get(),
                 this.createSilkTouchOrShearsDispatchTable(
                         ModBlocks.MUSAVACCA_LEAVES.get(), EmptyLootItem.emptyItem()));
        this.add(ModBlocks.MUSAVACCA_SLAB.get(), this.createSlabItemTable(ModBlocks.MUSAVACCA_SLAB.get()));
        musavaccaDoorDrops(ModBlocks.MUSAVACCA_DOOR.get());
        vocoTableDrops(ModBlocks.VOCO_TABLE.get());
        musavaccaTrapdoorDrops(ModBlocks.MUSAVACCA_TRAPDOOR.get());
        pearlCandleDrops();
        silkTouchMusavaccaEggByAge(ModBlocks.MUSAVACCA_EGG.get());
        silkTouchHexBlockWithAssignedHexColor(ModBlocks.HEX_BLOCK.get(), 0xFFFFFF);
        //? if >=26.3 {
        /*this.add(ModBlocks.BANANA_PEARL_CHALICE.get(), this.createSingleItemTable(ModItems.SMALL_BANANA_PEARL.get(), Holder.direct(new ConstantValue(6))));
        *///?} else {
        this.add(ModBlocks.BANANA_PEARL_CHALICE.get(), this.createSingleItemTable(ModItems.SMALL_BANANA_PEARL.get(), ConstantValue.exactly(6.0F)));
        //?}
        this.add(ModBlocks.CAROTENE_GRASS.get(), this.createSingleItemTableWithSilkTouch(ModBlocks.CAROTENE_GRASS.get(), Blocks.ROOTED_DIRT));
        this.add(ModBlocks.CAROTENE_SHORT_GRASS.get(), this.createGrassDrops(ModBlocks.CAROTENE_SHORT_GRASS.get()));
        this.add(ModBlocks.CAROTENE_TALL_GRASS.get(),
                this.createDoublePlantWithSeedDrops(ModBlocks.CAROTENE_TALL_GRASS.get(), ModBlocks.CAROTENE_SHORT_GRASS.get()));
        dropOther(ModBlocks.SMALL_BANANA_PEARL_BLOCK.get(), ModItems.SMALL_BANANA_PEARL.get());
    }

    private void dropSelfAll(Block... blocks) {
        for (Block block : blocks) {
            dropSelf(block);
        }
    }

    private void vocoTableDrops(Block block) {
        LootPool.Builder items = LootPool.lootPool()
                //? if >=26.3 {
                /*.setRolls(Holder.direct(new ConstantValue(1)))
                *///?} else {
                .setRolls(ConstantValue.exactly(1.0F))
                //?}
                .add(this.applyExplosionCondition(block,
                        LootItem.lootTableItem(ModItems.VOCO_TABLE.get()).when(this.doesNotHaveSilkTouch())));

        LootPool.Builder pearls = LootPool.lootPool()
                //? if >=26.3 {
                /*.setRolls(Holder.direct(new ConstantValue(1)));
                *///?} else {
                .setRolls(ConstantValue.exactly(1.0F));
                //?}

        for (int corners = 0; corners < ModItems.VOCO_TABLE_ITEMS.size(); corners++) {
            items.add(this.applyExplosionCondition(block,
                    LootItem.lootTableItem(ModItems.VOCO_TABLE_ITEMS.get(corners).get())
                            .when(this.hasSilkTouch())
                            .when(vocoTableState(block, corners))));

            if (corners == 0) continue;
            pearls.add(this.applyExplosionCondition(block,
                    LootItem.lootTableItem(ModItems.BANANA_PEARL.get())
                            .when(this.doesNotHaveSilkTouch())
                            .when(vocoTableState(block, corners))
                            //? if >=26.3 {
                            /*.apply(SetItemCountFunction.setCount(Holder.direct(new ConstantValue(Integer.bitCount(corners)))))
                            *///?} else {
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(Integer.bitCount(corners))))
                            //?}
            ));
        }

        this.add(block, LootTable.lootTable().withPool(items).withPool(pearls));
    }

    private static LootItemCondition.Builder vocoTableState(Block block, int corners) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block, StatePropertiesPredicate.Builder
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
        //?}
                        .properties()
                        .hasProperty(VocoTableBlock.LIT_NORTH_EAST, (corners & 1) != 0)
                        .hasProperty(VocoTableBlock.LIT_NORTH_WEST, (corners & 2) != 0)
                        .hasProperty(VocoTableBlock.LIT_SOUTH_EAST, (corners & 4) != 0)
                        .hasProperty(VocoTableBlock.LIT_SOUTH_WEST, (corners & 8) != 0));
    }

    private void musavaccaDoorDrops(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .when(lowerDoorHalf(block))
                                        .add(plainMusavaccaDoorDrop(block))
                                        .add(plainChargedMusavaccaDoorWithoutSilkTouchDrop(block))
                                        .add(chargedMusavaccaDoorWithSilkTouchDrop(block))
                                        .add(imbuedMusavaccaDoorWithoutSilkTouchDrop(block))
                                        .add(imbuedMusavaccaDoorWithSilkTouchDrop(block))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .when(lowerDoorHalf(block))
                                        .add(bananaPearlFromPlainChargedDoorDrop(block))
                                        .add(bananaPearlFromImbuedChargedDoorDrop(block))
                        )
        );
    }

    private LootItem.Builder<?> plainMusavaccaDoorDrop(Block block) {
        return this.applyExplosionCondition(block, LootItem.lootTableItem(ModItems.MUSAVACCA_DOOR.get()).when(musavaccaDoorState(block, false, false, false)));
    }

    private LootItem.Builder<?>
    plainChargedMusavaccaDoorWithoutSilkTouchDrop(Block block) {
        return this.applyExplosionCondition(block,
                LootItem.lootTableItem(ModItems.MUSAVACCA_DOOR.get()).when(musavaccaDoorState(block, true, false, false)).when(this.doesNotHaveSilkTouch()));
    }

    private LootItem.Builder<?>
    chargedMusavaccaDoorWithSilkTouchDrop(Block block) {
        return this.applyExplosionCondition(block,
                LootItem.lootTableItem(ModItems.MUSAVACCA_CHARGED_DOOR.get()).when(musavaccaDoorState(block, true, false, false)).when(this.hasSilkTouch()));
    }

    private LootItem.Builder<?>
    imbuedMusavaccaDoorWithoutSilkTouchDrop(Block block) {
        return this.applyExplosionCondition(block,
                LootItem.lootTableItem(ModItems.MUSAVACCA_DOOR.get()).when(musavaccaDoorLitPortalState(block, true)).when(this.doesNotHaveSilkTouch()));
    }

    private LootItem.Builder<?>
    imbuedMusavaccaDoorWithSilkTouchDrop(Block block) {
        return this.applyExplosionCondition(
                block,
                LootItem.lootTableItem(ModItems.MUSAVACCA_IMBUED_DOOR.get())
                        .when(musavaccaDoorLitPortalState(block, true))
                        .when(this.hasSilkTouch())
                        .apply(copyHexColorFromBlockEntity())
        );
    }

    private LootItem.Builder<?>
    bananaPearlFromPlainChargedDoorDrop(Block block) {
        return this.applyExplosionCondition(block,
                LootItem.lootTableItem(ModItems.BANANA_PEARL.get()).when(musavaccaDoorState(block, true, false, false)).when(this.doesNotHaveSilkTouch()));
    }

    private LootItem.Builder<?>
    bananaPearlFromImbuedChargedDoorDrop(Block block) {
        return this.applyExplosionCondition(block,
                LootItem.lootTableItem(ModItems.BANANA_PEARL.get()).when(musavaccaDoorLitAndLitPortalState(block, true, true)));
    }

    private void musavaccaTrapdoorDrops(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .add(
                                                this.applyExplosionCondition(block,
                                                        LootItem.lootTableItem(block).when(musavaccaTrapdoorLitPortalState(block, false)))
                                        )
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(musavaccaTrapdoorLitPortalState(block, true))
                                                                .when(this.doesNotHaveSilkTouch())
                                                )
                                        )
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(musavaccaTrapdoorLitPortalState(block, true))
                                                                .when(this.hasSilkTouch())
                                                                .apply(copyHexColorFromBlockEntity())
                                                )
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .add(
                                                this.applyExplosionCondition(block,
                                                        LootItem.lootTableItem(ModItems.BANANA_PEARL.get()).when(musavaccaTrapdoorLitState(block, true)))
                                        )
                        )
        );
    }

    private static LootItemCondition.Builder
    musavaccaTrapdoorLitState(Block block, boolean lit) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(MusavaccaPortalTrapdoorBlock.LIT, lit));
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
                                .properties()
                                .hasProperty(MusavaccaPortalTrapdoorBlock.LIT, lit));
        //?}
    }

    private static LootItemCondition.Builder
    musavaccaTrapdoorLitPortalState(Block block, boolean litPortal) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(MusavaccaPortalTrapdoorBlock.LIT_PORTAL, litPortal));
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
                                .properties()
                                .hasProperty(MusavaccaPortalTrapdoorBlock.LIT_PORTAL, litPortal));
        //?}
    }

    private static LootItemCondition.Builder lowerDoorHalf(Block block) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
                                .properties()
                                .hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        //?}
    }

    private static LootItemCondition.Builder
    musavaccaDoorLitPortalState(Block block, boolean litPortal) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block,
                StatePropertiesPredicate.Builder.properties().hasProperty(MusavaccaPortalDoorBlock.LIT_PORTAL, litPortal));
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
                                .properties()
                                .hasProperty(MusavaccaPortalDoorBlock.LIT_PORTAL, litPortal));
        //?}
    }

    private static LootItemCondition.Builder
    musavaccaDoorLitAndLitPortalState(Block block, boolean lit, boolean litPortal) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block, StatePropertiesPredicate.Builder
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
        //?}
                                .properties()
                                .hasProperty(MusavaccaPortalDoorBlock.LIT, lit)
                                .hasProperty(MusavaccaPortalDoorBlock.LIT_PORTAL, litPortal));
    }

    private static LootItemCondition.Builder
    musavaccaDoorState(Block block, boolean lit, boolean litPortal, boolean portal) {
        //? if >=26.3 {
        /*return MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block, StatePropertiesPredicate.Builder
        *///?} else {
        return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
        //?}
                                .properties()
                                .hasProperty(MusavaccaPortalDoorBlock.LIT, lit)
                                .hasProperty(MusavaccaPortalDoorBlock.LIT_PORTAL, litPortal)
                                .hasProperty(MusavaccaPortalDoorBlock.PORTAL, portal));
    }

    private static CopyComponentsFunction.Builder
    copyHexColorFromBlockEntity() {
        //? if >=1.21.9 {
        /*return
                CopyComponentsFunction
                        .copyComponentsFromBlockEntity(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY)
                        .include(ModDataComponents.HEX_COLOR.get());
        *///?} else {
        return CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(ModDataComponents.HEX_COLOR.get());
        //?}
    }

    private void silkTouchMusavaccaEggByAge(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .add(eggDropEntry(block, 0, ModBlocks.UNRIPE_MUSAVACCA_EGG.get()))
                                        .add(eggDropEntry(block, 1, ModBlocks.RIPENING_MUSAVACCA_EGG.get()))
                                        .add(eggDropEntry(block, 2, ModBlocks.RIPE_MUSAVACCA_EGG.get()))
                        )
        );
    }

    private LootItem.Builder<?> eggDropEntry(Block block, int age, Item item) {
        return this.applyExplosionCondition(
                block,
                LootItem.lootTableItem(item)
                        .when(this.hasSilkTouch())
                        .when(
                                //? if >=26.3 {
                                /*MatchBlock.blockMatches(BuiltInRegistries.BLOCK, block,
                                        StatePropertiesPredicate.Builder.properties().hasProperty(BreakBlock.AGE, age))
                                *///?} else {
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder
                                                        .properties()
                                                        .hasProperty(BreakBlock.AGE, age))
                                //?}
                        )
        );
    }

    private void silkTouchHexBlockWithAssignedHexColor(Block block, int assignedHexColor) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(this.hasSilkTouch())
                                                                .apply(
                                                                        CopyComponentsFunction
                                                                                //? if >=1.21.9 {
                                                                                /*.copyComponentsFromBlockEntity(
                                                                                        net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY
                                                                                )
                                                                                *///?} else {
                                                                                .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                                //?}
                                                                                .include(ModDataComponents.HEX_COLOR.get())
                                                                )
                                                )
                                        )
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .when(this.doesNotHaveSilkTouch())
                                                                .apply(SetComponentsFunction.setComponent(ModDataComponents.HEX_COLOR.get(), assignedHexColor))
                                                )
                                        )
                        )
        );
    }

    private void pearlCandleDrops() {
        for (var holder : ModBlocks.PEARL_CANDLES) {
            PearlCandleBlock pearlCandle = holder.get();
            vanillaCandleDrops(pearlCandle, pearlCandle.getVanillaCandleBlock());
        }
    }

    private void vanillaCandleDrops(PearlCandleBlock pearlCandle, Block vanillaCandle) {
        this.add(
                pearlCandle,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        //? if >=26.3 {
                                        /*.setRolls(Holder.direct(new ConstantValue(1)))
                                        *///?} else {
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        //?}
                                        .add(candleDropEntry(pearlCandle, vanillaCandle, 1))
                                        .add(candleDropEntry(pearlCandle, vanillaCandle, 2))
                                        .add(candleDropEntry(pearlCandle, vanillaCandle, 3))
                                        .add(candleDropEntry(pearlCandle, vanillaCandle, 4))
                        )
        );
    }

    private LootItem.Builder<?> candleDropEntry(PearlCandleBlock pearlCandle, Block vanillaCandle, int candles) {
        return this.applyExplosionCondition(
                pearlCandle,
                LootItem.lootTableItem(vanillaCandle)
                        .when(
                                //? if >=26.3 {
                                /*MatchBlock.blockMatches(BuiltInRegistries.BLOCK, pearlCandle,
                                        StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, candles))
                                *///?} else {
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(pearlCandle).setProperties(StatePropertiesPredicate.Builder
                                                        .properties()
                                                        .hasProperty(CandleBlock.CANDLES, candles))
                                //?}
                        )
                        //? if >=26.3 {
                        /*.apply(SetItemCountFunction.setCount(Holder.direct(new ConstantValue(candles))))
                        *///?} else {
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(candles)))
                        //?}
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get()).toList();
    }
}


