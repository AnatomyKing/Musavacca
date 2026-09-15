package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import space.anatomyuniverse.musavacca.block.custom.DecorationBlock;

import java.util.List;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
//?}

/** One entry point for the new declarations; legacy ModelSets can run alongside it. */
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
    }

    void simple(SimpleBlocks.Entry entry) { modelFamily(entry, false); }
    void cross(CrossBlocks.Entry entry) { modelFamily(entry, true); }
    void tallCross(TallCrossBlocks.Entry entry) { modelFamily(entry, true); }
    void portal(PortalBlocks.Entry entry) { modelFamily(entry, false); }

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

            case EXISTING -> blockItem(
                    entry.block(),
                    ResourceLocation.parse(entry.itemModel()),
                    entry.itemTint()
            );

            case PLACEMENT -> {
                DecorationBlocks.Model itemRule = entry.itemRule();
                ResourceLocation itemModel = models.resolve(
                        itemRule.source(),
                        Tints.effective(entry.tint(), itemRule.tint())
                );
                blockItem(entry.block(), itemModel, entry.itemTint());
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

    private void modelFamily(BlockFamily.ModelFamilyEntry<?, ?> entry, boolean flatItem) {
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
        if (entry.noItem()) return;

        if (entry.itemModel() != null) {
            blockItem(entry.block(), ResourceLocation.parse(entry.itemModel()), entry.itemTint());
            return;
        }
        var first = entry.mode() == ModelMode.MODELS ? rules.get(0) : rules.stream()
                .filter(rule -> rule.conditions().isAlways()).findFirst()
                .orElseThrow(() -> new IllegalStateException("Multipart block " + entry.block()
                        + " needs Part.always(...) or an explicit .item(...)."));
        Models.Source source;
        if (first.source() instanceof TallCrossModels.Source tall) {
            source = tall.upper();
        } else if (first.source() instanceof PortalModels portal) {
            var axis = ((PortalBlocks.Entry) entry).axis();
            source = axis.getPossibleValues().stream()
                    .filter(value -> first.conditions().allows(axis, value))
                    .map(portal::model).filter(java.util.Objects::nonNull).findFirst()
                    .orElseThrow(() -> new IllegalStateException("No portal item source for " + entry.block()));
        } else {
            source = (Models.Source) first.source();
        }
        ResourceLocation model = flatItem && source instanceof CrossModels.Generated cross
                ? models.flatItem(entry.block().asItem(), List.of(cross.textures().texture()))
                : models.resolve(source, Tints.effective(entry.tint(), first.tint()));
        blockItem(entry.block(), model, entry.itemTint());
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
                if (partModel == null) continue; // Partial overlay families intentionally omit some poses.
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
        family(entry, base, NewgenStates.TRAPDOORS);
        if (entry.itemMode() != TrapdoorBlocks.ItemMode.NONE) {
            ResourceLocation item = entry.itemMode() == TrapdoorBlocks.ItemMode.EXISTING
                    ? ResourceLocation.parse(entry.itemModel()) : base.model(Half.BOTTOM, false);
            blockItem(entry.block(), item, Tints.none());
        }
    }

    void door(DoorBlocks.Entry entry) {
        family(entry, models.door(entry), NewgenStates.DOORS);
        for (DoorBlocks.Item item : entry.items()) {
            List<ResourceLocation> textures = item.inferSingleTexture() ? List.of(TextureTokens.item(item.item()))
                    : item.textureTokens().stream().map(t -> TextureTokens.resolveItem(item.item(), t)).toList();
            ResourceLocation model = models.flatItem(item.item().asItem(), textures);
            List<Tints.Tint> tints = java.util.stream.IntStream.range(0, textures.size()).mapToObj(item::tintForLayer).toList();
            output.item(item.item().asItem(), model, tints);
        }
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
        if (!entry.noItem()) {
            blockItem(entry.block(), entry.itemModel() == null ? floor.get(0)
                    : ResourceLocation.parse(entry.itemModel()), entry.itemTint());
        }
    }

    private List<ResourceLocation> fireModels(FireBlocks.Entry entry, List<Tints.GeneratedLayer> layers,
                                              NewgenModels.Shape shape, String suffix) {
        return List.of(models.fire(entry, suffix + "0", shape, 0, layers),
                models.fire(entry, suffix + "1", shape, 1, layers));
    }

    private void familyItem(BlockFamily.StateFamilyEntry<?> entry, ResourceLocation defaultModel) {
        if (entry.familyItemMode() == FamilyItemMode.NONE) return;
        ResourceLocation model = entry.familyItemMode() == FamilyItemMode.EXISTING
                ? ResourceLocation.parse(entry.itemModel()) : defaultModel;
        if (model == null) throw new IllegalStateException("Missing default item model for " + entry.block());
        blockItem(entry.block(), model, Tints.none());
    }

    private void blockItem(Block block, ResourceLocation model, Tints.Tint tint) {
        output.item(block.asItem(), model, List.of(tint));
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
}


