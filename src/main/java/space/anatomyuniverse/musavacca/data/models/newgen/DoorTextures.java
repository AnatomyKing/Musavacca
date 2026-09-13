package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class DoorTextures {
    private DoorTextures() {}

    public record Set(
            ResourceLocation bottom,
            ResourceLocation top
    ) {
        public Set {
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(top, "top");
        }
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static final class Builder {
        private final Block block;

        private ResourceLocation bottom;
        private ResourceLocation top;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
        }

        public Builder bottom() {
            this.bottom = inferred("_bottom");
            return this;
        }

        public Builder top() {
            this.top = inferred("_top");
            return this;
        }

        public Builder bottom(String texture) {
            this.bottom = resolve(texture);
            return this;
        }

        public Builder top(String texture) {
            this.top = resolve(texture);
            return this;
        }

        public Set build() {
            if (bottom == null) {
                bottom();
            }

            if (top == null) {
                top();
            }

            return new Set(bottom, top);
        }

        private ResourceLocation inferred(String suffix) {
            ResourceLocation id = ModelUtil.idOf(block);

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "block/" + id.getPath() + suffix
            );
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
