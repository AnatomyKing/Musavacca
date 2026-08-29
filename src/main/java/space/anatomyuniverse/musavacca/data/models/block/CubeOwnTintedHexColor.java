package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;
import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.item.BlockModelWrapper;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?}
//?}

public final class CubeOwnTintedHexColor {
    private CubeOwnTintedHexColor() {}

    public record Entry(String modelId, boolean dynamicHexItemTint, int constantItemTint) {
        public static Entry dynamic(String modelId) {
            return new Entry(modelId, true, TintColorUtil.defaultHexBlockItemTint());
        }

        public static Entry constant(String modelId, int constantItemTint) {
            return new Entry(modelId, false, TintColorUtil.rgb(constantItemTint));
        }

        public ResourceLocation model() {
            return ResourceLocation.parse(modelId);
        }
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items,
            //?}
            Map<Block, Entry> entries
    ) {
        if (entries == null || entries.isEmpty()) return;

        //? if <1.21.4 {
        /*entries.forEach((block, entry) -> {
            if (block == null || entry == null || entry.modelId() == null || entry.modelId().isBlank()) return;

            ModelFile model = blocks.models().getExistingFile(entry.model());
            blocks.simpleBlock(block, model);
            blocks.simpleBlockItem(block, model);
        });
        *///?} else {
        entries.forEach((block, entry) -> {
            if (block == null || entry == null || entry.modelId() == null || entry.modelId().isBlank()) return;

            ResourceLocation model = entry.model();

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

            items.itemModelOutput.accept(
                    block.asItem(),
                    new BlockModelWrapper.Unbaked(
                            model,
                            entry.dynamicHexItemTint()
                                    ? List.of(HexColorItemTintSource.INSTANCE)
                                    : List.of(new Constant(entry.constantItemTint()))
                    )
            );
        });
        //?}
    }
}

