package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;

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

public final class CubeVocoPost {
    private CubeVocoPost() {}

    /**
     * base          -> always rendered, full unlit Voco Post model
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
            if (!(block instanceof VocoPostBlock) || stateModels == null) return;

            ModelFile base = gen.models().getExistingFile(stateModels.baseModel());
            ModelFile litOverlay = gen.models().getExistingFile(stateModels.litOverlayModel());
            ModelFile portalOverlay = gen.models().getExistingFile(stateModels.portalOverlayModel());

            gen.getMultipartBuilder(block)
                    .part()
                    .modelFile(base)
                    .rotationY(0)
                    .condition(VocoPostBlock.FACING, Direction.NORTH)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(base)
                    .rotationY(90)
                    .condition(VocoPostBlock.FACING, Direction.EAST)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(base)
                    .rotationY(180)
                    .condition(VocoPostBlock.FACING, Direction.SOUTH)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(base)
                    .rotationY(270)
                    .condition(VocoPostBlock.FACING, Direction.WEST)
                    .addModel()
                    .end()

                    .part()
                    .modelFile(litOverlay)
                    .rotationY(0)
                    .condition(VocoPostBlock.FACING, Direction.NORTH)
                    .condition(VocoPostBlock.LIT, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(litOverlay)
                    .rotationY(90)
                    .condition(VocoPostBlock.FACING, Direction.EAST)
                    .condition(VocoPostBlock.LIT, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(litOverlay)
                    .rotationY(180)
                    .condition(VocoPostBlock.FACING, Direction.SOUTH)
                    .condition(VocoPostBlock.LIT, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(litOverlay)
                    .rotationY(270)
                    .condition(VocoPostBlock.FACING, Direction.WEST)
                    .condition(VocoPostBlock.LIT, true)
                    .addModel()
                    .end()

                    .part()
                    .modelFile(portalOverlay)
                    .rotationY(0)
                    .condition(VocoPostBlock.FACING, Direction.NORTH)
                    .condition(VocoPostBlock.PORTAL, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(portalOverlay)
                    .rotationY(90)
                    .condition(VocoPostBlock.FACING, Direction.EAST)
                    .condition(VocoPostBlock.PORTAL, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(portalOverlay)
                    .rotationY(180)
                    .condition(VocoPostBlock.FACING, Direction.SOUTH)
                    .condition(VocoPostBlock.PORTAL, true)
                    .addModel()
                    .end()
                    .part()
                    .modelFile(portalOverlay)
                    .rotationY(270)
                    .condition(VocoPostBlock.FACING, Direction.WEST)
                    .condition(VocoPostBlock.PORTAL, true)
                    .addModel()
                    .end();

            gen.simpleBlockItem(block, base);
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoPostBlock) || stateModels == null) return;

            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            for (Direction facing : horizontalDirections()) {
                multi = addBase(multi, stateModels.baseModel(), facing);
                multi = addLitOverlay(multi, stateModels.litOverlayModel(), facing);
                multi = addPortalOverlay(multi, stateModels.portalOverlayModel(), facing);
            }

            gen.blockStateOutput.accept(multi);
            gen.registerSimpleItemModel(block, stateModels.itemModel());
        });
    }

    private static Direction[] horizontalDirections() {
        return new Direction[] {
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        };
    }

    //? if <1.21.5 {
    /*private static MultiPartGenerator addBase(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoPostBlock.FACING, facing),
                variant(modelId, facing)
        );
    }

    private static MultiPartGenerator addLitOverlay(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPostBlock.FACING, facing)
                        .term(VocoPostBlock.LIT, true),
                variant(modelId, facing)
        );
    }

    private static MultiPartGenerator addPortalOverlay(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPostBlock.FACING, facing)
                        .term(VocoPostBlock.PORTAL, true),
                variant(modelId, facing)
        );
    }

    private static Variant variant(ResourceLocation modelId, Direction facing) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, modelId);

        return switch (VocoPostBlock.yRotationDegrees(facing)) {
            case 90 -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case 180 -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            case 270 -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
            default -> variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0);
        };
    }
    *///?} else {
    private static MultiPartGenerator addBase(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition().term(VocoPostBlock.FACING, facing),
                BlockModelGenerators.variant(variant(modelId, facing))
        );
    }

    private static MultiPartGenerator addLitOverlay(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPostBlock.FACING, facing)
                        .term(VocoPostBlock.LIT, true),
                BlockModelGenerators.variant(variant(modelId, facing))
        );
    }

    private static MultiPartGenerator addPortalOverlay(
            MultiPartGenerator multi,
            ResourceLocation modelId,
            Direction facing
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPostBlock.FACING, facing)
                        .term(VocoPostBlock.PORTAL, true),
                BlockModelGenerators.variant(variant(modelId, facing))
        );
    }

    private static Variant variant(ResourceLocation modelId, Direction facing) {
        return new Variant(modelId).withYRot(yRotation(facing));
    }

    private static Quadrant yRotation(Direction facing) {
        return switch (VocoPostBlock.yRotationDegrees(facing)) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }
    //?}
    //?}
}