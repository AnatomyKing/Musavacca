// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/block/CubeVocoTable.java
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;

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
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

public final class CubeVocoTable {
    private CubeVocoTable() {}

    public record Models(
            String base,
            String litReceptorNorthEast
    ) {
        public ResourceLocation baseModel() {
            return ResourceLocation.parse(this.base);
        }

        public ResourceLocation itemModel() {
            return this.baseModel();
        }

        public ResourceLocation litReceptorModel() {
            return ResourceLocation.parse(this.litReceptorNorthEast);
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoTableBlock) || stateModels == null) return;

            ModelFile base = gen.models().getExistingFile(stateModels.baseModel());
            ModelFile litReceptor = gen.models().getExistingFile(stateModels.litReceptorModel());

            gen.getMultipartBuilder(block)
                    .part()
                    .modelFile(base)
                    .addModel()
                    .end()

                    // North-east: original model position.
                    .part()
                    .modelFile(litReceptor)
                    .condition(VocoTableBlock.LIT_NORTH_EAST, true)
                    .addModel()
                    .end()

                    // South-east: rotate NE model 90 degrees.
                    .part()
                    .modelFile(litReceptor)
                    .rotationY(90)
                    .condition(VocoTableBlock.LIT_SOUTH_EAST, true)
                    .addModel()
                    .end()

                    // South-west: rotate NE model 180 degrees.
                    .part()
                    .modelFile(litReceptor)
                    .rotationY(180)
                    .condition(VocoTableBlock.LIT_SOUTH_WEST, true)
                    .addModel()
                    .end()

                    // North-west: rotate NE model 270 degrees.
                    .part()
                    .modelFile(litReceptor)
                    .rotationY(270)
                    .condition(VocoTableBlock.LIT_NORTH_WEST, true)
                    .addModel()
                    .end();

            gen.simpleBlockItem(block, base);
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoTableBlock) || stateModels == null) return;

            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = addAlways(multi, stateModels.baseModel());

            multi = addLitReceptor(multi, stateModels, VocoTableBlock.LIT_NORTH_EAST, 0);
            multi = addLitReceptor(multi, stateModels, VocoTableBlock.LIT_SOUTH_EAST, 90);
            multi = addLitReceptor(multi, stateModels, VocoTableBlock.LIT_SOUTH_WEST, 180);
            multi = addLitReceptor(multi, stateModels, VocoTableBlock.LIT_NORTH_WEST, 270);

            gen.blockStateOutput.accept(multi);
            gen.registerSimpleItemModel(block, stateModels.itemModel());
        });
    }

    private static MultiPartGenerator addLitReceptor(
            MultiPartGenerator multi,
            Models models,
            BooleanProperty property,
            int yRot
    ) {
        return addConditional(
                multi,
                property,
                true,
                models.litReceptorModel(),
                yRot
        );
    }

    //? if <1.21.5 {
    /*private static MultiPartGenerator addAlways(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(variant(modelId, 0));
    }

    private static MultiPartGenerator addConditional(
            MultiPartGenerator multi,
            BooleanProperty property,
            boolean value,
            ResourceLocation modelId,
            int yRot
    ) {
        return multi.with(
                BlockModelGenerators.condition().term(property, value),
                variant(modelId, yRot)
        );
    }

    private static Variant variant(ResourceLocation modelId, int yRot) {
        Variant variant = Variant.variant()
                .with(VariantProperties.MODEL, modelId);

        VariantProperties.Rotation rotation = rotation(yRot);
        if (rotation != VariantProperties.Rotation.R0) {
            variant = variant.with(VariantProperties.Y_ROT, rotation);
        }

        return variant;
    }

    private static VariantProperties.Rotation rotation(int yRot) {
        return switch (Math.floorMod(yRot, 360)) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }
    *///?} else {
    private static MultiPartGenerator addAlways(MultiPartGenerator multi, ResourceLocation modelId) {
        return multi.with(BlockModelGenerators.variant(variant(modelId, 0)));
    }

    private static MultiPartGenerator addConditional(
            MultiPartGenerator multi,
            BooleanProperty property,
            boolean value,
            ResourceLocation modelId,
            int yRot
    ) {
        return multi.with(
                BlockModelGenerators.condition().term(property, value),
                BlockModelGenerators.variant(variant(modelId, yRot))
        );
    }

    private static Variant variant(ResourceLocation modelId, int yRot) {
        Variant variant = new Variant(modelId);
        Quadrant rotation = rotation(yRot);

        return rotation == Quadrant.R0
                ? variant
                : variant.withYRot(rotation);
    }

    private static Quadrant rotation(int yRot) {
        return switch (Math.floorMod(yRot, 360)) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }
    //?}
    //?}
}