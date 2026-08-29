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
/*import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.Variant;
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
            String litReceptorNorthEast,
            String rotaryDialers,
            String portalNorthEast,
            String portalSouthEast,
            String portalSouthWest,
            String portalNorthWest
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

        public ResourceLocation rotaryDialersModel() {
            return ResourceLocation.parse(this.rotaryDialers);
        }

        public ResourceLocation portalNorthEastModel() {
            return ResourceLocation.parse(this.portalNorthEast);
        }

        public ResourceLocation portalSouthEastModel() {
            return ResourceLocation.parse(this.portalSouthEast);
        }

        public ResourceLocation portalSouthWestModel() {
            return ResourceLocation.parse(this.portalSouthWest);
        }

        public ResourceLocation portalNorthWestModel() {
            return ResourceLocation.parse(this.portalNorthWest);
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoTableBlock) || stateModels == null) return;

            ModelFile base = gen.models().getExistingFile(stateModels.baseModel());
            ModelFile litReceptor = gen.models().getExistingFile(stateModels.litReceptorModel());
            ModelFile rotaryDialers = gen.models().getExistingFile(stateModels.rotaryDialersModel());

            ModelFile portalNorthEast = gen.models().getExistingFile(stateModels.portalNorthEastModel());
            ModelFile portalSouthEast = gen.models().getExistingFile(stateModels.portalSouthEastModel());
            ModelFile portalSouthWest = gen.models().getExistingFile(stateModels.portalSouthWestModel());
            ModelFile portalNorthWest = gen.models().getExistingFile(stateModels.portalNorthWestModel());

            gen.getMultipartBuilder(block)
                    .part()
                    .modelFile(base)
                    .addModel()
                    .end()

                    .part()
                    .modelFile(litReceptor)
                    .addModel()
                    .condition(VocoTableBlock.LIT_NORTH_EAST, true)
                    .end()

                    .part()
                    .modelFile(litReceptor)
                    .rotationY(90)
                    .addModel()
                    .condition(VocoTableBlock.LIT_SOUTH_EAST, true)
                    .end()

                    .part()
                    .modelFile(litReceptor)
                    .rotationY(180)
                    .addModel()
                    .condition(VocoTableBlock.LIT_SOUTH_WEST, true)
                    .end()

                    .part()
                    .modelFile(litReceptor)
                    .rotationY(270)
                    .addModel()
                    .condition(VocoTableBlock.LIT_NORTH_WEST, true)
                    .end()

                    .part()
                    .modelFile(rotaryDialers)
                    .addModel()
                    .condition(VocoTableBlock.ROTARY_DIALERS, true)
                    .end()

                    .part()
                    .modelFile(portalNorthEast)
                    .addModel()
                    .condition(VocoTableBlock.PORTAL_NORTH_EAST, true)
                    .end()

                    .part()
                    .modelFile(portalSouthEast)
                    .addModel()
                    .condition(VocoTableBlock.PORTAL_SOUTH_EAST, true)
                    .end()

                    .part()
                    .modelFile(portalSouthWest)
                    .addModel()
                    .condition(VocoTableBlock.PORTAL_SOUTH_WEST, true)
                    .end()

                    .part()
                    .modelFile(portalNorthWest)
                    .addModel()
                    .condition(VocoTableBlock.PORTAL_NORTH_WEST, true)
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

            multi = addConditional(
                    multi,
                    VocoTableBlock.ROTARY_DIALERS,
                    true,
                    stateModels.rotaryDialersModel(),
                    0
            );

            multi = addPortal(multi, VocoTableBlock.PORTAL_NORTH_EAST, stateModels.portalNorthEastModel());
            multi = addPortal(multi, VocoTableBlock.PORTAL_SOUTH_EAST, stateModels.portalSouthEastModel());
            multi = addPortal(multi, VocoTableBlock.PORTAL_SOUTH_WEST, stateModels.portalSouthWestModel());
            multi = addPortal(multi, VocoTableBlock.PORTAL_NORTH_WEST, stateModels.portalNorthWestModel());

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

    private static MultiPartGenerator addPortal(
            MultiPartGenerator multi,
            BooleanProperty property,
            ResourceLocation modelId
    ) {
        return addConditional(
                multi,
                property,
                true,
                modelId,
                0
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
                Condition.condition().term(property, value),
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



