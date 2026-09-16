package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class SpawnEggItems {
    private SpawnEggItems() {}

    public static final int DEFAULT_PRIMARY_COLOR = 0xFFFFFF;
    public static final int DEFAULT_SECONDARY_COLOR = 0xFFFFFF;

    public static final class Entry {
        private final ItemLike item;
        private final int primaryColor;
        private final int secondaryColor;
        private final boolean folder;
        private final String texture;
        private final boolean inferTexture;

        private Entry(Builder builder) {
            item = builder.item;
            primaryColor = builder.primaryColor & 0xFFFFFF;
            secondaryColor = builder.secondaryColor & 0xFFFFFF;
            folder = builder.folder;
            texture = builder.texture;
            inferTexture = builder.inferTexture;
        }

        public static Builder builder(ItemLike item) {
            return new Builder(item);
        }

        public ItemLike item() { return item; }
        public int primaryColor() { return primaryColor; }
        public int secondaryColor() { return secondaryColor; }

        SimpleItems.Model modernModel() {
            SimpleItems.Model model = SimpleItems.Model.generated().flat();
            if (folder) model.folder();
            if (inferTexture) model.texture();
            else model.texture(texture);
            return model;
        }
    }

    public static final class Builder {
        private final ItemLike item;
        private int primaryColor = DEFAULT_PRIMARY_COLOR;
        private int secondaryColor = DEFAULT_SECONDARY_COLOR;
        private boolean folder;
        private String texture;
        private boolean inferTexture;
        private boolean textureSelected;

        private Builder(ItemLike item) {
            this.item = Objects.requireNonNull(item, "item");
        }

        public Builder colors(int primary, int secondary) {
            primaryColor = primary;
            secondaryColor = secondary;
            return this;
        }

        public Builder folder() {
            folder = true;
            return this;
        }

        public Builder texture() {
            ensureTextureUnset();
            textureSelected = true;
            inferTexture = true;
            return this;
        }

        public Builder texture(String texture) {
            ensureTextureUnset();
            if (texture == null || texture.isBlank()) {
                throw new IllegalArgumentException("spawn egg texture must not be blank");
            }
            textureSelected = true;
            this.texture = texture;
            return this;
        }

        public Entry build() {
            if (!textureSelected) {
                throw new IllegalStateException(
                        "SpawnEggItems requires explicit texture handling for "
                                + ModelLocations.itemId(item) + ": call .texture() or .texture(model)"
                );
            }
            return new Entry(this);
        }

        private void ensureTextureUnset() {
            if (textureSelected) throw new IllegalStateException("Spawn egg texture is already configured");
        }
    }
}

