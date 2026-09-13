package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class FireTextures {
    private FireTextures() {}

    public record Set(ResourceLocation base) {
        public Set {
            Objects.requireNonNull(base, "base");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static ResourceLocation frame(Set textures, int frame) {
        ResourceLocation base = textures.base();
        return ResourceLocation.fromNamespaceAndPath(
                base.getNamespace(),
                base.getPath() + "_" + frame
        );
    }

    public static ResourceLocation layer(Set textures, int frame, int layer) {
        ResourceLocation base = textures.base();
        String path = base.getPath();
        int slash = path.lastIndexOf('/');
        String stem = slash >= 0 ? path.substring(slash + 1) : path;
        String frameStem = stem + "_" + frame;

        return ResourceLocation.fromNamespaceAndPath(
                base.getNamespace(),
                path + "/" + frameStem + "/" + frameStem + "_" + layer
        );
    }

    public static final class Builder {
        private final Block block;
        private ResourceLocation base;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder texture() {
            ResourceLocation id = ModelUtil.idOf(block);
            this.base = ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "block/" + id.getPath()
            );
            return this;
        }

        public Builder texture(String texture) {
            Objects.requireNonNull(texture, "texture");

            if (texture.indexOf(':') >= 0) {
                this.base = ResourceLocation.parse(texture);
            } else {
                ResourceLocation id = ModelUtil.idOf(block);
                String path = texture.startsWith("block/")
                        ? texture
                        : "block/" + texture;
                this.base = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path);
            }

            return this;
        }

        public Set build() {
            if (base == null) {
                throw new IllegalStateException("No fire texture configured for " + ModelUtil.idOf(block));
            }

            return new Set(base);
        }
    }
}
