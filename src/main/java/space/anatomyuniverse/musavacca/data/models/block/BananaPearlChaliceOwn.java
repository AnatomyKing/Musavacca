
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.BananaPearlChaliceBlock;

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

public final class BananaPearlChaliceOwn {
    private BananaPearlChaliceOwn() {}

    private static final Direction[] HORIZONTALS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private static final String[] SEGMENT_SUFFIXES = {
            "south",
            "south_22.5",
            "west_-45",
            "west_-22.5",
            "west",
            "west_22.5",
            "north_-45",
            "north_-22.5",
            "north",
            "north_22.5",
            "east_-45",
            "east_-22.5",
            "east",
            "east_22.5",
            "south_-45",
            "south_-22.5"
    };

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Block... blocks) {
        if (blocks == null) return;

        for (Block block : blocks) {
            if (!(block instanceof BananaPearlChaliceBlock)) continue;

            gen.getVariantBuilder(block).forAllStates(state -> {
                BananaPearlChaliceBlock.Mode mode = state.getValue(BananaPearlChaliceBlock.MODE);
                int rotation = state.getValue(BananaPearlChaliceBlock.ROTATION);
                Direction facing = state.getValue(BananaPearlChaliceBlock.FACING);

                ResourceLocation modelId = switch (mode) {
                    case UP -> upModel(block, rotation);
                    case GROUND -> groundModel(block, rotation);
                    case TILT -> tiltModel(block, facing);
                };

                ModelFile model = new ModelFile.UncheckedModelFile(modelId);

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .build();
            });

            gen.simpleBlockItem(
                    block,
                    new ModelFile.UncheckedModelFile(itemModel(block))
            );
        }
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        if (blocks == null) return;

        for (Block block : blocks) {
            if (!(block instanceof BananaPearlChaliceBlock)) continue;

            //? if <1.21.5 {
            /*var dispatch = PropertyDispatch.properties(
                    BananaPearlChaliceBlock.MODE,
                    BananaPearlChaliceBlock.ROTATION,
                    BananaPearlChaliceBlock.FACING
            );

            for (int rotation = 0; rotation < 16; rotation++) {
                for (Direction facing : HORIZONTALS) {
                    dispatch = dispatch
                            .select(BananaPearlChaliceBlock.Mode.UP, rotation, facing, variant(upModel(block, rotation)))
                            .select(BananaPearlChaliceBlock.Mode.GROUND, rotation, facing, variant(groundModel(block, rotation)))
                            .select(BananaPearlChaliceBlock.Mode.TILT, rotation, facing, variant(tiltModel(block, facing)));
                }
            }

            gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(dispatch)
            );
            *///?} else {
            MultiPartGenerator multipart = MultiPartGenerator.multiPart(block);

            for (int rotation = 0; rotation < 16; rotation++) {
                multipart = addUp(multipart, rotation, upModel(block, rotation));
                multipart = addGround(multipart, rotation, groundModel(block, rotation));
            }

            for (Direction facing : HORIZONTALS) {
                multipart = addTilt(multipart, facing, tiltModel(block, facing));
            }

            gen.blockStateOutput.accept(multipart);
            //?}

            gen.registerSimpleItemModel(block, itemModel(block));
        }
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
    *///?} else {
    private static MultiPartGenerator addUp(MultiPartGenerator gen, int rotation, ResourceLocation model) {
        return gen.with(
                BlockModelGenerators.condition()
                        .term(BananaPearlChaliceBlock.MODE, BananaPearlChaliceBlock.Mode.UP)
                        .term(BananaPearlChaliceBlock.ROTATION, rotation),
                BlockModelGenerators.variant(new Variant(model))
        );
    }

    private static MultiPartGenerator addGround(MultiPartGenerator gen, int rotation, ResourceLocation model) {
        return gen.with(
                BlockModelGenerators.condition()
                        .term(BananaPearlChaliceBlock.MODE, BananaPearlChaliceBlock.Mode.GROUND)
                        .term(BananaPearlChaliceBlock.ROTATION, rotation),
                BlockModelGenerators.variant(new Variant(model))
        );
    }

    private static MultiPartGenerator addTilt(MultiPartGenerator gen, Direction facing, ResourceLocation model) {
        return gen.with(
                BlockModelGenerators.condition()
                        .term(BananaPearlChaliceBlock.MODE, BananaPearlChaliceBlock.Mode.TILT)
                        .term(BananaPearlChaliceBlock.FACING, facing),
                BlockModelGenerators.variant(new Variant(model))
        );
    }
    //?}
    //?}

    private static ResourceLocation itemModel(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String ns = id.getNamespace();
        String path = id.getPath();

        return ResourceLocation.fromNamespaceAndPath(
                ns,
                "block/" + path + "_up/" + path + "_up_north"
        );
    }

    private static ResourceLocation upModel(Block block, int rotation) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String ns = id.getNamespace();
        String path = id.getPath();

        return ResourceLocation.fromNamespaceAndPath(
                ns,
                "block/" + path + "_up/" + path + "_up_" + SEGMENT_SUFFIXES[rotation]
        );
    }

    private static ResourceLocation groundModel(Block block, int rotation) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String ns = id.getNamespace();
        String path = id.getPath();

        return ResourceLocation.fromNamespaceAndPath(
                ns,
                "block/" + path + "_ground/" + path + "_ground_" + SEGMENT_SUFFIXES[rotation]
        );
    }

    private static ResourceLocation tiltModel(Block block, Direction facing) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        String ns = id.getNamespace();
        String path = id.getPath();

        return ResourceLocation.fromNamespaceAndPath(
                ns,
                "block/" + path + "_tilt/" + path + "_tilt_" + facing.getSerializedName()
        );
    }
}