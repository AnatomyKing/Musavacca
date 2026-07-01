package space.anatomyuniverse.musavacca.data.models.old;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.util.Optional;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class Stairs {
    private Stairs() {}

    //? if <1.21.4 {
    /*/^*
     * < 1.21.4:
     * Generate vanilla stairs models/blockstates via BlockStateProvider.
     * Texture source is auto-inferred from registry name:
     *   <name>_stairs -> <name>_block  (preferred)
     * fallback: <name>
     * fallback: StairBlock.baseState (reflection)
     ^/
    public static void generate(BlockStateProvider gen, Block... stairsBlocks) {
        if (stairsBlocks == null) return;

        for (Block b : stairsBlocks) {
            if (!(b instanceof StairBlock stairs)) continue;

            Block full = inferFullBlockFromStairs(b);
            ResourceLocation tex = ModelUtil.blockTex(full);

            // This generates straight/inner/outer + blockstate variants.
            gen.stairsBlock(stairs, tex);

            // Item model -> points at the "stairs" model
            gen.simpleBlockItem(stairs, gen.models().getExistingFile(
                    ResourceLocation.fromNamespaceAndPath(tex.getNamespace(), "block/" + BuiltInRegistries.BLOCK.getKey(stairs).getPath())
            ));
        }
    }
    *///?} else {
    /**
     * 1.21.4+:
     * Reuse the existing full-block model (prevents duplicate model definitions),
     * then generate the stairs from that family.
     */
    public static void generate(BlockModelGenerators gen, Block... stairsBlocks) {
        if (stairsBlocks == null) return;

        for (Block stairs : stairsBlocks) {
            Block full = inferFullBlockFromStairs(stairs);
            gen.familyWithExistingFullBlock(full).stairs(stairs);
        }
    }
    //?}

    private static Block inferFullBlockFromStairs(Block stairs) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(stairs);
        if (id != null) {
            String ns = id.getNamespace();
            String path = id.getPath();

            if (path.endsWith("_stairs")) {
                String stem = path.substring(0, path.length() - "_stairs".length());

                // preferred: <stem>_block
                Block b1 = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem + "_block"));
                if (b1 != null) return b1;

                // fallback: <stem>
                Block b2 = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(ns, stem));
                if (b2 != null) return b2;
            }
        }

        // reflection fallback: StairBlock.baseState -> block
        if (stairs instanceof StairBlock sb) {
            Block refl = reflectBaseBlock(sb);
            if (refl != null) return refl;
        }

        return stairs;
    }

    private static Block getBlockIfPresent(ResourceLocation id) {
        Optional<Block> opt = BuiltInRegistries.BLOCK.getOptional(id);
        return opt.orElse(null);
    }

    private static Block reflectBaseBlock(StairBlock stairs) {
        try {
            Field f = StairBlock.class.getDeclaredField("baseState");
            f.setAccessible(true);
            Object v = f.get(stairs);
            if (v instanceof BlockState st) return st.getBlock();
        } catch (Throwable ignored) {}
        return null;
    }
}
