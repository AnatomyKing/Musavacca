package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class PortalTextures {
    private PortalTextures() {}

    //? if >=1.21.11 {
    /*public record Set(Identifier texture, Identifier layerPrefix, Identifier particle) {
    *///?} else {
    public record Set(ResourceLocation texture, ResourceLocation layerPrefix, ResourceLocation particle) {
    //?}
        public Set {
            Objects.requireNonNull(texture, "texture");
            Objects.requireNonNull(layerPrefix, "layerPrefix");
        }

        //? if >=1.21.11 {
        /*public Identifier layer(int layer) {
            if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
            return Identifier.fromNamespaceAndPath(layerPrefix.getNamespace(), layerPrefix.getPath() + "_" + layer);
        *///?} else {
        public ResourceLocation layer(int layer) {
            if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
            return ResourceLocation.fromNamespaceAndPath(layerPrefix.getNamespace(), layerPrefix.getPath() + "_" + layer);
        //?}
        }

        //? if >=1.21.11 {
        /*Identifier source(Tints.Tint tint, int layer) {
        *///?} else {
        ResourceLocation source(Tints.Tint tint, int layer) {
        //?}
            return Tints.generatedLayerCount(tint) > 1 ? layer(layer) : texture;
        }
    }

    public static Builder of(Block block) { return new Builder(block); }

    public static final class Builder {
        private final Block block;
        //? if >=1.21.11 {
        /*private Identifier texture;
        private Identifier layerPrefix;
        private Identifier particle;
        *///?} else {
        private ResourceLocation texture;
        private ResourceLocation layerPrefix;
        private ResourceLocation particle;
        //?}

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
            texture();
        }

        public Builder texture() { return folder(TextureTokens.block(block).toString()); }
        public Builder texture(String texture) { return folder(texture); }
        public Builder folder() { return texture(); }

        public Builder folder(String texture) {
            this.texture = TextureTokens.resolveBlock(block, texture);
            String path = this.texture.getPath();
            String name = path.substring(path.lastIndexOf('/') + 1);
            //? if >=1.21.11 {
            /*layerPrefix = Identifier.fromNamespaceAndPath(this.texture.getNamespace(), path + "/" + name);
            *///?} else {
            layerPrefix = ResourceLocation.fromNamespaceAndPath(this.texture.getNamespace(), path + "/" + name);
            //?}
            return this;
        }

        public Builder root() { return root(TextureTokens.block(block).toString()); }

        public Builder root(String texture) {
            this.texture = TextureTokens.resolveBlock(block, texture);
            layerPrefix = this.texture;
            return this;
        }

        public Builder layerPrefix(String prefix) {
            layerPrefix = TextureTokens.resolveBlock(block, prefix);
            return this;
        }

        public Builder particle(String texture) {
            particle = TextureTokens.resolveBlock(block, texture);
            return this;
        }

        public Set build() { return new Set(texture, layerPrefix, particle); }
    }
}
