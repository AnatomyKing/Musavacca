package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class SlabTextures {
    private SlabTextures() {}

    public record Set(
            //? if >=1.21.11 {
            /*Identifier side,
            Identifier bottom,
            Identifier top
            *///?} else {
            ResourceLocation side,
            ResourceLocation bottom,
            ResourceLocation top
            //?}
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
        //? if >=1.21.11 {
        /*private Identifier side;
        private Identifier bottom;
        private Identifier top;
        *///?} else {
        private ResourceLocation side;
        private ResourceLocation bottom;
        private ResourceLocation top;
        //?}

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder all() {
            return all(BlockFamilySources.inferBaseBlock(block, "_slab"));
        }

        public Builder all(Block source) {
            //? if >=1.21.11 {
            /*Identifier texture = TextureTokens.block(
            *///?} else {
            ResourceLocation texture = TextureTokens.block(
            //?}
                    Objects.requireNonNull(source, "source")
            );

            side = texture;
            bottom = texture;
            top = texture;
            return this;
        }

        public Builder all(String texture) {
            //? if >=1.21.11 {
            /*Identifier resolved = TextureTokens.resolveBlock(block, texture);
            *///?} else {
            ResourceLocation resolved = TextureTokens.resolveBlock(block, texture);
            //?}
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
                //? if >=1.21.11 {
                /*Identifier fallback = TextureTokens.block(
                *///?} else {
                ResourceLocation fallback = TextureTokens.block(
                //?}
                        BlockFamilySources.inferBaseBlock(block, "_slab")
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
