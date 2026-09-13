package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class TrapdoorTextures {
    private TrapdoorTextures() {}

    public record Set(ResourceLocation texture) {
        public Set {
            Objects.requireNonNull(texture, "texture");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static final class Builder {
        private final Block block;
        private ResourceLocation texture;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder texture() {
            ResourceLocation id = ModelUtil.idOf(block);

            this.texture = ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "block/" + id.getPath()
            );
            return this;
        }

        public Builder texture(String texture) {
            this.texture = resolve(texture);
            return this;
        }

        public Set build() {
            if (texture == null) {
                texture();
            }

            return new Set(texture);
        }

        private ResourceLocation resolve(String token) {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("texture token must not be blank");
            }

            if (token.indexOf(':') >= 0) {
                return ResourceLocation.parse(token);
            }

            ResourceLocation id = ModelUtil.idOf(block);

            if (token.startsWith("_")) {
                return ResourceLocation.fromNamespaceAndPath(
                        id.getNamespace(),
                        "block/" + id.getPath() + token
                );
            }

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    token.startsWith("block/")
                            ? token
                            : "block/" + token
            );
        }
    }
}
