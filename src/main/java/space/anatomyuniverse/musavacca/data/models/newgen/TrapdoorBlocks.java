package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class TrapdoorBlocks {
    private TrapdoorBlocks() {}

    public enum BaseMode {
        GENERATED,
        EXISTING
    }

    public enum ItemMode {
        DEFAULT,
        EXISTING,
        NONE
    }

    public static final class Part {
        private final TrapdoorModels models;
        private final Conditions.Match conditions;

        private int rotationX;
        private int rotationY;
        private Tints.Tint tint = Tints.none();
        private Variants.Set variants = Variants.single();

        private Part(TrapdoorModels models, Conditions.Match conditions) {
            this.models = Objects.requireNonNull(models, "models");
            this.conditions = Objects.requireNonNull(conditions, "conditions");
        }

        public static Part always(TrapdoorModels models) {
            return new Part(models, Conditions.always());
        }

        public static Part when(TrapdoorModels models, Conditions.Match conditions) {
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

        public TrapdoorModels models() {
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

    public static final class Entry {
        private final Block block;
        private final BaseMode baseMode;
        private final TrapdoorTextures.Set textures;
        private final TrapdoorModels baseModels;
        private final List<Part> parts;
        private final int rotationX;
        private final int rotationY;
        private final Variants.Set variants;
        private final ItemMode itemMode;
        private final String itemModel;

        private Entry(Builder builder) {
            this.block = builder.block;
            this.baseMode = builder.baseMode;
            this.textures = builder.textures;
            this.baseModels = builder.baseModels;
            this.parts = List.copyOf(builder.parts);
            this.rotationX = builder.rotationX;
            this.rotationY = builder.rotationY;
            this.variants = builder.variants;
            this.itemMode = builder.itemMode;
            this.itemModel = builder.itemModel;
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

        public TrapdoorTextures.Set textures() {
            return textures;
        }

        public TrapdoorModels baseModels() {
            return baseModels;
        }

        public List<Part> parts() {
            return Collections.unmodifiableList(parts);
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

        public ItemMode itemMode() {
            return itemMode;
        }

        public String itemModel() {
            return itemModel;
        }

        public void validate() {
            if (!(block instanceof TrapDoorBlock)) {
                throw new IllegalStateException(
                        "TrapdoorBlocks requires a TrapDoorBlock: " + ModelUtil.idOf(block)
                );
            }

            requireTrapdoorProperty(TrapDoorBlock.FACING);
            requireTrapdoorProperty(TrapDoorBlock.HALF);
            requireTrapdoorProperty(TrapDoorBlock.OPEN);

            if (baseMode == null) {
                throw new IllegalStateException(
                        "No base trapdoor model selected for " + ModelUtil.idOf(block)
                );
            }

            if (baseMode == BaseMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated trapdoor texture configured for " + ModelUtil.idOf(block)
                );
            }

            if (baseMode == BaseMode.EXISTING && baseModels == null) {
                throw new IllegalStateException(
                        "No existing TrapdoorModels configured for " + ModelUtil.idOf(block)
                );
            }

            if (itemMode == null) {
                throw new IllegalStateException(
                        "No trapdoor item behavior selected for " + ModelUtil.idOf(block)
                                + ". Call .item(), .item(...), or .noItem()."
                );
            }

            for (Part part : parts) {
                validateConditions(part.conditions());
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

        private <T extends Comparable<T>> void requireTrapdoorProperty(Property<T> property) {
            if (!block.defaultBlockState().hasProperty(property)) {
                throw new IllegalStateException(
                        "TrapdoorBlocks requires property " + property.getName()
                                + " on " + ModelUtil.idOf(block)
                );
            }
        }

        public static final class Builder {
            private final Block block;
            private BaseMode baseMode;
            private TrapdoorTextures.Set textures;
            private TrapdoorModels baseModels;
            private final List<Part> parts = new ArrayList<>();
            private int rotationX;
            private int rotationY;
            private Variants.Set variants = Variants.single();
            private ItemMode itemMode;
            private String itemModel;

            private Builder(Block block) {
                this.block = Objects.requireNonNull(block, "block");
            }

            public Builder generated() {
                if (baseMode == BaseMode.EXISTING) {
                    throw new IllegalStateException("Cannot use .generated() after .models(...)");
                }

                this.baseMode = BaseMode.GENERATED;

                if (textures == null) {
                    this.textures = TrapdoorTextures.builder(block).build();
                }

                return this;
            }

            public Builder models(TrapdoorModels models) {
                if (baseMode == BaseMode.GENERATED) {
                    throw new IllegalStateException("Cannot use .models(...) after .generated()");
                }

                this.baseMode = BaseMode.EXISTING;
                this.baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                this.textures = TrapdoorTextures.builder(block)
                        .texture()
                        .build();
                return this;
            }

            public Builder texture(String texture) {
                this.textures = TrapdoorTextures.builder(block)
                        .texture(texture)
                        .build();
                return this;
            }

            public Builder textures(Consumer<TrapdoorTextures.Builder> textures) {
                Objects.requireNonNull(textures, "textures");

                TrapdoorTextures.Builder builder = TrapdoorTextures.builder(block);
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

            public Builder item() {
                if (itemMode == ItemMode.NONE) {
                    throw new IllegalStateException("Cannot use .item() after .noItem()");
                }

                this.itemMode = ItemMode.DEFAULT;
                this.itemModel = null;
                return this;
            }

            public Builder item(String model) {
                if (itemMode == ItemMode.NONE) {
                    throw new IllegalStateException("Cannot use .item(...) after .noItem()");
                }

                if (model == null || model.isBlank()) {
                    throw new IllegalArgumentException("item model must not be blank");
                }

                this.itemMode = ItemMode.EXISTING;
                this.itemModel = model;
                return this;
            }

            public Builder noItem() {
                if (itemMode != null && itemMode != ItemMode.NONE) {
                    throw new IllegalStateException("Cannot use .noItem() after .item(...)");
                }

                this.itemMode = ItemMode.NONE;
                this.itemModel = null;
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
                    "Only 0/90/180/270 trapdoor rotations are supported: " + degrees
            );
        }

        return value;
    }
}
