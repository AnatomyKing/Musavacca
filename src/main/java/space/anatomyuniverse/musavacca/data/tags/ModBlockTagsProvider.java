package space.anatomyuniverse.musavacca.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

//? if <1.21.4 {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
 *///?}

public final class ModBlockTagsProvider extends BlockTagsProvider {

    //? if <1.21.4 {
    /*public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                lookupProvider,
                MusaCore.MOD_ID,
                existingFileHelper
        );
    }
    *///?}

    //? if >=1.21.4 {
    public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(
                output,
                lookupProvider,
                MusaCore.MOD_ID
        );
    }
    //?}

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModBlockTags.PEARL_PORTAL_FRAME).add(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
        );

        tag(ModBlockTags.MUSAVACCA_STEMS).add(
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
        );

        tag(BlockTags.NEEDS_STONE_TOOL).add(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get()
        );

        /*
         * Entire Musavacca wood family is axe-mineable.
         */
        tag(BlockTags.MINEABLE_WITH_AXE).add(
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
                ModBlocks.MUSAVACCA_BUTTON.get()
        );

        tag(BlockTags.MINEABLE_WITH_HOE).add(
                ModBlocks.MUSAVACCA_LEAVES.get()
        );

        tag(BlockTags.LOGS).add(
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        );

        tag(BlockTags.LOGS_THAT_BURN).add(
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        );

        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(
                ModBlocks.MUSAVACCA_STEM.get()
        );

        tag(BlockTags.LEAVES).add(
                ModBlocks.MUSAVACCA_LEAVES.get()
        );

        tag(BlockTags.PLANKS).add(
                ModBlocks.MUSAVACCA_PLANKS.get()
        );

        tag(BlockTags.WOODEN_STAIRS).add(
                ModBlocks.MUSAVACCA_STAIRS.get()
        );

        tag(BlockTags.WOODEN_SLABS).add(
                ModBlocks.MUSAVACCA_SLAB.get()
        );

        tag(BlockTags.WOODEN_FENCES).add(
                ModBlocks.MUSAVACCA_FENCE.get()
        );

        tag(BlockTags.FENCE_GATES).add(
                ModBlocks.MUSAVACCA_FENCE_GATE.get()
        );

        tag(BlockTags.WOODEN_DOORS).add(
                ModBlocks.MUSAVACCA_DOOR.get()
        );

        tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.MUSAVACCA_DOOR.get()
        );

        /*
         * Allows mobs with door-opening AI, such as villagers,
         * to recognize this as an interactable door.
         */
        tag(BlockTags.MOB_INTERACTABLE_DOORS).add(
                ModBlocks.MUSAVACCA_DOOR.get()
        );

        tag(BlockTags.WOODEN_TRAPDOORS).add(
                ModBlocks.MUSAVACCA_TRAPDOOR.get()
        );

        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get()
        );

        tag(BlockTags.WOODEN_BUTTONS).add(
                ModBlocks.MUSAVACCA_BUTTON.get()
        );


        tag(BlockTags.DIRT).add(
                ModBlocks.CAROTENE_GRASS.get()
        );

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                ModBlocks.CAROTENE_GRASS.get()
        );

        tag(Tags.Blocks.VILLAGER_FARMLANDS).add(
                ModBlocks.CAROTENE_GRASS.get()
        );

        tag(BlockTags.FIRE).add(
                ModBlocks.PEARL_FIRE.get()
        );
    }
}


