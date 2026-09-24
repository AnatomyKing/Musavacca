package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.Objects;
import java.util.function.Consumer;

public final class TrapdoorBlocks {
    private TrapdoorBlocks() {}

    public enum Orientation { NORMAL, ORIENTABLE }

    public static final class Part extends BlockFamily.ModelRule<TrapdoorModels, Part> {
        private Part(TrapdoorModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(TrapdoorModels source) { return new Part(source, Conditions.always()); }
        public static Part when(TrapdoorModels source, Conditions.Match conditions) { return new Part(source, conditions); }
        public TrapdoorModels models() { return source(); }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final TrapdoorTextures.Set textures;
        private final TrapdoorModels baseModels;
        private final Orientation orientation;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
            orientation = builder.orientation;
        }

        public static Builder builder(Block block) { return new Builder(block); }
        public TrapdoorTextures.Set textures() { return textures; }
        public TrapdoorModels baseModels() { return baseModels; }
        public Orientation orientation() { return orientation; }

        public void validate() {
            if (!(block() instanceof TrapDoorBlock)) {
                throw new IllegalStateException(
                        "TrapdoorBlocks requires a TrapDoorBlock: " + ModelLocations.blockId(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.FACING, "TrapdoorBlocks");
            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.HALF, "TrapdoorBlocks");
            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.OPEN, "TrapdoorBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException("No base trapdoor model selected for " + ModelLocations.blockId(block()));
            }
            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException("No generated trapdoor texture configured for " + ModelLocations.blockId(block()));
            }
            if (baseMode() == BaseModelMode.EXISTING && baseModels == null) {
                throw new IllegalStateException("No existing TrapdoorModels configured for " + ModelLocations.blockId(block()));
            }

            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private TrapdoorTextures.Set textures;
            private TrapdoorModels baseModels;
            private Orientation orientation = Orientation.NORMAL;

            private Builder(Block block) { super(block); }

            public Builder generated() {
                selectGenerated();
                if (textures == null) textures = TrapdoorTextures.builder(block).build();
                return this;
            }

            public Builder models(TrapdoorModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder normal() {
                orientation = Orientation.NORMAL;
                return this;
            }

            public Builder orientable() {
                orientation = Orientation.ORIENTABLE;
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = TrapdoorTextures.builder(block).texture().build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = TrapdoorTextures.builder(block).texture(texture).build();
                return this;
            }

            public Builder textures(Consumer<TrapdoorTextures.Builder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                TrapdoorTextures.Builder builder = TrapdoorTextures.builder(block);
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
