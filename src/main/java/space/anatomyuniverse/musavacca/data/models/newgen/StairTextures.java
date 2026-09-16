package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class StairTextures {
    private StairTextures() {}

    public record Set(
            ResourceLocation side,
            ResourceLocation bottom,
            ResourceLocation top
    ) {
        public Set {
            Objects.requireNonNull(side, "side");
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static final class Builder {
        private final Block block;

        private ResourceLocation side;
        private ResourceLocation bottom;
        private ResourceLocation top;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder all() {
            return all(BlockFamilySources.inferBaseBlock(block, "_stairs"));
        }

        public Builder all(Block source) {
            ResourceLocation texture = TextureTokens.block(
                    Objects.requireNonNull(source, "source")
            );

            side = texture;
            bottom = texture;
            top = texture;
            return this;
        }

        public Builder all(String texture) {
            ResourceLocation resolved = TextureTokens.resolveBlock(block, texture);
            side = resolved;
            bottom = resolved;
            top = resolved;
            return this;
        }

        public Builder side(String texture) {
            side = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Builder bottom(String texture) {
            bottom = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Builder top(String texture) {
            top = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (side == null && bottom == null && top == null) {
                all();
            } else if (side == null || bottom == null || top == null) {
                ResourceLocation fallback = TextureTokens.block(
                        BlockFamilySources.inferBaseBlock(block, "_stairs")
                );

                if (side == null) {
                    side = fallback;
                }
                if (bottom == null) {
                    bottom = fallback;
                }
                if (top == null) {
                    top = fallback;
                }
            }

            return new Set(side, bottom, top);
        }
    }
}

