
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;

import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;

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

public final class CubeVocoTable {
    private CubeVocoTable() {}

    /**
     * Explicit existing model ids for both VocoTable states.
     *
     * unlit -> LIT = false
     * lit   -> LIT = true
     *
     * Item model is generated and points to the unlit model by default.
     */
    public record Models(String unlit, String lit) {
        public ResourceLocation unlitModel() {
            return ResourceLocation.parse(unlit);
        }

        public ResourceLocation litModel() {
            return ResourceLocation.parse(lit);
        }

        public ResourceLocation itemModel() {
            return unlitModel();
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoTableBlock) || stateModels == null) return;

            ModelFile unlit = gen.models().getExistingFile(stateModels.unlitModel());
            ModelFile lit = gen.models().getExistingFile(stateModels.litModel());

            gen.getVariantBuilder(block).forAllStates(state -> {
                boolean litState = state.getValue(VocoTableBlock.LIT);
                ModelFile model = litState ? lit : unlit;

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .build();
            });

            gen.simpleBlockItem(
                    block,
                    gen.models().getExistingFile(stateModels.itemModel())
            );
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoTableBlock) || stateModels == null) return;

            ResourceLocation unlit = stateModels.unlitModel();
            ResourceLocation lit = stateModels.litModel();

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(
                            PropertyDispatch.property(VocoTableBlock.LIT)
                                    .select(false, variant(unlit))
                                    .select(true,  variant(lit))
                    )
            );
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = multi.with(
                    BlockModelGenerators.condition().term(VocoTableBlock.LIT, false),
                    BlockModelGenerators.variant(new Variant(unlit))
            );

            multi = multi.with(
                    BlockModelGenerators.condition().term(VocoTableBlock.LIT, true),
                    BlockModelGenerators.variant(new Variant(lit))
            );

            gen.blockStateOutput.accept(multi);
            //?}

            gen.registerSimpleItemModel(block, stateModels.itemModel());
        });
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
    *///?}
    //?}
}