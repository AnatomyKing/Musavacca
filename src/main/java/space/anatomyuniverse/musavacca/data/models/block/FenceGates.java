package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;

import java.util.Optional;


//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class FenceGates {
    private FenceGates() {}

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            Block... blocks
    ) {
        if (blocks == null) {
            return;
        }

        for (Block block : blocks) {
            if (!(block instanceof FenceGateBlock typedBlock)) {
                continue;
            }

            Block fullBlock = inferBaseBlock(
                    typedBlock,
                    "_fence_gate"
            );

            ResourceLocation texture =
                    ModelUtil.blockTex(fullBlock);

            gen.fenceGateBlock(
                    typedBlock,
                    texture
            );

            gen.simpleBlockItem(
                    typedBlock,
                    gen.models().getExistingFile(
                            ResourceLocation.fromNamespaceAndPath(
                                    texture.getNamespace(),
                                    "block/"
                                            + BuiltInRegistries.BLOCK
                                            .getKey(typedBlock)
                                            .getPath()
                            )
                    )
            );
        }
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators gen,
            Block... blocks
    ) {
        if (blocks == null) {
            return;
        }

        for (Block block : blocks) {
            Block fullBlock = inferBaseBlock(
                    block,
                    "_fence_gate"
            );

            gen.familyWithExistingFullBlock(fullBlock)
                    .fenceGate(block);
        }
    }
    //?}

    private static Block inferBaseBlock(Block child, String suffix) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(child);

        if (id != null) {
            String namespace = id.getNamespace();
            String path = id.getPath();

            if (path.endsWith(suffix)) {
                String stem = path.substring(
                        0,
                        path.length() - suffix.length()
                );

                Block planks = getBlockIfPresent(
                        ResourceLocation.fromNamespaceAndPath(
                                namespace,
                                stem + "_planks"
                        )
                );
                if (planks != null) {
                    return planks;
                }

                Block fullBlock = getBlockIfPresent(
                        ResourceLocation.fromNamespaceAndPath(
                                namespace,
                                stem + "_block"
                        )
                );
                if (fullBlock != null) {
                    return fullBlock;
                }

                Block plain = getBlockIfPresent(
                        ResourceLocation.fromNamespaceAndPath(
                                namespace,
                                stem
                        )
                );
                if (plain != null) {
                    return plain;
                }
            }
        }

        throw new IllegalStateException(
                "Could not infer a full/base block for "
                        + id
                        + ". Expected a matching <stem>_planks, "
                        + "<stem>_block, or <stem> block."
        );
    }

    private static Block getBlockIfPresent(
            ResourceLocation id
    ) {
        Optional<Block> block =
                BuiltInRegistries.BLOCK.getOptional(id);

        return block.orElse(null);
    }

}


