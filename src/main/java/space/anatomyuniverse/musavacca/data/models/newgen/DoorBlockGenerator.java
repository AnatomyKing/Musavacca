package space.anatomyuniverse.musavacca.data.models.newgen;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

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
import java.util.Map;

public final class DoorBlockGenerator {
    private DoorBlockGenerator() {}

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks,
            ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            //?}
            Collection<DoorBlocks.Entry> entries
    ) {
        if (entries == null) {
            return;
        }

        for (DoorBlocks.Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            entry.validate();

            //? if <1.21.4 {
            /*DoorModels baseModels = baseModels(blocks, entry);
            generateLegacyBlockState(blocks, entry, baseModels);
            generateLegacyItems(items, entry);
            *///?} else {
            DoorModels baseModels = baseModels(blocks, entry);
            generateBlockState(blocks, entry, baseModels);
            generateItems(blocks, items, entry);
            //?}
        }
    }

    //? if <1.21.4 {
    /*private static DoorModels baseModels(
            BlockStateProvider blocks,
            DoorBlocks.Entry entry
    ) {
        if (entry.baseMode() == DoorBlocks.BaseMode.EXISTING) {
            return entry.baseModels();
        }

        ResourceLocation id = ModelUtil.idOf(entry.block());
        DoorTextures.Set textures = entry.textures();

        ResourceLocation bottomLeft = legacyDoorModel(
                blocks,
                id,
                "_bottom_left",
                "door_bottom_left",
                textures
        );
        ResourceLocation bottomLeftOpen = legacyDoorModel(
                blocks,
                id,
                "_bottom_left_open",
                "door_bottom_left_open",
                textures
        );
        ResourceLocation bottomRight = legacyDoorModel(
                blocks,
                id,
                "_bottom_right",
                "door_bottom_right",
                textures
        );
        ResourceLocation bottomRightOpen = legacyDoorModel(
                blocks,
                id,
                "_bottom_right_open",
                "door_bottom_right_open",
                textures
        );
        ResourceLocation topLeft = legacyDoorModel(
                blocks,
                id,
                "_top_left",
                "door_top_left",
                textures
        );
        ResourceLocation topLeftOpen = legacyDoorModel(
                blocks,
                id,
                "_top_left_open",
                "door_top_left_open",
                textures
        );
        ResourceLocation topRight = legacyDoorModel(
                blocks,
                id,
                "_top_right",
                "door_top_right",
                textures
        );
        ResourceLocation topRightOpen = legacyDoorModel(
                blocks,
                id,
                "_top_right_open",
                "door_top_right_open",
                textures
        );

        return DoorModels.full(
                bottomLeft.toString(),
                bottomLeftOpen.toString(),
                bottomRight.toString(),
                bottomRightOpen.toString(),
                topLeft.toString(),
                topLeftOpen.toString(),
                topRight.toString(),
                topRightOpen.toString()
        );
    }

    private static ResourceLocation legacyDoorModel(
            BlockStateProvider blocks,
            ResourceLocation blockId,
            String suffix,
            String parent,
            DoorTextures.Set textures
    ) {
        blocks.models()
                .withExistingParent(
                        blockId.getPath() + suffix,
                        ResourceLocation.withDefaultNamespace("block/" + parent)
                )
                .texture("bottom", textures.bottom())
                .texture("top", textures.top());

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + suffix
        );
    }

    private static void generateLegacyBlockState(
            BlockStateProvider blocks,
            DoorBlocks.Entry entry,
            DoorModels baseModels
    ) {
        MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(entry.block());

        for (Direction facing : horizontalDirections()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                for (DoorHingeSide hinge : DoorHingeSide.values()) {
                    for (boolean open : new boolean[] {false, true}) {
                        ResourceLocation baseModel = baseModels.model(half, hinge, open);

                        if (baseModel == null) {
                            throw new IllegalStateException(
                                    "Base DoorModels is missing "
                                            + poseName(half, hinge, open)
                                            + " for "
                                            + ModelUtil.idOf(entry.block())
                            );
                        }

                        int doorY = doorYRotation(facing, hinge, open);

                        addLegacyPart(
                                multi,
                                entry,
                                baseModel,
                                Conditions.always(),
                                facing,
                                half,
                                hinge,
                                open,
                                entry.rotationX(),
                                entry.rotationY(),
                                doorY,
                                entry.variants()
                        );

                        for (DoorBlocks.Part part : entry.parts()) {
                            if (!conditionsCompatible(part.conditions(), facing, half, hinge, open)) {
                                continue;
                            }

                            ResourceLocation model = part.models().model(half, hinge, open);

                            if (model == null) {
                                continue;
                            }

                            addLegacyPart(
                                    multi,
                                    entry,
                                    model,
                                    part.conditions(),
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    part.rotationX(),
                                    part.rotationY(),
                                    doorY,
                                    part.variants()
                            );
                        }
                    }
                }
            }
        }
    }

    private static void addLegacyPart(
            MultiPartBlockStateBuilder multi,
            DoorBlocks.Entry entry,
            ResourceLocation model,
            Conditions.Match extraConditions,
            Direction facing,
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open,
            int localX,
            int localY,
            int doorY,
            Variants.Set variants
    ) {
        var modelBuilder = multi.part();
        List<Variants.Option> options = variants.options();

        for (int i = 0; i < options.size(); i++) {
            Variants.Option option = options.get(i);

            modelBuilder = modelBuilder
                    .modelFile(new ModelFile.UncheckedModelFile(model))
                    .rotationX(combine(localX, option.rotationX()))
                    .rotationY(combine(combine(doorY, localY), option.rotationY()))
                    .weight(option.weight());

            if (i < options.size() - 1) {
                modelBuilder = modelBuilder.nextModel();
            }
        }

        MultiPartBlockStateBuilder.PartBuilder builder = modelBuilder
                .addModel()
                .condition(DoorBlock.FACING, facing)
                .condition(DoorBlock.HALF, half)
                .condition(DoorBlock.HINGE, hinge)
                .condition(DoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicDoorProperty(term.property())) {
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

    private static void generateLegacyItems(
            ItemModelProvider items,
            DoorBlocks.Entry entry
    ) {
        for (DoorBlocks.Item item : entry.items()) {
            List<ResourceLocation> textures = itemTextures(item);
            ResourceLocation id = itemId(item.item());

            var model = items.getBuilder(id.getPath())
                    .parent(new ModelFile.UncheckedModelFile(
                            ResourceLocation.withDefaultNamespace("item/generated")
                    ));

            for (int layer = 0; layer < textures.size(); layer++) {
                model.texture("layer" + layer, textures.get(layer));
            }
        }
    }
    *///?} else {
    private static DoorModels baseModels(
            BlockModelGenerators blocks,
            DoorBlocks.Entry entry
    ) {
        if (entry.baseMode() == DoorBlocks.BaseMode.EXISTING) {
            return entry.baseModels();
        }

        DoorTextures.Set textures = entry.textures();

        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, textures.bottom())
                .put(TextureSlot.TOP, textures.top());

        ResourceLocation bottomLeft = ModelTemplates.DOOR_BOTTOM_LEFT.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation bottomLeftOpen = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation bottomRight = ModelTemplates.DOOR_BOTTOM_RIGHT.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation bottomRightOpen = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation topLeft = ModelTemplates.DOOR_TOP_LEFT.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation topLeftOpen = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation topRight = ModelTemplates.DOOR_TOP_RIGHT.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );
        ResourceLocation topRightOpen = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(
                entry.block(),
                mapping,
                blocks.modelOutput
        );

        return DoorModels.full(
                bottomLeft.toString(),
                bottomLeftOpen.toString(),
                bottomRight.toString(),
                bottomRightOpen.toString(),
                topLeft.toString(),
                topLeftOpen.toString(),
                topRight.toString(),
                topRightOpen.toString()
        );
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            DoorBlocks.Entry entry,
            DoorModels baseModels
    ) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(entry.block());

        for (Direction facing : horizontalDirections()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                for (DoorHingeSide hinge : DoorHingeSide.values()) {
                    for (boolean open : new boolean[] {false, true}) {
                        ResourceLocation baseModel = baseModels.model(half, hinge, open);

                        if (baseModel == null) {
                            throw new IllegalStateException(
                                    "Base DoorModels is missing "
                                            + poseName(half, hinge, open)
                                            + " for "
                                            + ModelUtil.idOf(entry.block())
                            );
                        }

                        int doorY = doorYRotation(facing, hinge, open);

                        multi = addPart(
                                multi,
                                baseModel,
                                Conditions.always(),
                                facing,
                                half,
                                hinge,
                                open,
                                entry.rotationX(),
                                entry.rotationY(),
                                doorY,
                                entry.variants()
                        );

                        for (DoorBlocks.Part part : entry.parts()) {
                            if (!conditionsCompatible(part.conditions(), facing, half, hinge, open)) {
                                continue;
                            }

                            ResourceLocation model = part.models().model(half, hinge, open);

                            if (model == null) {
                                continue;
                            }

                            multi = addPart(
                                    multi,
                                    model,
                                    part.conditions(),
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    part.rotationX(),
                                    part.rotationY(),
                                    doorY,
                                    part.variants()
                            );
                        }
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
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open,
            int localX,
            int localY,
            int doorY,
            Variants.Set variants
    ) {
        int baseX = localX;
        int baseY = combine(doorY, localY);

        //? if <1.21.5 {
        /*Condition.TerminalCondition condition = Condition.condition()
                .term(DoorBlock.FACING, facing)
                .term(DoorBlock.HALF, half)
                .term(DoorBlock.HINGE, hinge)
                .term(DoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicDoorProperty(term.property())) {
                modernCondition(condition, term.property(), term.value());
            }
        }

        return multi.with(
                condition,
                variants(model, baseX, baseY, variants)
        );
        *///?} else {
        var condition = BlockModelGenerators.condition()
                .term(DoorBlock.FACING, facing)
                .term(DoorBlock.HALF, half)
                .term(DoorBlock.HINGE, hinge)
                .term(DoorBlock.OPEN, open);

        for (Conditions.Term<?> term : extraConditions.terms()) {
            if (!isIntrinsicDoorProperty(term.property())) {
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

    private static void generateItems(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            DoorBlocks.Entry entry
    ) {
        for (DoorBlocks.Item item : entry.items()) {
            List<ResourceLocation> textures = itemTextures(item);
            ResourceLocation modelId = itemModelLocation(item.item());

            JsonObject root = new JsonObject();
            root.addProperty("parent", "minecraft:item/generated");

            JsonObject textureJson = new JsonObject();
            for (int layer = 0; layer < textures.size(); layer++) {
                textureJson.addProperty("layer" + layer, textures.get(layer).toString());
            }
            root.add("textures", textureJson);

            blocks.modelOutput.accept(modelId, () -> root);

            List<ItemTintSource> tintSources = new ArrayList<>(textures.size());
            for (int layer = 0; layer < textures.size(); layer++) {
                tintSources.add(itemTintSource(item.tintForLayer(layer)));
            }

            items.itemModelOutput.accept(
                    item.item().asItem(),
                    new BlockModelWrapper.Unbaked(modelId, tintSources)
            );
        }
    }

    private static ItemTintSource itemTintSource(Tints.Tint tint) {
        return switch (tint.kind()) {
            case NONE ->
                    new Constant(0xFFFFFFFF);

            case CONSTANT ->
                    new Constant(
                            TintColorUtil.rgb(((Tints.Constant) tint).rgb())
                    );

            case BIOME_FOLIAGE ->
                    new Constant(TintColorUtil.defaultFoliageItemTint());

            case HEX_COLOR,
                 PEARL_FIRE ->
                    HexColorItemTintSource.INSTANCE;
        };
    }
    //?}

    private static List<ResourceLocation> itemTextures(DoorBlocks.Item item) {
        ResourceLocation itemId = itemId(item.item());

        if (item.inferSingleTexture()) {
            return List.of(
                    ResourceLocation.fromNamespaceAndPath(
                            itemId.getNamespace(),
                            "item/" + itemId.getPath()
                    )
            );
        }

        List<ResourceLocation> result = new ArrayList<>();

        for (String token : item.textureTokens()) {
            result.add(resolveItemTexture(itemId, token));
        }

        return List.copyOf(result);
    }

    private static ResourceLocation resolveItemTexture(
            ResourceLocation itemId,
            String token
    ) {
        if (token.indexOf(':') >= 0) {
            return ResourceLocation.parse(token);
        }

        return ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                token.startsWith("item/")
                        ? token
                        : "item/" + token
        );
    }

    private static ResourceLocation itemModelLocation(
            net.minecraft.world.level.ItemLike item
    ) {
        ResourceLocation id = itemId(item);

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath()
        );
    }

    private static ResourceLocation itemId(
            net.minecraft.world.level.ItemLike item
    ) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());

        if (id == null) {
            throw new IllegalStateException("Cannot generate a model for an unregistered item");
        }

        return id;
    }

    private static boolean conditionsCompatible(
            Conditions.Match conditions,
            Direction facing,
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open
    ) {
        for (Conditions.Term<?> term : conditions.terms()) {
            Property<?> property = term.property();
            Object value = term.value();

            if (property == DoorBlock.FACING && value != facing) {
                return false;
            }

            if (property == DoorBlock.HALF && value != half) {
                return false;
            }

            if (property == DoorBlock.HINGE && value != hinge) {
                return false;
            }

            if (property == DoorBlock.OPEN && !value.equals(open)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isIntrinsicDoorProperty(Property<?> property) {
        return property == DoorBlock.FACING
                || property == DoorBlock.HALF
                || property == DoorBlock.HINGE
                || property == DoorBlock.OPEN;
    }

    private static int doorYRotation(
            Direction facing,
            DoorHingeSide hinge,
            boolean open
    ) {
        int closedRotation = switch (facing) {
            case EAST -> 0;
            case SOUTH -> 90;
            case WEST -> 180;
            case NORTH -> 270;
            default -> 0;
        };

        if (!open) {
            return closedRotation;
        }

        return Math.floorMod(
                closedRotation
                        + (hinge == DoorHingeSide.LEFT ? 90 : -90),
                360
        );
    }

    private static int combine(int first, int second) {
        return DoorBlocks.normalize(first + second);
    }

    private static String poseName(
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open
    ) {
        return half.getSerializedName()
                + "/"
                + hinge.getSerializedName()
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
}
