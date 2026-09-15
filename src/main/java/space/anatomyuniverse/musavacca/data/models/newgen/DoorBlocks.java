package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public static final class Item {
        private final ItemLike item;
        private List<String> textureTokens;
        private boolean inferSingleTexture;
        private final Map<Integer, Tints.Tint> layerTints = new LinkedHashMap<>();

        private Item(ItemLike item) {
            this.item = Objects.requireNonNull(item, "item");
        }

        public static Item generated(ItemLike item) {
            return new Item(item);
        }

        public Item texture() {
            inferSingleTexture = true;
            textureTokens = List.of();
            return this;
        }

        public Item textures(String... textures) {
            if (textures == null || textures.length == 0) {
                throw new IllegalArgumentException("Door item textures must not be empty");
            }

            List<String> copy = new ArrayList<>(textures.length);

            for (String texture : textures) {
                if (texture == null || texture.isBlank()) {
                    throw new IllegalArgumentException("Door item texture must not be blank");
                }

                copy.add(texture);
            }

            inferSingleTexture = false;
            textureTokens = List.copyOf(copy);
            return this;
        }

        public Item layerTint(int layer, Tints.Tint tint) {
            if (layer < 0) {
                throw new IllegalArgumentException("layer must be >= 0");
            }

            layerTints.put(layer, Objects.requireNonNull(tint, "tint"));
            return this;
        }

        public ItemLike item() {
            return item;
        }

        public List<String> textureTokens() {
            return textureTokens == null ? List.of() : textureTokens;
        }

        public boolean inferSingleTexture() {
            return inferSingleTexture;
        }

        public Map<Integer, Tints.Tint> layerTints() {
            return Collections.unmodifiableMap(layerTints);
        }

        public Tints.Tint tintForLayer(int layer) {
            return layerTints.getOrDefault(layer, Tints.none());
        }

        public void validate() {
            if (textureTokens == null) {
                throw new IllegalStateException(
                        "No textures selected for door item " + ModelLocations.itemId(item)
                                + ". Call .texture() or .textures(...)."
                );
            }

            int layerCount = inferSingleTexture ? 1 : textureTokens.size();

            for (Integer layer : layerTints.keySet()) {
                if (layer >= layerCount) {
                    throw new IllegalStateException(
                            "Tint layer " + layer + " is outside the " + layerCount
                                    + " generated layers for door item " + ModelLocations.itemId(item)
                    );
                }
            }
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final DoorTextures.Set textures;
        private final DoorModels baseModels;
        private final List<Item> items;

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

        public List<Item> items() {
            return items;
        }

        public void validate() {
            if (!(block() instanceof DoorBlock)) {
                throw new IllegalStateException(
                        "DoorBlocks requires a DoorBlock: " + ModelUtil.idOf(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), DoorBlock.FACING, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.HALF, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.HINGE, "DoorBlocks");
            BlockFamilyValidation.requireProperty(block(), DoorBlock.OPEN, "DoorBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base door model selected for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated door textures configured for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING && baseModels == null) {
                throw new IllegalStateException(
                        "No existing DoorModels configured for " + ModelUtil.idOf(block())
                );
            }

            validatePartConditions();

            for (Item item : items) {
                item.validate();
            }
        }

        public static final class Builder extends BlockFamily.StateFamilyBuilder<Builder, Part> {
            private DoorTextures.Set textures;
            private DoorModels baseModels;
            private final List<Item> items = new ArrayList<>();

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

            public Builder items(Item... items) {
                if (items == null) {
                    throw new IllegalArgumentException("items must not be null");
                }

                for (Item item : items) {
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
