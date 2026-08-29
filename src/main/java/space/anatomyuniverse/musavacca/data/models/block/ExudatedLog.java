package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.ExudatedStrippedMusavaccaStemBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
//?}
//?}

/**
 * Dedicated model generator for ExudatedStrippedMusavaccaStemBlock.
 *
 * Expected side textures:
 *
 * block/exudated_stripped_musavacca_stem_0.png
 * block/exudated_stripped_musavacca_stem_1.png
 * block/exudated_stripped_musavacca_stem_2.png
 *
 * Shared end texture:
 *
 * block/exudated_stripped_musavacca_stem_top.png
 */
public final class ExudatedLog {
    private ExudatedLog() {}

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            ExudatedStrippedMusavaccaStemBlock... blocks
    ) {
        if (blocks == null) return;

        for (ExudatedStrippedMusavaccaStemBlock block : blocks) {
            if (block == null) continue;

            ModelFile[] stageModels = new ModelFile[3];

            for (int stage = 0; stage <= 2; ++stage) {
                stageModels[stage] = gen.models().cubeColumn(
                        modelName(block, stage),
                        stageTexture(block, stage),
                        topTexture(block)
                );
            }

            gen.getVariantBuilder(block).forAllStates(state -> {
                int stage = state.getValue(
                        ExudatedStrippedMusavaccaStemBlock.STAGE
                );

                Direction.Axis axis = state.getValue(
                        BlockStateProperties.AXIS
                );

                int rotationX = axis == Direction.Axis.Y ? 0 : 90;
                int rotationY = axis == Direction.Axis.X ? 90 : 0;

                return ConfiguredModel.builder()
                        .modelFile(stageModels[stage])
                        .rotationX(rotationX)
                        .rotationY(rotationY)
                        .build();
            });

            gen.simpleBlockItem(block, stageModels[0]);
        }
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators gen,
            ExudatedStrippedMusavaccaStemBlock... blocks
    ) {
        if (blocks == null) return;

        for (ExudatedStrippedMusavaccaStemBlock block : blocks) {
            if (block == null) continue;

            ResourceLocation[] stageModels = new ResourceLocation[3];

            for (int stage = 0; stage <= 2; ++stage) {
                TextureMapping mapping = new TextureMapping()
                        .put(TextureSlot.PARTICLE, stageTexture(block, stage))
                        .put(TextureSlot.SIDE, stageTexture(block, stage))
                        .put(TextureSlot.END, topTexture(block));

                stageModels[stage] = ModelTemplates.CUBE_COLUMN.create(
                        modelId(block, stage),
                        mapping,
                        gen.modelOutput
                );
            }

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(
                            PropertyDispatch.properties(
                                            ExudatedStrippedMusavaccaStemBlock.STAGE,
                                            BlockStateProperties.AXIS
                                    )
                                    .select(
                                            0,
                                            Direction.Axis.Y,
                                            variant(stageModels[0], 0, 0)
                                    )
                                    .select(
                                            0,
                                            Direction.Axis.Z,
                                            variant(stageModels[0], 90, 0)
                                    )
                                    .select(
                                            0,
                                            Direction.Axis.X,
                                            variant(stageModels[0], 90, 90)
                                    )

                                    .select(
                                            1,
                                            Direction.Axis.Y,
                                            variant(stageModels[1], 0, 0)
                                    )
                                    .select(
                                            1,
                                            Direction.Axis.Z,
                                            variant(stageModels[1], 90, 0)
                                    )
                                    .select(
                                            1,
                                            Direction.Axis.X,
                                            variant(stageModels[1], 90, 90)
                                    )

                                    .select(
                                            2,
                                            Direction.Axis.Y,
                                            variant(stageModels[2], 0, 0)
                                    )
                                    .select(
                                            2,
                                            Direction.Axis.Z,
                                            variant(stageModels[2], 90, 0)
                                    )
                                    .select(
                                            2,
                                            Direction.Axis.X,
                                            variant(stageModels[2], 90, 90)
                                    )
                    )
            );
            *///?} else {
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(block)
                            .with(
                                    PropertyDispatch.initial(
                                                    ExudatedStrippedMusavaccaStemBlock.STAGE
                                            )
                                            .select(
                                                    0,
                                                    BlockModelGenerators.plainVariant(
                                                            stageModels[0]
                                                    )
                                            )
                                            .select(
                                                    1,
                                                    BlockModelGenerators.plainVariant(
                                                            stageModels[1]
                                                    )
                                            )
                                            .select(
                                                    2,
                                                    BlockModelGenerators.plainVariant(
                                                            stageModels[2]
                                                    )
                                            )
                            )
                            .with(
                                    PropertyDispatch.modify(
                                                    BlockStateProperties.AXIS
                                            )
                                            .select(
                                                    Direction.Axis.Y,
                                                    BlockModelGenerators.NOP
                                            )
                                            .select(
                                                    Direction.Axis.Z,
                                                    BlockModelGenerators.X_ROT_90
                                            )
                                            .select(
                                                    Direction.Axis.X,
                                                    BlockModelGenerators.X_ROT_90.then(
                                                            BlockModelGenerators.Y_ROT_90
                                                    )
                                            )
                            )
            );
            //?}

            gen.registerSimpleItemModel(block, stageModels[0]);
        }
    }

    //? if <1.21.5 {
    /*private static Variant variant(
            ResourceLocation modelId,
            int rotationX,
            int rotationY
    ) {
        Variant variant = Variant.variant()
                .with(VariantProperties.MODEL, modelId);

        if (rotationX != 0) {
            variant = variant.with(
                    VariantProperties.X_ROT,
                    rotation(rotationX)
            );
        }

        if (rotationY != 0) {
            variant = variant.with(
                    VariantProperties.Y_ROT,
                    rotation(rotationY)
            );
        }

        return variant;
    }

    private static VariantProperties.Rotation rotation(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException(
                    "Unsupported rotation: " + degrees
            );
        };
    }
    *///?}
    //?}

    private static String modelName(
            ExudatedStrippedMusavaccaStemBlock block,
            int stage
    ) {
        return ModelUtil.pathOf(block)
                + (stage == 0 ? "" : "_stage" + stage);
    }

    private static ResourceLocation modelId(
            ExudatedStrippedMusavaccaStemBlock block,
            int stage
    ) {
        ResourceLocation blockId = ModelUtil.idOf(block);

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + modelName(block, stage)
        );
    }

    private static ResourceLocation stageTexture(
            ExudatedStrippedMusavaccaStemBlock block,
            int stage
    ) {
        ResourceLocation blockId = ModelUtil.idOf(block);

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + "_" + stage
        );
    }

    private static ResourceLocation topTexture(
            ExudatedStrippedMusavaccaStemBlock block
    ) {
        ResourceLocation blockId = ModelUtil.idOf(block);

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + "_top"
        );
    }
}
