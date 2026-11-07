package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;

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
            if (!(block() instanceof StairBlock)) throw new IllegalStateException("StairBlocks requires a StairBlock: " + ModelLocations.blockId(block()));
            Conditions.requireProperty(block(), StairBlock.FACING, "StairBlocks");
            Conditions.requireProperty(block(), StairBlock.HALF, "StairBlocks");
            Conditions.requireProperty(block(), StairBlock.SHAPE, "StairBlocks");
            if (!generated()) throw new IllegalStateException("No base stair model selected for " + ModelLocations.blockId(block()));
            if (textures == null) {
                throw new IllegalStateException("No generated stair textures configured for " + ModelLocations.blockId(block()));
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
                textures = FamilyTextures.faces(block, "_stairs").all().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.faces(block, "_stairs").all(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.faces(block, "_stairs").all(texture).build();
                return this;
            }

            public Builder textures(Consumer<FamilyTextures.FacesBuilder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                FamilyTextures.FacesBuilder builder = FamilyTextures.faces(block, "_stairs");
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
