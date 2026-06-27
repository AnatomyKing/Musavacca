package space.anatomyuniverse.musavacca.data.models.block;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.DecorationBlock;

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
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

public final class DecorationModelBlocks {
    private DecorationModelBlocks() {}

    private static final Direction[] HORIZONTALS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private static final float[] EXTRA_Y_ROT = {
            0.0F,
            -22.5F,
            -45.0F,
            22.5F
    };

    public record Models(
            @Nullable String floor,
            @Nullable String side,
            @Nullable String sneak,
            @Nullable String roof,
            @Nullable String item
    ) {
        public static Models auto() {
            return new Models(null, null, null, null, null);
        }

        public static Models all(String floor, String side, String sneak, String roof) {
            return new Models(floor, side, sneak, roof, floor);
        }

        public static Models of(
                @Nullable String floor,
                @Nullable String side,
                @Nullable String sneak,
                @Nullable String roof,
                @Nullable String item
        ) {
            return new Models(floor, side, sneak, roof, item);
        }

        public ResourceLocation model(Block block, DecorationBlock.Placement placement) {
            String explicit = switch (placement) {
                case FLOOR -> this.floor;
                case SIDE -> this.side;
                case SNEAK -> this.sneak;
                case ROOF -> this.roof;
            };

            return explicit == null || explicit.isBlank()
                    ? DecorationBlock.defaultModelLocation(block, placement)
                    : ResourceLocation.parse(explicit);
        }

        public ResourceLocation itemModel(Block block, DecorationBlock decorationBlock) {
            if (this.item != null && !this.item.isBlank()) {
                return ResourceLocation.parse(this.item);
            }

            for (DecorationBlock.Placement placement : DecorationBlock.Placement.values()) {
                if (decorationBlock.isEnabled(placement)) {
                    return this.model(block, placement);
                }
            }

            return this.model(block, DecorationBlock.Placement.FLOOR);
        }
    }

