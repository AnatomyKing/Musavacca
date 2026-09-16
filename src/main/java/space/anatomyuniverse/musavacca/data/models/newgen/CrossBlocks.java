package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class CrossBlocks {
    private CrossBlocks() {}

    public static final class Model extends BlockFamily.ModelRule<Models.Source, Model> {
        private Model(Models.Source source, Conditions.Match conditions) {
            super(source, conditions);
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
    }

    public static final class Part extends BlockFamily.ModelRule<Models.Source, Part> {
        private Part(Models.Source source, Conditions.Match conditions) {
            super(source, conditions);
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
    }

    public static final class Entry extends BlockFamily.ModelFamilyEntry<Model, Part> {
        private Entry(Builder builder) {
            super(
                    builder,
                    builder.generated
                            ? List.of(Model.always(CrossModels.generated(builder.block, builder.generatedTextureConfig)))
                            : builder.models,
                    builder.generated ? List.of() : builder.parts
            );
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public void validate() {
            validateStructure("CrossBlocks");

            for (Model model : models()) {
                validateSource(model.source());
            }

            for (Part part : parts()) {
                validateSource(part.source());
            }
        }

        private static void validateSource(Models.Source source) {
            if (!(source instanceof Models.Existing)
                    && !(source instanceof CrossModels.Generated)) {
                throw new IllegalStateException(
                        "CrossBlocks only accepts existing models or CrossModels.generated(...): " + source
                );
            }
        }

        public static final class Builder extends BlockFamily.ModelFamilyBuilder<Builder, Model, Part> {
            private Consumer<CrossTextures.Builder> generatedTextureConfig = textures -> {};

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                beginGenerated();
                return this;
            }

            public Builder model(String modelId) {
                return model(Models.existing(modelId));
            }

            public Builder model(Models.Source source) {
                addModel(Model.always(source));
                return this;
            }

            public Builder models(Model... models) {
                addModels(models);
                return this;
            }

            public Builder multipart(Part... parts) {
                addParts(parts);
                return this;
            }

            public Builder texture() {
                return textureConfig(CrossTextures.Builder::texture);
            }

            public Builder texture(String texture) {
                return textureConfig(textures -> textures.texture(texture));
            }

            private Builder textureConfig(Consumer<CrossTextures.Builder> config) {
                if (!generated) {
                    throw new IllegalStateException("texture(...) requires .generated() first");
                }

                Objects.requireNonNull(config, "config");
                Consumer<CrossTextures.Builder> previous = generatedTextureConfig;

                generatedTextureConfig = textures -> {
                    previous.accept(textures);
                    config.accept(textures);
                };

                return this;
            }

            public Entry build() {
                validateReadyToBuild("Generated cross model");

                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}

