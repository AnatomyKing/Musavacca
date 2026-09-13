package space.anatomyuniverse.musavacca.data.models.newgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FireBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
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
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.random.WeightedList;
//?}
//?}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FireBlockGenerator {
    private FireBlockGenerator() {}

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks,
            ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            //?}
            Collection<FireBlocks.Entry> entries
    ) {
        if (entries == null) {
            return;
        }

        for (FireBlocks.Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            entry.validate();

            //? if <1.21.4 {
            /*GeneratedModels<ModelFile> models = generateLegacyModels(blocks, entry);
            generateLegacyBlockState(blocks, entry, models);

            if (!entry.noItem()) {
                generateLegacyItem(blocks, entry, models.floor0());
            }
            *///?} else {
            GeneratedModels<ResourceLocation> models = generateModels(blocks, entry);
            generateBlockState(blocks, entry, models);

            if (!entry.noItem()) {
                generateItem(blocks, items, entry, models.floor0());
            }
            //?}
        }
    }

    //? if <1.21.4 {
    /*private static GeneratedModels<ModelFile> generateLegacyModels(
            BlockStateProvider blocks,
            FireBlocks.Entry entry
    ) {
        return new GeneratedModels<>(
                generateLegacyModel(blocks, entry, "floor0", Shape.FLOOR, 0),
                generateLegacyModel(blocks, entry, "floor1", Shape.FLOOR, 1),
                generateLegacyModel(blocks, entry, "side0", Shape.SIDE, 0),
                generateLegacyModel(blocks, entry, "side1", Shape.SIDE, 1),
                generateLegacyModel(blocks, entry, "side_alt0", Shape.SIDE_ALT, 0),
                generateLegacyModel(blocks, entry, "side_alt1", Shape.SIDE_ALT, 1),
                generateLegacyModel(blocks, entry, "up0", Shape.UP, 0),
                generateLegacyModel(blocks, entry, "up1", Shape.UP, 1),
                generateLegacyModel(blocks, entry, "up_alt0", Shape.UP_ALT, 0),
                generateLegacyModel(blocks, entry, "up_alt1", Shape.UP_ALT, 1)
        );
    }

    private static ModelFile generateLegacyModel(
            BlockStateProvider blocks,
            FireBlocks.Entry entry,
            String suffix,
            Shape shape,
            int frame
    ) {
        ResourceLocation id = modelId(entry, suffix);
        String modelName = blockModelPath(id);

        List<Tints.GeneratedLayer> layers = Tints.generatedLayers(entry.tint());

        BlockModelBuilder model = blocks.models()
                .getBuilder(modelName)
                .ao(false);

        ResourceLocation particle = textureForLayer(entry.textures(), entry.tint(), frame, layers.get(0));
        model.texture("particle", particle);

        for (Tints.GeneratedLayer layer : layers) {
            String key = textureKey(layer);
            model.texture(key, textureForLayer(entry.textures(), entry.tint(), frame, layer));
        }

        switch (shape) {
            case FLOOR -> addLegacyFloor(model, layers);
            case SIDE -> addLegacySide(model, layers, false);
            case SIDE_ALT -> addLegacySide(model, layers, true);
            case UP -> addLegacyUp(model, layers, false);
            case UP_ALT -> addLegacyUp(model, layers, true);
        }

        return model;
    }

    private static void addLegacyFloor(
            BlockModelBuilder model,
            List<Tints.GeneratedLayer> layers
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            addLegacyRotatedPlane(
                    model, layer,
                    0, 0, 8.8F,
                    16, 22.4F, 8.8F,
                    8, 8, 8,
                    Direction.Axis.X, -22.5F,
                    Direction.SOUTH, 0
            );

            addLegacyRotatedPlane(
                    model, layer,
                    0, 0, 7.2F,
                    16, 22.4F, 7.2F,
                    8, 8, 8,
                    Direction.Axis.X, 22.5F,
                    Direction.NORTH, 0
            );

            addLegacyRotatedPlane(
                    model, layer,
                    8.8F, 0, 0,
                    8.8F, 22.4F, 16,
                    8, 8, 8,
                    Direction.Axis.Z, -22.5F,
                    Direction.WEST, 0
            );

            addLegacyRotatedPlane(
                    model, layer,
                    7.2F, 0, 0,
                    7.2F, 22.4F, 16,
                    8, 8, 8,
                    Direction.Axis.Z, 22.5F,
                    Direction.EAST, 0
            );
        }
    }

    private static void addLegacySide(
            BlockModelBuilder model,
            List<Tints.GeneratedLayer> layers,
            boolean alternateUv
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            var element = model.element()
                    .from(0, 0, 0.01F)
                    .to(16, 22.4F, 0.01F)
                    .shade(false);

            addLegacyFace(element, Direction.SOUTH, layer, alternateUv, 0);
            addLegacyFace(element, Direction.NORTH, layer, alternateUv, 0);
            element.end();
        }
    }

    private static void addLegacyUp(
            BlockModelBuilder model,
            List<Tints.GeneratedLayer> layers,
            boolean alternate
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            if (!alternate) {
                addLegacyRotatedPlane(
                        model, layer,
                        0, 16, 0,
                        16, 16, 16,
                        16, 16, 8,
                        Direction.Axis.Z, 22.5F,
                        Direction.DOWN, 270
                );

                addLegacyRotatedPlane(
                        model, layer,
                        0, 16, 0,
                        16, 16, 16,
                        0, 16, 8,
                        Direction.Axis.Z, -22.5F,
                        Direction.DOWN, 90
                );
            } else {
                addLegacyRotatedPlane(
                        model, layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 16,
                        Direction.Axis.X, -22.5F,
                        Direction.DOWN, 180
                );

                addLegacyRotatedPlane(
                        model, layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 0,
                        Direction.Axis.X, 22.5F,
                        Direction.DOWN, 0
                );
            }
        }
    }

    private static void addLegacyRotatedPlane(
            BlockModelBuilder model,
            Tints.GeneratedLayer layer,
            float fromX,
            float fromY,
            float fromZ,
            float toX,
            float toY,
            float toZ,
            float originX,
            float originY,
            float originZ,
            Direction.Axis axis,
            float angle,
            Direction face,
            int faceRotation
    ) {
        var element = model.element()
                .from(fromX, fromY, fromZ)
                .to(toX, toY, toZ)
                .shade(false);

        element.rotation()
                .origin(originX, originY, originZ)
                .axis(axis)
                .angle(angle)
                .rescale(true)
                .end();

        addLegacyFace(element, face, layer, false, faceRotation);
        element.end();
    }

    private static void addLegacyFace(
            ModelBuilder<BlockModelBuilder>.ElementBuilder element,
            Direction direction,
            Tints.GeneratedLayer layer,
            boolean alternateUv,
            int rotation
    ) {
        var face = element.face(direction)
                .uvs(
                        alternateUv ? 16 : 0,
                        0,
                        alternateUv ? 0 : 16,
                        16
                )
                .texture("#" + textureKey(layer));

        if (layer.tintIndex() >= 0) {
            face.tintindex(layer.tintIndex());
        }

        if (rotation != 0) {
            face.rotation(faceRotation(rotation));
        }

        face.end();
    }

    private static ModelBuilder.FaceRotation faceRotation(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> ModelBuilder.FaceRotation.ZERO;
            case 90 -> ModelBuilder.FaceRotation.CLOCKWISE_90;
            case 180 -> ModelBuilder.FaceRotation.UPSIDE_DOWN;
            case 270 -> ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90;
            default -> throw new IllegalArgumentException("Unsupported face rotation: " + degrees);
        };
    }

    private static void generateLegacyBlockState(
            BlockStateProvider blocks,
            FireBlocks.Entry entry,
            GeneratedModels<ModelFile> models
    ) {
        MultiPartBlockStateBuilder multipart = blocks.getMultipartBuilder(entry.block());

        addLegacyChoice(
                multipart,
                List.of(models.floor0(), models.floor1()),
                0,
                entry.variants(),
                null,
                true
        );

        addLegacySideChoice(multipart, FireBlock.NORTH, models.sideModels(), 0, entry.variants());
        addLegacySideChoice(multipart, FireBlock.EAST, models.sideModels(), 90, entry.variants());
        addLegacySideChoice(multipart, FireBlock.SOUTH, models.sideModels(), 180, entry.variants());
        addLegacySideChoice(multipart, FireBlock.WEST, models.sideModels(), 270, entry.variants());

        addLegacyChoice(
                multipart,
                models.upModels(),
                0,
                entry.variants(),
                FireBlock.UP,
                false
        );
    }

    private static void addLegacySideChoice(
            MultiPartBlockStateBuilder multipart,
            BooleanProperty property,
            List<ModelFile> models,
            int y,
            Variants.Set variants
    ) {
        addLegacyChoice(multipart, models, y, variants, property, false);
        addLegacyChoice(multipart, models, y, variants, null, true);
    }

    private static void addLegacyChoice(
            MultiPartBlockStateBuilder multipart,
            List<ModelFile> models,
            int baseY,
            Variants.Set variants,
            BooleanProperty property,
            boolean noFaces
    ) {
        var modelBuilder = multipart.part();

        int choiceCount = models.size() * variants.options().size();
        int choice = 0;

        for (ModelFile model : models) {
            for (Variants.Option option : variants.options()) {
                modelBuilder = modelBuilder
                        .modelFile(model)
                        .rotationX(option.rotationX())
                        .rotationY(combine(baseY, option.rotationY()))
                        .weight(option.weight());

                choice++;
                if (choice < choiceCount) {
                    modelBuilder = modelBuilder.nextModel();
                }
            }
        }

        MultiPartBlockStateBuilder.PartBuilder part = modelBuilder.addModel();

        if (property != null) {
            part.condition(property, true);
        }

        if (noFaces) {
            part.condition(FireBlock.EAST, false)
                    .condition(FireBlock.NORTH, false)
                    .condition(FireBlock.SOUTH, false)
                    .condition(FireBlock.UP, false)
                    .condition(FireBlock.WEST, false);
        }

        part.end();
    }

    private static void generateLegacyItem(
            BlockStateProvider blocks,
            FireBlocks.Entry entry,
            ModelFile defaultModel
    ) {
        ModelFile model = entry.itemModel() != null
                ? blocks.models().getExistingFile(ResourceLocation.parse(entry.itemModel()))
                : defaultModel;

        blocks.simpleBlockItem(entry.block(), model);
    }

    *///?} else {
    private static GeneratedModels<ResourceLocation> generateModels(
            BlockModelGenerators blocks,
            FireBlocks.Entry entry
    ) {
        return new GeneratedModels<>(
                generateModel(blocks, entry, "floor0", Shape.FLOOR, 0),
                generateModel(blocks, entry, "floor1", Shape.FLOOR, 1),
                generateModel(blocks, entry, "side0", Shape.SIDE, 0),
                generateModel(blocks, entry, "side1", Shape.SIDE, 1),
                generateModel(blocks, entry, "side_alt0", Shape.SIDE_ALT, 0),
                generateModel(blocks, entry, "side_alt1", Shape.SIDE_ALT, 1),
                generateModel(blocks, entry, "up0", Shape.UP, 0),
                generateModel(blocks, entry, "up1", Shape.UP, 1),
                generateModel(blocks, entry, "up_alt0", Shape.UP_ALT, 0),
                generateModel(blocks, entry, "up_alt1", Shape.UP_ALT, 1)
        );
    }

    private static ResourceLocation generateModel(
            BlockModelGenerators blocks,
            FireBlocks.Entry entry,
            String suffix,
            Shape shape,
            int frame
    ) {
        ResourceLocation id = modelId(entry, suffix);
        JsonObject json = modelJson(entry, shape, frame);
        blocks.modelOutput.accept(id, () -> json);
        return id;
    }

    private static JsonObject modelJson(
            FireBlocks.Entry entry,
            Shape shape,
            int frame
    ) {
        List<Tints.GeneratedLayer> layers = Tints.generatedLayers(entry.tint());

        JsonObject root = new JsonObject();
        root.addProperty("ambientocclusion", false);

        JsonObject textures = new JsonObject();
        textures.addProperty(
                "particle",
                textureForLayer(entry.textures(), entry.tint(), frame, layers.get(0)).toString()
        );

        for (Tints.GeneratedLayer layer : layers) {
            textures.addProperty(
                    textureKey(layer),
                    textureForLayer(entry.textures(), entry.tint(), frame, layer).toString()
            );
        }

        root.add("textures", textures);

        JsonArray elements = new JsonArray();

        switch (shape) {
            case FLOOR -> addFloor(elements, layers);
            case SIDE -> addSide(elements, layers, false);
            case SIDE_ALT -> addSide(elements, layers, true);
            case UP -> addUp(elements, layers, false);
            case UP_ALT -> addUp(elements, layers, true);
        }

        root.add("elements", elements);
        return root;
    }

    private static void addFloor(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            elements.add(rotatedPlane(
                    layer,
                    0, 0, 8.8F,
                    16, 22.4F, 8.8F,
                    8, 8, 8,
                    "x", -22.5F,
                    "south", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    0, 0, 7.2F,
                    16, 22.4F, 7.2F,
                    8, 8, 8,
                    "x", 22.5F,
                    "north", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    8.8F, 0, 0,
                    8.8F, 22.4F, 16,
                    8, 8, 8,
                    "z", -22.5F,
                    "west", 0
            ));

            elements.add(rotatedPlane(
                    layer,
                    7.2F, 0, 0,
                    7.2F, 22.4F, 16,
                    8, 8, 8,
                    "z", 22.5F,
                    "east", 0
            ));
        }
    }

    private static void addSide(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers,
            boolean alternateUv
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            JsonObject element = element(
                    0, 0, 0.01F,
                    16, 22.4F, 0.01F
            );
            element.addProperty("shade", false);

            JsonObject faces = new JsonObject();
            faces.add("south", face(layer, alternateUv, 0));
            faces.add("north", face(layer, alternateUv, 0));
            element.add("faces", faces);

            elements.add(element);
        }
    }

    private static void addUp(
            JsonArray elements,
            List<Tints.GeneratedLayer> layers,
            boolean alternate
    ) {
        for (Tints.GeneratedLayer layer : layers) {
            if (!alternate) {
                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        16, 16, 8,
                        "z", 22.5F,
                        "down", 270
                ));

                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        0, 16, 8,
                        "z", -22.5F,
                        "down", 90
                ));
            } else {
                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 16,
                        "x", -22.5F,
                        "down", 180
                ));

                elements.add(rotatedPlane(
                        layer,
                        0, 16, 0,
                        16, 16, 16,
                        8, 16, 0,
                        "x", 22.5F,
                        "down", 0
                ));
            }
        }
    }

    private static JsonObject rotatedPlane(
            Tints.GeneratedLayer layer,
            float fromX,
            float fromY,
            float fromZ,
            float toX,
            float toY,
            float toZ,
            float originX,
            float originY,
            float originZ,
            String axis,
            float angle,
            String faceDirection,
            int faceRotation
    ) {
        JsonObject element = element(fromX, fromY, fromZ, toX, toY, toZ);
        element.addProperty("shade", false);

        JsonObject rotation = new JsonObject();
        rotation.add("origin", vector(originX, originY, originZ));
        rotation.addProperty("axis", axis);
        rotation.addProperty("angle", angle);
        rotation.addProperty("rescale", true);
        element.add("rotation", rotation);

        JsonObject faces = new JsonObject();
        faces.add(faceDirection, face(layer, false, faceRotation));
        element.add("faces", faces);

        return element;
    }

    private static JsonObject element(
            float fromX,
            float fromY,
            float fromZ,
            float toX,
            float toY,
            float toZ
    ) {
        JsonObject element = new JsonObject();
        element.add("from", vector(fromX, fromY, fromZ));
        element.add("to", vector(toX, toY, toZ));
        return element;
    }

    private static JsonObject face(
            Tints.GeneratedLayer layer,
            boolean alternateUv,
            int rotation
    ) {
        JsonObject face = new JsonObject();

        JsonArray uv = new JsonArray();
        uv.add(alternateUv ? 16 : 0);
        uv.add(0);
        uv.add(alternateUv ? 0 : 16);
        uv.add(16);
        face.add("uv", uv);

        face.addProperty("texture", "#" + textureKey(layer));

        if (layer.tintIndex() >= 0) {
            face.addProperty("tintindex", layer.tintIndex());
        }

        if (rotation != 0) {
            face.addProperty("rotation", rotation);
        }

        return face;
    }

    private static JsonArray vector(float x, float y, float z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            FireBlocks.Entry entry,
            GeneratedModels<ResourceLocation> models
    ) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(entry.block());

        //? if <1.21.5 {
        /*multi = multi.with(
                noFaces(),
                variants(List.of(models.floor0(), models.floor1()), 0, entry.variants())
        );

        multi = multi.with(
                Condition.or(
                        Condition.condition().term(FireBlock.NORTH, true),
                        noFaces()
                ),
                variants(models.sideModels(), 0, entry.variants())
        );

        multi = multi.with(
                Condition.or(
                        Condition.condition().term(FireBlock.EAST, true),
                        noFaces()
                ),
                variants(models.sideModels(), 90, entry.variants())
        );

        multi = multi.with(
                Condition.or(
                        Condition.condition().term(FireBlock.SOUTH, true),
                        noFaces()
                ),
                variants(models.sideModels(), 180, entry.variants())
        );

        multi = multi.with(
                Condition.or(
                        Condition.condition().term(FireBlock.WEST, true),
                        noFaces()
                ),
                variants(models.sideModels(), 270, entry.variants())
        );

        multi = multi.with(
                Condition.condition().term(FireBlock.UP, true),
                variants(models.upModels(), 0, entry.variants())
        );
        *///?} else {
        multi = multi.with(
                noFaces(),
                variants(List.of(models.floor0(), models.floor1()), 0, entry.variants())
        );

        multi = multi.with(
                BlockModelGenerators.or(
                        BlockModelGenerators.condition().term(FireBlock.NORTH, true),
                        noFaces()
                ),
                variants(models.sideModels(), 0, entry.variants())
        );

        multi = multi.with(
                BlockModelGenerators.or(
                        BlockModelGenerators.condition().term(FireBlock.EAST, true),
                        noFaces()
                ),
                variants(models.sideModels(), 90, entry.variants())
        );

        multi = multi.with(
                BlockModelGenerators.or(
                        BlockModelGenerators.condition().term(FireBlock.SOUTH, true),
                        noFaces()
                ),
                variants(models.sideModels(), 180, entry.variants())
        );

        multi = multi.with(
                BlockModelGenerators.or(
                        BlockModelGenerators.condition().term(FireBlock.WEST, true),
                        noFaces()
                ),
                variants(models.sideModels(), 270, entry.variants())
        );

        multi = multi.with(
                BlockModelGenerators.condition().term(FireBlock.UP, true),
                variants(models.upModels(), 0, entry.variants())
        );
        //?}

        blocks.blockStateOutput.accept(multi);
    }

    private static void generateItem(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            FireBlocks.Entry entry,
            ResourceLocation defaultModel
    ) {
        ResourceLocation model = entry.itemModel() != null
                ? ResourceLocation.parse(entry.itemModel())
                : defaultModel;

        Tints.Tint tint = entry.itemTint();

        if (!tint.tinted()) {
            blocks.registerSimpleItemModel(entry.block(), model);
            return;
        }

        if (tint.kind() == Tints.Kind.PEARL_FIRE) {
            throw new IllegalStateException(
                    "Pearl-fire generated item models need a profile-aware ItemTintSource. "
                            + "Use .noItem() for this FireBlocks entry until that item tint source is configured."
            );
        }

        ItemTintSource source = switch (tint.kind()) {
            case NONE -> throw new IllegalStateException("NONE reached tinted item generation");
            case CONSTANT -> new Constant(TintColorUtil.rgb(((Tints.Constant) tint).rgb()));
            case BIOME_FOLIAGE -> new Constant(TintColorUtil.defaultFoliageItemTint());
            case HEX_COLOR -> HexColorItemTintSource.INSTANCE;
            case PEARL_FIRE -> throw new IllegalStateException("Handled above");
        };

        items.itemModelOutput.accept(
                entry.block().asItem(),
                new BlockModelWrapper.Unbaked(model, List.of(source))
        );
    }

    //? if <1.21.5 {
    /*private static List<Variant> variants(
            List<ResourceLocation> models,
            int baseY,
            Variants.Set variants
    ) {
        List<Variant> result = new ArrayList<>();

        for (ResourceLocation model : models) {
            for (Variants.Option option : variants.options()) {
                result.add(
                        variant(
                                model,
                                option.rotationX(),
                                combine(baseY, option.rotationY()),
                                option.weight()
                        )
                );
            }
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

    private static Condition noFaces() {
        return Condition.condition()
                .term(FireBlock.EAST, false)
                .term(FireBlock.NORTH, false)
                .term(FireBlock.SOUTH, false)
                .term(FireBlock.UP, false)
                .term(FireBlock.WEST, false);
    }

    *///?} else {
    private static MultiVariant variants(
            List<ResourceLocation> models,
            int baseY,
            Variants.Set variants
    ) {
        WeightedList.Builder<Variant> result = WeightedList.<Variant>builder();

        for (ResourceLocation model : models) {
            for (Variants.Option option : variants.options()) {
                result.add(
                        variant(
                                model,
                                option.rotationX(),
                                combine(baseY, option.rotationY())
                        ),
                        option.weight()
                );
            }
        }

        return new MultiVariant(result.build());
    }

    private static Variant variant(ResourceLocation model, int x, int y) {
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

    private static ConditionBuilder noFaces() {
        return BlockModelGenerators.condition()
                .term(FireBlock.EAST, false)
                .term(FireBlock.NORTH, false)
                .term(FireBlock.SOUTH, false)
                .term(FireBlock.UP, false)
                .term(FireBlock.WEST, false);
    }
    //?}
    //?}

    private static ResourceLocation textureForLayer(
            FireTextures.Set textures,
            Tints.Tint tint,
            int frame,
            Tints.GeneratedLayer layer
    ) {
        if (tint.kind() == Tints.Kind.PEARL_FIRE) {
            return FireTextures.layer(textures, frame, layer.sourceLayer());
        }

        return FireTextures.frame(textures, frame);
    }

    private static String textureKey(Tints.GeneratedLayer layer) {
        return "fire_" + layer.sourceLayer();
    }

    private static ResourceLocation modelId(FireBlocks.Entry entry, String suffix) {
        ResourceLocation id = ModelUtil.idOf(entry.block());
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + "_" + suffix
        );
    }

    private static String blockModelPath(ResourceLocation model) {
        String path = model.getPath();
        return path.startsWith("block/")
                ? path.substring("block/".length())
                : path;
    }

    private static int combine(int first, int second) {
        return Math.floorMod(first + second, 360);
    }

    private enum Shape {
        FLOOR,
        SIDE,
        SIDE_ALT,
        UP,
        UP_ALT
    }

    private record GeneratedModels<T>(
            T floor0,
            T floor1,
            T side0,
            T side1,
            T sideAlt0,
            T sideAlt1,
            T up0,
            T up1,
            T upAlt0,
            T upAlt1
    ) {
        List<T> sideModels() {
            return List.of(side0, side1, sideAlt0, sideAlt1);
        }

        List<T> upModels() {
            return List.of(up0, up1, upAlt0, upAlt1);
        }
    }
}
