package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class PortalBlocks {
    private PortalBlocks() {}

    public static final class Model extends BlockFamily.ModelRule<PortalModels, Model> {
        private Model(PortalModels source, Conditions.Match conditions) { super(source, conditions); }
        public static Model always(PortalModels source) { return new Model(source, Conditions.always()); }
        public static Model when(PortalModels source, Conditions.Match conditions) { return new Model(source, conditions); }
    }

    public static final class Part extends BlockFamily.ModelRule<PortalModels, Part> {
        private Part(PortalModels source, Conditions.Match conditions) { super(source, conditions); }
        public static Part always(PortalModels source) { return new Part(source, Conditions.always()); }
        public static Part when(PortalModels source, Conditions.Match conditions) { return new Part(source, conditions); }
    }

    public static final class Entry extends BlockFamily.ModelFamilyEntry<Model, Part> {
        private final Property<Direction.Axis> axis;

        private Entry(Builder builder) {
            super(builder, builder.generated
                    ? List.of(Model.always(PortalModels.generated(builder.block, builder.textureConfig))) : builder.models,
                    builder.parts);
            axis = builder.axis;
        }

        public static Builder builder(Block block) { return new Builder(block); }
        public Property<Direction.Axis> axis() { return axis; }

        public void validate() {
            BlockFamilyValidation.requireProperty(block(), axis, "PortalBlocks");
            if (rotations().property() == axis) {
                throw new IllegalStateException("PortalBlocks already manages " + axis.getName() + " on " + block());
            }
            validateStructure("PortalBlocks");
            for (Model model : models()) validateSource(model, true);
            for (Part part : parts()) validateSource(part, false);
        }

        private void validateSource(BlockFamily.ModelRule<PortalModels, ?> rule, boolean complete) {
            boolean applies = false;
            for (Direction.Axis value : axis.getPossibleValues()) {
                if (!rule.conditions().allows(axis, value)) continue;
                if (rule.source().model(value) != null) applies = true;
                else if (complete) {
                    throw new IllegalStateException("Missing portal base model for " + axis.getName() + "=" + value + " on " + block());
                }
            }
            if (!applies) throw new IllegalStateException("Portal rule has no applicable orientation on " + block());
        }

        public static final class Builder extends BlockFamily.ModelFamilyBuilder<Builder, Model, Part> {
            private Property<Direction.Axis> axis = BlockStateProperties.AXIS;
            private Consumer<PortalTextures.Builder> textureConfig = textures -> {};

            private Builder(Block block) { super(block); }

            public Builder axis(Property<Direction.Axis> property) {
                axis = Objects.requireNonNull(property, "property");
                return this;
            }

            public Builder generated() { beginGenerated(); return this; }
            public Builder model(PortalModels source) { addModel(Model.always(source)); return this; }
            public Builder models(Model... models) { addModels(models); return this; }
            public Builder multipart(Part... parts) { addParts(parts); return this; }
            public Builder texture() { return textures(PortalTextures.Builder::texture); }
            public Builder texture(String texture) { return textures(t -> t.texture(texture)); }

            public Builder textures(Consumer<PortalTextures.Builder> config) {
                if (!generated) throw new IllegalStateException("texture(s) requires .generated() first");
                Objects.requireNonNull(config, "config");
                textureConfig = textureConfig.andThen(config);
                return this;
            }

            public Entry build() {
                validateReadyToBuild("Generated portal model");
                Entry entry = new Entry(this);
                entry.validate();
                return entry;
            }
        }
    }
}

