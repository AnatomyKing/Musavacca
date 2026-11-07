package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;

public final class TrapdoorBlocks {
    private TrapdoorBlocks() {}

    public enum Orientation {
        NORMAL, ORIENTABLE
    }

    public static final class Part extends BlockFamily.ModelRule<TrapdoorModels, Part> {
        private Part(TrapdoorModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(TrapdoorModels source) { return new Part(source, Conditions.always()); }

        public static Part when(TrapdoorModels source, Conditions.Match conditions) { return new Part(source, conditions); }

        public TrapdoorModels models() { return source(); }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Set textures;
        private final Orientation orientation;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            orientation = builder.orientation;
        }

        public static Builder builder(Block block) { return new Builder(block); }

        public FamilyTextures.Set textures() { return textures; }

        public Orientation orientation() { return orientation; }

        public void validate() {
            if (!(block() instanceof TrapDoorBlock)) {
                throw new IllegalStateException("TrapdoorBlocks requires a TrapDoorBlock: " + ModelLocations.blockId(block()));
            }
            Conditions.requireProperty(block(), TrapDoorBlock.FACING, "TrapdoorBlocks");
            Conditions.requireProperty(block(), TrapDoorBlock.HALF, "TrapdoorBlocks");
            Conditions.requireProperty(block(), TrapDoorBlock.OPEN, "TrapdoorBlocks");
            if (!generated()) throw new IllegalStateException("No base trapdoor model selected for " + ModelLocations.blockId(block()));
            if (textures == null) {
                throw new IllegalStateException("No generated trapdoor texture configured for " + ModelLocations.blockId(block()));
            }
            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Set textures;
            private Orientation orientation = Orientation.NORMAL;

            private Builder(Block block) { super(block); }

            public Builder generated() {
                selectGenerated();
                if (textures == null) textures = FamilyTextures.builder(block).build();
                return this;
            }

            public Builder orientable() {
                orientation = Orientation.ORIENTABLE;
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block).texture().build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block).texture(texture).build();
                return this;
            }

            public Builder textures(Consumer<FamilyTextures.Builder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                FamilyTextures.Builder builder = FamilyTextures.builder(block);
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
