package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class TallCrossBlocks {
    private TallCrossBlocks() {}

    public static final class Model extends BlockFamily.ModelRule<TallCrossModels.Source, Model> {
        private Model(TallCrossModels.Source source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Model always(TallCrossModels.Source source) {
            return new Model(source, Conditions.always());
        }

        public static Model when(TallCrossModels.Source source, Conditions.Match conditions) {
            return new Model(source, conditions);
        }
    }

    public static final class Part extends BlockFamily.ModelRule<TallCrossModels.Source, Part> {
        private Part(TallCrossModels.Source source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(TallCrossModels.Source source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(TallCrossModels.Source source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }
    }

    public static final class Entry extends BlockFamily.ModelFamilyEntry<Model, Part> {
        private Entry(Builder builder) {
            super(
                    builder,
                    builder.generated
                            ? List.of(Model.always(TallCrossModels.generated(builder.block, builder.generatedTextureConfig)))
                            : builder.models,
                    builder.generated ? List.of() : builder.parts
            );
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public void validate() {
            BlockFamilyValidation.requireProperty(
                    block(),
                    DoublePlantBlock.HALF,
                    "TallCrossBlocks"
            );

            if (rotations().type() != Rotations.Type.BRICKS
                    && rotations().property() == DoublePlantBlock.HALF) {
                throw new IllegalStateException(
                        "TallCrossBlocks manages DoublePlantBlock.HALF automatically for " + block()
                );
            }

            for (Model model : models()) {
                validateConditions(model.conditions());
            }

            for (Part part : parts()) {
                validateConditions(part.conditions());
            }

            validateStructure("TallCrossBlocks");
        }

        private void validateConditions(Conditions.Match conditions) {
            if (conditions.contains(DoublePlantBlock.HALF)) {
                throw new IllegalStateException(
                        "TallCrossBlocks manages DoublePlantBlock.HALF automatically for " + block()
                );
            }
        }

        public static final class Builder extends BlockFamily.ModelFamilyBuilder<Builder, Model, Part> {
            private Consumer<TallCrossTextures.Builder> generatedTextureConfig = textures -> {};

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                beginGenerated();
                return this;
            }

            public Builder model(String lowerModel, String upperModel) {
                return model(TallCrossModels.existing(lowerModel, upperModel));
            }

            public Builder model(TallCrossModels.Source source) {
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

            public Builder textures(Consumer<TallCrossTextures.Builder> config) {
                if (!generated) {
                    throw new IllegalStateException("textures(...) requires .generated() first");
                }

                Objects.requireNonNull(config, "config");
                Consumer<TallCrossTextures.Builder> previous = generatedTextureConfig;

                generatedTextureConfig = textures -> {
                    previous.accept(textures);
                    config.accept(textures);
                };

                return this;
            }

            public Entry build() {
                validateReadyToBuild("Generated tall cross model");

                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}