    private record RotationStep(ResourceLocation model, int yRot) {}

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Models> entries) {
        if (entries == null || entries.isEmpty()) return;

        entries.forEach((block, models) -> {
            if (!(block instanceof DecorationBlock decorationBlock) || models == null) return;

            gen.getVariantBuilder(block).forAllStates(state -> {
                DecorationBlock.Placement placement = state.getValue(DecorationBlock.PLACEMENT);
                DecorationBlock.Orientation orientation = decorationBlock.orientation(placement);

                RotationStep step = switch (orientation) {
                    case FIXED -> new RotationStep(models.model(block, placement), 0);
                    case FACING -> new RotationStep(
                            models.model(block, placement),
                            sideYRot(state.getValue(DecorationBlock.FACING))
                    );
                    case ROTATION -> rotationStep(
                            block,
                            models,
                            placement,
                            state.getValue(DecorationBlock.ROTATION)
                    );
                };

                return ConfiguredModel.builder()
                        .modelFile(new ModelFile.UncheckedModelFile(step.model()))
                        .rotationY(step.yRot())
                        .build();
            });

            gen.simpleBlockItem(
                    block,
                    new ModelFile.UncheckedModelFile(models.itemModel(block, decorationBlock))
            );
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Models> entries) {
        if (entries == null || entries.isEmpty()) return;

        entries.forEach((block, models) -> {
            if (!(block instanceof DecorationBlock decorationBlock) || models == null) return;

            generateRootTransformWrappers(gen, block, decorationBlock, models);

            //? if <1.21.5 {
            /*var dispatch = PropertyDispatch.properties(
                    DecorationBlock.PLACEMENT,
                    DecorationBlock.ROTATION,
                    DecorationBlock.FACING
            );

            for (DecorationBlock.Placement placement : DecorationBlock.Placement.values()) {
                for (int rotation = 0; rotation < 16; rotation++) {
                    for (Direction facing : HORIZONTALS) {
                        if (!decorationBlock.isEnabled(placement)) {
                            dispatch = dispatch.select(
                                    placement,
                                    rotation,
                                    facing,
                                    variant(models.itemModel(block, decorationBlock), 0)
                            );
                            continue;
                        }

                        DecorationBlock.Orientation orientation = decorationBlock.orientation(placement);

                        RotationStep step = switch (orientation) {
                            case FIXED -> new RotationStep(models.model(block, placement), 0);
                            case FACING -> new RotationStep(models.model(block, placement), sideYRot(facing));
                            case ROTATION -> rotationStep(block, models, placement, rotation);
                        };

                        dispatch = dispatch.select(
                                placement,
                                rotation,
                                facing,
                                variant(step.model(), step.yRot())
                        );
                    }
                }
            }

            gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(dispatch)
            );
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            for (DecorationBlock.Placement placement : DecorationBlock.Placement.values()) {
                if (!decorationBlock.isEnabled(placement)) {
                    continue;
                }

                multi = switch (decorationBlock.orientation(placement)) {
                    case FIXED -> addFixed(multi, placement, models.model(block, placement));

                    case FACING -> addFacingModels(
                            multi,
                            placement,
                            models.model(block, placement)
                    );

                    case ROTATION -> addRotationModels(
                            multi,
                            block,
                            models,
                            placement
                    );
                };
            }

            gen.blockStateOutput.accept(multi);
            //?}

            gen.registerSimpleItemModel(block, models.itemModel(block, decorationBlock));
        });
    }

    private static void generateRootTransformWrappers(
            BlockModelGenerators gen,
            Block block,
            DecorationBlock decorationBlock,
            Models models
    ) {
        for (DecorationBlock.Placement placement : DecorationBlock.Placement.values()) {
            if (!decorationBlock.isEnabled(placement)) {
                continue;
            }

            if (decorationBlock.orientation(placement) != DecorationBlock.Orientation.ROTATION) {
                continue;
            }

            ResourceLocation parent = models.model(block, placement);

            for (float extraRot : EXTRA_Y_ROT) {
                if (extraRot != 0.0F) {
                    writeRootTransformWrapper(
                            gen,
                            wrapperModel(block, placement, extraRot),
                            parent,
                            extraRot
                    );
                }
            }
        }
    }

    private static void writeRootTransformWrapper(
            BlockModelGenerators gen,
            ResourceLocation outputModel,
            ResourceLocation parentModel,
            float yDegrees
    ) {
        gen.modelOutput.accept(outputModel, () -> {
            JsonObject root = new JsonObject();
            root.addProperty("parent", parentModel.toString());

            JsonObject transform = new JsonObject();
            transform.addProperty("origin", "center");

            JsonObject rotation = new JsonObject();
            rotation.addProperty("y", yDegrees);

            transform.add("rotation", rotation);
            root.add("transform", transform);

            return root;
        });
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId, int yRot) {
        Variant variant = Variant.variant().with(VariantProperties.MODEL, modelId);

        return yRot == 0
                ? variant
                : variant.with(VariantProperties.Y_ROT, rotation(yRot));
    }

    private static VariantProperties.Rotation rotation(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }
    *///?} else {
    private static MultiPartGenerator addFixed(
            MultiPartGenerator multi,
            DecorationBlock.Placement placement,
            ResourceLocation model
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(DecorationBlock.PLACEMENT, placement),
                BlockModelGenerators.variant(variant(model, 0))
        );
    }

    private static MultiPartGenerator addFacingModels(
            MultiPartGenerator multi,
            DecorationBlock.Placement placement,
            ResourceLocation model
    ) {
        for (Direction facing : HORIZONTALS) {
            multi = multi.with(
                    BlockModelGenerators.condition()
                            .term(DecorationBlock.PLACEMENT, placement)
                            .term(DecorationBlock.FACING, facing),
                    BlockModelGenerators.variant(variant(model, sideYRot(facing)))
            );
        }

        return multi;
    }

    private static MultiPartGenerator addRotationModels(
            MultiPartGenerator multi,
            Block block,
            Models models,
            DecorationBlock.Placement placement
    ) {
        for (int rotation = 0; rotation < 16; rotation++) {
            RotationStep step = rotationStep(block, models, placement, rotation);

            multi = multi.with(
                    BlockModelGenerators.condition()
                            .term(DecorationBlock.PLACEMENT, placement)
                            .term(DecorationBlock.ROTATION, rotation),
                    BlockModelGenerators.variant(variant(step.model(), step.yRot()))
            );
        }

        return multi;
    }

    private static Variant variant(ResourceLocation modelId, int yRot) {
        return new Variant(modelId).withYRot(quadrant(yRot));
    }

    private static Quadrant quadrant(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }
    //?}
    //?}

    private static RotationStep rotationStep(
            Block block,
            Models models,
            DecorationBlock.Placement placement,
            int rotation
    ) {
        int normalized = Math.floorMod(rotation, 16);
        int part = normalized & 3;
        int quadrant = normalized >> 2;

        int yRot = (part == 3 ? quadrant + 1 : quadrant) * 90;
        float extraRot = EXTRA_Y_ROT[part];

        return new RotationStep(
                extraRot == 0.0F
                        ? models.model(block, placement)
                        : wrapperModel(block, placement, extraRot),
                yRot
        );
    }

    private static ResourceLocation wrapperModel(
            Block block,
            DecorationBlock.Placement placement,
            float offset
    ) {
        ResourceLocation base = DecorationBlock.defaultModelLocation(block, placement);

        return ResourceLocation.fromNamespaceAndPath(
                base.getNamespace(),
                base.getPath() + "_rot_" + offsetSuffix(offset)
        );
    }

    private static String offsetSuffix(float offset) {
        if (offset == -45.0F) return "neg_45";
        if (offset == -22.5F) return "neg_22_5";
        if (offset == 22.5F) return "pos_22_5";

        throw new IllegalArgumentException("Unsupported decoration model offset: " + offset);
    }

    private static int sideYRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}