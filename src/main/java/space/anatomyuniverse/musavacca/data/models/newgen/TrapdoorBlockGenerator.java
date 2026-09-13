package space.anatomyuniverse.musavacca.data.models.newgen;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.random.WeightedList;
//?}
//?}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TrapdoorBlockGenerator {
    private TrapdoorBlockGenerator() {}

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks,
            ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            //?}
            Collection<TrapdoorBlocks.Entry> entries
    ) {
        if (entries == null) {
            return;
        }

        for (TrapdoorBlocks.Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            entry.validate();

            //? if <1.21.4 {
            /*TrapdoorModels baseModels = baseModels(blocks, entry);
            generateLegacyBlockState(blocks, entry, baseModels);
            generateLegacyItem(blocks, entry, baseModels);
            *///?} else {
            TrapdoorModels baseModels = baseModels(blocks, entry);
            generateBlockState(blocks, entry, baseModels);
            generateItem(blocks, entry, baseModels);
            //?}
        }
    }

    //? if <1.21.4 {
    /*private static TrapdoorModels baseModels(
            BlockStateProvider blocks,
            TrapdoorBlocks.Entry entry
    ) {
        if (entry.baseMode() == TrapdoorBlocks.BaseMode.EXISTING) {
            return entry.baseModels();
        }

        ResourceLocation id = ModelUtil.idOf(entry.block());
        ResourceLocation texture = entry.textures().texture();

        ResourceLocation bottom = legacyTrapdoorModel(
                blocks,
                id,
                "_bottom",
                "template_trapdoor_bottom",
                texture
        );
        ResourceLocation top = legacyTrapdoorModel(
                blocks,
                id,
                "_top",
                "template_trapdoor_top",
                texture
        );
        ResourceLocation open = legacyTrapdoorModel(
                blocks,
                id,
                "_open",
                "template_trapdoor_open",
                texture
        );

        return TrapdoorModels.full(
                bottom.toString(),
                top.toString(),
                open.toString()
        );
    }

    private static ResourceLocation legacyTrapdoorModel(
            BlockStateProvider blocks,
            ResourceLocation blockId,
            String suffix,
            String parent,
            ResourceLocation texture
    ) {
        blocks.models()
                .withExistingParent(
                        blockId.getPath() + suffix,
                        ResourceLocation.withDefaultNamespace("block/" + parent)
                )
                .texture("texture", texture);

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + suffix
        );
    }

    private static void generateLegacyBlockState(
            BlockStateProvider blocks,
            TrapdoorBlocks.Entry entry,
            TrapdoorModels baseModels
    ) {
        MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(entry.block());

        for (Direction facing : horizontalDirections()) {
            for (Half half : Half.values()) {
                for (boolean open : new boolean[] {false, true}) {
                    ResourceLocation baseModel = baseModels.model(half, open);

                    if (baseModel == null) {
                        throw new IllegalStateException(
                                "Base TrapdoorModels is missing "
                                        + poseName(half, open)
                                        + " for "
                                        + ModelUtil.idOf(entry.block())
                        );
                    }

                    Transform intrinsic = intrinsicTransform(facing, half, open);

                    addLegacyPart(
                            multi,
                            baseModel,
                            Conditions.always(),
                            facing,
                            half,
                            open,
                            entry.rotationX(),
                            entry.rotationY(),
                            intrinsic,
                            entry.variants()
                    );

                    for (TrapdoorBlocks.Part part : entry.parts()) {
                        if (!conditionsCompatible(part.conditions(), facing, half, open)) {
                            continue;
                        }

                        ResourceLocation model = part.models().model(half, open);

                        if (model == null) {
                            continue;
                        }

                        addLegacyPart(
                                multi,
                                model,
                                part.conditions(),
                                facing,
                                half,
                                open,
                                part.rotationX(),
                                part.rotationY(),
                                intrinsic,
                                part.variants()
                        );
                    }
                }
            }
        }
    }

    private static void addLegacyPart(
            MultiPartBlockStateBuilder multi,
            ResourceLocation model,
            Conditions.Match extraConditions,
            Direction facing,
            Half half,
            boolean open,
            int localX,
            int localY,
            Transform intrinsic,
            Variants.Set variants
    ) {
        var modelBuilder = multi.part();
        List<Variants.Option> options = variants.options();

        for (int i = 0; i < options.size(); i++) {
            Variants.Option option = options.get(i);

            modelBuilder = modelBuilder
                    .modelFile(new ModelFile.UncheckedModelFile(model))
                    .rotationX(combine(combine(intrinsic.x(), localX), option.rotationX()))
                    .rotationY(combine(combine(intrinsic.y(), localY), option.rotationY()))
                    .weight(option.weight());

            if (i < options.size() - 1) {
                modelBuilder = modelBuilder.nextModel();
            }
        }

        MultiPartBlockStateBuilder.PartBuilder builder = modelBuilder
                .addModel()
                .condition(TrapDoorBlock.FACING, facing)
                .condition(TrapDoorBlock.HALF, half)
                .condition(TrapDoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicTrapdoorProperty(term.property())) {
                legacyCondition(builder, term.property(), term.value());
            }
        }

        builder.end();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void legacyCondition(
            MultiPartBlockStateBuilder.PartBuilder builder,
            Property property,
            Comparable value
    ) {
        builder.condition(property, value);
    }

    private static void generateLegacyItem(
            BlockStateProvider blocks,
            TrapdoorBlocks.Entry entry,
            TrapdoorModels baseModels
    ) {
        if (entry.itemMode() == TrapdoorBlocks.ItemMode.NONE) {
            return;
        }

        ResourceLocation model = itemModel(entry, baseModels);

        blocks.simpleBlockItem(
                entry.block(),
                new ModelFile.UncheckedModelFile(model)
        );
    }
    *///?} else {
    private static TrapdoorModels baseModels(
            BlockModelGenerators blocks,
            TrapdoorBlocks.Entry entry
    ) {
        if (entry.baseMode() == TrapdoorBlocks.BaseMode.EXISTING) {
            return entry.baseModels();
        }

        ResourceLocation blockId = ModelUtil.idOf(entry.block());
        ResourceLocation texture = entry.textures().texture();

        ResourceLocation bottom = generatedTrapdoorModel(
                blocks,
                blockId,
                "_bottom",
                "minecraft:block/template_trapdoor_bottom",
                texture
        );
        ResourceLocation top = generatedTrapdoorModel(
                blocks,
                blockId,
                "_top",
                "minecraft:block/template_trapdoor_top",
                texture
        );
        ResourceLocation open = generatedTrapdoorModel(
                blocks,
                blockId,
                "_open",
                "minecraft:block/template_trapdoor_open",
                texture
        );

        return TrapdoorModels.full(
                bottom.toString(),
                top.toString(),
                open.toString()
        );
    }

    private static ResourceLocation generatedTrapdoorModel(
            BlockModelGenerators blocks,
            ResourceLocation blockId,
            String suffix,
            String parent,
            ResourceLocation texture
    ) {
        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + suffix
        );

        JsonObject root = new JsonObject();
        root.addProperty("parent", parent);

        JsonObject textures = new JsonObject();
        textures.addProperty("texture", texture.toString());
        root.add("textures", textures);

        blocks.modelOutput.accept(model, () -> root);
        return model;
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            TrapdoorBlocks.Entry entry,
            TrapdoorModels baseModels
    ) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(entry.block());

        for (Direction facing : horizontalDirections()) {
            for (Half half : Half.values()) {
                for (boolean open : new boolean[] {false, true}) {
                    ResourceLocation baseModel = baseModels.model(half, open);

                    if (baseModel == null) {
                        throw new IllegalStateException(
                                "Base TrapdoorModels is missing "
                                        + poseName(half, open)
                                        + " for "
                                        + ModelUtil.idOf(entry.block())
                        );
                    }

                    Transform intrinsic = intrinsicTransform(facing, half, open);

                    multi = addPart(
                            multi,
                            baseModel,
                            Conditions.always(),
                            facing,
                            half,
                            open,
                            entry.rotationX(),
                            entry.rotationY(),
                            intrinsic,
                            entry.variants()
                    );

                    for (TrapdoorBlocks.Part part : entry.parts()) {
                        if (!conditionsCompatible(part.conditions(), facing, half, open)) {
                            continue;
                        }

                        ResourceLocation model = part.models().model(half, open);

                        if (model == null) {
                            continue;
                        }

                        multi = addPart(
                                multi,
                                model,
                                part.conditions(),
                                facing,
                                half,
                                open,
                                part.rotationX(),
                                part.rotationY(),
                                intrinsic,
                                part.variants()
                        );
                    }
                }
            }
        }

        blocks.blockStateOutput.accept(multi);
    }

    private static MultiPartGenerator addPart(
            MultiPartGenerator multi,
            ResourceLocation model,
            Conditions.Match extraConditions,
            Direction facing,
            Half half,
            boolean open,
            int localX,
            int localY,
            Transform intrinsic,
            Variants.Set variants
    ) {
        int baseX = combine(intrinsic.x(), localX);
        int baseY = combine(intrinsic.y(), localY);

        //? if <1.21.5 {
        /*Condition.TerminalCondition condition = Condition.condition()
                .term(TrapDoorBlock.FACING, facing)
                .term(TrapDoorBlock.HALF, half)
                .term(TrapDoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicTrapdoorProperty(term.property())) {
                modernCondition(condition, term.property(), term.value());
            }
        }

        return multi.with(
                condition,
                variants(model, baseX, baseY, variants)
        );
        *///?} else {
        var condition = BlockModelGenerators.condition()
                .term(TrapDoorBlock.FACING, facing)
                .term(TrapDoorBlock.HALF, half)
                .term(TrapDoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicTrapdoorProperty(term.property())) {
                modernCondition(condition, term.property(), term.value());
            }
        }

        return multi.with(
                condition,
                variants(model, baseX, baseY, variants)
        );
        //?}
    }

    //? if <1.21.5 {
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    private static void modernCondition(
            Condition.TerminalCondition condition,
            Property property,
            Comparable value
    ) {
        condition.term(property, value);
    }

    private static List<Variant> variants(
            ResourceLocation model,
            int baseX,
            int baseY,
            Variants.Set variants
    ) {
        List<Variant> result = new ArrayList<>();

        for (Variants.Option option : variants.options()) {
            result.add(
                    variant(
                            model,
                            combine(baseX, option.rotationX()),
                            combine(baseY, option.rotationY()),
                            option.weight()
                    )
            );
        }

        return result;
    }

    private static Variant variant(
            ResourceLocation model,
            int x,
            int y,
            int weight
    ) {
        Variant variant = Variant.variant()
                .with(VariantProperties.MODEL, model);

        if (x != 0) {
            variant = variant.with(VariantProperties.X_ROT, rotation(x));
        }

        if (y != 0) {
            variant = variant.with(VariantProperties.Y_ROT, rotation(y));
        }

        if (weight != 1) {
            variant = variant.with(VariantProperties.WEIGHT, weight);
        }

        return variant;
    }

    private static VariantProperties.Rotation rotation(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + degrees);
        };
    }
    *///?} else {
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void modernCondition(
            net.minecraft.client.data.models.blockstates.ConditionBuilder condition,
            Property property,
            Comparable value
    ) {
        condition.term(property, value);
    }

    private static MultiVariant variants(
            ResourceLocation model,
            int baseX,
            int baseY,
            Variants.Set variants
    ) {
        WeightedList.Builder<Variant> result = WeightedList.<Variant>builder();

        for (Variants.Option option : variants.options()) {
            result.add(
                    variant(
                            model,
                            combine(baseX, option.rotationX()),
                            combine(baseY, option.rotationY())
                    ),
                    option.weight()
            );
        }

        return new MultiVariant(result.build());
    }

    private static Variant variant(
            ResourceLocation model,
            int x,
            int y
    ) {
        Variant variant = new Variant(model);

        Quadrant xRotation = quadrant(x);
        Quadrant yRotation = quadrant(y);

        if (xRotation != Quadrant.R0) {
            variant = variant.withXRot(xRotation);
        }

        if (yRotation != Quadrant.R0) {
            variant = variant.withYRot(yRotation);
        }

        return variant;
    }

    private static Quadrant quadrant(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> Quadrant.R0;
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + degrees);
        };
    }
    //?}

    private static void generateItem(
            BlockModelGenerators blocks,
            TrapdoorBlocks.Entry entry,
            TrapdoorModels baseModels
    ) {
        if (entry.itemMode() == TrapdoorBlocks.ItemMode.NONE) {
            return;
        }

        blocks.registerSimpleItemModel(
                entry.block(),
                itemModel(entry, baseModels)
        );
    }
    //?}

    private static ResourceLocation itemModel(
            TrapdoorBlocks.Entry entry,
            TrapdoorModels baseModels
    ) {
        if (entry.itemMode() == TrapdoorBlocks.ItemMode.EXISTING) {
            return ResourceLocation.parse(entry.itemModel());
        }

        ResourceLocation bottom = baseModels.model(Half.BOTTOM, false);

        if (bottom == null) {
            throw new IllegalStateException(
                    "Default trapdoor item requires a bottom model for "
                            + ModelUtil.idOf(entry.block())
            );
        }

        return bottom;
    }

    private static boolean conditionsCompatible(
            Conditions.Match conditions,
            Direction facing,
            Half half,
            boolean open
    ) {
        for (Conditions.Term<?> term : conditions.terms()) {
            Property<?> property = term.property();
            Object value = term.value();

            if (property == TrapDoorBlock.FACING && value != facing) {
                return false;
            }

            if (property == TrapDoorBlock.HALF && value != half) {
                return false;
            }

            if (property == TrapDoorBlock.OPEN && !value.equals(open)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isIntrinsicTrapdoorProperty(Property<?> property) {
        return property == TrapDoorBlock.FACING
                || property == TrapDoorBlock.HALF
                || property == TrapDoorBlock.OPEN;
    }

    private static Transform intrinsicTransform(
            Direction facing,
            Half half,
            boolean open
    ) {
        int y = switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };

        if (open && half == Half.TOP) {
            return new Transform(180, combine(y, 180));
        }

        return new Transform(0, y);
    }

    private static int combine(int first, int second) {
        return TrapdoorBlocks.normalize(first + second);
    }

    private static String poseName(Half half, boolean open) {
        return half.getSerializedName()
                + "/"
                + (open ? "open" : "closed");
    }

    private static Direction[] horizontalDirections() {
        return new Direction[] {
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        };
    }

    private record Transform(int x, int y) {}
}
