package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.*;
import space.anatomyuniverse.musavacca.data.models.newgen.CrossBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.DoorBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.FireBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.SimpleBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.TallCrossBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.TrapdoorBlockGenerator;
import space.anatomyuniverse.musavacca.data.models.newgen.NewModelSets;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
//?}

public final class ModModelProvider
        //? if <1.21.4 {
        /*extends BlockStateProvider
         *///?} else {
        extends ModelProvider
        //?}
{

    //? if <1.21.4 {
    /*public ModModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MusaCore.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerAll(this, itemModels());
    }
    *///?} else {
    public ModModelProvider(PackOutput output) {
        super(output, MusaCore.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
        registerAll(blocks, items);
    }
    //?}

    private static void registerAll(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items
             *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items
            //?}
    ) {
        SimpleBlockGenerator.generate(blocks, items, NewModelSets.simpleBlocks());
        CrossBlockGenerator.generate(blocks, items, NewModelSets.crossBlocks());
        TallCrossBlockGenerator.generate(blocks, items, NewModelSets.tallCrossBlocks());
        FireBlockGenerator.generate(blocks, items, NewModelSets.fireBlocks());
        DoorBlockGenerator.generate(blocks, items, NewModelSets.doorBlocks());
        TrapdoorBlockGenerator.generate(blocks, items, NewModelSets.trapdoorBlocks());

        Stairs.generate(blocks, ModelSets.stairsBlocks());
        Slabs.generate(blocks, ModelSets.slabBlocks());
        Fences.generate(blocks, ModelSets.fenceBlocks());
        FenceGates.generate(blocks, ModelSets.fenceGateBlocks());
        Doors.generate(blocks, ModelSets.doorBlocks());


        Trapdoors.generate(blocks, ModelSets.trapdoorBlocks());
        PressurePlates.generate(blocks, ModelSets.pressurePlateBlocks());
        Buttons.generate(blocks, ModelSets.buttonBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());
        PanePortalBlockTinted15.generate(blocks, ModelSets.panePortalBlockTinted15Models());
        DecorationModelBlocks.generate(blocks, ModelSets.decorationBlockModels());
        CubePearlCandles.generate(blocks, ModelSets.pearlCandleBlocks());


        FlatItems.generateHexTinted(items, ModelSets.hexTintedFlatItems());
        CustomItemModel.generate(items, ModelSets.customItemModels());
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
        CustomArmorSet.generate(items, ModelSets.customArmorSets());
        SpawnEggItems.generate(items, ModelSets.spawnEggItems());
    }
}
