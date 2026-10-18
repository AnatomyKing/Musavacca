package space.anatomyuniverse.musavacca.data.tags;
//? if >=26.2 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?}

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;import net.neoforged.fml.common.Mod;import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

//? if <1.21.4 {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?}
public final class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(
            PackOutput output,
    //? if >=1.21.4 {
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, MusaCore.MOD_ID);
    //?} else {
            /*CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, MusaCore.MOD_ID, existingFileHelper);
    *///?}
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModBlockTags.PEARL_PORTAL_FRAME).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BLOCK.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_CHAPITER.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_PILLAR.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BRICKS.get()).orElseThrow()
                *///?} else {
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
                //?}
        );

        tag(ModBlockTags.MUSAVACCA_STEMS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.STRIPPED_MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
                //?}
        );

        tag(ModBlockTags.EDIBLE_FOR_BANANA_COW).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.CAROTENE_SHORT_GRASS.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.CAROTENE_TALL_GRASS.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(Blocks.PINK_PETALS).orElseThrow()
                *///?} else {
                ModBlocks.CAROTENE_SHORT_GRASS.get(),
                ModBlocks.CAROTENE_TALL_GRASS.get(),
                Blocks.PINK_PETALS
                //?}
        );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BLOCK.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BRICKS.get()).orElseThrow()
                *///?} else {
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
                //?}
        );

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BLOCK.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.BANANA_PEARL_BRICKS.get()).orElseThrow()
                *///?} else {
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
                //?}
        );

        tag(BlockTags.MINEABLE_WITH_AXE).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.STRIPPED_MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_PLANKS.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STAIRS.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_SLAB.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_FENCE.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_FENCE_GATE.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_DOOR.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_TRAPDOOR.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_PRESSURE_PLATE.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_BUTTON.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.VOCO_POST.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.VOCO_TABLE.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.MUSAVACCA_PLANKS.get(),
                ModBlocks.MUSAVACCA_STAIRS.get(),
                ModBlocks.MUSAVACCA_SLAB.get(),
                ModBlocks.MUSAVACCA_FENCE.get(),
                ModBlocks.MUSAVACCA_FENCE_GATE.get(),
                ModBlocks.MUSAVACCA_DOOR.get(),
                ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get(),
                ModBlocks.MUSAVACCA_BUTTON.get(),
                ModBlocks.VOCO_POST.get(),
                ModBlocks.VOCO_TABLE.get()
                //?}
        );

        tag(BlockTags.MINEABLE_WITH_HOE).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_LEAVES.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_LEAVES.get()
                //?}
        );

        tag(BlockTags.LOGS).add(
        //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.STRIPPED_MUSAVACCA_STEM.get()).orElseThrow(),
                BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()).orElseThrow()
        *///?} else {
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        );

        tag(BlockTags.LOGS_THAT_BURN).add(
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        //?}
        );

        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STEM.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_STEM.get()
                //?}
        );

        tag(BlockTags.LEAVES).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_LEAVES.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_LEAVES.get()
                //?}
        );

        tag(BlockTags.PLANKS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_PLANKS.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_PLANKS.get()
                //?}
        );

        tag(BlockTags.WOODEN_STAIRS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_STAIRS.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_STAIRS.get()
                //?}
        );

        tag(BlockTags.WOODEN_SLABS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_SLAB.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_SLAB.get()
                //?}
        );

        tag(BlockTags.WOODEN_FENCES).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_FENCE.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_FENCE.get()
                //?}
        );

        tag(BlockTags.FENCE_GATES).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_FENCE_GATE.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_FENCE_GATE.get()
                //?}
        );

        tag(BlockTags.WOODEN_DOORS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_DOOR.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_DOOR.get()
                //?}
        );

        tag(BlockTags.MOB_INTERACTABLE_DOORS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_DOOR.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_DOOR.get()
                //?}
        );

        tag(BlockTags.WOODEN_TRAPDOORS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_TRAPDOOR.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_TRAPDOOR.get()
                //?}
        );

        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_PRESSURE_PLATE.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get()
                //?}
        );

        tag(BlockTags.WOODEN_BUTTONS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.MUSAVACCA_BUTTON.get()).orElseThrow()
                *///?} else {
                ModBlocks.MUSAVACCA_BUTTON.get()
                //?}
        );

        tag(BlockTags.DIRT).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.CAROTENE_GRASS.get()).orElseThrow()
                *///?} else {
                ModBlocks.CAROTENE_GRASS.get()
                //?}
        );

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.CAROTENE_GRASS.get()).orElseThrow()
                *///?} else {
                ModBlocks.CAROTENE_GRASS.get()
                //?}
        );

        tag(Tags.Blocks.VILLAGER_FARMLANDS).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.CAROTENE_GRASS.get()).orElseThrow()
                *///?} else {
                ModBlocks.CAROTENE_GRASS.get()
                //?}
        );

        tag(BlockTags.FIRE).add(
                //? if >=26.2 {
                /*BuiltInRegistries.BLOCK.getResourceKey(ModBlocks.PEARL_FIRE.get()).orElseThrow()
                *///?} else {
                ModBlocks.PEARL_FIRE.get()
                //?}
        );
    }
}
