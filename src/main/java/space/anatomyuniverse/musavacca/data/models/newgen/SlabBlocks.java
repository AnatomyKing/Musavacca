package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class SlabBlocks {
    private SlabBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<SlabModels, Part> {
        private Part(SlabModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(SlabModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(SlabModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public SlabModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final SlabTextures.Set textures;
        private final SlabModels baseModels;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public SlabTextures.Set textures() {
            return textures;
        }

        public SlabModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof SlabBlock)) {
                throw new IllegalStateException(
                        "SlabBlocks requires a SlabBlock: " + ModelUtil.idOf(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), SlabBlock.TYPE, "SlabBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base model selected for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated textures configured for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING) {
                if (baseModels == null || !baseModels.complete()) {
                    throw new IllegalStateException(
                            "Base SlabModels must provide the complete family for "
                                    + ModelUtil.idOf(block())
                    );
                }
            }


            validatePartConditions();

        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private SlabTextures.Set textures;
            private SlabModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(SlabModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }


            public Builder texture() {
                requireGeneratedTextures();
                textures = SlabTextures.builder(block).all().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = SlabTextures.builder(block).all(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = SlabTextures.builder(block).all(texture).build();
                return this;
            }

            public Builder textures(Consumer<SlabTextures.Builder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                SlabTextures.Builder builder = SlabTextures.builder(block);
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
