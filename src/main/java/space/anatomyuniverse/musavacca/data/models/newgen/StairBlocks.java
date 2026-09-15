package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class StairBlocks {
    private StairBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<StairModels, Part> {
        private Part(StairModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(StairModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(StairModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public StairModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final StairTextures.Set textures;
        private final StairModels baseModels;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public StairTextures.Set textures() {
            return textures;
        }

        public StairModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof StairBlock)) {
                throw new IllegalStateException(
                        "StairBlocks requires a StairBlock: " + ModelUtil.idOf(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), StairBlock.FACING, "StairBlocks");
            BlockFamilyValidation.requireProperty(block(), StairBlock.HALF, "StairBlocks");
            BlockFamilyValidation.requireProperty(block(), StairBlock.SHAPE, "StairBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base stair model selected for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated stair textures configured for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING) {
                if (baseModels == null) {
                    throw new IllegalStateException(
                            "No existing StairModels configured for " + ModelUtil.idOf(block())
                    );
                }

                if (!baseModels.complete()) {
                    throw new IllegalStateException(
                            "Base StairModels must provide straight, inner, and outer models for "
                                    + ModelUtil.idOf(block())
                    );
                }
            }


            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private StairTextures.Set textures;
            private StairModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(StairModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = StairTextures.builder(block)
                        .all()
                        .build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = StairTextures.builder(block)
                        .all(source)
                        .build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = StairTextures.builder(block)
                        .all(texture)
                        .build();
                return this;
            }

            public Builder textures(Consumer<StairTextures.Builder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");

                StairTextures.Builder builder = StairTextures.builder(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Entry build() {
                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}
