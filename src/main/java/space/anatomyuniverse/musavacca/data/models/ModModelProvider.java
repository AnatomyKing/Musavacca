package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.*;

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
        CubeAll.generate(blocks, ModelSets.cubeAllBlocks());
        CaroteneGrassModels.generate(blocks, ModelSets.caroteneGrassModels());
        Chapiter.generate(blocks, ModelSets.chapiterBlocks());
        Log.generate(blocks, ModelSets.logBlocks());
        ExudatedLog.generate(blocks, ModelSets.exudatedLogBlocks());
        Stairs.generate(blocks, ModelSets.stairsBlocks());
        Slabs.generate(blocks, ModelSets.slabBlocks());
        Fences.generate(blocks, ModelSets.fenceBlocks());
        FenceGates.generate(blocks, ModelSets.fenceGateBlocks());
        Doors.generate(
                blocks,
                ModelSets.doorBlocks()
        );

        CubeMusavaccaPortalDoorTinted.generate(
                blocks,
                items,
                ModelSets
                        .cubeMusavaccaPortalDoorTintedModels()
        );

        CubeMusavaccaPortalTrapdoorTinted.generate(
                blocks,
                ModelSets
                        .cubeMusavaccaPortalTrapdoorTintedModels()
        );

        Trapdoors.generate(blocks, ModelSets.trapdoorBlocks());
        PressurePlates.generate(blocks, ModelSets.pressurePlateBlocks());
        Buttons.generate(blocks, ModelSets.buttonBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        SmallBananaPearlOwn.generate(blocks, ModelSets.smallBananaPearlBlocks());
        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());
        CubeFireBlockTinted32.generate(blocks, ModelSets.cubeFireBlockTinted32Models());
        PanePortalBlockTinted15.generate(blocks, ModelSets.panePortalBlockTinted15Models());
        DecorationModelBlocks.generate(blocks, ModelSets.decorationBlockModels());
        BreakBlockOwn.generate(blocks, ModelSets.breakBlockOwnModels());
        CubePearlCandles.generate(blocks, ModelSets.pearlCandleBlocks());

        BarrelCropOwnTintedFoliage.generate(blocks, items, ModelSets.barrelCropOwnTintedFoliageModels());
        CubeOwnTintedHexColor.generate(blocks, items, ModelSets.cubeOwnTintedHexColorModels());
        CubeOwnTintedHexColorClipped.generate(blocks, items, ModelSets.cubeOwnTintedHexColorClippedModels());
        CubeMusavaccaCropOwnTintedFoliage.generate(blocks,ModelSets.cubeMusavaccaCropOwnTintedFoliageModels());
        CubeVocoTable.generate(blocks, ModelSets.cubeVocoTableModels());
        CubeVocoPost.generate(blocks, ModelSets.cubeVocoPostModels());

        ItemTintedLayers.generate(items, ModelSets.itemTintedLayers());
        CustomItemModel.generate(items, ModelSets.customItemModels());
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
        CustomArmorSet.generate(items, ModelSets.customArmorSets());
        SpawnEggItems.generate(items, ModelSets.spawnEggItems());
    }
}
