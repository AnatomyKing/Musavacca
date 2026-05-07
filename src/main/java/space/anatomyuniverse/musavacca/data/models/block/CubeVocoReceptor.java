package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.VocoReceptorBlock;

import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

public final class CubeVocoReceptor {
    private CubeVocoReceptor() {}

    /**
     * base          -> always rendered, full unlit receptor model
     * litOverlay    -> only rendered when LIT = true
     * portalOverlay -> only rendered when PORTAL = true, should contain tinted portal layers
     */
    public record Models(String base, String litOverlay, String portalOverlay) {
        public ResourceLocation baseModel() {
            return ResourceLocation.parse(this.base);
        }

        public ResourceLocation litOverlayModel() {
            return ResourceLocation.parse(this.litOverlay);
        }

        public ResourceLocation portalOverlayModel() {
            return ResourceLocation.parse(this.portalOverlay);
        }

        public ResourceLocation itemModel() {
            return this.baseModel();
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoReceptorBlock) || stateModels == null) return;

            ModelFile base = gen.models().getExistingFile(stateModels.baseModel());
            ModelFile litOverlay = gen.models().getExistingFile(stateModels.litOverlayModel());
            ModelFile portalOverlay = gen.models().getExistingFile(stateModels.portalOverlayModel());

            gen.getMultipartBuilder(block)
                    .part()
                    .modelFile(base)
                    .addModel()
                    .end()

                    .part()
                    .modelFile(litOverlay)
                    .condition(VocoReceptorBlock.LIT, true)
                    .addModel()
                    .end()

                    .part()
                    .modelFile(portalOverlay)
                    .condition(VocoReceptorBlock.PORTAL, true)
                    .addModel()
                    .end();

            gen.simpleBlockItem(block, base);
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoReceptorBlock) || stateModels == null) return;

            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = addAlways(multi, stateModels.baseModel());
            multi = addWhenLit(multi, stateModels.litOverlayModel());
            multi = addWhenPortal(multi, stateModels.portalOverlayModel());

            gen.blockStateOutput.accept(multi);
            gen.registerSimpleItemModel(block, stateModels.itemModel());
        });
    }

    //? if <1.21.5 {
    /*private static MultiPartGenerator addAlways(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(variant(modelId));
    }

    private static MultiPartGenerator addWhenLit(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoReceptorBlock.LIT, true),
                variant(modelId)
        );
    }

    private static MultiPartGenerator addWhenPortal(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoReceptorBlock.PORTAL, true),
                variant(modelId)
        );
    }

    private static Variant variant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
    *///?} else {
    private static MultiPartGenerator addAlways(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(BlockModelGenerators.variant(new Variant(modelId)));
    }

    private static MultiPartGenerator addWhenLit(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoReceptorBlock.LIT, true),
                BlockModelGenerators.variant(new Variant(modelId))
        );
    }

    private static MultiPartGenerator addWhenPortal(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoReceptorBlock.PORTAL, true),
                BlockModelGenerators.variant(new Variant(modelId))
        );
    }
    //?}
    //?}
}