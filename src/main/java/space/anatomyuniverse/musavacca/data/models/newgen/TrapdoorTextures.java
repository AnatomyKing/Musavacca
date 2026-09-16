package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class TrapdoorTextures {
    private TrapdoorTextures() {}

    public record Set(ResourceLocation texture) {
        public Set {
            Objects.requireNonNull(texture, "texture");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static final class Builder {
        private final Block block;
        private ResourceLocation texture;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder texture() {
            this.texture = TextureTokens.block(block);
            return this;
        }

        public Builder texture(String texture) {
            this.texture = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (texture == null) {
                texture();
            }

            return new Set(texture);
        }

    }
}

