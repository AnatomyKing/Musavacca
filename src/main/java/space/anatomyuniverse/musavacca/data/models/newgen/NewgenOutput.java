package space.anatomyuniverse.musavacca.data.models.newgen;

import space.anatomyuniverse.musavacca.tint.ArmorTrimItemTintSource;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
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

final class NewgenOutput {
    //? if <1.21.4 {
    /*private final BlockStateProvider blocks;
    private final ItemModelProvider items;

    NewgenOutput(BlockStateProvider blocks, ItemModelProvider items) {
        this.blocks = blocks;
        this.items = items;
    }
    *///?} else {
    private final BlockModelGenerators blocks;
    private final ItemModelGenerators items;

    NewgenOutput(BlockModelGenerators blocks, ItemModelGenerators items) {
        this.blocks = blocks;
        this.items = items;
    }
    //?}

    private final Map<ResourceLocation, JsonObject> models = new HashMap<>();
    private final Set<Block> generatedBlocks = new HashSet<>();
    private final Map<Item, ItemRequest> generatedItems = new HashMap<>();

    private record ItemRequest(List<NewgenModels.ItemPart> parts, String kind) {
        ItemRequest {
            parts = List.copyOf(parts);
        }
    }

    ResourceLocation model(ResourceLocation id, JsonObject json) {
        JsonObject previous = models.putIfAbsent(id, json);
        if (previous != null) {
            if (!previous.equals(json)) {
                throw new IllegalStateException("Different generated models share " + id
                        + ". Give their sources different suffixes.");
            }
            return id;
        }

        //? if <1.21.4 {
        /*if (id.getPath().startsWith("item/")) {
            if (items.generatedModels.containsKey(id)) throw duplicate(id);
            items.getBuilder(id.toString());
            items.generatedModels.put(id, new ItemModelBuilder(id, items.existingFileHelper) {
                @Override public JsonObject toJson() { return json; }
            });
        } else {
            if (blocks.models().generatedModels.containsKey(id)) throw duplicate(id);
            blocks.models().getBuilder(id.toString());
            blocks.models().generatedModels.put(id, new BlockModelBuilder(id, blocks.models().existingFileHelper) {
                @Override public JsonObject toJson() { return json; }
            });
        }
        *///?} else {
        blocks.modelOutput.accept(id, () -> json);
        //?}
        return id;
    }

    void item(Item item, ResourceLocation model, List<Tints.Tint> tints) {
        item(item, new NewgenModels.ItemRender(
                List.of(new NewgenModels.ItemPart(model, tints))
        ));
    }

    void item(Item item, NewgenModels.ItemRender render) {
        if (item == Items.AIR) {
            throw new IllegalStateException("Cannot generate an item model for air. Use .noItem() for itemless blocks.");
        }

        ItemRequest request = new ItemRequest(render.parts(), "simple");
        ItemRequest previous = generatedItems.putIfAbsent(item, request);
        if (previous != null) {
            if (!previous.equals(request)) throw new IllegalStateException("Conflicting item models for " + item);
            return;
        }

        //? if <1.21.4 {
        /*if (render.parts().size() != 1) {
            throw new IllegalStateException("Legacy item output must be represented by one model JSON");
        }

        NewgenModels.ItemPart part = render.parts().get(0);
        ResourceLocation id = ModelLocations.itemModel(item);
        if (!id.equals(part.model())) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", part.model().toString());
            model(id, json);
        }
        *///?} else {
        List<ItemModel.Unbaked> children = render.parts().stream()
                .map(part -> (ItemModel.Unbaked) new BlockModelWrapper.Unbaked(
                        part.model(),
                        itemTints(part.tints())
                ))
                .toList();

        ItemModel.Unbaked model = children.size() == 1
                ? children.get(0)
                : new CompositeModel.Unbaked(children);

        items.itemModelOutput.accept(item, model);
        //?}
    }

