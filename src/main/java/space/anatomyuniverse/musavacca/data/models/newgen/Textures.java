package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Textures {
    private Textures() {}

    public record Set(
            //? if >=1.21.11 {
            /*List<Identifier> top,
            List<Identifier> bottom,
            List<Identifier> north,
            List<Identifier> south,
            List<Identifier> west,
            List<Identifier> east,
            Identifier particle
            *///?} else {
            List<ResourceLocation> top,
            List<ResourceLocation> bottom,
            List<ResourceLocation> north,
            List<ResourceLocation> south,
            List<ResourceLocation> west,
            List<ResourceLocation> east,
            ResourceLocation particle
            //?}
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
        //? if >=1.21.11 {
        /*private static List<Identifier> first(List<Identifier>... values) {
            for (List<Identifier> value : values) {
        *///?} else {
        private static List<ResourceLocation> first(List<ResourceLocation>... values) {
            for (List<ResourceLocation> value : values) {
        //?}
                if (value != null) return value;
            }
            throw new IllegalStateException("No texture resolves for generated face");
        }

        //? if >=1.21.11 {
        /*private static List<Identifier> requireLayers(
                List<Identifier> layers,
        *///?} else {
        private static List<ResourceLocation> requireLayers(
                List<ResourceLocation> layers,
        //?}
                String name
        ) {
            Objects.requireNonNull(layers, name);
            if (layers.isEmpty()) {
                throw new IllegalStateException("No textures resolve for generated " + name + " face");
            }
            //? if >=1.21.11 {
            /*for (Identifier layer : layers) {
            *///?} else {
            for (ResourceLocation layer : layers) {
            //?}
                Objects.requireNonNull(layer, name + " layer");
            }
            return List.copyOf(layers);
        }
    }

    public static final class Builder {
        private final Block block;

        //? if >=1.21.11 {
        /*private List<Identifier> all;
        private List<Identifier> sides;
        private List<Identifier> ends;
        *///?} else {
        private List<ResourceLocation> all;
        private List<ResourceLocation> sides;
        private List<ResourceLocation> ends;
        //?}

        //? if >=1.21.11 {
        /*private List<Identifier> top;
        private List<Identifier> bottom;
        *///?} else {
        private List<ResourceLocation> top;
        private List<ResourceLocation> bottom;
        //?}

        //? if >=1.21.11 {
        /*private List<Identifier> north;
        private List<Identifier> south;
        private List<Identifier> west;
        private List<Identifier> east;
        *///?} else {
        private List<ResourceLocation> north;
        private List<ResourceLocation> south;
        private List<ResourceLocation> west;
        private List<ResourceLocation> east;
        //?}

        //? if >=1.21.11 {
        /*private Identifier particle;
        *///?} else {
        private ResourceLocation particle;
        //?}

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

        //? if >=1.21.11 {
        /*private List<Identifier> resolve(String... tokens) {
        *///?} else {
        private List<ResourceLocation> resolve(String... tokens) {
        //?}
            if (tokens == null || tokens.length == 0) {
                throw new IllegalArgumentException("At least one texture is required");
            }

            return Arrays.stream(tokens)
                    .map(this::resolve)
                    .toList();
        }

        //? if >=1.21.11 {
        /*private Identifier resolve(String token) {
        *///?} else {
        private ResourceLocation resolve(String token) {
        //?}
            return TextureTokens.resolveBlock(block, Objects.requireNonNull(token, "texture"));
        }
    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
