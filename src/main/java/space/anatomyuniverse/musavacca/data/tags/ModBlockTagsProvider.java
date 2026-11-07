package space.anatomyuniverse.musavacca.data.tags;

//? if >=26.2 {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?}
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;
//? if <1.21.4 {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?}

public final class ModBlockTagsProvider extends BlockTagsProvider {
    //? if >=1.21.4 {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MusaCore.MOD_ID);
    //?} else {
    /*public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MusaCore.MOD_ID, existingFileHelper);
    *///?}
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        addBlocks(ModBlockTags.PEARL_PORTAL_FRAME, ModBlocks.BANANA_PEARL_BLOCK.get(), ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(), ModBlocks.BANANA_PEARL_BRICKS.get());
        addBlocks(ModBlockTags.MUSAVACCA_STEMS, ModBlocks.MUSAVACCA_STEM.get(), ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get());
        addBlocks(ModBlockTags.EDIBLE_FOR_BANANA_COW, ModBlocks.CAROTENE_SHORT_GRASS.get(), ModBlocks.CAROTENE_TALL_GRASS.get(), Blocks.PINK_PETALS);
        addBlocks(BlockTags.MINEABLE_WITH_PICKAXE, ModBlocks.BANANA_PEARL_BLOCK.get(), ModBlocks.BANANA_PEARL_BRICKS.get());
        addBlocks(BlockTags.NEEDS_STONE_TOOL, ModBlocks.BANANA_PEARL_BLOCK.get(), ModBlocks.BANANA_PEARL_BRICKS.get());
        addBlocks(BlockTags.MINEABLE_WITH_AXE, ModBlocks.MUSAVACCA_STEM.get(), ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(), ModBlocks.MUSAVACCA_PLANKS.get(), ModBlocks.MUSAVACCA_STAIRS.get(),
                ModBlocks.MUSAVACCA_SLAB.get(), ModBlocks.MUSAVACCA_FENCE.get(), ModBlocks.MUSAVACCA_FENCE_GATE.get(), ModBlocks.MUSAVACCA_DOOR.get(),
                ModBlocks.MUSAVACCA_TRAPDOOR.get(), ModBlocks.MUSAVACCA_PRESSURE_PLATE.get(), ModBlocks.MUSAVACCA_BUTTON.get(), ModBlocks.VOCO_POST.get(),
                ModBlocks.VOCO_TABLE.get());
        addBlocks(BlockTags.MINEABLE_WITH_HOE, ModBlocks.MUSAVACCA_LEAVES.get());
        addBlocks(BlockTags.LOGS, ModBlocks.MUSAVACCA_STEM.get(), ModBlocks.STRIPPED_MUSAVACCA_STEM.get(), ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get());
        //? if <26.2 {
        addBlocks(BlockTags.LOGS_THAT_BURN, ModBlocks.MUSAVACCA_STEM.get(), ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get());
        //?}
        addBlocks(BlockTags.OVERWORLD_NATURAL_LOGS, ModBlocks.MUSAVACCA_STEM.get());
        addBlocks(BlockTags.LEAVES, ModBlocks.MUSAVACCA_LEAVES.get());
        addBlocks(BlockTags.PLANKS, ModBlocks.MUSAVACCA_PLANKS.get());
        addBlocks(BlockTags.WOODEN_STAIRS, ModBlocks.MUSAVACCA_STAIRS.get());
        addBlocks(BlockTags.WOODEN_SLABS, ModBlocks.MUSAVACCA_SLAB.get());
        addBlocks(BlockTags.WOODEN_FENCES, ModBlocks.MUSAVACCA_FENCE.get());
        addBlocks(BlockTags.FENCE_GATES, ModBlocks.MUSAVACCA_FENCE_GATE.get());
        addBlocks(BlockTags.WOODEN_DOORS, ModBlocks.MUSAVACCA_DOOR.get());
        addBlocks(BlockTags.MOB_INTERACTABLE_DOORS, ModBlocks.MUSAVACCA_DOOR.get());
        addBlocks(BlockTags.WOODEN_TRAPDOORS, ModBlocks.MUSAVACCA_TRAPDOOR.get());
        addBlocks(BlockTags.WOODEN_PRESSURE_PLATES, ModBlocks.MUSAVACCA_PRESSURE_PLATE.get());
        addBlocks(BlockTags.WOODEN_BUTTONS, ModBlocks.MUSAVACCA_BUTTON.get());
        addBlocks(BlockTags.DIRT, ModBlocks.CAROTENE_GRASS.get());
        addBlocks(BlockTags.MINEABLE_WITH_SHOVEL, ModBlocks.CAROTENE_GRASS.get());
        addBlocks(Tags.Blocks.VILLAGER_FARMLANDS, ModBlocks.CAROTENE_GRASS.get());
        addBlocks(BlockTags.FIRE, ModBlocks.PEARL_FIRE.get());
        addBlocks(BlockTags.CANDLES, ModBlocks.PEARL_CANDLE.get());
    }

    private void addBlocks(TagKey<Block> tag, Block... blocks) {
        var builder = tag(tag);
        for (Block block : blocks) {
            //? if >=26.2 {
            /*builder.add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            *///?} else {
            builder.add(block);
            //?}
        }
    }
}
