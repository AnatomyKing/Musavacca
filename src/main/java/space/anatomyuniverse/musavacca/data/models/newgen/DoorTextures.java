package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class DoorTextures {
    private DoorTextures() {}

    public record Set(
            ResourceLocation bottom,
            ResourceLocation top
    ) {
        public Set {
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static final class Builder {
        private final Block block;

        private ResourceLocation bottom;
        private ResourceLocation top;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder bottom() {
            this.bottom = TextureTokens.block(block, "_bottom");
            return this;
        }

        public Builder top() {
            this.top = TextureTokens.block(block, "_top");
            return this;
        }

        public Builder bottom(String texture) {
            this.bottom = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Builder top(String texture) {
            this.top = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (bottom == null) {
                bottom();
            }

            if (top == null) {
                top();
            }

            return new Set(bottom, top);
        }

    }
}
