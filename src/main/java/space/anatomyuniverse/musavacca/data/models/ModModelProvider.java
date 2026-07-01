package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.data.PackOutput;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.block.AgeBlocks;
import space.anatomyuniverse.musavacca.data.models.block.ColumnBlocks;
import space.anatomyuniverse.musavacca.data.models.block.CrossBlocks;
import space.anatomyuniverse.musavacca.data.models.block.FireBlocks;
import space.anatomyuniverse.musavacca.data.models.block.PortalBlocks;
import space.anatomyuniverse.musavacca.data.models.block.SimpleBlocks;
import space.anatomyuniverse.musavacca.data.models.item.ArmorItems;
import space.anatomyuniverse.musavacca.data.models.item.SimpleItems;
import space.anatomyuniverse.musavacca.data.models.item.SpawnEggItems;
import space.anatomyuniverse.musavacca.data.models.old.CubeCrafterLike;
import space.anatomyuniverse.musavacca.data.models.old.CubePearlCandles;
import space.anatomyuniverse.musavacca.data.models.old.DecorationBlocks;
import space.anatomyuniverse.musavacca.data.models.old.Fences;
import space.anatomyuniverse.musavacca.data.models.old.Slabs;
import space.anatomyuniverse.musavacca.data.models.old.SmallBananaPearlOwn;
import space.anatomyuniverse.musavacca.data.models.old.Stairs;

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
        /*
         * Engine-backed block helpers.
         */
        SimpleBlocks.generate(blocks, items, ModelSets.simpleBlocks());
        CrossBlocks.generate(blocks, items, ModelSets.crossBlocks());
        AgeBlocks.generate(blocks, items, ModelSets.ageBlocks());

        ColumnBlocks.generate(blocks, items, ModelSets.columnBlocks());

        /*
         * Engine-backed special block helpers.
         */
        FireBlocks.generate(blocks, ModelSets.fireBlocks());
        PortalBlocks.generate(blocks, ModelSets.portalBlocks());

        /*
         * Old wrappers that are intentionally not migrated yet.
         */
        Stairs.generate(blocks, ModelSets.stairsBlocks());
        Slabs.generate(blocks, ModelSets.slabBlocks());
        Fences.generate(blocks, ModelSets.fenceBlocks());
        CubeCrafterLike.generate(blocks, ModelSets.cubeCrafterLikeBlocks());
        SmallBananaPearlOwn.generate(blocks, ModelSets.smallBananaPearlBlocks());
        DecorationBlocks.generate(blocks, ModelSets.decorationBlockModels());
        CubePearlCandles.generate(blocks, ModelSets.pearlCandleBlocks());

        /*
         * Engine-backed item helpers.
         */
        SimpleItems.generate(items, ModelSets.simpleItems());

        /*
         * Dedicated item wrappers that still add extra behavior on top of the engine.
         */
        ArmorItems.generate(items, ModelSets.armorItems());
        SpawnEggItems.generate(items, ModelSets.spawnEggItems());
    }
}
