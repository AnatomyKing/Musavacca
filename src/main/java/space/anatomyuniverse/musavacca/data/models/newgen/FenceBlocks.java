package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;

import java.util.Objects;

public final class FenceBlocks {
    private FenceBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<FenceModels, Part> {
        private Part(FenceModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(FenceModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(FenceModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public FenceModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Set textures;
        private final FenceModels baseModels;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public FamilyTextures.Set textures() {
            return textures;
        }

        public FenceModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof FenceBlock)) {
                throw new IllegalStateException(
                        "FenceBlocks requires a FenceBlock: " + ModelLocations.blockId(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), FenceBlock.NORTH, "FenceBlocks");
            BlockFamilyValidation.requireProperty(block(), FenceBlock.EAST, "FenceBlocks");
            BlockFamilyValidation.requireProperty(block(), FenceBlock.SOUTH, "FenceBlocks");
            BlockFamilyValidation.requireProperty(block(), FenceBlock.WEST, "FenceBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base model selected for " + ModelLocations.blockId(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated textures configured for " + ModelLocations.blockId(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING) {
                if (baseModels == null || !baseModels.worldComplete()) {
                    throw new IllegalStateException(
                            "Base FenceModels must provide the complete in-world family for "
                                    + ModelLocations.blockId(block())
                    );
                }
            }

            if (familyItemMode() == FamilyItemMode.DEFAULT
                    && baseMode() == BaseModelMode.EXISTING
                    && baseModels.inventory() == null) {
                throw new IllegalStateException(
                        ".item() requires an inventory model for " + ModelLocations.blockId(block())
                                + "; provide it in the model family, use .item(model), or .noItem()."
                );
            }

            validatePartConditions();

        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Set textures;
            private FenceModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(FenceModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence").source().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence").source(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence").source(texture).build();
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

