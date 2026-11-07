package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.Objects;
import net.minecraft.world.level.block.Block;

public final class FireTextures {
    private FireTextures() {}

    //? if >=1.21.11 {
    /*public record Set(Identifier base) {
    *///?} else {
    public record Set(ResourceLocation base) {
    //?}
        public Set {
            Objects.requireNonNull(base, "base");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    //? if >=1.21.11 {
    /*public static Identifier frame(Set textures, int frame) {
    *///?} else {
    public static ResourceLocation frame(Set textures, int frame) {
    //?}
        var base = textures.base();
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_" + frame);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_" + frame);
        //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier layer(Set textures, int frame, int layer) {
    *///?} else {
    public static ResourceLocation layer(Set textures, int frame, int layer) {
    //?}
        var base = textures.base();
        String path = base.getPath();
        int slash = path.lastIndexOf('/');
        String stem = slash >= 0 ? path.substring(slash + 1) : path;
        String frameStem = stem + "_" + frame;
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(base.getNamespace(), path + "/" + frameStem + "/" + frameStem + "_" + layer);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), path + "/" + frameStem + "/" + frameStem + "_" + layer);
        //?}
    }

    public static final class Builder {
        private final Block block;
        //? if >=1.21.11 {
        /*private Identifier base;

        *///?} else {
        private ResourceLocation base;

        //?}
        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder texture() {
            this.base = TextureTokens.block(block);
            return this;
        }

        public Builder texture(String texture) {
            this.base = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() {
            if (base == null) throw new IllegalStateException("No fire texture configured for " + block);
            return new Set(base);
        }
    }
}
