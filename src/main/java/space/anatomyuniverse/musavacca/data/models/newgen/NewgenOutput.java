package space.anatomyuniverse.musavacca.data.models.newgen;

import space.anatomyuniverse.musavacca.tint.ArmorTrimItemTintSource;
import com.google.gson.JsonObject;
//? if >=1.21.4 {
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.item.ItemModels;
//?}
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
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
//? if >=1.21.4 {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
//?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
*///?} else if >=1.21.4 <26.1 {
import net.minecraft.client.renderer.item.BlockModelWrapper;
//?}
//? if >=1.21.4 {
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
//?}
//? if >=1.21.5 {
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
//?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.Variant;
*///?} else if >=1.21.5 <26.1 {
import net.minecraft.client.renderer.block.model.Variant;
//?} else if >=1.21.4 <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Condition;
*///?}
//? if >=1.21.5 {
import net.minecraft.util.random.WeightedList;
//?} else if >=1.21.4 <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
/*import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?}

final class NewgenOutput {
    //? if >=1.21.4 {
    private final BlockModelGenerators blocks;
    private final ItemModelGenerators items;
    //?} else {
    /*private final BlockStateProvider blocks;
    private final ItemModelProvider items;
    *///?}
    //? if >=1.21.4 {
    NewgenOutput(BlockModelGenerators blocks, ItemModelGenerators items) {
    //?} else {
    /*NewgenOutput(BlockStateProvider blocks, ItemModelProvider items) {
    *///?}
        this.blocks = blocks;
        this.items = items;
    }

    //? if >=1.21.11 {
    /*private final Map<Identifier, JsonObject> models = new HashMap<>();
    *///?} else {
    private final Map<ResourceLocation, JsonObject> models = new HashMap<>();
    //?}
    //? if >=1.21.11 {
    /*private final Map<Identifier, JsonObject> namedDefinitions = new LinkedHashMap<>();
    *///?} else {
    private final Map<ResourceLocation, JsonObject> namedDefinitions = new LinkedHashMap<>();
    //?}
    private final Set<Block> generatedBlocks = new HashSet<>();
    private final Map<Item, ItemRequest> generatedItems = new HashMap<>();
    private record ItemRequest(List<NewgenModels.ItemPart> parts, String kind, boolean blockPerspective) {
        ItemRequest {
            parts = List.copyOf(parts);
        }
    }
    //? if >=1.21.11 {
    /*Identifier model(Identifier id, JsonObject json) {
    *///?} else {
    ResourceLocation model(ResourceLocation id, JsonObject json) {
    //?}
        JsonObject previous = models.putIfAbsent(id, json);
        if (previous != null) {
            if (!previous.equals(json)) throw new IllegalStateException("Different generated models share " + id + ". Give their sources different suffixes.");
            return id;
        }
        //? if >=1.21.4 {
        blocks.modelOutput.accept(id, () -> json);
        return id;
        //?} else {
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
        return id;
        *///?}
    }
    //? if >=1.21.11 {
    /*void item(Item item, Identifier model, List<Tints.Tint> tints) {
    *///?} else {
    void item(Item item, ResourceLocation model, List<Tints.Tint> tints) {
    //?}
        item(item, new NewgenModels.ItemRender(List.of(new NewgenModels.ItemPart(model, tints))));
    }
    void item(Item item, NewgenModels.ItemRender render) {
        if (item == Items.AIR) throw new IllegalStateException("Cannot generate an item model for air. Use .noItem() for itemless blocks.");
        ItemRequest request = new ItemRequest(render.parts(), "simple", render.blockPerspective());
        ItemRequest previous = generatedItems.putIfAbsent(item, request);
        if (previous != null) {
            if (!previous.equals(request)) throw new IllegalStateException("Conflicting item models for " + item);
            return;
        }
        //? if >=1.21.4 {
        items.itemModelOutput.accept(item, itemModel(render));
        //?} else {
        /*if (render.parts().size() > 1) {
            List<List<Tints.Tint>> tinted = render.parts().stream().map(NewgenModels.ItemPart::tints)
                    .filter(tints -> tints.stream().anyMatch(Tints.Tint::tinted)).distinct().toList();
            if (tinted.size() > 1) throw new IllegalStateException("Legacy composite items require compatible tint indices: " + item);
            JsonObject json = new JsonObject();
            json.addProperty("loader", "neoforge:composite");
            if (render.blockPerspective()) applyLegacyBlockItemDisplay(json);
            JsonObject children = new JsonObject();
            for (int i = 0; i < render.parts().size(); i++) {
                JsonObject child = new JsonObject();
                child.addProperty("parent", render.parts().get(i).model().toString());
                children.add("part_" + i, child);
            }
            json.add("children", children);
            model(ModelLocations.itemModel(item), json);
            return;
        }
        *///?}
        //? if <1.21.4 {
        /*NewgenModels.ItemPart part = render.parts().get(0);
        ResourceLocation id = ModelLocations.itemModel(item);
        if (!id.equals(part.model())) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", part.model().toString());
            if (render.blockPerspective()) applyLegacyBlockItemDisplay(json);
            model(id, json);
        }
        *///?}
    }
    //? if >=1.21.11 {
    /*Map<Identifier, JsonObject> namedDefinitions() { return Map.copyOf(namedDefinitions); }

    void namedItem(Identifier id, NewgenModels.ItemRender render) {
    *///?} else {
    Map<ResourceLocation, JsonObject> namedDefinitions() { return Map.copyOf(namedDefinitions); }

    void namedItem(ResourceLocation id, NewgenModels.ItemRender render) {
    //?}
        if (!id.getNamespace().equals("musavacca")) throw new IllegalArgumentException("Named item model must use the musavacca namespace: " + id);
        //? if >=1.21.4 {
        JsonObject definition = new JsonObject();
        definition.add("model", ItemModels.CODEC.encodeStart(JsonOps.INSTANCE, itemModel(render)).getOrThrow());
        if (namedDefinitions.putIfAbsent(id, definition) != null) throw new IllegalStateException("Duplicate named item: " + id);
        //?}
    }

    //? if >=1.21.4 {
    private static ItemModel.Unbaked itemModel(NewgenModels.ItemRender render) {
        List<ItemModel.Unbaked> children = render.parts().stream()
                //? if >=26.1 {
                /*.map(part -> (ItemModel.Unbaked) new CuboidItemModelWrapper.Unbaked(part.model(), java.util.Optional.empty(), itemTints(part.tints())))
                *///?} else {
                .map(part -> (ItemModel.Unbaked) new BlockModelWrapper.Unbaked(part.model(), itemTints(part.tints())))
                //?}
                .toList();
        //? if >=26.1 {
        /*return children.size() == 1 ? children.get(0) : new CompositeModel.Unbaked(children, java.util.Optional.empty());
        *///?} else {
        return children.size() == 1 ? children.get(0) : new CompositeModel.Unbaked(children);
        //?}
    }
    //?}
    //? if <1.21.4 {
    /*private static com.google.gson.JsonArray vec(float x, float y, float z) {
        com.google.gson.JsonArray array = new com.google.gson.JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private static JsonObject display(float rx, float ry, float rz, float tx, float ty, float tz, float scale) {
        JsonObject json = new JsonObject();
        json.add("rotation", vec(rx, ry, rz));
        json.add("translation", vec(tx, ty, tz));
        json.add("scale", vec(scale, scale, scale));
        return json;
    }

    private static void applyLegacyBlockItemDisplay(JsonObject json) {
        json.addProperty("gui_light", "side");
        JsonObject transforms = new JsonObject();
        transforms.add("gui", display(30, 225, 0, 0, 0, 0, 0.625F));
        transforms.add("ground", display(0, 0, 0, 0, 3, 0, 0.25F));
        transforms.add("fixed", display(0, 0, 0, 0, 0, 0, 0.5F));
        transforms.add("thirdperson_righthand", display(75, 45, 0, 0, 2.5F, 0, 0.375F));
        transforms.add("thirdperson_lefthand", display(75, 45, 0, 0, 2.5F, 0, 0.375F));
        transforms.add("firstperson_righthand", display(0, 45, 0, 0, 0, 0, 0.4F));
        transforms.add("firstperson_lefthand", display(0, 225, 0, 0, 0, 0, 0.4F));
        json.add("display", transforms);
    }
    *///?}

    //? if >=1.21.11 {
    /*void armorItem(Item item, Identifier baseModel, Identifier trimmedModel, Identifier headModel) {
    *///?} else {
    void armorItem(Item item, ResourceLocation baseModel, ResourceLocation trimmedModel, ResourceLocation headModel) {
    //?}
        if (item == Items.AIR) throw new IllegalStateException("Cannot generate armor model for air");
        ItemRequest request = new ItemRequest(List.of(new NewgenModels.ItemPart(baseModel, List.of())), "armor:" + trimmedModel + ":" + headModel, false);
        ItemRequest previous = generatedItems.putIfAbsent(item, request);
        if (previous != null) {
            if (!previous.equals(request)) throw new IllegalStateException("Conflicting item models for " + item);
            return;
        }
        //? if >=1.21.4 {
        ItemModel.Unbaked base = ItemModelUtils.plainModel(baseModel);
        ItemModel.Unbaked inventory = base;
        if (trimmedModel != null) {
            //? if >=26.1 {
            /*ItemModel.Unbaked trimmed = new CuboidItemModelWrapper.Unbaked(trimmedModel, java.util.Optional.empty(),
                    List.of(new Constant(0xFFFFFFFF), ArmorTrimItemTintSource.itemTintSource()));
            *///?} else {
            ItemModel.Unbaked trimmed = new BlockModelWrapper.Unbaked(trimmedModel, List.of(new Constant(0xFFFFFFFF), ArmorTrimItemTintSource.itemTintSource()));
            //?}
        //?}
        //? if >=1.21.4 {
            inventory = ItemModelUtils.conditional(new HasComponent(DataComponents.TRIM, false), trimmed, base);
        //?} else {
        /*ResourceLocation natural = ModelLocations.itemModel(item);
        if (!natural.equals(baseModel) && trimmedModel == null) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", baseModel.toString());
            model(natural, json);
        *///?}
        }
        //? if >=1.21.4 {
        if (headModel != null) {
            inventory = ItemModelUtils.select(new DisplayContext(), inventory,
                    List.of(new SelectItemModel.SwitchCase<>(List.of(ItemDisplayContext.HEAD), ItemModelUtils.plainModel(headModel))));
        }
        items.itemModelOutput.accept(item, inventory);
        //?}
    }

    //? if >=1.21.4 {
    private static List<ItemTintSource> itemTints(List<Tints.Tint> tints) {
        if (tints.stream().allMatch(t -> !t.tinted())) return List.of();
        return tints.stream().map(NewgenOutput::itemTint).toList();
    }

    private static ItemTintSource itemTint(Tints.Tint tint) {
        return tint.itemTintSource();
    }
    //?}
    State state(Block block) {
        if (!generatedBlocks.add(block)) throw new IllegalStateException("Block is declared twice in newgen: " + block);
        return new State(block);
    }
    //? if >=1.21.11 {
    /*record Application(List<Identifier> models, int x, int y, Variants.Set variants, boolean uvLock) {}
    *///?} else {
    record Application(List<ResourceLocation> models, int x, int y, Variants.Set variants, boolean uvLock) {}
    //?}
    final class State {
        private final Block block;
        private final Map<Application, List<Conditions.Match>> parts = new LinkedHashMap<>();
        private State(Block block) { this.block = block; }
        //? if >=1.21.11 {
        /*void add(Identifier model, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
        *///?} else {
        void add(ResourceLocation model, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
        //?}
            add(List.of(model), when, x, y, variants, uvLock);
        }
        //? if >=1.21.11 {
        /*void add(List<Identifier> models, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
        *///?} else {
        void add(List<ResourceLocation> models, Conditions.Match when, int x, int y, Variants.Set variants, boolean uvLock) {
        //?}
            if (models.isEmpty()) throw new IllegalArgumentException("A state needs at least one model");
            Application apply = new Application(List.copyOf(models), ModelTransforms.quarterTurn(x), ModelTransforms.quarterTurn(y), variants, uvLock);
            List<Conditions.Match> matches = parts.computeIfAbsent(apply, ignored -> new ArrayList<>());
            if (matches.stream().anyMatch(Conditions.Match::isAlways)) return;
            if (when.isAlways()) matches.clear();
            if (!matches.contains(when)) matches.add(when);
        }
        void finish() {
            if (parts.isEmpty()) throw new IllegalStateException("No model parts generated for " + block);
            //? if >=1.21.4 {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);
            //?} else {
            /*MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(block);
            *///?}
            for (var entry : parts.entrySet()) {
                //? if >=1.21.4 {
                var variants = variants(entry.getKey());
                //?} else {
                /*Application apply = entry.getKey();
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
                *///?}
                List<Conditions.Match> matches = entry.getValue();
                //? if >=1.21.4 {
                if (matches.get(0).isAlways()) {
                    multi = multi.with(variants);
                //?} else {
                /*if (matches.size() == 1) {
                    for (Conditions.Term<?> term : matches.get(0).terms()) term(part, term);
                *///?}
                } else {
                    //? if <1.21.4 || >=1.21.5 {
                    //? if >=1.21.5 {
                    ConditionBuilder[] conditions = matches.stream().map(NewgenOutput::condition).toArray(ConditionBuilder[]::new);
                    if (conditions.length == 1) {
                        multi = multi.with(conditions[0], variants);
                    } else {
                        multi = multi.with(BlockModelGenerators.or(conditions), variants);
                    //?} else {
                    /*part.useOr();
                    for (Conditions.Match match : matches) {
                        var group = part.nestedGroup();
                        for (Conditions.Term<?> term : match.terms()) term(group, term);
                        group.end();
                    *///?}
                    }
                    //?}
                    //? if >=1.21.4 <1.21.5 {
                    /*Condition[] conditions = matches.stream().map(NewgenOutput::condition).toArray(Condition[]::new);
                    multi = multi.with(conditions.length == 1 ? conditions[0] : Condition.or(conditions), variants);
                    *///?}
                }
                //? if <1.21.4 {
                /*part.end();
                *///?}
            }
            //? if >=1.21.4 {
            blocks.blockStateOutput.accept(multi);
            //?}
        }
    }
    //? if >=1.21.4 {
    //? if >=1.21.5 {
    private static final Quadrant[] QUADRANTS = Quadrant.values();
    //?} else {
    /*private static final VariantProperties.Rotation[] ROTATIONS = VariantProperties.Rotation.values();
    *///?}
    //?}
    //? if <1.21.4 {
    /*private static IllegalStateException duplicate(ResourceLocation id) {
        return new IllegalStateException("Another generator already owns model " + id);
    }
    *///?}
    //? if >=1.21.4 {
    @SuppressWarnings({"rawtypes", "unchecked"})
    //? if >=1.21.5 {
    private static ConditionBuilder condition(Conditions.Match match) {
        var condition = BlockModelGenerators.condition();
    //?} else {
    /*private static Condition condition(Conditions.Match match) {
        var condition = Condition.condition();
    *///?}
        for (Conditions.Term<?> term : match.terms()) condition.term((Property) term.property(), (Comparable) term.value());
        return condition;
    }
    //?}
    //? if <1.21.4 || >=1.21.5 {
    //? if <1.21.4 {
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    private static void term(MultiPartBlockStateBuilder.PartBuilder part, Conditions.Term<?> term) {
        part.condition((Property) term.property(), (Comparable) term.value());
    }
    *///?}
    //? if >=1.21.5 {
    private static MultiVariant variants(Application apply) {
    //?}
    //?}
    //? if >=1.21.4 {
    //? if >=1.21.5 {
        WeightedList.Builder<Variant> result = WeightedList.builder();
    //?} else {
    /*private static List<Variant> variants(Application apply) {
    *///?}
        //? if >=1.21.4 <1.21.5 || >=1.21.11 {
        /*//? if >=1.21.11 {
        /^for (Identifier model : apply.models()) {
        ^///?} else {
        List<Variant> result = new ArrayList<>();
        //?}
        *///?}
        //? if >=1.21.4 <1.21.11 {
        for (ResourceLocation model : apply.models()) {
        //?}
            for (Variants.Option option : apply.variants().options()) {
                //? if >=1.21.5 {
                Variant variant = new Variant(model)
                        .withXRot(QUADRANTS[ModelTransforms.combine(apply.x(), option.rotationX()) / 90])
                        .withYRot(QUADRANTS[ModelTransforms.combine(apply.y(), option.rotationY()) / 90])
                        .withUvLock(apply.uvLock());
                //?} else {
                /*Variant variant = Variant.variant().with(VariantProperties.MODEL, model)
                        .with(VariantProperties.X_ROT, ROTATIONS[ModelTransforms.combine(apply.x(), option.rotationX()) / 90])
                        .with(VariantProperties.Y_ROT, ROTATIONS[ModelTransforms.combine(apply.y(), option.rotationY()) / 90])
                        .with(VariantProperties.WEIGHT, option.weight());
                if (apply.uvLock()) variant = variant.with(VariantProperties.UV_LOCK, true);
                *///?}
                //? if >=1.21.5 {
                result.add(variant, option.weight());
                //?} else {
                /*result.add(variant);
                *///?}
            }
        }
        //? if >=1.21.5 {
        return new MultiVariant(result.build());
        //?} else {
        /*return result;
        *///?}
    }
    //?}
    //? if <1.21.4 {
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    private static void term(MultiPartBlockStateBuilder.PartBuilder.ConditionGroup group, Conditions.Term<?> term) {
        group.condition((Property) term.property(), (Comparable) term.value());
    }
    *///?}
}


