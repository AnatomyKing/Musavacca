package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.HexBlock;
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;
import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.item.BlockModelWrapper;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

public final class CubeOwnTintedHexColorClipped {
    private CubeOwnTintedHexColorClipped() {}

    public record Entry(
            String normalModelId,
            String clippedModelId,
            boolean dynamicHexItemTint,
            int constantItemTint
    ) {
        public static Entry dynamic(String normalModelId, String clippedModelId) {
            return new Entry(
                    normalModelId,
                    clippedModelId,
                    true,
                    TintColorUtil.defaultHexBlockItemTint()
            );
        }

        public static Entry constant(String normalModelId, String clippedModelId, int constantItemTint) {
            return new Entry(
                    normalModelId,
                    clippedModelId,
                    false,
                    TintColorUtil.rgb(constantItemTint)
            );
        }

        public ResourceLocation normalModel() {
            return ResourceLocation.parse(normalModelId);
        }

        public ResourceLocation clippedModel() {
            return ResourceLocation.parse(clippedModelId);
        }

        public ResourceLocation itemModel() {
            return normalModel();
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
            if (block == null || entry == null) return;
            if (entry.normalModelId() == null || entry.normalModelId().isBlank()) return;
            if (entry.clippedModelId() == null || entry.clippedModelId().isBlank()) return;

            ModelFile normal = blocks.models().getExistingFile(entry.normalModel());
            ModelFile clipped = blocks.models().getExistingFile(entry.clippedModel());

            blocks.getVariantBuilder(block).forAllStates(state -> {
                boolean clippedState = state.getValue(HexBlock.CLIPPED);
                ModelFile model = clippedState ? clipped : normal;

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .build();
            });

            blocks.simpleBlockItem(
                    block,
                    blocks.models().getExistingFile(entry.itemModel())
            );
        });
        *///?} else {
        entries.forEach((block, entry) -> {
            if (block == null || entry == null) return;
            if (entry.normalModelId() == null || entry.normalModelId().isBlank()) return;
            if (entry.clippedModelId() == null || entry.clippedModelId().isBlank()) return;

            ResourceLocation normal = entry.normalModel();
            ResourceLocation clipped = entry.clippedModel();

            //? if <1.21.5 {
            /*blocks.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(
                            PropertyDispatch.property(HexBlock.CLIPPED)
                                    .select(false, variant(normal))
                                    .select(true,  variant(clipped))
                    )
            );
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = multi.with(
                    BlockModelGenerators.condition().term(HexBlock.CLIPPED, false),
                    BlockModelGenerators.variant(new Variant(normal))
            );

            multi = multi.with(
                    BlockModelGenerators.condition().term(HexBlock.CLIPPED, true),
                    BlockModelGenerators.variant(new Variant(clipped))
            );

            blocks.blockStateOutput.accept(multi);
            //?}

            items.itemModelOutput.accept(
                    block.asItem(),
                    new BlockModelWrapper.Unbaked(
                            entry.itemModel(),
                            entry.dynamicHexItemTint()
                                    ? List.of(HexColorItemTintSource.INSTANCE)
                                    : List.of(new Constant(entry.constantItemTint()))
                    )
            );
        });
        //?}
    }

    //? if >=1.21.4 <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
    *///?}
}
