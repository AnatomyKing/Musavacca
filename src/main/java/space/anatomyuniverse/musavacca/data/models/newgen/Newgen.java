package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Item;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import space.anatomyuniverse.musavacca.block.custom.DecorationBlock;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.List;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
//?}

public final class Newgen {
    private static final float[] DECORATION_EXTRA_Y_ROT = {0.0F, -22.5F, -45.0F, 22.5F};

    private final NewgenOutput output;
    private final NewgenModels models;

    Newgen(NewgenOutput output) {
        this.output = output;
        this.models = new NewgenModels(output);
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items
            //?}
    ) {
        Newgen gen = new Newgen(new NewgenOutput(blocks, items));
        NewModelSets.simpleBlocks().forEach(gen::simple);
        NewModelSets.crossBlocks().forEach(gen::cross);
        NewModelSets.tallCrossBlocks().forEach(gen::tallCross);
        NewModelSets.fireBlocks().forEach(gen::fire);
        NewModelSets.portalBlocks().forEach(gen::portal);
        NewModelSets.decorationBlocks().forEach(gen::decoration);
        NewModelSets.doorBlocks().forEach(gen::door);
        NewModelSets.trapdoorBlocks().forEach(gen::trapdoor);
        NewModelSets.stairBlocks().forEach(gen::stair);
        NewModelSets.slabBlocks().forEach(gen::slab);
        NewModelSets.fenceBlocks().forEach(gen::fence);
        NewModelSets.fenceGateBlocks().forEach(gen::fenceGate);
        NewModelSets.pressurePlateBlocks().forEach(gen::pressurePlate);
        NewModelSets.buttonBlocks().forEach(gen::button);
        NewModelSets.wallBlocks().forEach(gen::wall);

        NewModelSets.simpleItems().forEach(gen::simpleItem);
        NewModelSets.spawnEggItems().forEach(gen::spawnEgg);
        NewModelSets.armorItems().forEach(gen::armor);
    }

    void simple(SimpleBlocks.Entry entry) { modelFamily(entry); }
    void cross(CrossBlocks.Entry entry) { modelFamily(entry); }
    void tallCross(TallCrossBlocks.Entry entry) { modelFamily(entry); }
    void portal(PortalBlocks.Entry entry) { modelFamily(entry); }

    void decoration(DecorationBlocks.Entry entry) {
        NewgenOutput.State state = output.state(entry.block());

        for (DecorationBlocks.Model rule : entry.models()) {
            ResourceLocation baseModel = models.resolve(
                    rule.source(),
                    Tints.effective(entry.tint(), rule.tint())
            );

            Conditions.Match placementMatch = rule.conditions()
                    .and(DecorationBlock.PLACEMENT, rule.placement());

            switch (rule.orientation()) {
                case FIXED -> state.add(
                        baseModel,
                        placementMatch,
                        rule.rotationX(),
                        rule.rotationY(),
                        rule.variants(),
                        false
                );

                case FACING -> {
                    for (var facing : ModelDirections.horizontal()) {
                        state.add(
                                baseModel,
                                placementMatch.and(DecorationBlock.FACING, facing),
                                rule.rotationX(),
                                ModelTransforms.combine(
                                        NewgenStates.northY(facing),
                                        rule.rotationY()
                                ),
                                rule.variants(),
                                false
                        );
                    }
                }

                case ROTATION -> {
                    for (int rotation = 0; rotation < 16; rotation++) {
                        DecorationRotation step = decorationRotation(rotation);
                        ResourceLocation model = step.rootY() == 0.0F
                                ? baseModel
                                : models.rootRotateY(entry.block(), baseModel, step.rootY());

                        state.add(
                                model,
                                placementMatch.and(DecorationBlock.ROTATION, rotation),
                                rule.rotationX(),
                                ModelTransforms.combine(step.y(), rule.rotationY()),
                                rule.variants(),
                                false
                        );
                    }
                }
            }
        }

        state.finish();

        switch (entry.itemMode()) {
            case NONE -> {
            }

            case CUSTOM -> emitItem(entry.block().asItem(), entry.itemModel());

            case PLACEMENT -> {
                DecorationBlocks.Model itemRule = entry.itemRule();
                ResourceLocation itemModel = models.resolve(
                        itemRule.source(),
                        Tints.effective(entry.tint(), itemRule.tint())
                );
                emitItem(
                        entry.block().asItem(),
                        SimpleItems.Model.existing(itemModel)
                                .tint(Tints.effective(entry.tint(), itemRule.tint()))
                );
            }

            case UNSET -> throw new IllegalStateException(
                    "Decoration item handling was not configured for " + entry.block()
            );
        }
    }

