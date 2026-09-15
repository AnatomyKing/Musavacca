package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class ButtonBlocks {
    private ButtonBlocks() {}

    public static final class Part extends BlockFamily.ModelRule<ButtonModels, Part> {
        private Part(ButtonModels source, Conditions.Match conditions) {
            super(source, conditions);
        }

        public static Part always(ButtonModels source) {
            return new Part(source, Conditions.always());
        }

        public static Part when(ButtonModels source, Conditions.Match conditions) {
            return new Part(source, conditions);
        }

        public ButtonModels models() {
            return source();
        }
    }

    public static final class Entry extends BlockFamily.StateFamilyEntry<Part> {
        private final FamilyTextures.Set textures;
        private final ButtonModels baseModels;

        private Entry(Builder builder) {
            super(builder);
            textures = builder.textures;
            baseModels = builder.baseModels;
        }

        public static Builder builder(Block block) {
            return new Builder(block);
        }

        public FamilyTextures.Set textures() {
            return textures;
        }

        public ButtonModels baseModels() {
            return baseModels;
        }

        public void validate() {
            if (!(block() instanceof ButtonBlock)) {
                throw new IllegalStateException(
                        "ButtonBlocks requires a ButtonBlock: " + ModelUtil.idOf(block())
                );
            }

            BlockFamilyValidation.requireProperty(block(), ButtonBlock.FACE, "ButtonBlocks");
            BlockFamilyValidation.requireProperty(block(), ButtonBlock.FACING, "ButtonBlocks");
            BlockFamilyValidation.requireProperty(block(), ButtonBlock.POWERED, "ButtonBlocks");

            if (baseMode() == null) {
                throw new IllegalStateException(
                        "No base model selected for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.GENERATED && textures == null) {
                throw new IllegalStateException(
                        "No generated textures configured for " + ModelUtil.idOf(block())
                );
            }

            if (baseMode() == BaseModelMode.EXISTING) {
                if (baseModels == null || !baseModels.worldComplete()) {
                    throw new IllegalStateException(
                            "Base ButtonModels must provide the complete in-world family for "
                                    + ModelUtil.idOf(block())
                    );
                }
            }


            if (familyItemMode() == FamilyItemMode.DEFAULT
                    && baseMode() == BaseModelMode.EXISTING
                    && baseModels.inventory() == null) {
                throw new IllegalStateException(
                        ".item() requires an inventory model for " + ModelUtil.idOf(block())
                                + "; provide it in the model family, use .item(model), or .noItem()."
                );
            }

            validatePartConditions();

        }

        public static final class Builder extends BlockFamily.ItemFamilyBuilder<Builder, Part> {
            private FamilyTextures.Set textures;
            private ButtonModels baseModels;

            private Builder(Block block) {
                super(block);
            }

            public Builder generated() {
                selectGenerated();
                return this;
            }

            public Builder models(ButtonModels models) {
                selectExisting();
                baseModels = Objects.requireNonNull(models, "models");
                return this;
            }


            public Builder texture() {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_button").source().build();
                return this;
            }

            public Builder texture(Block source) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_button").source(source).build();
                return this;
            }

            public Builder texture(String texture) {
                requireGeneratedTextures();
                textures = FamilyTextures.builder(block, "_button").source(texture).build();
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
