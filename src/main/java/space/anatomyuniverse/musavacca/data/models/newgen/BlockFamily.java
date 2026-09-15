package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.level.block.Block;

final class BlockFamily {
    private BlockFamily() {}

    abstract static class ModelRule<S, SELF extends ModelRule<S, SELF>> {
        private final S source;
        private final Conditions.Match conditions;

        private int rotationX;
        private int rotationY;
        private Tints.Tint tint;
        private Variants.Set variants = Variants.single();

        protected ModelRule(S source, Conditions.Match conditions) {
            this.source = Objects.requireNonNull(source, "source");
            this.conditions = Objects.requireNonNull(conditions, "conditions");
        }

        @SuppressWarnings("unchecked")
        private SELF self() {
            return (SELF) this;
        }

        public final SELF rotateX(int degrees) {
            this.rotationX = ModelTransforms.quarterTurn(degrees);
            return self();
        }

        public final SELF rotateY(int degrees) {
            this.rotationY = ModelTransforms.quarterTurn(degrees);
            return self();
        }

        public final SELF tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");
            return self();
        }

        public final SELF variants(Variants.Set variants) {
            this.variants = Objects.requireNonNull(variants, "variants");
            return self();
        }

        public final S source() {
            return source;
        }

        public final Conditions.Match conditions() {
            return conditions;
        }

        public final int rotationX() {
            return rotationX;
        }

        public final int rotationY() {
            return rotationY;
        }

        public final Tints.Tint tint() {
            return tint;
        }