    void armorItem(
            Item item,
            ResourceLocation baseModel,
            ResourceLocation trimmedModel,
            ResourceLocation headModel
    ) {
        if (item == Items.AIR) throw new IllegalStateException("Cannot generate armor model for air");

        ItemRequest request = new ItemRequest(
                List.of(new NewgenModels.ItemPart(baseModel, List.of())),
                "armor:" + trimmedModel + ":" + headModel
        );
        ItemRequest previous = generatedItems.putIfAbsent(item, request);
        if (previous != null) {
            if (!previous.equals(request)) throw new IllegalStateException("Conflicting item models for " + item);
            return;
        }

        //? if <1.21.4 {
        /*ResourceLocation natural = ModelLocations.itemModel(item);
        if (!natural.equals(baseModel) && trimmedModel == null) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", baseModel.toString());
            model(natural, json);
        }
        *///?} else {
        ItemModel.Unbaked base = ItemModelUtils.plainModel(baseModel);
        ItemModel.Unbaked inventory = base;

        if (trimmedModel != null) {
            ItemModel.Unbaked trimmed = new BlockModelWrapper.Unbaked(
                    trimmedModel,
                    List.of(
                            new Constant(0xFFFFFFFF),
                            ArmorTrimItemTintSource.itemTintSource()
                    )
            );

            inventory = ItemModelUtils.conditional(
                    new HasComponent(DataComponents.TRIM, false),
                    trimmed,
                    base
            );
        }

        if (headModel != null) {
            inventory = ItemModelUtils.select(
                    new DisplayContext(),
                    inventory,
                    List.of(
                            new SelectItemModel.SwitchCase<>(
                                    List.of(ItemDisplayContext.HEAD),
                                    ItemModelUtils.plainModel(headModel)
                            )
                    )
            );
        }

        items.itemModelOutput.accept(item, inventory);
        //?}
    }

    //? if >=1.21.4 {
    private static List<ItemTintSource> itemTints(List<Tints.Tint> tints) {
        if (tints.stream().allMatch(t -> !t.tinted())) return List.of();
        return tints.stream().map(NewgenOutput::itemTint).toList();
    }
    //?}

    //? if >=1.21.4 {
    private static ItemTintSource itemTint(Tints.Tint tint) {
        return tint.itemTintSource();
    }
    //?}

    State state(Block block) {
        if (!generatedBlocks.add(block)) throw new IllegalStateException("Block is declared twice in newgen: " + block);
        return new State(block);
    }

    record Application(List<ResourceLocation> models, int x, int y, Variants.Set variants, boolean uvLock) {}

    final class State {
        private final Block block;
        private final Map<Application, List<Conditions.Match>> parts = new LinkedHashMap<>();

        private State(Block block) { this.block = block; }

        void add(ResourceLocation model, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
            add(List.of(model), when, x, y, variants, uvLock);
        }

        void add(List<ResourceLocation> models, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
            if (models.isEmpty()) throw new IllegalArgumentException("A state needs at least one model");
            Application apply = new Application(List.copyOf(models), ModelTransforms.quarterTurn(x),
                    ModelTransforms.quarterTurn(y), variants, uvLock);
            List<Conditions.Match> matches = parts.computeIfAbsent(apply, ignored -> new ArrayList<>());
            if (matches.stream().anyMatch(Conditions.Match::isAlways)) return;
            if (when.isAlways()) matches.clear();
            if (!matches.contains(when)) matches.add(when);
        }

