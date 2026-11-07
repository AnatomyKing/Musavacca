package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.Objects;
import net.minecraft.world.level.block.Block;

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

    //? if >=1.21.11 {
    /*public record Halves(Identifier bottom, Identifier top) {
    *///?} else {
    public record Halves(ResourceLocation bottom, ResourceLocation top) {
    //?}
        public Halves {
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    //? if >=1.21.11 {
    /*public record Faces(Identifier side, Identifier bottom, Identifier top) {
    *///?} else {
    public record Faces(ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    //?}
        public Faces {
            Objects.requireNonNull(side, "side");
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block, null);
    }

    public static Builder builder(Block block, String suffix) {
        if (suffix == null || suffix.isBlank()) throw new IllegalArgumentException("suffix must not be blank");
        return new Builder(block, suffix);
    }

    public static HalvesBuilder halves(Block block) {
        return new HalvesBuilder(block);
    }

    public static FacesBuilder faces(Block block, String suffix) {
        return new FacesBuilder(block, suffix);
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
            this.suffix = suffix;
        }

        public Builder texture() {
            return texture(suffix == null ? block : ModelLocations.inferBaseBlock(block, suffix));
        }

        public Builder texture(Block source) {
            texture = TextureTokens.block(Objects.requireNonNull(source, "source"));
            return this;
        }

        public Builder texture(String texture) {
            this.texture = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (texture == null) texture();
            return new Set(texture);
        }
    }

    public static final class HalvesBuilder {
        private final Block block;
        //? if >=1.21.11 {
        /*private Identifier bottom;
        private Identifier top;
        *///?} else {
        private ResourceLocation bottom;
        private ResourceLocation top;
        //?}

        private HalvesBuilder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public HalvesBuilder bottom() {
            bottom = TextureTokens.block(block, "_bottom");
            return this;
        }

        public HalvesBuilder bottom(String texture) {
            bottom = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public HalvesBuilder top() {
            top = TextureTokens.block(block, "_top");
            return this;
        }

        public HalvesBuilder top(String texture) {
            top = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Halves build() {
            if (bottom == null) bottom();
            if (top == null) top();
            return new Halves(bottom, top);
        }
    }

    public static final class FacesBuilder {
        private final Block block;
        private final String suffix;
        //? if >=1.21.11 {
        /*private Identifier side;
        private Identifier bottom;
        private Identifier top;
        *///?} else {
        private ResourceLocation side;
        private ResourceLocation bottom;
        private ResourceLocation top;
        //?}

        private FacesBuilder(Block block, String suffix) {
            this.block = Objects.requireNonNull(block, "block");
            if (suffix == null || suffix.isBlank()) throw new IllegalArgumentException("suffix must not be blank");
            this.suffix = suffix;
        }

        public FacesBuilder all() {
            return all(ModelLocations.inferBaseBlock(block, suffix));
        }

        public FacesBuilder all(Block source) {
            var texture = TextureTokens.block(Objects.requireNonNull(source, "source"));
            side = texture;
            bottom = texture;
            top = texture;
            return this;
        }

        public FacesBuilder all(String texture) {
            var resolved = TextureTokens.resolveBlock(block, texture);
            side = resolved;
            bottom = resolved;
            top = resolved;
            return this;
        }

        public FacesBuilder side(String texture) {
            side = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public FacesBuilder bottom(String texture) {
            bottom = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public FacesBuilder top(String texture) {
            top = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Faces build() {
            if (side == null || bottom == null || top == null) {
                var fallback = TextureTokens.block(ModelLocations.inferBaseBlock(block, suffix));
                if (side == null) side = fallback;
                if (bottom == null) bottom = fallback;
                if (top == null) top = fallback;
            }
            return new Faces(side, bottom, top);
        }
    }
}
