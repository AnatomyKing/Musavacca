package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;

import java.util.Optional;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class Fences {
    private Fences() {}

    //? if <1.21.4 {
    /*/^*
     * < 1.21.4:
     * Generate vanilla fence post/side/inventory models and blockstates via BlockStateProvider.
     *
     * Texture source is auto-inferred from registry name:
     *   musavacca_fence -> musavacca_planks
     * fallback:
     *   <stem>_block
     *   <stem>
     ^/
    public static void generate(BlockStateProvider gen, Block... fenceBlocks) {
        if (fenceBlocks == null) return;

        for (Block b : fenceBlocks) {
            if (!(b instanceof FenceBlock fence)) continue;

            Block full = inferFullBlockFromFence(b);
            ResourceLocation tex = ModelUtil.blockTex(full);

            gen.fenceBlock(fence, tex);

            gen.simpleBlockItem(fence, gen.models().getExistingFile(
                    ResourceLocation.fromNamespaceAndPath(
                            tex.getNamespace(),
                            "block/" + BuiltInRegistries.BLOCK.getKey(fence).getPath() + "_inventory"
                    )
            ));
        }
    }
    *///?} else {
    /**
     * 1.21.4+:
     * Reuse the existing full-block model, then generate fence blockstates/models/items from that family.
     */
    public static void generate(BlockModelGenerators gen, Block... fenceBlocks) {
        if (fenceBlocks == null) return;

        for (Block fence : fenceBlocks) {
            Block full = inferFullBlockFromFence(fence);
            gen.familyWithExistingFullBlock(full).fence(fence);
        }
    }
    //?}

    private static Block inferFullBlockFromFence(Block fence) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(fence);

        if (id != null) {
            String ns = id.getNamespace();
            String path = id.getPath();

            if (path.endsWith("_fence")) {
                String stem = path.substring(0, path.length() - "_fence".length());

                Block planks = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem + "_planks"));
                if (planks != null) return planks;

                Block block = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem + "_block"));
                if (block != null) return block;

                Block plain = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem));
                if (plain != null) return plain;
            }
        }

        return fence;
    }

    private static Block getBlockIfPresent(ResourceLocation id) {
        Optional<Block> opt = BuiltInRegistries.BLOCK.getOptional(id);
        return opt.orElse(null);
    }
}
