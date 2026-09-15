package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import java.util.Objects;

public final class Textures {
    private Textures() {}

    public record Set(ResourceLocation top, ResourceLocation bottom,
                      ResourceLocation north, ResourceLocation south,
                      ResourceLocation west, ResourceLocation east,
                      ResourceLocation particle) {
        public Set {
            Objects.requireNonNull(top, "top");
            Objects.requireNonNull(bottom, "bottom");
            Objects.requireNonNull(north, "north");
            Objects.requireNonNull(south, "south");
            Objects.requireNonNull(west, "west");
            Objects.requireNonNull(east, "east");
            Objects.requireNonNull(particle, "particle");
        }

        private Set(Builder b) {
            this(first(b.top, b.ends, b.all), first(b.bottom, b.ends, b.all),
                    first(b.north, b.sides, b.all), first(b.south, b.sides, b.all),
                    first(b.west, b.sides, b.all), first(b.east, b.sides, b.all),
                    first(b.particle, b.north, b.sides, b.all));
        }

        private static ResourceLocation first(ResourceLocation... values) {
            for (ResourceLocation value : values) {
                if (value != null) return value;
            }
            throw new IllegalStateException("No texture resolves for generated face");
        }
    }

    public static final class Builder {
        private final Block block;

        private ResourceLocation all;
        private ResourceLocation sides;
        private ResourceLocation ends;

        private ResourceLocation top;
        private ResourceLocation bottom;

        private ResourceLocation north;
        private ResourceLocation south;
        private ResourceLocation west;
        private ResourceLocation east;

        private ResourceLocation particle;

        private Builder(Block block) {
            this.block = Objects.requireNonNull(block, "block");

            /*
             * Default:
             *
             * <namespace>:block/<block-id>
             */
            this.all = TextureTokens.block(block);
        }

        /*
         * Explicitly use this block's normal texture on all faces:
         *
         * <namespace>:block/<block-id>
         *
         * The builder already knows this default, but all() exists so
         * NewModelSets can state the intent visibly.
         */
        public Builder all() {
            this.all = TextureTokens.block(block);

            return this;
        }

        public Builder all(String texture) {
            this.all = resolve(texture);

            return this;
        }

        public Builder sides(String texture) {
            this.sides = resolve(texture);

            return this;
        }

        public Builder ends(String texture) {
            this.ends = resolve(texture);

            return this;
        }

        public Builder top(String texture) {
            this.top = resolve(texture);

            return this;
        }

        public Builder bottom(String texture) {
            this.bottom = resolve(texture);

            return this;
        }

        public Builder north(String texture) {
            this.north = resolve(texture);

            return this;
        }

        public Builder south(String texture) {
            this.south = resolve(texture);

            return this;
        }

        public Builder west(String texture) {
            this.west = resolve(texture);

            return this;
        }

        public Builder east(String texture) {
            this.east = resolve(texture);

            return this;
        }

        public Builder particle(String texture) {
            this.particle = resolve(texture);

            return this;
        }

        public Set build() {
            return new Set(this);
        }

        private ResourceLocation resolve(String token) {
            return TextureTokens.resolveBlock(block, token);
        }
    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
