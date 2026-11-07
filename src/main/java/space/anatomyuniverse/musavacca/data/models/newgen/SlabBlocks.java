package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

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
        private final FamilyTextures.Faces textures;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public FamilyTextures.Faces textures() {
            return textures;
        }

        public void validate() {
            if (!(block() instanceof SlabBlock)) throw new IllegalStateException("SlabBlocks requires a SlabBlock: " + ModelLocations.blockId(block()));
            Conditions.requireProperty(block(), SlabBlock.TYPE, "SlabBlocks");
            if (!generated()) throw new IllegalStateException("No base model selected for " + ModelLocations.blockId(block()));
            if (textures == null) {
                throw new IllegalStateException("No generated textures configured for " + ModelLocations.blockId(block()));
            }
            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Faces textures;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.faces(block, "_slab").all().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.faces(block, "_slab").all(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.faces(block, "_slab").all(texture).build();
                return this;
            }

            public Builder textures(Consumer<FamilyTextures.FacesBuilder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                FamilyTextures.FacesBuilder builder = FamilyTextures.faces(block, "_slab");
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
