package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

import java.util.Objects;
import java.util.function.Consumer;

public final class FireBlocks {
    private FireBlocks() {}

    public static final class Entry {
        private final Block block;
        private final FireTextures.Set textures;
        private final Tints.Tint tint;
        private final Variants.Set variants;
        private final FamilyItemMode itemMode;
        private final SimpleItems.Model item;

        private Entry(Builder builder) {
            block = builder.block;
            textures = builder.textures;
            tint = builder.tint;
            variants = builder.variants;
            itemMode = builder.itemMode;
            item = builder.item == null ? null : builder.item.copy();
        }

        public static Builder builder(Block block) { return new Builder(block); }
        public Block block() { return block; }
        public FireTextures.Set textures() { return textures; }
        public Tints.Tint tint() { return tint; }
        public Variants.Set variants() { return variants; }
        FamilyItemMode itemMode() { return itemMode; }
        SimpleItems.Model item() { return item; }

        public void validate() {
            if (textures == null) {
                throw new IllegalStateException("No texture configured for " + ModelLocations.blockId(block));
            }
            BlockFamilyValidation.requireProperty(block, FireBlock.NORTH, "FireBlocks");
            BlockFamilyValidation.requireProperty(block, FireBlock.EAST, "FireBlocks");
            BlockFamilyValidation.requireProperty(block, FireBlock.SOUTH, "FireBlocks");
            BlockFamilyValidation.requireProperty(block, FireBlock.WEST, "FireBlocks");
            BlockFamilyValidation.requireProperty(block, FireBlock.UP, "FireBlocks");
        }

        public static final class Builder {
            private final Block block;
            private boolean generated;
            private FireTextures.Set textures;
            private Tints.Tint tint = Tints.none();
            private Variants.Set variants = Variants.single();
            private FamilyItemMode itemMode = FamilyItemMode.DEFAULT;
            private SimpleItems.Model item;
            private boolean explicitItem;

            private Builder(Block block) { this.block = Objects.requireNonNull(block, "block"); }

            public Builder generated() { generated = true; return this; }

            public Builder texture() {
                requireGeneratedTextures();
                textures = FireTextures.builder(block).texture().build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FireTextures.builder(block).texture(texture).build();
                return this;
            }

            public Builder textures(Consumer<FireTextures.Builder> textures) {
                requireGeneratedTextures();
                Objects.requireNonNull(textures, "textures");
                FireTextures.Builder builder = FireTextures.builder(block);
                textures.accept(builder);
                this.textures = builder.build();
                return this;
            }

            public Builder tint(Tints.Tint tint) { this.tint = Objects.requireNonNull(tint, "tint"); return this; }
            public Builder variants(Variants.Set variants) { this.variants = Objects.requireNonNull(variants, "variants"); return this; }

            public Builder item() {
                requireItem();
                itemMode = FamilyItemMode.DEFAULT;
                item = null;
                return this;
            }

            public Builder item(String model) { return item(SimpleItems.Model.existing(model)); }

            public Builder item(SimpleItems.Model model) {
                requireItem();
                itemMode = FamilyItemMode.CUSTOM;
                item = Objects.requireNonNull(model, "model").copy();
                return this;
            }

            public Builder noItem() {
                if (explicitItem) throw new IllegalStateException("Cannot use .noItem() after .item(...)");
                itemMode = FamilyItemMode.NONE;
                item = null;
                return this;
            }

            private void requireItem() {
                if (itemMode == FamilyItemMode.NONE) throw new IllegalStateException("Cannot use .item(...) after .noItem()");
                explicitItem = true;
            }

            private void requireGeneratedTextures() {
                if (!generated) throw new IllegalStateException("texture(s) requires .generated() first");
            }

            public Entry build() {
                if (!generated) {
                    throw new IllegalStateException(
                            "FireBlocks currently represents generated Minecraft fire geometry; call .generated() for "
                                    + ModelLocations.blockId(block)
                    );
                }
                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}

