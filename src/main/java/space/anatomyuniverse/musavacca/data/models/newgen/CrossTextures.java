package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class CrossTextures {
    private CrossTextures() {}

    public record Set(ResourceLocation texture) {
        public Set {
            Objects.requireNonNull(texture, "texture");
        }
    }

    public static final class Builder {
        private final Block block;
        private ResourceLocation texture;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
            this.texture = TextureTokens.block(block);
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
            return new Set(texture);
        }

    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
