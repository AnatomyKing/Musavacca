package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
        private final DoorTextures.Set textures;
        private final DoorModels baseModels;
        private final List<SimpleItems.Entry> items;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
            items = List.copyOf(builder.items);
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public DoorTextures.Set textures() {
            return textures;
        }

        public DoorModels baseModels() {
            return baseModels;
        }

        public List<SimpleItems.Entry> items() {
            return items;
        }

        public void validate() {
            if (!(block() instanceof DoorBlock)) {
                throw new IllegalStateException(
                        "DoorBlocks requires a DoorBlock: " + ModelLocations.blockId(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), DoorBlock.FACING, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.HALF, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.HINGE, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.OPEN, "DoorBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base door model selected for " + ModelLocations.blockId(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated door textures configured for " + ModelLocations.blockId(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING && baseModels == null) {
                throw new IllegalStateException(
                        "No existing DoorModels configured for " + ModelLocations.blockId(block())
                );
            }

            validatePartConditions();

        }

        public static final class Builder extends BlockFamily.StateFamilyBuilder<Builder, Part> {
            private DoorTextures.Set textures;
            private DoorModels baseModels;
            private final List<SimpleItems.Entry> items = new ArrayList<>();

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();

                if (textures == null) {
                    textures = DoorTextures.builder(block).build();
                }

                return this;
            }

            public Builder models(DoorModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder textures(Consumer<DoorTextures.Builder> textures) {
                if (baseMode != BaseModelMode.GENERATED) {
                    throw new IllegalStateException("textures(...) requires .generated() first");
                }

                Objects.requireNonNull(textures, "textures");

                DoorTextures.Builder builder = DoorTextures.builder(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Builder items(SimpleItems.Entry... items) {
                if (items == null) {
                    throw new IllegalArgumentException("items must not be null");
                }

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

