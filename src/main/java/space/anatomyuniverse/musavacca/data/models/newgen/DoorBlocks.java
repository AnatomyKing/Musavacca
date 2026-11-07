package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

public final class DoorBlocks {
    private DoorBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<DoorModels, Part> {
        private Part(DoorModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(DoorModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(DoorModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public DoorModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Halves textures;
        private final List<SimpleItems.Entry> items;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            items = List.copyOf(builder.items);
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public FamilyTextures.Halves textures() {
            return textures;
        }

        public List<SimpleItems.Entry> items() {
            return items;
        }

        public void validate() {
            if (!(block() instanceof DoorBlock)) throw new IllegalStateException("DoorBlocks requires a DoorBlock: " + ModelLocations.blockId(block()));
            Conditions.requireProperty(block(), DoorBlock.FACING, "DoorBlocks");
            Conditions.requireProperty(block(), DoorBlock.HALF, "DoorBlocks");
            Conditions.requireProperty(block(), DoorBlock.HINGE, "DoorBlocks");
            Conditions.requireProperty(block(), DoorBlock.OPEN, "DoorBlocks");
            if (!generated()) throw new IllegalStateException("No base door model selected for " + ModelLocations.blockId(block()));
            if (textures == null) {
                throw new IllegalStateException("No generated door textures configured for " + ModelLocations.blockId(block()));
            }
            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.StateFamilyBuilder<Builder, Part> {
            private FamilyTextures.Halves textures;
            private final List<SimpleItems.Entry> items = new ArrayList<>();

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                if (textures == null) {
                    textures = FamilyTextures.halves(block).build();
                }
                return this;
            }

            public Builder textures(Consumer<FamilyTextures.HalvesBuilder> textures) {
                if (!generated) throw new IllegalStateException("textures(...) requires .generated() first");
                Objects.requireNonNull(textures, "textures");
                FamilyTextures.HalvesBuilder builder = FamilyTextures.halves(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Builder items(SimpleItems.Entry... items) {
                if (items == null) throw new IllegalArgumentException("items must not be null");
                for (SimpleItems.Entry item : items) {
                    this.items.add(Objects.requireNonNull(item, "item"));
                }
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
