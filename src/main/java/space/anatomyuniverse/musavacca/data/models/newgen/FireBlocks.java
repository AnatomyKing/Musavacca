package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class FireBlocks {
    private FireBlocks() {}

    public static final class Entry {
        private final Block block;
        private final FireTextures.Set textures;
        private final Tints.Tint tint;
        private final Tints.Tint itemTint;
        private final Variants.Set variants;
        private final String itemModel;
        private final boolean noItem;

        private Entry(Builder builder) {
            this.block = builder.block;
            this.textures = builder.textures;
            this.tint = builder.tint;
            this.itemTint = builder.itemTint;
            this.variants = builder.variants;
            this.itemModel = builder.itemModel;
            this.noItem = builder.noItem;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public Block block() {
            return block;
        }

        public FireTextures.Set textures() {
            return textures;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Tints.Tint itemTint() {
            return itemTint != null ? itemTint : tint;
        }

        public Variants.Set variants() {
            return variants;
        }

        public String itemModel() {
            return itemModel;
        }

        public boolean noItem() {
            return noItem;
        }

        public void validate() {
            if (textures == null) {
                throw new IllegalStateException("No texture configured for " + ModelUtil.idOf(block));
            }

            if (!block.defaultBlockState().hasProperty(FireBlock.NORTH)
                    || !block.defaultBlockState().hasProperty(FireBlock.EAST)
                    || !block.defaultBlockState().hasProperty(FireBlock.SOUTH)
                    || !block.defaultBlockState().hasProperty(FireBlock.WEST)
                    || !block.defaultBlockState().hasProperty(FireBlock.UP)) {
                throw new IllegalStateException(
                        "FireBlocks requires the vanilla fire attachment properties on " + ModelUtil.idOf(block)
                );
            }
        }

        public static final class Builder {
            private final Block block;
            private boolean generated;
            private FireTextures.Set textures;
            private Tints.Tint tint = Tints.none();
            private Tints.Tint itemTint;
            private Variants.Set variants = Variants.single();
            private String itemModel;
            private boolean noItem;

            private Builder(Block block) {
                this.block = Objects.requireNonNull(block, "block");
            }

            public Builder generated() {
                this.generated = true;
                return this;
            }

            public Builder texture() {
                this.textures = FireTextures.builder(block)
                        .texture()
                        .build();
                return this;
            }

            public Builder texture(String texture) {
                this.textures = FireTextures.builder(block)
                        .texture(texture)
                        .build();
                return this;
            }

            public Builder textures(Consumer<FireTextures.Builder> textures) {
                FireTextures.Builder builder = FireTextures.builder(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Builder tint(Tints.Tint tint) {
                this.tint = Objects.requireNonNull(tint, "tint");
                return this;
            }

            public Builder itemTint(Tints.Tint tint) {
                this.itemTint = Objects.requireNonNull(tint, "tint");
                return this;
            }

            public Builder variants(Variants.Set variants) {
                this.variants = Objects.requireNonNull(variants, "variants");
                return this;
            }

            public Builder item(String model) {
                if (noItem) {
                    throw new IllegalStateException("Cannot use .item(...) after .noItem()");
                }
                this.itemModel = Objects.requireNonNull(model, "model");
                return this;
            }

            public Builder noItem() {
                if (itemModel != null) {
                    throw new IllegalStateException("Cannot use .noItem() after .item(...)");
                }
                this.noItem = true;
                return this;
            }

            public Entry build() {
                if (!generated) {
                    throw new IllegalStateException(
                            "FireBlocks currently represents generated Minecraft fire geometry; call .generated() for "
                                    + ModelUtil.idOf(block)
                    );
                }

                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}
