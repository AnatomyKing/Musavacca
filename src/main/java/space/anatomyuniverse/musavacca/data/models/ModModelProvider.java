package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.block.BananaPearlChaliceOwn;
import space.anatomyuniverse.musavacca.data.models.block.BarrelCropOwnTintedFoliage;
import space.anatomyuniverse.musavacca.data.models.block.BreakBlockOwn;
import space.anatomyuniverse.musavacca.data.models.block.Chapiter;
import space.anatomyuniverse.musavacca.data.models.block.CubeAll;
import space.anatomyuniverse.musavacca.data.models.block.CubeCrafterLike;
import space.anatomyuniverse.musavacca.data.models.block.CubeOwn;
import space.anatomyuniverse.musavacca.data.models.block.Log;
import space.anatomyuniverse.musavacca.data.models.block.SmallBananaPearlOwn;
import space.anatomyuniverse.musavacca.data.models.block.Stairs;
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
//?}

//? if <1.21.4 {
/*public final class ModModelProvider extends BlockStateProvider {

    public ModModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MusaCore.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // -------------------------
        // Block section
        // -------------------------
        registerBlockModels(this);

        // -------------------------
        // Item section
        // -------------------------
        registerItemModels(itemModels());

        // Pre-1.21.4 foliage block items still use normal block item models here.
        // Runtime tinting happens in your ModTints class.
        // CubeOwnTintedFoliage.generate(this, ModelSets.cubeOwnTintedFoliageModels());
    }

    private static void registerBlockModels(BlockStateProvider blocks) {
        CubeAll.generate(blocks, ModelSets.cubeAllBlocks());
        Chapiter.generate(blocks, ModelSets.chapiterBlocks());
        Log.generate(blocks, ModelSets.logBlocks());
        Stairs.generate(blocks, ModelSets.stairsBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        SmallBananaPearlOwn.generate(blocks, ModelSets.smallBananaPearlBlocks());

        // Existing custom block model ids
        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());

        BananaPearlChaliceOwn.generate(blocks, ModelSets.bananaPearlChaliceBlocks());
        BreakBlockOwn.generate(blocks, ModelSets.breakBlockOwnModels());

        // Leaves / foliage custom-model blocks
        // CubeOwnTintedFoliage.generate(blocks, ModelSets.cubeOwnTintedFoliageModels());
        BarrelCropOwnTintedFoliage.generate(blocks, ModelSets.barrelCropOwnTintedFoliageModels());
    }

    private static void registerItemModels(ItemModelProvider items) {
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
    }
}
*///?} else {
public final class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, MusaCore.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
        // -------------------------
        // Block section
        // -------------------------
        registerBlockModels(blocks);

        // Existing custom foliage block models with tinted block-items / client items
        // CubeOwnTintedFoliage.generate(blocks, items, ModelSets.cubeOwnTintedFoliageModels());
        BarrelCropOwnTintedFoliage.generate(blocks, items, ModelSets.barrelCropOwnTintedFoliageModels());

        // -------------------------
        // Item section
        // -------------------------
        registerItemModels(items);
    }

    private static void registerBlockModels(BlockModelGenerators blocks) {
        CubeAll.generate(blocks, ModelSets.cubeAllBlocks());
        Chapiter.generate(blocks, ModelSets.chapiterBlocks());
        Log.generate(blocks, ModelSets.logBlocks());
        Stairs.generate(blocks, ModelSets.stairsBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());

        SmallBananaPearlOwn.generate(blocks, ModelSets.smallBananaPearlBlocks());

        // Existing custom block model ids
        CubeOwn.generate(blocks, ModelSets.cubeOwnModels());

        BananaPearlChaliceOwn.generate(blocks, ModelSets.bananaPearlChaliceBlocks());
        BreakBlockOwn.generate(blocks, ModelSets.breakBlockOwnModels());
    }

    private static void registerItemModels(ItemModelGenerators items) {
        FlatItems.generate(items, ModelSets.flatItems());
        HandheldItems.generate(items, ModelSets.handheldItems());
    }
}
//?}