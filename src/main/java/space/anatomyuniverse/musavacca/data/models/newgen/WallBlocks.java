package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public FamilyTextures.Set textures() {
            return textures;
        }

        public void validate() {
            if (!(block() instanceof WallBlock)) throw new IllegalStateException("WallBlocks requires a WallBlock: " + ModelLocations.blockId(block()));
            Conditions.requireProperty(block(), BlockStateProperties.UP, "WallBlocks");
            Conditions.requireProperty(block(), BlockStateProperties.NORTH_WALL, "WallBlocks");
            Conditions.requireProperty(block(), BlockStateProperties.EAST_WALL, "WallBlocks");
            Conditions.requireProperty(block(), BlockStateProperties.SOUTH_WALL, "WallBlocks");
            Conditions.requireProperty(block(), BlockStateProperties.WEST_WALL, "WallBlocks");
            if (!generated()) throw new IllegalStateException("No base model selected for " + ModelLocations.blockId(block()));
            if (textures == null) {
                throw new IllegalStateException("No generated textures configured for " + ModelLocations.blockId(block()));
            }
            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Set textures;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").texture().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").texture(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_wall").texture(texture).build();
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
