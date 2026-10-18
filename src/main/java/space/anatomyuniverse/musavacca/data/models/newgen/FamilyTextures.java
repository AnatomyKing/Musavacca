package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class FamilyTextures {
    private FamilyTextures() {}

    //? if >=1.21.11 {
    /*public record Set(Identifier texture) {
    *///?} else {
    public record Set(ResourceLocation texture) {
    //?}
        public Set {
            Objects.requireNonNull(texture, "texture");
        }
    }

    public static Builder builder(Block block, String suffix) {
        return new Builder(block, suffix);
    }

    public static final class Builder {
        private final Block block;
        private final String suffix;
        //? if >=1.21.11 {
        /*private Identifier texture;
        *///?} else {
        private ResourceLocation texture;
        //?}

        private Builder(Block block, String suffix) {
            this.block = Objects.requireNonNull(block, "block");

            if (suffix == null || suffix.isBlank()) {
                throw new IllegalArgumentException("suffix must not be blank");
            }

            this.suffix = suffix;
        }

        public Builder source() {
            return source(BlockFamilySources.inferBaseBlock(block, suffix));
        }

        public Builder source(Block source) {
            texture = TextureTokens.block(Objects.requireNonNull(source, "source"));
            return this;
        }

        public Builder source(String texture) {
            this.texture = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (texture == null) {
                source();
            }

            return new Set(texture);
        }
    }
}
