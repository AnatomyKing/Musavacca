package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class CrossTextures {
    private CrossTextures() {}

    public record Set(ResourceLocation texture) {
        public Set {
            Objects.requireNonNull(texture, "texture");
        }
    }

    public static final class Builder {
        private final Block block;
        private ResourceLocation texture;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
            this.texture = ModelUtil.blockTex(block);
        }

        public Builder texture() {
            this.texture = ModelUtil.blockTex(block);
            return this;
        }

        public Builder texture(String texture) {
            this.texture = resolve(texture);
            return this;
        }

        public Set build() {
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
            ResourceLocation base = ModelUtil.blockTex(block);

            if (token.startsWith("_")) {
                return ResourceLocation.fromNamespaceAndPath(
                        id.getNamespace(),
                        base.getPath() + token
                );
            }

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    token.startsWith("block/") ? token : "block/" + token
            );
        }
    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
