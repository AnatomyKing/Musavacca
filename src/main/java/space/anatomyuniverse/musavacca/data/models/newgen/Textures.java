package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Textures {
    private Textures() {}

    public record Set(
            List<ResourceLocation> top,
            List<ResourceLocation> bottom,
            List<ResourceLocation> north,
            List<ResourceLocation> south,
            List<ResourceLocation> west,
            List<ResourceLocation> east,
            ResourceLocation particle
    ) {
        public Set {
            top = requireLayers(top, "top");
            bottom = requireLayers(bottom, "bottom");
            north = requireLayers(north, "north");
            south = requireLayers(south, "south");
            west = requireLayers(west, "west");
            east = requireLayers(east, "east");
            Objects.requireNonNull(particle, "particle");
        }

        private Set(Builder b) {
            this(
                    first(b.top, b.ends, b.all),
                    first(b.bottom, b.ends, b.all),
                    first(b.north, b.sides, b.all),
                    first(b.south, b.sides, b.all),
                    first(b.west, b.sides, b.all),
                    first(b.east, b.sides, b.all),
                    b.particle != null
                            ? b.particle
                            : first(b.north, b.sides, b.all).get(0)
            );
        }

        public int layerCount() {
            return Math.max(
                    Math.max(Math.max(top.size(), bottom.size()), Math.max(north.size(), south.size())),
                    Math.max(west.size(), east.size())
            );
        }

        @SafeVarargs
        private static List<ResourceLocation> first(List<ResourceLocation>... values) {
            for (List<ResourceLocation> value : values) {
                if (value != null) return value;
            }
            throw new IllegalStateException("No texture resolves for generated face");
        }

        private static List<ResourceLocation> requireLayers(
                List<ResourceLocation> layers,
                String name
        ) {
            Objects.requireNonNull(layers, name);
            if (layers.isEmpty()) {
                throw new IllegalStateException("No textures resolve for generated " + name + " face");
            }
            for (ResourceLocation layer : layers) {
                Objects.requireNonNull(layer, name + " layer");
            }
            return List.copyOf(layers);
        }
    }

    public static final class Builder {
        private final Block block;

        private List<ResourceLocation> all;
        private List<ResourceLocation> sides;
        private List<ResourceLocation> ends;

        private List<ResourceLocation> top;
        private List<ResourceLocation> bottom;

        private List<ResourceLocation> north;
        private List<ResourceLocation> south;
        private List<ResourceLocation> west;
        private List<ResourceLocation> east;

        private ResourceLocation particle;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");
            this.all = List.of(TextureTokens.block(block));
        }

        public Builder all() {
            this.all = List.of(TextureTokens.block(block));
            return this;
        }

        public Builder all(String... textures) {
            this.all = resolve(textures);
            return this;
        }

        public Builder sides(String... textures) {
            this.sides = resolve(textures);
            return this;
        }

        public Builder ends(String... textures) {
            this.ends = resolve(textures);
            return this;
        }

        public Builder top(String... textures) {
            this.top = resolve(textures);
            return this;
        }

        public Builder bottom(String... textures) {
            this.bottom = resolve(textures);
            return this;
        }

        public Builder north(String... textures) {
            this.north = resolve(textures);
            return this;
        }

        public Builder south(String... textures) {
            this.south = resolve(textures);
            return this;
        }

        public Builder west(String... textures) {
            this.west = resolve(textures);
            return this;
        }

        public Builder east(String... textures) {
            this.east = resolve(textures);
            return this;
        }

        public Builder particle(String texture) {
            this.particle = resolve(texture);
            return this;
        }

        public Set build() {
            return new Set(this);
        }

        private List<ResourceLocation> resolve(String... tokens) {
            if (tokens == null || tokens.length == 0) {
                throw new IllegalArgumentException("At least one texture is required");
            }

            return Arrays.stream(tokens)
                    .map(this::resolve)
                    .toList();
        }

        private ResourceLocation resolve(String token) {
            return TextureTokens.resolveBlock(block, Objects.requireNonNull(token, "texture"));
        }
    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
