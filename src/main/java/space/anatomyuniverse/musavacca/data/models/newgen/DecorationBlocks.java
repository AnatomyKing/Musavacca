package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.DecorationBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DecorationBlocks {
    private DecorationBlocks() {}

    enum ItemMode {
        UNSET,
        PLACEMENT,
        CUSTOM,
        NONE
    }

    public static final class Model extends BlockFamily.ModelRule<Models.Source, Model> {
        private final DecorationBlock.Placement placement;
        private final DecorationBlock.Orientation orientation;

        private Model(
                DecorationBlock.Placement placement,
                DecorationBlock.Orientation orientation,
                Models.Source source,
                Conditions.Match conditions
        ) {
            super(source, conditions);
            this.placement = Objects.requireNonNull(placement, "placement");
            this.orientation = Objects.requireNonNull(orientation, "orientation");
        }

        public static Model fixed(DecorationBlock.Placement placement, String modelId) {
            return fixed(placement, Models.existing(modelId), Conditions.always());
        }

        public static Model fixed(DecorationBlock.Placement placement, Models.Source source) {
            return fixed(placement, source, Conditions.always());
        }

        public static Model fixed(
                DecorationBlock.Placement placement,
                String modelId,
                Conditions.Match conditions
        ) {
            return fixed(placement, Models.existing(modelId), conditions);
        }

        public static Model fixed(
                DecorationBlock.Placement placement,
                Models.Source source,
                Conditions.Match conditions
        ) {
            return new Model(
                    placement,
                    DecorationBlock.Orientation.FIXED,
                    source,
                    conditions
            );
        }

        public static Model facing(DecorationBlock.Placement placement, String modelId) {
            return facing(placement, Models.existing(modelId), Conditions.always());
        }

        public static Model facing(DecorationBlock.Placement placement, Models.Source source) {
            return facing(placement, source, Conditions.always());
        }

        public static Model facing(
                DecorationBlock.Placement placement,
                String modelId,
                Conditions.Match conditions
        ) {
            return facing(placement, Models.existing(modelId), conditions);
        }

        public static Model facing(
                DecorationBlock.Placement placement,
                Models.Source source,
                Conditions.Match conditions
        ) {
            return new Model(
                    placement,
                    DecorationBlock.Orientation.FACING,
                    source,
                    conditions
            );
        }

        public static Model rotating(DecorationBlock.Placement placement, String modelId) {
            return rotating(placement, Models.existing(modelId), Conditions.always());
        }

        public static Model rotating(DecorationBlock.Placement placement, Models.Source source) {
            return rotating(placement, source, Conditions.always());
        }

        public static Model rotating(
                DecorationBlock.Placement placement,
                String modelId,
                Conditions.Match conditions
        ) {
            return rotating(placement, Models.existing(modelId), conditions);
        }

        public static Model rotating(
                DecorationBlock.Placement placement,
                Models.Source source,
                Conditions.Match conditions
        ) {
            return new Model(
                    placement,
                    DecorationBlock.Orientation.ROTATION,
                    source,
                    conditions
            );
        }

        public DecorationBlock.Placement placement() {
            return placement;
        }

        public DecorationBlock.Orientation orientation() {
            return orientation;
        }
    }

    public static final class Entry {
        private final DecorationBlock block;
        private final List<Model> models;
        private final Tints.Tint tint;
        private final ItemMode itemMode;
        private final DecorationBlock.Placement itemPlacement;
        private final SimpleItems.Model itemModel;

        private Entry(Builder builder) {
            this.block = builder.block;
            this.models = List.copyOf(builder.models);
            this.tint = builder.tint;
            this.itemMode = builder.itemMode;
            this.itemPlacement = builder.itemPlacement;
            this.itemModel = builder.itemModel == null ? null : builder.itemModel.copy();
        }

        public static Builder builder(DecorationBlock block) {
            return new Builder(block);
        }

        public DecorationBlock block() {
            return block;
        }

        public List<Model> models() {
            return models;
        }

        public Tints.Tint tint() {
            return tint;
        }

        ItemMode itemMode() {
            return itemMode;
        }

        DecorationBlock.Placement itemPlacement() {
            return itemPlacement;
        }

        SimpleItems.Model itemModel() {
            return itemModel;
        }

        Model itemRule() {
            if (itemMode != ItemMode.PLACEMENT) {
                throw new IllegalStateException("Decoration item is not configured from a placement");
            }

            List<Model> candidates = models.stream()
                    .filter(model -> model.placement() == itemPlacement)
                    .filter(model -> model.conditions().isAlways())
                    .toList();

            if (candidates.size() != 1) {
                throw new IllegalStateException(
                        "Decoration item placement " + itemPlacement.getSerializedName()
                                + " on " + ModelLocations.blockId(block)
                                + " must have exactly one unconditional model; use .item(model) instead"
                );
            }

            return candidates.get(0);
        }

        private void validate() {
            if (models.isEmpty()) {
                throw new IllegalStateException(
                        "DecorationBlocks requires at least one model for " + ModelLocations.blockId(block)
                );
            }

            BlockFamilyValidation.requireProperty(
                    block,
                    DecorationBlock.PLACEMENT,
                    "DecorationBlocks"
            );
            BlockFamilyValidation.requireProperty(
                    block,
                    DecorationBlock.ROTATION,
                    "DecorationBlocks"
            );
            BlockFamilyValidation.requireProperty(
                    block,
                    DecorationBlock.FACING,
                    "DecorationBlocks"
            );

            for (Model model : models) {
                BlockFamilyValidation.conditions(block, model.conditions());

                if (model.conditions().contains(DecorationBlock.PLACEMENT)
                        || model.conditions().contains(DecorationBlock.ROTATION)
                        || model.conditions().contains(DecorationBlock.FACING)) {
                    throw new IllegalStateException(
                            "DecorationBlocks manages placement, rotation and facing automatically for "
                                    + ModelLocations.blockId(block)
                    );
                }

                if (!block.isEnabled(model.placement())) {
                    throw new IllegalStateException(
                            "Decoration model declares disabled placement "
                                    + model.placement().getSerializedName()
                                    + " on " + ModelLocations.blockId(block)
                    );
                }

                DecorationBlock.Orientation actual = block.orientation(model.placement());
                if (actual != model.orientation()) {
                    throw new IllegalStateException(
                            "Decoration model declares " + model.orientation()
                                    + " for placement " + model.placement().getSerializedName()
                                    + " on " + ModelLocations.blockId(block)
                                    + ", but DecorationBlock.Options uses " + actual
                    );
                }
            }

            for (DecorationBlock.Placement placement : DecorationBlock.Placement.values()) {
                List<Model> placementModels = models.stream()
                        .filter(model -> model.placement() == placement)
                        .toList();

                if (!block.isEnabled(placement)) {
                    if (!placementModels.isEmpty()) {
                        throw new IllegalStateException(
                                "DecorationBlocks has models for disabled placement "
                                        + placement.getSerializedName()
                                        + " on " + ModelLocations.blockId(block)
                        );
                    }
                    continue;
                }

                if (placementModels.isEmpty()) {
                    throw new IllegalStateException(
                            "DecorationBlocks is missing a model for enabled placement "
                                    + placement.getSerializedName()
                                    + " on " + ModelLocations.blockId(block)
                    );
                }

                validatePlacementCoverage(placement, placementModels);
            }

            if (itemMode == ItemMode.UNSET) {
                throw new IllegalStateException(
                        "DecorationBlocks requires explicit item handling for " + ModelLocations.blockId(block)
                                + ": use .item(placement), .item(model), .item(SimpleItems.Model), or .noItem()"
                );
            }

            if (itemMode == ItemMode.PLACEMENT) {
                if (!block.isEnabled(itemPlacement)) {
                    throw new IllegalStateException(
                            "Decoration item uses disabled placement "
                                    + itemPlacement.getSerializedName()
                                    + " on " + ModelLocations.blockId(block)
                    );
                }

                itemRule();
            }
        }

        private void validatePlacementCoverage(
                DecorationBlock.Placement placement,
                List<Model> placementModels
        ) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                if (state.getValue(DecorationBlock.PLACEMENT) != placement) {
                    continue;
                }

                int matches = 0;
                for (Model model : placementModels) {
                    if (model.conditions().matches(state)) {
                        matches++;
                    }
                }

                if (matches != 1) {
                    throw new IllegalStateException(
                            "DecorationBlocks must match each enabled placement state exactly once, but "
                                    + matches + " models match " + state
                    );
                }
            }
        }
    }

    public static final class Builder {
        private final DecorationBlock block;
        private final List<Model> models = new ArrayList<>();
        private Tints.Tint tint = Tints.none();
        private ItemMode itemMode = ItemMode.UNSET;
        private DecorationBlock.Placement itemPlacement;
        private SimpleItems.Model itemModel;

        private Builder(DecorationBlock block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder model(Model model) {
            models.add(Objects.requireNonNull(model, "model"));
            return this;
        }

        public Builder models(Model... models) {
            if (models == null || models.length == 0) {
                throw new IllegalArgumentException("models(...) requires at least one model");
            }

            Arrays.stream(models)
                    .map(model -> Objects.requireNonNull(model, "model"))
                    .forEach(this.models::add);

            return this;
        }

        public Builder tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");
            return this;
        }

        public Builder item(DecorationBlock.Placement placement) {
            requireItemUnset();
            itemMode = ItemMode.PLACEMENT;
            itemPlacement = Objects.requireNonNull(placement, "placement");
            return this;
        }

        public Builder item(String modelId) {
            return item(SimpleItems.Model.existing(modelId));
        }

        public Builder item(SimpleItems.Model model) {
            requireItemUnset();
            itemMode = ItemMode.CUSTOM;
            itemModel = Objects.requireNonNull(model, "model").copy();
            return this;
        }

        public Builder noItem() {
            requireItemUnset();
            itemMode = ItemMode.NONE;
            return this;
        }

        private void requireItemUnset() {
            if (itemMode != ItemMode.UNSET) {
                throw new IllegalStateException("Decoration item handling is already configured");
            }
        }

        public Entry build() {
            Entry entry = new Entry(this);
            entry.validate();
            return entry;
        }
    }
}

