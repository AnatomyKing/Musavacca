package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class DoorBlocks {
    private DoorBlocks() {}

    public enum BaseMode {
        GENERATED,
        EXISTING
    }

    public static final class Part {
        private final DoorModels models;
        private final Conditions.Match conditions;

        private int rotationX;
        private int rotationY;
        private Tints.Tint tint = Tints.none();
        private Variants.Set variants = Variants.single();

        private Part(DoorModels models, Conditions.Match conditions) {
            this.models = Objects.requireNonNull(models, "models");
            this.conditions = Objects.requireNonNull(conditions, "conditions");
        }

        public static Part always(DoorModels models) {
            return new Part(models, Conditions.always());
        }

        public static Part when(DoorModels models, Conditions.Match conditions) {
            return new Part(models, conditions);
        }

        public Part rotateX(int degrees) {
            this.rotationX = normalize(degrees);
            return this;
        }

        public Part rotateY(int degrees) {
            this.rotationY = normalize(degrees);
            return this;
        }

        public Part tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");
            return this;
        }

        public Part variants(Variants.Set variants) {
            this.variants = Objects.requireNonNull(variants, "variants");
            return this;
        }

        public DoorModels models() {
            return models;
        }

        public Conditions.Match conditions() {
            return conditions;
        }

        public int rotationX() {
            return rotationX;
        }

        public int rotationY() {
            return rotationY;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Variants.Set variants() {
            return variants;
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
            this.inferSingleTexture = true;
            this.textureTokens = List.of();
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

            this.inferSingleTexture = false;
            this.textureTokens = List.copyOf(copy);
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
            return textureTokens == null
                    ? List.of()
                    : textureTokens;
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
                        "No textures selected for door item " + itemId()
                                + ". Call .texture() or .textures(...)."
                );
            }

            int layerCount = inferSingleTexture
                    ? 1
                    : textureTokens.size();

            for (Integer layer : layerTints.keySet()) {
                if (layer >= layerCount) {
                    throw new IllegalStateException(
                            "Tint layer " + layer + " is outside the " + layerCount
                                    + " generated layers for door item " + itemId()
                    );
                }
            }
        }

        private ResourceLocation itemId() {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.asItem());

            if (id == null) {
                throw new IllegalStateException("Cannot use an unregistered door item");
            }

            return id;
        }
    }

    public static final class Entry {
        private final Block block;
        private final BaseMode baseMode;
        private final DoorTextures.Set textures;
        private final DoorModels baseModels;
        private final List<Part> parts;
        private final List<Item> items;
        private final int rotationX;
        private final int rotationY;
        private final Variants.Set variants;

        private Entry(Builder builder) {
            this.block = builder.block;
            this.baseMode = builder.baseMode;
            this.textures = builder.textures;
            this.baseModels = builder.baseModels;
            this.parts = List.copyOf(builder.parts);
            this.items = List.copyOf(builder.items);
            this.rotationX = builder.rotationX;
            this.rotationY = builder.rotationY;
            this.variants = builder.variants;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public Block block() {
            return block;
        }

        public BaseMode baseMode() {
            return baseMode;
        }

        public DoorTextures.Set textures() {
            return textures;
        }

        public DoorModels baseModels() {
            return baseModels;
        }

        public List<Part> parts() {
            return Collections.unmodifiableList(parts);
        }

        public List<Item> items() {
            return Collections.unmodifiableList(items);
        }

        public int rotationX() {
            return rotationX;
        }

        public int rotationY() {
            return rotationY;
        }

        public Variants.Set variants() {
            return variants;
        }

        public void validate() {
            if (!(block instanceof DoorBlock)) {
                throw new IllegalStateException(
                        "DoorBlocks requires a DoorBlock: " + ModelUtil.idOf(block)
                );
            }

            requireDoorProperty(DoorBlock.FACING);
            requireDoorProperty(DoorBlock.HALF);
            requireDoorProperty(DoorBlock.HINGE);
            requireDoorProperty(DoorBlock.OPEN);

            if (baseMode == null) {
                throw new IllegalStateException("No base door model selected for " + ModelUtil.idOf(block));
            }

            if (baseMode == BaseMode.GENERATED && textures == null) {
                throw new IllegalStateException("No generated door textures configured for " + ModelUtil.idOf(block));
            }

            if (baseMode == BaseMode.EXISTING && baseModels == null) {
                throw new IllegalStateException("No existing DoorModels configured for " + ModelUtil.idOf(block));
            }

            for (Part part : parts) {
                validateConditions(part.conditions());
            }

            for (Item item : items) {
                item.validate();
            }
        }

        private void validateConditions(Conditions.Match conditions) {
            for (Conditions.Term<?> term : conditions.terms()) {
                if (!block.defaultBlockState().hasProperty(term.property())) {
                    throw new IllegalStateException(
                            "Condition property " + term.property()
                                    + " is not present on " + ModelUtil.idOf(block)
                    );
                }
            }
        }

        private <T extends Comparable<T>> void requireDoorProperty(Property<T> property) {
            if (!block.defaultBlockState().hasProperty(property)) {
                throw new IllegalStateException(
                        "DoorBlocks requires property " + property.getName()
                                + " on " + ModelUtil.idOf(block)
                );
            }
        }

        public static final class Builder {
            private final Block block;
            private BaseMode baseMode;
            private DoorTextures.Set textures;
            private DoorModels baseModels;
            private final List<Part> parts = new ArrayList<>();
            private final List<Item> items = new ArrayList<>();
            private int rotationX;
            private int rotationY;
            private Variants.Set variants = Variants.single();

            private Builder(Block block) {
                this.block = Objects.requireNonNull(block, "block");
            }

            public Builder generated() {
                if (baseMode == BaseMode.EXISTING) {
                    throw new IllegalStateException("Cannot use .generated() after .models(...)");
                }

                this.baseMode = BaseMode.GENERATED;

                if (textures == null) {
                    this.textures = DoorTextures.builder(block).build();
                }

                return this;
            }

            public Builder models(DoorModels models) {
                if (baseMode == BaseMode.GENERATED) {
                    throw new IllegalStateException("Cannot use .models(...) after .generated()");
                }

                this.baseMode = BaseMode.EXISTING;
                this.baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder textures(Consumer<DoorTextures.Builder> textures) {
                Objects.requireNonNull(textures, "textures");

                DoorTextures.Builder builder = DoorTextures.builder(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Builder multipart(Part... parts) {
                if (parts == null) {
                    throw new IllegalArgumentException("parts must not be null");
                }

                for (Part part : parts) {
                    this.parts.add(Objects.requireNonNull(part, "part"));
                }

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

            public Builder rotateX(int degrees) {
                this.rotationX = normalize(degrees);
                return this;
            }

            public Builder rotateY(int degrees) {
                this.rotationY = normalize(degrees);
                return this;
            }

            public Builder variants(Variants.Set variants) {
                this.variants = Objects.requireNonNull(variants, "variants");
                return this;
            }

            public Entry build() {
                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }

    static int normalize(int degrees) {
        int value = Math.floorMod(degrees, 360);

        if (!Arrays.asList(0, 90, 180, 270).contains(value)) {
            throw new IllegalArgumentException(
                    "Only 0/90/180/270 door rotations are supported: " + degrees
            );
        }

        return value;
    }
}
