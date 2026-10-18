package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class TallCrossTextures {
    private TallCrossTextures() {}

    //? if >=1.21.11 {
    /*public record Set(Identifier bottom, Identifier top) {
    *///?} else {
    public record Set(ResourceLocation bottom, ResourceLocation top) {
    //?}
        public Set {
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    public static final class Builder {
        private final Block block;

        //? if >=1.21.11 {
        /*private Identifier bottom;
        private Identifier top;
        *///?} else {
        private ResourceLocation bottom;
        private ResourceLocation top;
        //?}

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
            this.bottom = suffix("_bottom");
            this.top = suffix("_top");
        }

        public Builder bottom() {
            this.bottom = suffix("_bottom");
            return this;
        }

        public Builder bottom(String texture) {
            this.bottom = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Builder top() {
            this.top = suffix("_top");
            return this;
        }

        public Builder top(String texture) {
            this.top = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            return new Set(bottom, top);
        }

        //? if >=1.21.11 {
        /*private Identifier suffix(String suffix) {
        *///?} else {
        private ResourceLocation suffix(String suffix) {
        //?}
            return TextureTokens.block(block, suffix);
        }

    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
