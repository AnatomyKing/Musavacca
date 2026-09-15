package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public final class SlabModels {
    private final ResourceLocation bottom;
    private final ResourceLocation top;
    private final ResourceLocation doubleModel;

    private SlabModels(Builder builder) {
        bottom = builder.bottom;
        top = builder.top;
        doubleModel = builder.doubleModel;

        if (isEmpty()) {
            throw new IllegalStateException("SlabModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SlabModels full(String bottom, String top, String doubleModel) {
        return builder()
                .bottom(bottom)
                .top(top)
                .doubleModel(doubleModel)
                .build();
    }

    @Nullable
    public ResourceLocation model(SlabType type) {
        return switch (type) {
            case BOTTOM -> bottom;
            case TOP -> top;
            case DOUBLE -> doubleModel;
        };
    }

    public boolean complete() {
        return bottom != null && top != null && doubleModel != null;
    }

    public boolean isEmpty() {
        return bottom == null && top == null && doubleModel == null;
    }

    public static final class Builder {
        private ResourceLocation bottom;
        private ResourceLocation top;
        private ResourceLocation doubleModel;

        private Builder() {}

        public Builder bottom(String model) {
            bottom = parse(model, "bottom");
            return this;
        }

        public Builder top(String model) {
            top = parse(model, "top");
            return this;
        }

        public Builder doubleModel(String model) {
            doubleModel = parse(model, "doubleModel");
            return this;
        }

        public SlabModels build() {
            return new SlabModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }

        return ResourceLocation.parse(model);
    }
}