        void finish() {
            if (parts.isEmpty()) throw new IllegalStateException("No model parts generated for " + block);
            //? if <1.21.4 {
            /*MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(block);
            for (var entry : parts.entrySet()) {
                Application apply = entry.getKey();
                var builder = multi.part();
                int remaining = apply.models().size() * apply.variants().options().size();
                for (ResourceLocation model : apply.models()) {
                    for (Variants.Option option : apply.variants().options()) {
                        builder = builder.modelFile(new ModelFile.UncheckedModelFile(model))
                                .rotationX(ModelTransforms.combine(apply.x(), option.rotationX()))
                                .rotationY(ModelTransforms.combine(apply.y(), option.rotationY()))
                                .weight(option.weight()).uvLock(apply.uvLock());
                        if (--remaining > 0) builder = builder.nextModel();
                    }
                }
                var part = builder.addModel();
                List<Conditions.Match> matches = entry.getValue();
                if (matches.size() == 1) {
                    for (Conditions.Term<?> term : matches.get(0).terms()) term(part, term);
                } else {
                    part.useOr();
                    for (Conditions.Match match : matches) {
                        var group = part.nestedGroup();
                        for (Conditions.Term<?> term : match.terms()) term(group, term);
                        group.end();
                    }
                }
                part.end();
            }
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);
            for (var entry : parts.entrySet()) {
                var variants = variants(entry.getKey());
                List<Conditions.Match> matches = entry.getValue();
                if (matches.get(0).isAlways()) {
                    multi = multi.with(variants);
                } else {
                    //? if <1.21.5 {
                    /*Condition[] conditions = matches.stream().map(NewgenOutput::condition).toArray(Condition[]::new);
                    multi = multi.with(conditions.length == 1 ? conditions[0] : Condition.or(conditions), variants);
                    *///?} else {
                    ConditionBuilder[] conditions = matches.stream().map(NewgenOutput::condition).toArray(ConditionBuilder[]::new);
                    if (conditions.length == 1) {
                        multi = multi.with(conditions[0], variants);
                    } else {
                        multi = multi.with(BlockModelGenerators.or(conditions), variants);
                    }
                    //?}
                }
            }
            blocks.blockStateOutput.accept(multi);
            //?}
        }
    }

    //? if <1.21.4 {
    /*private static IllegalStateException duplicate(ResourceLocation id) {
        return new IllegalStateException("Another generator already owns model " + id);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void term(MultiPartBlockStateBuilder.PartBuilder part, Conditions.Term<?> term) {
        part.condition((Property) term.property(), (Comparable) term.value());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void term(MultiPartBlockStateBuilder.PartBuilder.ConditionGroup group, Conditions.Term<?> term) {
        group.condition((Property) term.property(), (Comparable) term.value());
    }
    *///?} else {
    //? if <1.21.5 {
    /*private static final VariantProperties.Rotation[] ROTATIONS = VariantProperties.Rotation.values();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Condition condition(Conditions.Match match) {
        var condition = Condition.condition();
        for (Conditions.Term<?> term : match.terms()) condition.term((Property) term.property(), (Comparable) term.value());
        return condition;
    }

    private static List<Variant> variants(Application apply) {
        List<Variant> result = new ArrayList<>();
        for (ResourceLocation model : apply.models()) {
            for (Variants.Option option : apply.variants().options()) {
                Variant variant = Variant.variant().with(VariantProperties.MODEL, model)
                        .with(VariantProperties.X_ROT, ROTATIONS[ModelTransforms.combine(apply.x(), option.rotationX()) / 90])
                        .with(VariantProperties.Y_ROT, ROTATIONS[ModelTransforms.combine(apply.y(), option.rotationY()) / 90])
                        .with(VariantProperties.WEIGHT, option.weight());
                if (apply.uvLock()) variant = variant.with(VariantProperties.UV_LOCK, true);
                result.add(variant);
            }
        }
        return result;
    }
    *///?} else {
    private static final Quadrant[] QUADRANTS = Quadrant.values();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ConditionBuilder condition(Conditions.Match match) {
        var condition = BlockModelGenerators.condition();
        for (Conditions.Term<?> term : match.terms()) condition.term((Property) term.property(), (Comparable) term.value());
        return condition;
    }

    private static MultiVariant variants(Application apply) {
        WeightedList.Builder<Variant> result = WeightedList.builder();
        for (ResourceLocation model : apply.models()) {
            for (Variants.Option option : apply.variants().options()) {
                Variant variant = new Variant(model)
                        .withXRot(QUADRANTS[ModelTransforms.combine(apply.x(), option.rotationX()) / 90])
                        .withYRot(QUADRANTS[ModelTransforms.combine(apply.y(), option.rotationY()) / 90])
                        .withUvLock(apply.uvLock());
                result.add(variant, option.weight());
            }
        }
        return new MultiVariant(result.build());
    }
    //?}
    //?}
}

