package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class PressurePlateBlocks {
    private PressurePlateBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<PressurePlateModels, Part> {
        private Part(PressurePlateModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(PressurePlateModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(PressurePlateModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public PressurePlateModels models() {
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
            if (!(block() instanceof BasePressurePlateBlock)) {
                throw new IllegalStateException("PressurePlateBlocks requires a BasePressurePlateBlock: " + ModelLocations.blockId(block()));
            }
            if (!block().defaultBlockState().hasProperty(PressurePlateBlock.POWERED)) {
                Conditions.requireProperty(block(), BlockStateProperties.POWER, "PressurePlateBlocks");
            }
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
                textures = FamilyTextures.builder(block, "_pressure_plate").texture().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_pressure_plate").texture(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_pressure_plate").texture(texture).build();
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
