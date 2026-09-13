package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class Textures {
    private Textures() {}

    public static final class Set {
        private final ResourceLocation all;
        private final ResourceLocation sides;
        private final ResourceLocation ends;

        private final ResourceLocation top;
        private final ResourceLocation bottom;

        private final ResourceLocation north;
        private final ResourceLocation south;
        private final ResourceLocation west;
        private final ResourceLocation east;

        private final ResourceLocation particle;

        private Set(Builder builder) {
            this.all = builder.all;

            this.sides = builder.sides;

            this.ends = builder.ends;

            this.top = builder.top;

            this.bottom = builder.bottom;

            this.north = builder.north;

            this.south = builder.south;

            this.west = builder.west;

            this.east = builder.east;

            this.particle = builder.particle;

            require("top", top());

            require("bottom", bottom());

            require("north", north());

            require("south", south());

            require("west", west());

            require("east", east());
        }

        public ResourceLocation top() {
            return first(top, ends, all);
        }

        public ResourceLocation bottom() {
            return first(bottom, ends, all);
        }

        public ResourceLocation north() {
            return first(north, sides, all);
        }

        public ResourceLocation south() {
            return first(south, sides, all);
        }

        public ResourceLocation west() {
            return first(west, sides, all);
        }

        public ResourceLocation east() {
            return first(east, sides, all);
        }

        public ResourceLocation particle() {
            return first(particle, north(), all);
        }

        private static ResourceLocation first(ResourceLocation... values) {
            for (ResourceLocation value : values) {
                if (value != null) {
                    return value;
                }
            }

            return null;
        }

        private static void require(String face, ResourceLocation texture) {
            if (texture == null) {
                throw new IllegalStateException("No texture resolves for generated face '" + face + "'");
            }
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
            this.all = ModelUtil.blockTex(block);
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
            this.all = ModelUtil.blockTex(block);

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
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("texture token must not be blank");
            }

            /*
             * Exact reference:
             *
             * minecraft:block/stone
             * musavacca:block/foo
             */
            if (token.indexOf(':') >= 0) {
                return ResourceLocation.parse(token);
            }

            ResourceLocation id = ModelUtil.idOf(block);

            ResourceLocation base = ModelUtil.blockTex(block);

            /*
             * Suffix shorthand:
             *
             * "_top"
             *
             * becomes:
             *
             * musavacca:block/<id>_top
             */
            if (token.startsWith("_")) {
                return ResourceLocation
                        .fromNamespaceAndPath(id.getNamespace(), base.getPath() + token);
            }

            return ResourceLocation
                    .fromNamespaceAndPath(id.getNamespace(), token.startsWith("block/") ? token : "block/" + token);
        }
    }

    public static Builder of(Block block) {
        return new Builder(block);
    }
}