    private record DecorationRotation(int y, float rootY) {
    }

    private static DecorationRotation decorationRotation(int rotation) {
        int normalized = Math.floorMod(rotation, 16);
        int part = normalized & 3;
        int quadrant = normalized >> 2;

        int y = (part == 3 ? quadrant + 1 : quadrant) * 90;
        float rootY = DECORATION_EXTRA_Y_ROT[part];

        return new DecorationRotation(
                ModelTransforms.quarterTurn(y),
                rootY
        );
    }

    private void modelFamily(BlockFamily.ModelFamilyEntry<?, ?> entry) {
        NewgenOutput.State state = output.state(entry.block());
        List<? extends BlockFamily.ModelRule<?, ?>> rules = entry.mode() == ModelMode.MODELS
                ? entry.models() : entry.parts();

        for (var rule : rules) {
            if (rule.source() instanceof TallCrossModels.Source tall) {
                modelRule(state, entry, rule, tall.lower(),
                        rule.conditions().and(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
                modelRule(state, entry, rule, tall.upper(),
                        rule.conditions().and(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
            } else if (rule.source() instanceof PortalModels portal) {
                var axis = ((PortalBlocks.Entry) entry).axis();
                for (var value : axis.getPossibleValues()) {
                    Models.Source source = portal.model(value);
                    if (source != null && rule.conditions().allows(axis, value)) {
                        modelRule(state, entry, rule, source, rule.conditions().and(axis, value));
                    }
                }
            } else {
                modelRule(state, entry, rule, (Models.Source) rule.source(), rule.conditions());
            }
        }

        state.finish();

        if (entry.familyItemMode() == FamilyItemMode.NONE) return;
        if (entry.familyItemMode() == FamilyItemMode.CUSTOM) {
            emitItem(entry.block().asItem(), entry.item());
            return;
        }

        var first = entry.mode() == ModelMode.MODELS
                ? rules.get(0)
                : rules.stream()
                .filter(rule -> rule.conditions().isAlways())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Multipart block " + entry.block()
                                + " needs Part.always(...), an explicit .item(...), or .noItem()."
                ));

        Models.Source source;
        if (first.source() instanceof TallCrossModels.Source tall) {
            source = tall.upper();
        } else if (first.source() instanceof PortalModels portal) {
            var axis = ((PortalBlocks.Entry) entry).axis();
            source = axis.getPossibleValues().stream()
                    .filter(value -> first.conditions().allows(axis, value))
                    .map(portal::model)
                    .filter(java.util.Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No portal item source for " + entry.block()));
        } else {
            source = (Models.Source) first.source();
        }

        Tints.Tint effectiveTint = Tints.effective(entry.tint(), first.tint());

        if (source instanceof CrossModels.Generated cross) {
            emitItem(
                    entry.block().asItem(),
                    SimpleItems.Model.generated()
                            .flat()
                            .texture(cross.textures().texture().toString())
                            .tint(effectiveTint)
            );
            return;
        }

        ResourceLocation model = models.resolve(source, effectiveTint);
        emitItem(
                entry.block().asItem(),
                SimpleItems.Model.existing(model).tint(effectiveTint)
        );
    }

    private void modelRule(NewgenOutput.State state, BlockFamily.ModelFamilyEntry<?, ?> entry,
                           BlockFamily.ModelRule<?, ?> rule, Models.Source source, Conditions.Match when) {
        ResourceLocation model = models.resolve(source, Tints.effective(entry.tint(), rule.tint()));
        for (Rotations.Case rotation : entry.rotations().cases()) {
            Conditions.Match match = when;
            if (rotation.value() != null) {
                Property<?> property = entry.rotations().property();
                if (!property.getPossibleValues().contains(rotation.value()) || !match.allows(property, rotation.value())) continue;
                match = with(match, property, rotation.value());
            }
            state.add(model, match, ModelTransforms.combine(rotation.x(), rule.rotationX()),
                    ModelTransforms.combine(rotation.y(), rule.rotationY()), rule.variants(), false);
        }
    }

    private <M, P extends BlockFamily.ModelRule<M, ?>> void family(
            BlockFamily.StateFamilyEntry<P> entry, M base, List<NewgenStates.Pose<M>> poses) {
        NewgenOutput.State state = output.state(entry.block());
        for (var pose : poses) {
            ResourceLocation model = pose.model().apply(base);
            if (model == null) throw new IllegalStateException("Incomplete base model family for " + entry.block()
                    + ": " + pose.when().terms());
            state.add(model, pose.when(), ModelTransforms.combine(pose.x(), entry.rotationX()),
                    ModelTransforms.combine(pose.y(), entry.rotationY()), entry.variants(), pose.uvLock());
            for (P part : entry.parts()) {
                if (!compatible(pose.when(), part.conditions())) continue;
                ResourceLocation partModel = pose.model().apply(part.source());
                if (partModel == null) continue; 
                partModel = models.resolve(new Models.Existing(partModel),
                        Tints.effective(Tints.none(), part.tint()));
                state.add(partModel, pose.when().and(part.conditions()),
                        ModelTransforms.combine(pose.x(), part.rotationX()),
                        ModelTransforms.combine(pose.y(), part.rotationY()), part.variants(), pose.uvLock());
            }
        }
        state.finish();
    }

    void slab(SlabBlocks.Entry entry) {
        SlabModels base = models.slab(entry);
        family(entry, base, NewgenStates.SLABS);
        familyItem(entry, base.model(SlabType.BOTTOM));
    }

    void stair(StairBlocks.Entry entry) {
        StairModels base = models.stair(entry);
        family(entry, base, NewgenStates.STAIRS);
        familyItem(entry, base.model(StairsShape.STRAIGHT));
    }

    void fence(FenceBlocks.Entry entry) {
        FenceModels base = models.fence(entry);
        family(entry, base, NewgenStates.FENCES);
        familyItem(entry, base.inventory());
    }

    void fenceGate(FenceGateBlocks.Entry entry) {
        FenceGateModels base = models.fenceGate(entry);
        family(entry, base, NewgenStates.FENCE_GATES);
        familyItem(entry, base.closed());
    }

    void pressurePlate(PressurePlateBlocks.Entry entry) {
        PressurePlateModels base = models.pressurePlate(entry);
        family(entry, base, NewgenStates.pressurePlates(entry.block()));
        familyItem(entry, base.up());
    }

    void button(ButtonBlocks.Entry entry) {
        ButtonModels base = models.button(entry);
        family(entry, base, NewgenStates.BUTTONS);
        familyItem(entry, base.inventory());
    }

    void wall(WallBlocks.Entry entry) {
        WallModels base = models.wall(entry);
        family(entry, base, NewgenStates.WALLS);
        familyItem(entry, base.inventory());
    }

    void trapdoor(TrapdoorBlocks.Entry entry) {
        TrapdoorModels base = models.trapdoor(entry);
        family(entry, base, NewgenStates.trapdoors(entry.orientation()));
        familyItem(entry, base.model(Half.BOTTOM, false));
    }

    void door(DoorBlocks.Entry entry) {
        family(entry, models.door(entry), NewgenStates.DOORS);
        for (SimpleItems.Entry item : entry.items()) simpleItem(item);
    }

    void fire(FireBlocks.Entry entry) {
        List<Tints.GeneratedLayer> layers = Tints.generatedLayers(entry.tint());
        List<ResourceLocation> floor = fireModels(entry, layers, NewgenModels.Shape.FLOOR, "floor");
        List<ResourceLocation> sides = new java.util.ArrayList<>(fireModels(entry, layers, NewgenModels.Shape.SIDE, "side"));
        sides.addAll(fireModels(entry, layers, NewgenModels.Shape.SIDE_ALT, "side_alt"));
        List<ResourceLocation> up = new java.util.ArrayList<>(fireModels(entry, layers, NewgenModels.Shape.UP, "up"));
        up.addAll(fireModels(entry, layers, NewgenModels.Shape.UP_ALT, "up_alt"));
        Conditions.Match noFaces = Conditions.when(FireBlock.NORTH, false).and(FireBlock.EAST, false)
                .and(FireBlock.SOUTH, false).and(FireBlock.WEST, false).and(FireBlock.UP, false);
        NewgenOutput.State state = output.state(entry.block());
        state.add(floor, noFaces, 0, 0, entry.variants(), false);
        List<BooleanProperty> directions = List.of(FireBlock.NORTH, FireBlock.EAST, FireBlock.SOUTH, FireBlock.WEST);
        for (int i = 0; i < directions.size(); i++) {
            state.add(sides, Conditions.when(directions.get(i), true), 0, i * 90, entry.variants(), false);
            state.add(sides, noFaces, 0, i * 90, entry.variants(), false);
        }
        state.add(up, Conditions.when(FireBlock.UP, true), 0, 0, entry.variants(), false);
        state.finish();
        if (entry.itemMode() == FamilyItemMode.CUSTOM) {
            emitItem(entry.block().asItem(), entry.item());
        } else if (entry.itemMode() == FamilyItemMode.DEFAULT) {
            emitItem(
                    entry.block().asItem(),
                    SimpleItems.Model.existing(floor.get(0)).tint(entry.tint())
            );
        }
    }

    private List<ResourceLocation> fireModels(FireBlocks.Entry entry, List<Tints.GeneratedLayer> layers,
                                              NewgenModels.Shape shape, String suffix) {
        return List.of(models.fire(entry, suffix + "0", shape, 0, layers),
                models.fire(entry, suffix + "1", shape, 1, layers));
    }

    private void familyItem(BlockFamily.StateFamilyEntry<?> entry, ResourceLocation defaultModel) {
        if (entry.familyItemMode() == FamilyItemMode.NONE) return;
        if (entry.familyItemMode() == FamilyItemMode.CUSTOM) {
            emitItem(entry.block().asItem(), entry.item());
            return;
        }
        if (defaultModel == null) throw new IllegalStateException("Missing default item model for " + entry.block());
        emitItem(entry.block().asItem(), SimpleItems.Model.existing(defaultModel));
    }

    void simpleItem(SimpleItems.Entry entry) {
        emitItem(entry.item(), entry.model());
    }

    private void emitItem(ItemLike item, SimpleItems.Model spec) {
        NewgenModels.ItemRender render = models.item(item.asItem(), spec);
        output.item(item.asItem(), render);
    }

    void spawnEgg(SpawnEggItems.Entry entry) {
        //? if <1.21.4 {
        /*emitItem(
                entry.item(),
                SimpleItems.Model.existing("minecraft:item/template_spawn_egg")
        );
        *///?} else if =1.21.4 {
        /*output.item(
                entry.item().asItem(),
                ResourceLocation.fromNamespaceAndPath("minecraft", "item/template_spawn_egg"),
                List.of(
                        Tints.constant(entry.primaryColor()),
                        Tints.constant(entry.secondaryColor())
                )
        );
        *///?} else {
        emitItem(entry.item(), entry.modernModel());
        //?}
    }

    void armor(ArmorItems.Entry entry) {
        for (ArmorItems.Piece piece : ArmorItems.Piece.values()) {
            Item item = entry.item(piece).asItem();
            ResourceLocation head = piece == ArmorItems.Piece.HELMET ? entry.helmetHeadModel() : null;

            if (entry.inventory().existing()) {
                ResourceLocation model = entry.existingInventoryModel(piece);
                if (head == null) {
                    emitItem(item, SimpleItems.Model.existing(model));
                } else {
                    output.armorItem(item, model, null, head);
                }
                continue;
            }

            ResourceLocation texture = entry.inventoryTexture(piece);

            if (entry.inventory().trims()) {
                NewgenModels.ArmorItemModels armorModels = models.armorItemModels(
                        item,
                        texture,
                        ArmorItems.trimTexture(piece)
                );
                output.armorItem(item, armorModels.base(), armorModels.trimmed(), head);
            } else {
                SimpleItems.Model model = SimpleItems.Model.generated()
                        .flat()
                        .texture(texture.toString());
                NewgenModels.ItemRender render = models.item(item, model);

                if (head == null) output.item(item, render);
                else {
                    if (render.parts().size() != 1) {
                        throw new IllegalStateException("Armor HEAD override requires one base inventory model");
                    }
                    output.armorItem(item, render.parts().get(0).model(), null, head);
                }
            }
        }
    }

    private static boolean compatible(Conditions.Match first, Conditions.Match second) {
        for (Conditions.Term<?> term : first.terms()) {
            if (!second.allows(term.property(), term.value())) return false;
        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Conditions.Match with(Conditions.Match match, Property property, Comparable value) {
        return match.and(property, value);
    }

    public static final class Provider
            //? if <1.21.4 {
            /*extends BlockStateProvider
             *///?} else {
            extends ModelProvider
            //?}
    {
        //? if <1.21.4 {
        /*public Provider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, MusaCore.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            Newgen.generate(this, itemModels());
        }
        *///?} else {
        public Provider(PackOutput output) {
            super(output, MusaCore.MOD_ID);
        }

        @Override
        protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
            Newgen.generate(blocks, items);
        }
        //?}
    }

}
