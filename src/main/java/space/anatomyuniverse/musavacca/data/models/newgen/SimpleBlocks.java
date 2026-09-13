package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class SimpleBlocks {
    private SimpleBlocks() {}

    public enum Mode {
        MODELS,
        MULTIPART
    }

    public static final class Model {
        private final Models.Source source;
        private final Conditions.Match conditions;

        private int rotationX;
        private int rotationY;

        private Tints.Tint tint;
        private Variants.Set variants = Variants.single();

        private Model(Models.Source source, Conditions.Match conditions) {
            this.source = Objects.requireNonNull(source, "source");

            this.conditions = Objects.requireNonNull(conditions, "conditions");
        }

        public static Model always(String modelId) {
            return always(Models.existing(modelId));
        }

        public static Model always(Models.Source source) {
            return new Model(source, Conditions.always());
        }

        public static Model when(String modelId, Conditions.Match conditions) {
            return when(Models.existing(modelId), conditions);
        }

        public static Model when(Models.Source source, Conditions.Match conditions) {
            return new Model(source, conditions);
        }

        public Model rotateX(int degrees) {
            this.rotationX = normalize(degrees);

            return this;
        }

        public Model rotateY(int degrees) {
            this.rotationY = normalize(degrees);

            return this;
        }

        public Model tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");

            return this;
        }

        public Model variants(Variants.Set variants) {
            this.variants = Objects.requireNonNull(variants, "variants");

            return this;
        }

        public Models.Source source() {
            return source;
        }

        public Conditions.Match conditions() {
            return conditions;
        }

        public int rotationX() {
            return rotationX;
        }

        public int rotationY() {
            return rotationY;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Variants.Set variants() {
            return variants;
        }
    }

    public static final class Part {
        private final Models.Source source;
        private final Conditions.Match conditions;

        private int rotationX;
        private int rotationY;

        private Tints.Tint tint;
        private Variants.Set variants = Variants.single();

        private Part(Models.Source source, Conditions.Match conditions) {
            this.source = Objects.requireNonNull(source, "source");

            this.conditions = Objects.requireNonNull(conditions, "conditions");
        }

        public static Part always(String modelId) {
            return always(Models.existing(modelId));
        }

        public static Part always(Models.Source source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(String modelId, Conditions.Match conditions) {
            return when(Models.existing(modelId), conditions);
        }

        public static Part when(Models.Source source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public Part rotateX(int degrees) {
            this.rotationX = normalize(degrees);

            return this;
        }

        public Part rotateY(int degrees) {
            this.rotationY = normalize(degrees);

            return this;
        }

        public Part tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");

            return this;
        }

        public Part variants(Variants.Set variants) {
            this.variants = Objects.requireNonNull(variants, "variants");

            return this;
        }

        public Models.Source source() {
            return source;
        }

        public Conditions.Match conditions() {
            return conditions;
        }

        public int rotationX() {
            return rotationX;
        }

        public int rotationY() {
            return rotationY;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Variants.Set variants() {
            return variants;
        }
    }

    public static final class Entry {
        private final Block block;
        private final Mode mode;

        private final List<Model> models;
        private final List<Part> parts;

        private final Rotations.Spec rotations;
        private final Tints.Tint tint;
        private final Tints.Tint itemTint;
        private final String itemModel;
        private final boolean noItem;

        private Entry(Builder builder) {
            this.block = builder.block;

            this.mode = builder.mode;

            this.rotations = builder.rotations;

            this.tint = builder.tint;

            this.itemTint = builder.itemTint;

            this.itemModel = builder.itemModel;
            this.noItem = builder.noItem;

            if (builder.generated) {
                this.models = List.of(Model.always(Models.generated(builder.block, builder.generatedTextureConfig)));

                this.parts = List.of();
            } else {
                this.models = List.copyOf(builder.models);

                this.parts = List.copyOf(builder.parts);
            }
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public Block block() {
            return block;
        }

        public Mode mode() {
            return mode;
        }

        public List<Model> models() {
            return Collections.unmodifiableList(models);
        }

        public List<Part> parts() {
            return Collections.unmodifiableList(parts);
        }

        public Rotations.Spec rotations() {
            return rotations;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Tints.Tint itemTint() {
            return itemTint != null
                    ? itemTint
                    : tint;
        }

        public String itemModel() {
            return itemModel;
        }

        public boolean noItem() {
            return noItem;
        }

        public void validate() {
            if (mode == null) {
                throw new IllegalStateException("No model mode selected for " + block);
            }

            if (mode == Mode.MODELS && models.isEmpty()) {
                throw new IllegalStateException("No models for " + block);
            }

            if (mode == Mode.MULTIPART && parts.isEmpty()) {
                throw new IllegalStateException("No multipart parts for " + block);
            }

            if (
                    rotations.type()
                            != Rotations.Type.BRICKS
                            && !block
                            .defaultBlockState()
                            .hasProperty(rotations.property())
            ) {
                throw new IllegalStateException(
                        "Rotation "
                                + rotations.type()
                                + " expects property "
                                + rotations.property()
                                + " on "
                                + block
                );
            }

            for (Model model : models) {
                validateSource(model.source());
                validateConditions(model.conditions());
            }

            for (Part part : parts) {
                validateSource(part.source());
                validateConditions(part.conditions());
            }
        }

        private void validateSource(Models.Source source) {
            if (!(source instanceof Models.Existing)
                    && !(source instanceof Models.Generated)) {
                throw new IllegalStateException(
                        "SimpleBlocks only accepts existing models or Models.generated(...): " + source
                );
            }
        }

        private void validateConditions(Conditions.Match conditions) {
            for (Conditions.Term<?> term : conditions.terms()) {
                if (!block.defaultBlockState() .hasProperty(term.property())) {
                    throw new IllegalStateException(
                            "Condition property "
                                    + term.property()
                                    + " is not present on "
                                    + block
                    );
                }
            }
        }

        public static final class Builder {
            private final Block block;

            private Mode mode;

            private boolean generated;

            private final List<Model> models = new ArrayList<>();

            private final List<Part> parts = new ArrayList<>();

            private Consumer<Textures.Builder>
                    generatedTextureConfig = textures -> {
                    };

            /*
             * Universal optional defaults.
             */
            private Rotations.Spec rotations = Rotations.bricks();

            private Tints.Tint tint = Tints.none();

            private Tints.Tint itemTint;

            private String itemModel;
            private boolean noItem;

            private Builder(Block block) {
                this.block = Objects.requireNonNull(block, "block");
            }

            /*
             * Generate one full-cube model for this block.
             *
             * Texture selection is configured separately through
             * .textures(...), so the intent stays visible in NewModelSets.
             */
            public Builder generated() {
                selectMode(Mode.MODELS);

                requireNoModelSource();

                this.generated = true;

                return this;
            }

            public Builder model(String modelId) {
                return model(Models.existing(modelId));
            }

            public Builder model(Models.Source source) {
                selectMode(Mode.MODELS);

                requireNoModelSource();

                models.add(Model.always(source));

                return this;
            }

            public Builder models(Model... models) {
                selectMode(Mode.MODELS);

                requireNoModelSource();

                if (models == null || models.length == 0) {
                    throw new IllegalArgumentException("models(...) requires at least one model");
                }

                this.models.addAll(Arrays.asList(models));

                return this;
            }

            public Builder multipart(Part... parts) {
                selectMode(Mode.MULTIPART);

                requireNoModelSource();

                if (parts == null || parts.length == 0) {
                    throw new IllegalArgumentException("multipart(...) requires at least one part");
                }

                this.parts.addAll(Arrays.asList(parts));

                return this;
            }

            /*
             * Only valid for .generated().
             *
             * Example:
             *
             * .generated()
             * .textures(
             *     textures ->
             *         textures
             *             .all()
             *             .ends("_top")
             * )
             */
            public Builder textures(Consumer<Textures.Builder> config) {
                if (!generated) {
                    throw new IllegalStateException("textures(...) requires.generated() first");
                }

                Objects.requireNonNull(config, "config");

                Consumer<Textures.Builder> previous = generatedTextureConfig;

                generatedTextureConfig = textures -> {
                            previous.accept(textures);

                            config.accept(textures);
                        };

                return this;
            }

            /*
             * Optional.
             *
             * Default:
             * Rotations.bricks()
             */
            public Builder rotations(Rotations.Spec rotations) {
                this.rotations = Objects.requireNonNull(rotations, "rotations");

                return this;
            }

            /*
             * Optional.
             *
             * Default:
             * Tints.none()
             */
            public Builder tint(Tints.Tint tint) {
                this.tint = Objects.requireNonNull(tint, "tint");

                return this;
            }

            /*
             * Optional item-only tint override.
             *
             * Without this, the normal entry tint is reused.
             */
            public Builder itemTint(Tints.Tint tint) {
                if (noItem) {
                    throw new IllegalStateException("itemTint(...) cannot be used after .noItem()");
                }

                this.itemTint = Objects.requireNonNull(tint, "tint");
                return this;
            }

            /*
             * Optional item-model override.
             *
             * Without this:
             *
             * generated  -> generated model
             * model      -> referenced model
             * models     -> first declared model
             * multipart  -> first unconditional Part.always(...)
             */
            public Builder item(String modelId) {
                if (noItem) {
                    throw new IllegalStateException("item(...) cannot be used after .noItem()");
                }

                if (modelId == null || modelId.isBlank()) {
                    throw new IllegalArgumentException("item model id must not be blank");
                }

                this.itemModel = modelId;
                return this;
            }

            public Builder noItem() {
                if (itemModel != null) {
                    throw new IllegalStateException(".noItem() cannot be used after .item(...)");
                }

                if (itemTint != null) {
                    throw new IllegalStateException(".noItem() cannot be used after .itemTint(...)");
                }

                this.noItem = true;
                return this;
            }

            public Entry build() {
                if (mode == null) {
                    throw new IllegalStateException(
                            "Choose .generated(), .model(...), "
                                    + ".models(...), or .multipart(...) before .build()"
                    );
                }

                if (generated && mode != Mode.MODELS) {
                    throw new IllegalStateException("Generated full cube must use MODELS mode");
                }

                Entry entry = new Entry(this);

                entry.validate();

                return entry;
            }

            private void selectMode(Mode requested) {
                if (mode != null && mode != requested) {
                    throw new IllegalStateException(
                            "Entry model mode is already "
                                    + mode
                                    + "; cannot switch to "
                                    + requested
                    );
                }

                mode = requested;
            }

            private void requireNoModelSource() {
                if (generated || !models.isEmpty() || !parts.isEmpty()) {
                    throw new IllegalStateException("A model source has already been selected for this entry");
                }
            }
        }
    }

    static int normalize(int degrees) {
        int value = Math.floorMod(degrees, 360);

        if (value != 0 && value != 90 && value != 180 && value != 270) {
            throw new IllegalArgumentException("Only 0/90/180/270 rotations are supported: " + degrees);
        }

        return value;
    }
}
