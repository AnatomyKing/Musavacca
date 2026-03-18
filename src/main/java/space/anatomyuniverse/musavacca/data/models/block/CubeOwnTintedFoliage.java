
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.Map;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.resources.ResourceLocation;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?}
//?}

public final class CubeOwnTintedFoliage {
    private CubeOwnTintedFoliage() {}

    /**
     * Custom existing block model ids + vanilla-like foliage-tinted item appearance.
     *
     * Example model id:
     *   "musavacca:block/musavacca_leaves"
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
    public static void generate(BlockModelGenerators blocks, ItemModelGenerators items, Map<Block, String> models) {
        if (models == null || models.isEmpty()) return;

        final int foliageTint = TintColorUtil.defaultFoliageItemTint();

        models.forEach((block, modelId) -> {
            if (block == null || modelId == null || modelId.isBlank()) return;

            ResourceLocation model = ResourceLocation.parse(modelId);

            // Blockstate -> existing custom block model
            //? if <1.21.5 {
            /*blocks.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(
                            block,
                            Variant.variant().with(VariantProperties.MODEL, model)
                    )
            );
            *///?} else {
            blocks.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model))
            );
            //?}

            // Item client definition (1.21.4+) -> same block model + tintindex 0 constant foliage tint
            items.itemModelOutput.accept(
                    block.asItem(),
                    new BlockModelWrapper.Unbaked(
                            model,
                            java.util.List.of(new Constant(foliageTint))
                    )
            );
        });
    }
    //?}
}