package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaLeaves;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
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
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
//?}
//?}

public final class BarrelCropOwnTintedFoliage {
    private BarrelCropOwnTintedFoliage() {}

    public record AgeModels(String age0, String age1, String age2) {
        public String forAge(int age) {
            return switch (age) {
                case 0 -> age0;
                case 1 -> age1;
                case 2 -> age2;
                default -> throw new IllegalArgumentException("Unsupported age: " + age);
            };
        }

        public ResourceLocation itemModel() {
            return ResourceLocation.parse(age0);
        }
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items,
            //?}
            Map<Block, AgeModels> models
    ) {
        if (models == null || models.isEmpty()) return;

        //? if <1.21.4 {
        /*models.forEach((block, ageModels) -> {
            if (block == null || ageModels == null) return;

            ModelFile age0 = blocks.models().getExistingFile(ResourceLocation.parse(ageModels.age0()));
            ModelFile age1 = blocks.models().getExistingFile(ResourceLocation.parse(ageModels.age1()));
            ModelFile age2 = blocks.models().getExistingFile(ResourceLocation.parse(ageModels.age2()));

            blocks.getVariantBuilder(block).forAllStates(state -> {
                Direction facing = state.getValue(BlockStateProperties.FACING);
                int age = state.getValue(MusavaccaLeaves.AGE);

                ModelFile model = switch (age) {
                    case 0 -> age0;
                    case 1 -> age1;
                    case 2 -> age2;
                    default -> age0;
                };

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationX(xRot(facing))
                        .rotationY(yRot(facing))
                        .build();
            });

            // Pre-1.21.4 item tint is handled at runtime in ModTints
            blocks.simpleBlockItem(block, age0);
        });
        *///?} else {
        final int foliageTint = TintColorUtil.defaultFoliageItemTint();

        models.forEach((block, ageModels) -> {
            if (block == null || ageModels == null) return;

            ResourceLocation age0 = ResourceLocation.parse(ageModels.age0());
            ResourceLocation age1 = ResourceLocation.parse(ageModels.age1());
            ResourceLocation age2 = ResourceLocation.parse(ageModels.age2());

            //? if <1.21.5 {
            /*blocks.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(
                            PropertyDispatch.properties(BlockStateProperties.FACING, MusavaccaLeaves.AGE)
                                    .select(Direction.UP,    0, variant(age0,   0,   0))
                                    .select(Direction.DOWN,  0, variant(age0, 180,   0))
                                    .select(Direction.NORTH, 0, variant(age0,  90,   0))
                                    .select(Direction.SOUTH, 0, variant(age0,  90, 180))
                                    .select(Direction.WEST,  0, variant(age0,  90, 270))
                                    .select(Direction.EAST,  0, variant(age0,  90,  90))

                                    .select(Direction.UP,    1, variant(age1,   0,   0))
                                    .select(Direction.DOWN,  1, variant(age1, 180,   0))
                                    .select(Direction.NORTH, 1, variant(age1,  90,   0))
                                    .select(Direction.SOUTH, 1, variant(age1,  90, 180))
                                    .select(Direction.WEST,  1, variant(age1,  90, 270))
                                    .select(Direction.EAST,  1, variant(age1,  90,  90))

                                    .select(Direction.UP,    2, variant(age2,   0,   0))
                                    .select(Direction.DOWN,  2, variant(age2, 180,   0))
                                    .select(Direction.NORTH, 2, variant(age2,  90,   0))
                                    .select(Direction.SOUTH, 2, variant(age2,  90, 180))
                                    .select(Direction.WEST,  2, variant(age2,  90, 270))
                                    .select(Direction.EAST,  2, variant(age2,  90,  90))
                    )
            );
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = add(multi, 0, Direction.UP,    age0,   0,   0);
            multi = add(multi, 0, Direction.DOWN,  age0, 180,   0);
            multi = add(multi, 0, Direction.NORTH, age0,  90,   0);
            multi = add(multi, 0, Direction.SOUTH, age0,  90, 180);
            multi = add(multi, 0, Direction.WEST,  age0,  90, 270);
            multi = add(multi, 0, Direction.EAST,  age0,  90,  90);

            multi = add(multi, 1, Direction.UP,    age1,   0,   0);
            multi = add(multi, 1, Direction.DOWN,  age1, 180,   0);
            multi = add(multi, 1, Direction.NORTH, age1,  90,   0);
            multi = add(multi, 1, Direction.SOUTH, age1,  90, 180);
            multi = add(multi, 1, Direction.WEST,  age1,  90, 270);
            multi = add(multi, 1, Direction.EAST,  age1,  90,  90);

            multi = add(multi, 2, Direction.UP,    age2,   0,   0);
            multi = add(multi, 2, Direction.DOWN,  age2, 180,   0);
            multi = add(multi, 2, Direction.NORTH, age2,  90,   0);
            multi = add(multi, 2, Direction.SOUTH, age2,  90, 180);
            multi = add(multi, 2, Direction.WEST,  age2,  90, 270);
            multi = add(multi, 2, Direction.EAST,  age2,  90,  90);

            blocks.blockStateOutput.accept(multi);
            //?}

            items.itemModelOutput.accept(
                    block.asItem(),
                    new BlockModelWrapper.Unbaked(
                            ageModels.itemModel(),
                            java.util.List.of(new Constant(foliageTint))
                    )
            );
        });
        //?}
    }

    //? if <1.21.4 {
    /*private static int xRot(Direction facing) {
        return switch (facing) {
            case UP -> 0;
            case DOWN -> 180;
            case NORTH, SOUTH, WEST, EAST -> 90;
        };
    }

    private static int yRot(Direction facing) {
        return switch (facing) {
            case UP, DOWN, NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
        };
    }
    *///?}

    //? if >=1.21.4 <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId, int x, int y) {
        Variant v = Variant.variant().with(VariantProperties.MODEL, modelId);
        if (x != 0) v = v.with(VariantProperties.X_ROT, rot(x));
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
    *///?}

    //? if >=1.21.5 {
    private static MultiPartGenerator add(
            MultiPartGenerator gen,
            int age,
            Direction facing,
            ResourceLocation model,
            int xDeg,
            int yDeg
    ) {
        Variant v = new Variant(model);

        if (xDeg != 0) {
            v = v.with(VariantMutator.X_ROT.withValue(quadrant(xDeg)));
        }
        if (yDeg != 0) {
            v = v.with(VariantMutator.Y_ROT.withValue(quadrant(yDeg)));
        }

        return gen.with(
                BlockModelGenerators.condition()
                        .term(MusavaccaLeaves.AGE, age)
                        .term(BlockStateProperties.FACING, facing),
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
}
