package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CrossBlockGenerator {
    private CrossBlockGenerator() {}

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks,
            ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            //?}
            Collection<CrossBlocks.Entry> entries
    ) {
        if (entries == null) {
            return;
        }

        for (CrossBlocks.Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            entry.validate();

            //? if <1.21.4 {
            /*ModelCache<ModelFile> cache =
                    new ModelCache<>();

            if (entry.mode() == CrossBlocks.Mode.MODELS) {
                generateModels(blocks, entry, cache);
            } else {
                generateMultipart(blocks, entry, cache);
            }

            if (!entry.noItem()) {
                generateLegacyItem(blocks, items, entry);
            }
            *///?} else {
            ModelCache<ResourceLocation> cache = new ModelCache<>();

            MultiPartGenerator multi = MultiPartGenerator
                            .multiPart(entry.block());

            if (entry.mode() == CrossBlocks.Mode.MODELS) {
                for (CrossBlocks.Model model : entry.models()) {
                    multi = addModelCases(multi, blocks, entry, model, cache);
                }
            } else {
                for (CrossBlocks.Part part : entry.parts()) {
                    multi = addPartCases(multi, blocks, entry, part, cache);
                }
            }

            blocks.blockStateOutput
                    .accept(multi);

            if (!entry.noItem()) {
                generateItem(blocks, items, entry);
            }
            //?}
        }
    }

    //? if <1.21.4 {
    /*private static void generateModels(
            BlockStateProvider blocks,
            CrossBlocks.Entry entry,
            ModelCache<ModelFile> cache
    ) {
        blocks.getVariantBuilder(entry.block())
                .forAllStates(
                        state -> {
                            CrossBlocks.Model selected = selectModel(entry, state);

                            int[] rotation = rotationForState(entry, state);

                            ModelFile model = resolve(
                                            blocks,
                                            selected.source(),
                                            effectiveTint(entry.tint(), selected.tint()), 
                                            cache
                                    );

                            return configuredVariants(
                                    model,
                                    combine(rotation[0], selected.rotationX()),
                                    combine(rotation[1], selected.rotationY()),
                                    selected.variants()
                            );
                        }
                );
    }

    private static CrossBlocks.Model selectModel(CrossBlocks.Entry entry, BlockState state) {
        CrossBlocks.Model selected = null;

        for (CrossBlocks.Model model : entry.models()) {
            if (model.conditions() .matches(state)) {
                if (selected != null) {
                    throw new IllegalStateException("More than one model matches block state " + state);
                }

                selected = model;
            }
        }

        if (selected == null) {
            throw new IllegalStateException("No model matches block state " + state);
        }

        return selected;
    }

    private static void generateMultipart(
            BlockStateProvider blocks,
            CrossBlocks.Entry entry,
            ModelCache<ModelFile> cache
    ) {
        MultiPartBlockStateBuilder multipart = blocks.getMultipartBuilder(entry.block());

        for (CrossBlocks.Part part : entry.parts()) {
            ModelFile model = resolve(blocks, part.source(), effectiveTint(entry.tint(), part.tint()), cache);

            for (Rotations.Case rotation : entry.rotations().cases()) {
                var modelBuilder = multipart.part();

                List<Variants.Option> options = part.variants().options();

                for (int i = 0; i < options.size(); i++) {
                    Variants.Option option = options.get(i);

                    modelBuilder = modelBuilder
                            .modelFile(model)
                            .rotationX(combine(combine(rotation.x(), part.rotationX()), option.rotationX()))
                            .rotationY(combine(combine(rotation.y(), part.rotationY()), option.rotationY()))
                            .weight(option.weight());

                    if (i < options.size() - 1) {
                        modelBuilder = modelBuilder.nextModel();
                    }
                }

                MultiPartBlockStateBuilder.PartBuilder builder = modelBuilder.addModel();

                if (rotation.value() != null) {
                    legacyCondition(builder, entry.rotations().property(), rotation.value());
                }

                for (Conditions.Term<?> term : part.conditions().terms()) {
                    legacyCondition(builder, term.property(), term.value());
                }

                builder.end();
            }
        }
    }

    private static ConfiguredModel[] configuredVariants(
            ModelFile model,
            int baseX,
            int baseY,
            Variants.Set variants
    ) {
        List<ConfiguredModel> configured = new ArrayList<>();

        for (Variants.Option option : variants.options()) {
            configured.add(
                    ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationX(combine(baseX, option.rotationX()))
                            .rotationY(combine(baseY, option.rotationY()))
                            .weight(option.weight())
                            .buildLast()
            );
        }

        return configured.toArray(ConfiguredModel[]::new);
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static void legacyCondition(
            MultiPartBlockStateBuilder.PartBuilder builder,
            Property property,
            Comparable value
    ) {
        builder.condition(property, value);
    }

    private static void generateLegacyItem(
            BlockStateProvider blocks,
            ItemModelProvider items,
            CrossBlocks.Entry entry
    ) {
        if (entry.itemModel() != null) {
            ModelFile model = blocks.models().getExistingFile(
                    ResourceLocation.parse(entry.itemModel())
            );
            blocks.simpleBlockItem(entry.block(), model);
            return;
        }

        Models.Source source = defaultItemSource(entry);

        if (source instanceof CrossModels.Generated generated) {
            items.singleTexture(
                    ModelUtil.pathOf(entry.block()),
                    ResourceLocation.withDefaultNamespace("item/generated"),
                    "layer0",
                    generated.textures().texture()
            );
            return;
        }

        if (source instanceof Models.Existing existing) {
            blocks.simpleBlockItem(
                    entry.block(),
                    blocks.models().getExistingFile(existing.model())
            );
            return;
        }

        throw unsupportedSource(source);
    }

    private static ModelFile resolve(
            BlockStateProvider blocks,
            Models.Source source,
            Tints.Tint tint,
            ModelCache<ModelFile> cache
    ) {
        ModelFile cached = cache.get(source, tint);

        if (cached != null) {
            return cached;
        }

        ModelFile result;

        if (source instanceof Models.Existing existing) {
            result = blocks.models().getExistingFile(existing.model());
        } else if (source instanceof CrossModels.Generated generated) {
            requireGeneratedCrossTint(tint);

            String parent = tint.tinted()
                    ? "block/tinted_cross"
                    : "block/cross";

            result = blocks.models()
                    .getBuilder(blockModelPath(generated.model()))
                    .parent(
                            new ModelFile.UncheckedModelFile(
                                    ResourceLocation.withDefaultNamespace(parent)
                            )
                    )
                    .texture("cross", generated.textures().texture());
        } else {
            throw unsupportedSource(source);
        }

        cache.put(source, tint, result);
        return result;
    }

    private static int[] rotationForState(CrossBlocks.Entry entry, BlockState state) {
        if (entry.rotations() .type() == Rotations.Type.BRICKS) {
            return new int[]{
                    0,
                    0
            };
        }

        Comparable<?> value = stateValue(state, entry.rotations() .property());

        for (Rotations.Case rotation : entry.rotations() .cases()) {
            if (rotation.value() .equals(value)) {
                return new int[]{
                        rotation.x(),
                        rotation.y()
                };
            }
        }

        throw new IllegalStateException("No rotation mapping for " + value + " on " + state);
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static Comparable<?> stateValue(BlockState state, Property property) {
        return (Comparable<?>)
                state.getValue(property);
    }

    *///?} else {
    private static MultiPartGenerator addModelCases(
            MultiPartGenerator multi,
            BlockModelGenerators blocks,
            CrossBlocks.Entry entry,
            CrossBlocks.Model model,
            ModelCache<ResourceLocation> cache
    ) {
        ResourceLocation modelId = resolve(blocks, model.source(), effectiveTint(entry.tint(), model.tint()), cache);

        for (Rotations.Case rotation : entry.rotations().cases()) {
            multi = addCase(
                    multi,
                    entry,
                    model.conditions(),
                    rotation,
                    modelId,
                    model.rotationX(),
                    model.rotationY(),
                    model.variants()
            );
        }

        return multi;
    }

    private static MultiPartGenerator addPartCases(
            MultiPartGenerator multi,
            BlockModelGenerators blocks,
            CrossBlocks.Entry entry,
            CrossBlocks.Part part,
            ModelCache<ResourceLocation> cache
    ) {
        ResourceLocation modelId = resolve(blocks, part.source(), effectiveTint(entry.tint(), part.tint()), cache);

        for (Rotations.Case rotation : entry.rotations().cases()) {
            multi = addCase(
                    multi,
                    entry,
                    part.conditions(),
                    rotation,
                    modelId,
                    part.rotationX(),
                    part.rotationY(),
                    part.variants()
            );
        }

        return multi;
    }

    private static MultiPartGenerator addCase(
            MultiPartGenerator multi,
            CrossBlocks.Entry entry,
            Conditions.Match conditions,
            Rotations.Case rotation,
            ResourceLocation model,
            int localX,
            int localY,
            Variants.Set variants
    ) {
        int x = combine(rotation.x(), localX);

        int y = combine(rotation.y(), localY);

        boolean conditional = rotation.value()
                        != null
                        || !conditions.isAlways();

        if (!conditional) {
            //? if <1.21.5 {
            /*return multi.with(
                    variants(model, x, y, variants)
            );
            *///?} else {
            return multi.with(variants(model, x, y, variants));
            //?}
        }

        //? if <1.21.5 {
        /*Condition.TerminalCondition condition =
                Condition.condition();

        if (rotation.value() != null) {
            modernCondition(condition, entry.rotations() .property(), rotation.value());
        }

        for (Conditions.Term<?> term : conditions.terms()) {
            modernCondition(condition, term.property(), term.value());
        }

        return multi.with(condition, variants(model, x, y, variants));
        *///?} else {
        var condition = BlockModelGenerators
                        .condition();

        if (rotation.value() != null) {
            modernCondition(condition, entry.rotations() .property(), rotation.value());
        }

        for (Conditions.Term<?> term : conditions.terms()) {
            modernCondition(condition, term.property(), term.value());
        }

        return multi.with(condition, variants(model, x, y, variants));
        //?}
    }

    //? if <1.21.5 {
    /*@SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static void modernCondition(Condition.TerminalCondition condition, Property property, Comparable value) {
        condition.term(property, value);
    }

    *///?} else {
    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static void modernCondition(
            net.minecraft.client.data.models.blockstates.ConditionBuilder condition,
            Property property,
            Comparable value
    ) {
        condition.term(property, value);
    }
    //?}

    private static ResourceLocation resolve(
            BlockModelGenerators blocks,
            Models.Source source,
            Tints.Tint tint,
            ModelCache<ResourceLocation> cache
    ) {
        ResourceLocation cached = cache.get(source, tint);

        if (cached != null) {
            return cached;
        }

        ResourceLocation result;

        if (source instanceof Models.Existing existing) {
            result = existing.model();
        } else if (source instanceof CrossModels.Generated generated) {
            requireGeneratedCrossTint(tint);
            cache.claimGeneratedModel(generated.model(), tint);

            TextureMapping mapping = TextureMapping.cross(
                    generated.textures().texture()
            );

            if (tint.tinted()) {
                ModelTemplates.TINTED_CROSS.create(
                        generated.model(),
                        mapping,
                        blocks.modelOutput
                );
            } else {
                ModelTemplates.CROSS.create(
                        generated.model(),
                        mapping,
                        blocks.modelOutput
                );
            }

            result = generated.model();
        } else {
            throw unsupportedSource(source);
        }

        cache.put(source, tint, result);
        return result;
    }

    private static void generateItem(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            CrossBlocks.Entry entry
    ) {
        ResourceLocation model;

        if (entry.itemModel() != null) {
            model = ResourceLocation.parse(entry.itemModel());
        } else {
            Models.Source source = defaultItemSource(entry);

            if (source instanceof CrossModels.Generated generated) {
                model = ModelLocationUtils.getModelLocation(entry.block().asItem());

                ModelTemplates.FLAT_ITEM.create(
                        model,
                        TextureMapping.layer0(generated.textures().texture()),
                        blocks.modelOutput
                );
            } else if (source instanceof Models.Existing existing) {
                model = existing.model();
            } else {
                throw unsupportedSource(source);
            }
        }

        Tints.Tint tint = entry.itemTint();

        if (!tint.tinted()) {
            blocks.registerSimpleItemModel(entry.block(), model);
            return;
        }

        ItemTintSource source = switch (tint.kind()) {
            case NONE ->
                    throw new IllegalStateException("NONE reached tinted item generation");

            case CONSTANT ->
                    new Constant(TintColorUtil.rgb(((Tints.Constant) tint).rgb()));

            case BIOME_FOLIAGE ->
                    new Constant(TintColorUtil.defaultFoliageItemTint());

            case HEX_COLOR,
                 PEARL_FIRE ->
                    HexColorItemTintSource.INSTANCE;
        };

        items.itemModelOutput.accept(
                entry.block().asItem(),
                new BlockModelWrapper.Unbaked(model, List.of(source))
        );
    }

    //? if <1.21.5 {
    /*private static List<Variant> variants(
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
    //?}
    //?}

    private static Models.Source defaultItemSource(CrossBlocks.Entry entry) {
        if (entry.mode() == CrossBlocks.Mode.MODELS) {
            return entry.models().get(0).source();
        }

        return defaultMultipartItemPart(entry).source();
    }

    private static void requireGeneratedCrossTint(Tints.Tint tint) {
        if (tint.tinted() && tint.tintIndex() != 0) {
            throw new IllegalStateException(
                    "Generated cross models use Minecraft's tinted_cross template and require tint index 0. "
                            + "Use an existing model when a custom tint index is required."
            );
        }
    }

    private static IllegalStateException unsupportedSource(Models.Source source) {
        return new IllegalStateException(
                "CrossBlockGenerator cannot resolve model source "
                        + source
                        + ". Use Models.existing(...) or CrossModels.generated(...)."
        );
    }

    private static CrossBlocks.Part defaultMultipartItemPart(CrossBlocks.Entry entry) {
        for (CrossBlocks.Part part : entry.parts()) {
            if (part.conditions() .isAlways()) {
                return part;
            }
        }

        throw new IllegalStateException(
                "Multipart block "
                        + ModelUtil.idOf(entry.block())
                        + " has no unconditional base part for its default item model. "
                        + "Add Part.always(...) or explicitly use .item(...)."
        );
    }

    private static Tints.Tint effectiveTint(Tints.Tint inherited, Tints.Tint local) {
        return local != null
                ? local
                : inherited;
    }

    private static int combine(int first, int second) {
        return CrossBlocks.normalize(first + second);
    }

    private static String blockModelPath(ResourceLocation model) {
        String path = model.getPath();

        return path.startsWith("block/")
                ? path.substring("block/".length())
                : path;
    }

    private static final class ModelCache<T> {
        private final Map<
                Models.Source,
                Map<TintKey, T>
                > cache = new IdentityHashMap<>();

        private final Map<
                ResourceLocation,
                TintKey
                > generatedModels = new HashMap<>();

        T get(Models.Source source, Tints.Tint tint) {
            Map<TintKey, T> byTint = cache.get(source);

            if (byTint == null) {
                return null;
            }

            return byTint.get(TintKey.of(tint));
        }

        void put(Models.Source source, Tints.Tint tint, T value) {
            cache.computeIfAbsent(
                            source,
                            ignored ->
                                    new HashMap<>()
                    )
                    .put(TintKey.of(tint), value);
        }

        void claimGeneratedModel(ResourceLocation model, Tints.Tint tint) {
            TintKey key = TintKey.of(tint);

            TintKey previous = generatedModels.putIfAbsent(model, key);

            if (previous != null && !previous.equals(key)) {
                throw new IllegalStateException(
                        "Generated model "
                                + model
                                + " was requested with two different tint configurations. "
                                + "Give the generated models different suffixes."
                );
            }
        }
    }

    private record TintKey(Tints.Kind kind, int tintIndex, int color) {
        static TintKey of(Tints.Tint tint) {
            int color = tint instanceof Tints.Constant constant
                            ? constant.rgb()
                            : 0;

            return new TintKey(tint.kind(), tint.tintIndex(), color);
        }
    }
}
