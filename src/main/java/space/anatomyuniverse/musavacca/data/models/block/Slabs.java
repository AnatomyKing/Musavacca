package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

import java.util.Optional;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class Slabs {
    private Slabs() {}

    //? if <1.21.4 {
    /*/^*
     * < 1.21.4:
     * Generate vanilla slab models/blockstates via BlockStateProvider.
     *
     * Texture source is auto-inferred from registry name:
     *   musavacca_slab -> musavacca_planks
     * fallback:
     *   <stem>_block
     *   <stem>
     ^/
    public static void generate(BlockStateProvider gen, Block... slabBlocks) {
        if (slabBlocks == null) return;

        for (Block b : slabBlocks) {
            if (!(b instanceof SlabBlock slab)) continue;

            Block full = inferFullBlockFromSlab(b);
            ResourceLocation tex = ModelUtil.blockTex(full);

            ResourceLocation fullModel = ResourceLocation.fromNamespaceAndPath(
                    tex.getNamespace(),
                    "block/" + BuiltInRegistries.BLOCK.getKey(full).getPath()
            );

            gen.slabBlock(slab, fullModel, tex);

            gen.simpleBlockItem(slab, gen.models().getExistingFile(
                    ResourceLocation.fromNamespaceAndPath(
                            tex.getNamespace(),
                            "block/" + BuiltInRegistries.BLOCK.getKey(slab).getPath()
                    )
            ));
        }
    }
    *///?} else {
    /**
     * 1.21.4+:
     * Reuse the existing full-block model, then generate slab blockstates/models/items from that family.
     */
    public static void generate(BlockModelGenerators gen, Block... slabBlocks) {
        if (slabBlocks == null) return;

        for (Block slab : slabBlocks) {
            Block full = inferFullBlockFromSlab(slab);
            gen.familyWithExistingFullBlock(full).slab(slab);
        }
    }
    //?}

    private static Block inferFullBlockFromSlab(Block slab) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(slab);

        if (id != null) {
            String ns = id.getNamespace();
            String path = id.getPath();

            if (path.endsWith("_slab")) {
                String stem = path.substring(0, path.length() - "_slab".length());

                Block planks = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem + "_planks"));
                if (planks != null) return planks;

                Block block = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem + "_block"));
                if (block != null) return block;

                Block plain = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem));
                if (plain != null) return plain;
            }
        }

        return slab;
    }

    private static Block getBlockIfPresent(ResourceLocation id) {
        Optional<Block> opt = BuiltInRegistries.BLOCK.getOptional(id);
        return opt.orElse(null);
    }
}
