package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;

public final class TrapdoorModels {
    private final ResourceLocation bottom;
    private final ResourceLocation top;
    private final ResourceLocation open;

    private TrapdoorModels(Builder builder) {
        this.bottom = builder.bottom;
        this.top = builder.top;
        this.open = builder.open;

        if (isEmpty()) {
            throw new IllegalStateException("TrapdoorModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TrapdoorModels full(
            String bottom,
            String top,
            String open
    ) {
        return builder()
                .bottom(bottom)
                .top(top)
                .open(open)
                .build();
    }

    @Nullable
    public ResourceLocation model(Half half, boolean open) {
        if (open) {
            return this.open;
        }

        return half == Half.TOP
                ? top
                : bottom;
    }

    public boolean isEmpty() {
        return bottom == null
                && top == null
                && open == null;
    }

    public static final class Builder {
        private ResourceLocation bottom;
        private ResourceLocation top;
        private ResourceLocation open;

        private Builder() {}

        public Builder bottom(String model) {
            this.bottom = parse(model, "bottom");
            return this;
        }

        public Builder top(String model) {
            this.top = parse(model, "top");
            return this;
        }

        public Builder open(String model) {
            this.open = parse(model, "open");
            return this;
        }

        public TrapdoorModels build() {
            return new TrapdoorModels(this);
        }

        private static ResourceLocation parse(String model, String name) {
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }

            return ResourceLocation.parse(model);
        }
    }
}
