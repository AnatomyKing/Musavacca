package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.FlatItems;
import space.anatomyuniverse.musavacca.data.models.item.HandheldItems;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import space.anatomyuniverse.musavacca.data.models.item.SpawnEggItems;
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
        // -------------------------
        // Standard block helpers
        // -------------------------
        CubeAll.generate(blocks, ModelSets.cubeAllBlocks());
        Chapiter.generate(blocks, ModelSets.chapiterBlocks());
        Log.generate(blocks, ModelSets.logBlocks());
        Stairs.generate(blocks, ModelSets.stairsBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        SmallBananaPearlOwn.generate(blocks, ModelSets.smallBananaPearlBlocks());
        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());
        CubeFireBlock.generate(blocks, ModelSets.cubeFireBlockModels());
        BananaPearlChaliceOwn.generate(blocks, ModelSets.bananaPearlChaliceBlocks());
        BreakBlockOwn.generate(blocks, ModelSets.breakBlockOwnModels());

        // -------------------------
        // Special tinted/custom block item handling
        // -------------------------
        BarrelCropOwnTintedFoliage.generate(blocks, items, ModelSets.barrelCropOwnTintedFoliageModels());
        CubeOwnTintedHexColor.generate(blocks, items, ModelSets.cubeOwnTintedHexColorModels());
        CubeOwnTintedHexColorClipped.generate(blocks, items, ModelSets.cubeOwnTintedHexColorClippedModels());

        // -------------------------
        // Item helpers
        // -------------------------
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
        SpawnEggItems.generate(items, ModelSets.spawnEggItems());
    }
}