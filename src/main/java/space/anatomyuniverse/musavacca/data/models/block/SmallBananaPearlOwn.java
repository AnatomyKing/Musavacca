package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.SmallBananaPearlBlock;

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
import net.minecraft.client.renderer.block.model.VariantMutator;
//?}
//?}

public final class SmallBananaPearlOwn {
    private SmallBananaPearlOwn() {}

    private static final String[] WORDS = {
            "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen"
    };

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Block... blocks) {
        if (blocks == null) return;

        for (Block block : blocks) {
            if (!(block instanceof SmallBananaPearlBlock)) continue;

            gen.getVariantBuilder(block).forAllStates(state -> {
                int amount = state.getValue(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT);
                Direction facing = state.getValue(SmallBananaPearlBlock.FACING);

                ModelFile model = gen.models().getExistingFile(modelForAmount(block, amount));

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationY(yRot(facing))
                        .build();
            });

            ModelFile fullModel = gen.models().getExistingFile(
                    modelForAmount(block, SmallBananaPearlBlock.MAX_SMALL_PEARL_AMOUNT)
            );
            gen.simpleBlockItem(block, fullModel);
        }
    }

    private static int yRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        if (blocks == null) return;

        for (Block block : blocks) {
            if (!(block instanceof SmallBananaPearlBlock)) continue;

            //? if <1.21.5 {
            /*var dispatch = PropertyDispatch.properties(
                    SmallBananaPearlBlock.SMALL_PEARL_AMOUNT,
                    SmallBananaPearlBlock.FACING
            );

            for (int amount = 1; amount <= SmallBananaPearlBlock.MAX_SMALL_PEARL_AMOUNT; amount++) {
                ResourceLocation model = modelForAmount(block, amount);

                dispatch = dispatch
                        .select(amount, Direction.NORTH, variant(model, 0))
                        .select(amount, Direction.EAST, variant(model, 90))
                        .select(amount, Direction.SOUTH, variant(model, 180))
                        .select(amount, Direction.WEST, variant(model, 270));
            }

            gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(dispatch)
            );
            *///?} else {
            MultiPartGenerator multipart = MultiPartGenerator.multiPart(block);

            for (int amount = 1; amount <= SmallBananaPearlBlock.MAX_SMALL_PEARL_AMOUNT; amount++) {
                ResourceLocation model = modelForAmount(block, amount);

                multipart = add(multipart, amount, Direction.NORTH, model, 0);
                multipart = add(multipart, amount, Direction.EAST, model, 90);
                multipart = add(multipart, amount, Direction.SOUTH, model, 180);
                multipart = add(multipart, amount, Direction.WEST, model, 270);
            }

            gen.blockStateOutput.accept(multipart);
            //?}

            gen.registerSimpleItemModel(
                    block,
                    modelForAmount(block, SmallBananaPearlBlock.MAX_SMALL_PEARL_AMOUNT)
            );
        }
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId, int y) {
        Variant v = Variant.variant().with(VariantProperties.MODEL, modelId);
        if (y != 0) v = v.with(VariantProperties.Y_ROT, rot(y));
        return v;
    }

    private static VariantProperties.Rotation rot(int deg) {
        return switch (Math.floorMod(deg, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + deg);
        };
    }
    *///?} else {
    private static MultiPartGenerator add(
            MultiPartGenerator gen,
            int amount,
            Direction facing,
            ResourceLocation model,
            int yDeg
    ) {
        Variant v = new Variant(model);

        if (yDeg != 0) {
            v = v.with(VariantMutator.Y_ROT.withValue(quadrant(yDeg)));
        }

        return gen.with(
                BlockModelGenerators.condition()
                        .term(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, amount)
                        .term(SmallBananaPearlBlock.FACING, facing),
                BlockModelGenerators.variant(v)
        );
    }

    private static Quadrant quadrant(int deg) {
        return switch (Math.floorMod(deg, 360)) {
            case 0 -> Quadrant.R0;
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + deg);
        };
    }
    //?}
    //?}

    private static ResourceLocation modelForAmount(Block block, int amount) {
        ResourceLocation blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        String ns = blockId.getNamespace();

        if (amount >= SmallBananaPearlBlock.MAX_SMALL_PEARL_AMOUNT) {
            return ResourceLocation.fromNamespaceAndPath(ns, "block/small_banana_pearl_block");
        }

        if (amount <= 15) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl/small_banana_pearl_" + countSuffix(amount)
            );
        }

        if (amount == 16) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height3/small_banana_pearl_height3"
            );
        }
        if (amount <= 31) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height3/small_banana_pearl_height3_" + countSuffix(amount - 16)
            );
        }

        if (amount == 32) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height6/small_banana_pearl_height6"
            );
        }
        if (amount <= 47) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height6/small_banana_pearl_height6_" + countSuffix(amount - 32)
            );
        }

        if (amount == 48) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height9/small_banana_pearl_height9"
            );
        }
        if (amount <= 63) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height9/small_banana_pearl_height9_" + countSuffix(amount - 48)
            );
        }

        if (amount == 64) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height12/small_banana_pearl_height12"
            );
        }
        if (amount <= 79) {
            return ResourceLocation.fromNamespaceAndPath(
                    ns,
                    "block/small_banana_pearl_height12/small_banana_pearl_height12_" + countSuffix(amount - 64)
            );
        }

        return ResourceLocation.fromNamespaceAndPath(ns, "block/small_banana_pearl_block");
    }

    private static String countSuffix(int count) {
        return count == 1
                ? WORDS[0] + "_pearl"
                : WORDS[count - 1] + "_pearls";
    }
}

