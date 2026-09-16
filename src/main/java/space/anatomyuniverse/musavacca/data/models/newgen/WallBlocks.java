package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;

public final class WallBlocks {
    private WallBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<WallModels, Part> {
        private Part(WallModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(WallModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(WallModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public WallModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Set textures;
        private final WallModels baseModels;

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

        public WallModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof WallBlock)) {
                throw new IllegalStateException(
                        "WallBlocks requires a WallBlock: " + ModelLocations.blockId(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), BlockStateProperties.UP, "WallBlocks");
            BlockFamilyValidation.requireProperty(block(), BlockStateProperties.NORTH_WALL, "WallBlocks");
            BlockFamilyValidation.requireProperty(block(), BlockStateProperties.EAST_WALL, "WallBlocks");
            BlockFamilyValidation.requireProperty(block(), BlockStateProperties.SOUTH_WALL, "WallBlocks");
            BlockFamilyValidation.requireProperty(block(), BlockStateProperties.WEST_WALL, "WallBlocks");

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
                            "Base WallModels must provide the complete in-world family for "
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
            private WallModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(WallModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").source().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").source(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").source(texture).build();
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

