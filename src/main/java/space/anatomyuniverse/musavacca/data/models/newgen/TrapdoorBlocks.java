package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class TrapdoorBlocks {
    private TrapdoorBlocks() {}

    public enum ItemMode {
        DEFAULT,
        EXISTING,
        NONE
    }

    public static final class Part extends BlockFamily.ModelRule<TrapdoorModels, Part> {
        private Part(TrapdoorModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(TrapdoorModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(TrapdoorModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public TrapdoorModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final TrapdoorTextures.Set textures;
        private final TrapdoorModels baseModels;
        private final ItemMode itemMode;
        private final String itemModel;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
            itemMode = builder.itemMode;
            itemModel = builder.itemModel;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public TrapdoorTextures.Set textures() {
            return textures;
        }

        public TrapdoorModels baseModels() {
            return baseModels;
        }

        public ItemMode itemMode() {
            return itemMode;
        }

        public String itemModel() {
            return itemModel;
        }

        public void validate() {
            if (!(block() instanceof TrapDoorBlock)) {
                throw new IllegalStateException(
                        "TrapdoorBlocks requires a TrapDoorBlock: " + ModelUtil.idOf(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.FACING, "TrapdoorBlocks");
            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.HALF, "TrapdoorBlocks");
            BlockFamilyValidation.requireProperty(block(), TrapDoorBlock.OPEN, "TrapdoorBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base trapdoor model selected for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated trapdoor texture configured for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING && baseModels == null) {
                throw new IllegalStateException(
                        "No existing TrapdoorModels configured for " + ModelUtil.idOf(block())
                );
            }


            validatePartConditions();
        }

        public static final class Builder extends BlockFamily.StateFamilyBuilder<Builder, Part> {
            private TrapdoorTextures.Set textures;
            private TrapdoorModels baseModels;
            private ItemMode itemMode = ItemMode.DEFAULT;
            private boolean explicitItem;
            private String itemModel;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();

                if (textures == null) {
                    textures = TrapdoorTextures.builder(block).build();
                }

                return this;
            }

            public Builder models(TrapdoorModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }

            public Builder texture() {
                requireGeneratedTextures();
                textures = TrapdoorTextures.builder(block)
                        .texture()
                        .build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = TrapdoorTextures.builder(block)
                        .texture(texture)
                        .build();
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


            public Builder item() {
                if (itemMode == ItemMode.NONE) {
                    throw new IllegalStateException("Cannot use .item() after .noItem()");
                }

                explicitItem = true;
                itemMode = ItemMode.DEFAULT;
                itemModel = null;
                return this;
            }

            public Builder item(String model) {
                if (itemMode == ItemMode.NONE) {
                    throw new IllegalStateException("Cannot use .item(...) after .noItem()");
                }

                if (model == null || model.isBlank()) {
                    throw new IllegalArgumentException("item model must not be blank");
                }

                explicitItem = true;
                itemMode = ItemMode.EXISTING;
                itemModel = model;
                return this;
            }

            public Builder noItem() {
                if (explicitItem) {
                    throw new IllegalStateException("Cannot use .noItem() after .item(...)");
                }

                itemMode = ItemMode.NONE;
                itemModel = null;
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
