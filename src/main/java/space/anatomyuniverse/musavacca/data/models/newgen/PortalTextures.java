package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class PortalTextures {
    private PortalTextures() {}

    public record Set(ResourceLocation texture, ResourceLocation layerPrefix, ResourceLocation particle) {
        public Set {
            Objects.requireNonNull(texture, "texture");
            Objects.requireNonNull(layerPrefix, "layerPrefix");
        }

        public ResourceLocation layer(int layer) {
            if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
            return ResourceLocation.fromNamespaceAndPath(layerPrefix.getNamespace(), layerPrefix.getPath() + "_" + layer);
        }

        ResourceLocation source(Tints.Tint tint, int layer) {
            return Tints.generatedLayerCount(tint) > 1 ? layer(layer) : texture;
        }
    }

    public static Builder of(Block block) { return new Builder(block); }

    public static final class Builder {
        private final Block block;
        private ResourceLocation texture;
        private ResourceLocation layerPrefix;
        private ResourceLocation particle;

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
            layerPrefix = ResourceLocation.fromNamespaceAndPath(this.texture.getNamespace(), path + "/" + name);
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

