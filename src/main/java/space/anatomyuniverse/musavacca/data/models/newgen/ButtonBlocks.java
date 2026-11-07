package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;

public final class ButtonBlocks {
    private ButtonBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<ButtonModels, Part> {
        private Part(ButtonModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(ButtonModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(ButtonModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public ButtonModels models() {
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
            if (!(block() instanceof ButtonBlock)) throw new IllegalStateException("ButtonBlocks requires a ButtonBlock: " + ModelLocations.blockId(block()));
            Conditions.requireProperty(block(), ButtonBlock.FACE, "ButtonBlocks");
            Conditions.requireProperty(block(), ButtonBlock.FACING, "ButtonBlocks");
            Conditions.requireProperty(block(), ButtonBlock.POWERED, "ButtonBlocks");
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
                textures = FamilyTextures.builder(block, "_button").texture().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_button").texture(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_button").texture(texture).build();
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
