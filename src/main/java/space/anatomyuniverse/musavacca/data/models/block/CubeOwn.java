
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;

import java.util.Map;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.resources.ResourceLocation;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?}
//?}

public final class CubeOwn {
    private CubeOwn() {}

    /**
     * Use a custom *existing* model id per block.
     * Example model id: "musavacca:block/some_other_model_name"
     */
    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, String> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, modelId) -> {
            if (block == null || modelId == null || modelId.isBlank()) return;

            ModelFile model = gen.models().getExistingFile(ResourceLocation.parse(modelId));
            gen.simpleBlock(block, model);
            gen.simpleBlockItem(block, model);
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, String> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, modelId) -> {
            if (block == null || modelId == null || modelId.isBlank()) return;

            ResourceLocation model = ResourceLocation.parse(modelId);

            // 1.21.4 uses Variant/VariantProperties. 1.21.5+ uses plainVariant().
            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(
                            block,
                            Variant.variant().with(VariantProperties.MODEL, model)
                    )
            );
            *///?} else {
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model))
            );
            //?}

            // Make the BlockItem point at the same model.
            gen.registerSimpleItemModel(block, model);
        });
    }
    //?}

    /**
     * Use the block's *own* model location (namespace:block/<path>).
     * This matches the usual "models/block/<blockid>.json" export path.
     */
    //? if <1.21.4 {
    /*public static void self(BlockStateProvider gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (b == null) continue;

            ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(b);
            ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
            ModelFile model = gen.models().getExistingFile(modelId);

            gen.simpleBlock(b, model);
            gen.simpleBlockItem(b, model);
        }
    }
    *///?} else {
    public static void self(BlockModelGenerators gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (b == null) continue;

            ResourceLocation model = defaultBlockModelLocation(b);

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(
                            b,
                            Variant.variant().with(VariantProperties.MODEL, model)
                    )
            );
            *///?} else {
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(model))
            );
            //?}

            gen.registerSimpleItemModel(b, model);
        }
    }

    private static ResourceLocation defaultBlockModelLocation(Block b) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }
    //?}
}


