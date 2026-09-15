package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.newgen.Newgen;
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
        Newgen.generate(blocks, items);

        Doors.generate(blocks, ModelSets.doorBlocks());
        Trapdoors.generate(blocks, ModelSets.trapdoorBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());


        FlatItems.generateHexTinted(items, ModelSets.hexTintedFlatItems());
        CustomItemModel.generate(items, ModelSets.customItemModels());
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
        CustomArmorSet.generate(items, ModelSets.customArmorSets());
        SpawnEggItems.generate(items, ModelSets.spawnEggItems());
    }
}
