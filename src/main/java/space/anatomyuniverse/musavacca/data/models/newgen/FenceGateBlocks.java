package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;

import java.util.Objects;

public final class FenceGateBlocks {
    private FenceGateBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<FenceGateModels, Part> {
        private Part(FenceGateModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(FenceGateModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(FenceGateModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public FenceGateModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Set textures;
        private final FenceGateModels baseModels;

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

        public FenceGateModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof FenceGateBlock)) {
                throw new IllegalStateException(
                        "FenceGateBlocks requires a FenceGateBlock: " + ModelLocations.blockId(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), FenceGateBlock.FACING, "FenceGateBlocks");
            BlockFamilyValidation.requireProperty(block(), FenceGateBlock.OPEN, "FenceGateBlocks");
            BlockFamilyValidation.requireProperty(block(), FenceGateBlock.IN_WALL, "FenceGateBlocks");

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
                if (baseModels == null || !baseModels.complete()) {
                    throw new IllegalStateException(
                            "Base FenceGateModels must provide the complete family for "
                                    + ModelLocations.blockId(block())
                    );
                }
            }

            validatePartConditions();

        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Set textures;
            private FenceGateModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(FenceGateModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence_gate").source().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence_gate").source(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_fence_gate").source(texture).build();
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