        public final Variants.Set variants() {
            return variants;
        }
    }

    abstract static class ModelFamilyEntry<
            M extends ModelRule<?, ?>,
            P extends ModelRule<?, ?>
            > {
        private final Block block;
        private final ModelMode mode;
        private final List<M> models;
        private final List<P> parts;
        private final Rotations.Spec rotations;
        private final Tints.Tint tint;
        private final Tints.Tint itemTint;
        private final String itemModel;
        private final boolean noItem;

        protected ModelFamilyEntry(
                ModelFamilyBuilder<?, M, P> builder,
                List<M> models,
                List<P> parts
        ) {
            this.block = builder.block;
            this.mode = builder.mode;
            this.models = List.copyOf(models);
            this.parts = List.copyOf(parts);
            this.rotations = builder.rotations;
            this.tint = builder.tint;
            this.itemTint = builder.itemTint;
            this.itemModel = builder.itemModel;
            this.noItem = builder.noItem;
        }

        public final Block block() {
            return block;
        }

        public final ModelMode mode() {
            return mode;
        }

        public final List<M> models() {
            return models;
        }

        public final List<P> parts() {
            return parts;
        }

        public final Rotations.Spec rotations() {
            return rotations;
        }

        public final Tints.Tint tint() {
            return tint;
        }

        public final Tints.Tint itemTint() {
            return itemTint != null ? itemTint : tint;
        }

        public final String itemModel() {
            return itemModel;
        }

        public final boolean noItem() {
            return noItem;
        }

        protected final void validateStructure(String family) {
            if (mode == null) {
                throw new IllegalStateException("No model mode selected for " + block);
            }

            if (mode == ModelMode.MODELS && models.isEmpty()) {
                throw new IllegalStateException("No models for " + block);
            }

            if (mode == ModelMode.MULTIPART && parts.isEmpty()) {
                throw new IllegalStateException("No multipart parts for " + block);
            }

            BlockFamilyValidation.rotation(block, rotations, family);

            for (M model : models) {
                BlockFamilyValidation.conditions(block, model.conditions());
            }

            for (P part : parts) {
                BlockFamilyValidation.conditions(block, part.conditions());
            }

            if (mode == ModelMode.MODELS) {
                BlockFamilyValidation.exactlyOneModelPerState(block, models, family);
            }
        }
    }

    abstract static class ModelFamilyBuilder<
            SELF extends ModelFamilyBuilder<SELF, M, P>,
            M extends ModelRule<?, ?>,
            P extends ModelRule<?, ?>
            > {
        protected final Block block;
        protected ModelMode mode;
        protected boolean generated;
        protected final List<M> models = new ArrayList<>();
        protected final List<P> parts = new ArrayList<>();
        protected Rotations.Spec rotations = Rotations.bricks();
        protected Tints.Tint tint = Tints.none();
        protected Tints.Tint itemTint;
        protected String itemModel;
        protected boolean noItem;

        protected ModelFamilyBuilder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        @SuppressWarnings("unchecked")
        protected final SELF self() {
            return (SELF) this;
        }

        protected final void selectMode(ModelMode requested) {
            Objects.requireNonNull(requested, "requested");

            if (mode != null && mode != requested) {
                throw new IllegalStateException(
                        "Entry model mode is already " + mode
                                + "; cannot switch to " + requested
                );
            }

            mode = requested;
        }

        protected final void requireNoModelSource() {
            if (generated || !models.isEmpty() || !parts.isEmpty()) {
                throw new IllegalStateException(
                        "A model source has already been selected for this entry"
                );
            }
        }

        protected final void beginGenerated() {
            selectMode(ModelMode.MODELS);
            requireNoModelSource();
            generated = true;
        }

        protected final void addModel(M model) {
            selectMode(ModelMode.MODELS);
            requireNoModelSource();
            models.add(Objects.requireNonNull(model, "model"));
        }

        @SafeVarargs
        protected final void addModels(M... models) {
            selectMode(ModelMode.MODELS);
            requireNoModelSource();

            if (models == null || models.length == 0) {
                throw new IllegalArgumentException("models(...) requires at least one model");
            }

            for (M model : models) {
                Objects.requireNonNull(model, "model");
            }

            Collections.addAll(this.models, models);
        }

        @SafeVarargs
        protected final void addParts(P... parts) {
            selectMode(ModelMode.MULTIPART);
            requireNoModelSource();

            if (parts == null || parts.length == 0) {
                throw new IllegalArgumentException("multipart(...) requires at least one part");
            }

            for (P part : parts) {
                Objects.requireNonNull(part, "part");
            }

            Collections.addAll(this.parts, parts);
        }

        public final SELF rotations(Rotations.Spec rotations) {
            this.rotations = Objects.requireNonNull(rotations, "rotations");
            return self();
        }

        public final SELF tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");
            return self();
        }

        public final SELF itemTint(Tints.Tint tint) {
            if (noItem) {
                throw new IllegalStateException("itemTint(...) cannot be used after .noItem()");
            }

            this.itemTint = Objects.requireNonNull(tint, "tint");
            return self();
        }

        public final SELF item(String modelId) {
            if (noItem) {
                throw new IllegalStateException("item(...) cannot be used after .noItem()");
            }

            if (modelId == null || modelId.isBlank()) {
                throw new IllegalArgumentException("item model id must not be blank");
            }

            this.itemModel = modelId;
            return self();
        }

        public final SELF noItem() {
            if (itemModel != null) {
                throw new IllegalStateException(".noItem() cannot be used after .item(...)");
            }

            if (itemTint != null) {
                throw new IllegalStateException(".noItem() cannot be used after .itemTint(...)");
            }

            noItem = true;
            return self();
        }

        protected final void validateReadyToBuild(String generatedDescription) {
            if (mode == null) {
                throw new IllegalStateException(
                        "Choose .generated(), .model(...), .models(...), or .multipart(...) before .build()"
                );
            }

            if (generated && mode != ModelMode.MODELS) {
                throw new IllegalStateException(generatedDescription + " must use MODELS mode");
            }
        }
    }

    abstract static class StateFamilyEntry<P extends ModelRule<?, ?>> {
        private final Block block;
        private final BaseModelMode baseMode;
        private final List<P> parts;
        private final int rotationX;
        private final int rotationY;
        private final Variants.Set variants;
        private final FamilyItemMode itemMode;
        private final String itemModel;

        protected StateFamilyEntry(StateFamilyBuilder<?, P> builder) {
            this.block = builder.block;
            this.baseMode = builder.baseMode;
            this.parts = List.copyOf(builder.parts);
            this.rotationX = builder.rotationX;
            this.rotationY = builder.rotationY;
            this.variants = builder.variants;
            this.itemMode = builder.itemMode;
            this.itemModel = builder.itemModel;
        }

        public final Block block() {
            return block;
        }

        public final BaseModelMode baseMode() {
            return baseMode;
        }

        public final List<P> parts() {
            return parts;
        }

        public final int rotationX() {
            return rotationX;
        }

        public final int rotationY() {
            return rotationY;
        }

        public final Variants.Set variants() {
            return variants;
        }

        FamilyItemMode familyItemMode() { return itemMode; }

        public String itemModel() { return itemModel; }

        protected final void validatePartConditions() {
            for (P part : parts) {
                BlockFamilyValidation.conditions(block, part.conditions());
            }
        }
    }

    abstract static class StateFamilyBuilder<
            SELF extends StateFamilyBuilder<SELF, P>,
            P extends ModelRule<?, ?>
            > {
        protected final Block block;
        protected BaseModelMode baseMode;
        protected final List<P> parts = new ArrayList<>();
        protected int rotationX;
        protected int rotationY;
        protected Variants.Set variants = Variants.single();
        protected FamilyItemMode itemMode = FamilyItemMode.DEFAULT;
        protected String itemModel;

        protected StateFamilyBuilder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        @SuppressWarnings("unchecked")
        protected final SELF self() {
            return (SELF) this;
        }

        protected final void selectGenerated() {
            if (baseMode == BaseModelMode.EXISTING) {
                throw new IllegalStateException("Cannot use .generated() after .models(...)");
            }

            baseMode = BaseModelMode.GENERATED;
        }

        protected final void selectExisting() {
            if (baseMode == BaseModelMode.GENERATED) {
                throw new IllegalStateException("Cannot use .models(...) after .generated()");
            }

            baseMode = BaseModelMode.EXISTING;
        }

        @SafeVarargs
        public final SELF multipart(P... parts) {
            if (parts == null || parts.length == 0) {
                throw new IllegalArgumentException("multipart(...) requires at least one part");
            }

            for (P part : parts) {
                this.parts.add(Objects.requireNonNull(part, "part"));
            }

            return self();
        }

        public final SELF rotateX(int degrees) {
            rotationX = ModelTransforms.quarterTurn(degrees);
            return self();
        }

        public final SELF rotateY(int degrees) {
            rotationY = ModelTransforms.quarterTurn(degrees);
            return self();
        }

        public final SELF variants(Variants.Set variants) {
            this.variants = Objects.requireNonNull(variants, "variants");
            return self();
        }

        protected final void requireGeneratedTextures() {
            if (baseMode != BaseModelMode.GENERATED) {
                throw new IllegalStateException("texture(s) requires .generated() first");
            }
        }

    }

    abstract static class ItemFamilyBuilder<SELF extends ItemFamilyBuilder<SELF, P>,
            P extends ModelRule<?, ?>> extends StateFamilyBuilder<SELF, P> {
        private boolean explicitItem;

        protected ItemFamilyBuilder(Block block) { super(block); }

        public SELF item() {
            requireItem();
            itemMode = FamilyItemMode.DEFAULT;
            itemModel = null;
            return self();
        }

        public SELF item(String model) {
            requireItem();
            itemMode = FamilyItemMode.EXISTING;
            itemModel = Models.existing(model).model().toString();
            return self();
        }

        public SELF noItem() {
            if (explicitItem) throw new IllegalStateException("Cannot use .noItem() after .item(...)");
            itemMode = FamilyItemMode.NONE;
            itemModel = null;
            return self();
        }

        private void requireItem() {
            if (itemMode == FamilyItemMode.NONE) throw new IllegalStateException("Cannot use .item(...) after .noItem()");
            explicitItem = true;
        }
    }
}
