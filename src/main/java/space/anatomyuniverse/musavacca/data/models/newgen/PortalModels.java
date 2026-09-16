package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Consumer;

public record PortalModels(Models.Source northSouth, Models.Source eastWest, Models.Source upDown) {
    public PortalModels {
        if (northSouth == null && eastWest == null && upDown == null) {
            throw new IllegalArgumentException("PortalModels requires at least one orientation");
        }
        validate(northSouth, Direction.Axis.X);
        validate(eastWest, Direction.Axis.Z);
        validate(upDown, Direction.Axis.Y);
    }

    private static void validate(Models.Source source, Direction.Axis axis) {
        if (source == null || source instanceof Models.Existing) return;
        if (source instanceof Pane pane && pane.axis() == axis) return;
        throw new IllegalArgumentException("Expected an existing model or generated portal pane for axis " + axis);
    }

    public Models.Source model(Direction.Axis axis) {
        return switch (axis) {
            case X -> northSouth;
            case Z -> eastWest;
            case Y -> upDown;
        };
    }

    public static PortalModels existing(String northSouth, String eastWest, String upDown) {
        return new PortalModels(Models.existing(northSouth), Models.existing(eastWest), Models.existing(upDown));
    }

    public static PortalModels generated(Block block) { return generated(block, "", textures -> {}); }

    public static PortalModels generated(Block block, Consumer<PortalTextures.Builder> textures) {
        return generated(block, "", textures);
    }

    public static PortalModels generated(Block block, String suffix, Consumer<PortalTextures.Builder> textures) {
        Objects.requireNonNull(suffix, "suffix");
        return generated(block, ModelLocations.blockModel(block, suffix), textures);
    }

    public static PortalModels generated(Block block, ResourceLocation modelStem, Consumer<PortalTextures.Builder> textures) {
        Objects.requireNonNull(modelStem, "modelStem");
        Objects.requireNonNull(textures, "textures");
        PortalTextures.Builder builder = PortalTextures.of(block);
        textures.accept(builder);
        PortalTextures.Set set = builder.build();
        return new PortalModels(pane(modelStem, "_ns", Direction.Axis.X, set),
                pane(modelStem, "_ew", Direction.Axis.Z, set), pane(modelStem, "_ud", Direction.Axis.Y, set));
    }

    private static Pane pane(ResourceLocation stem, String suffix, Direction.Axis axis, PortalTextures.Set textures) {
        return new Pane(ResourceLocation.fromNamespaceAndPath(stem.getNamespace(), stem.getPath() + suffix), axis, textures);
    }

    record Pane(ResourceLocation model, Direction.Axis axis, PortalTextures.Set textures) implements Models.Source {
        Pane {
            Objects.requireNonNull(model, "model");
            Objects.requireNonNull(axis, "axis");
            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Models.Source northSouth;
        private Models.Source eastWest;
        private Models.Source upDown;

        private Builder() {}

        public Builder northSouth(String model) { return northSouth(Models.existing(model)); }
        public Builder eastWest(String model) { return eastWest(Models.existing(model)); }
        public Builder upDown(String model) { return upDown(Models.existing(model)); }
        public Builder northSouth(Models.Source model) { northSouth = Objects.requireNonNull(model, "model"); return this; }
        public Builder eastWest(Models.Source model) { eastWest = Objects.requireNonNull(model, "model"); return this; }
        public Builder upDown(Models.Source model) { upDown = Objects.requireNonNull(model, "model"); return this; }
        public PortalModels build() { return new PortalModels(northSouth, eastWest, upDown); }
    }
}

