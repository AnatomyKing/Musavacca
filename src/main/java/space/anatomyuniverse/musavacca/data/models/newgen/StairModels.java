package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;

public final class StairModels {
    private final ResourceLocation straight;
    private final ResourceLocation inner;
    private final ResourceLocation outer;

    private StairModels(Builder builder) {
        straight = builder.straight;
        inner = builder.inner;
        outer = builder.outer;

        if (isEmpty()) {
            throw new IllegalStateException("StairModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StairModels full(
            String straight,
            String inner,
            String outer
    ) {
        return builder()
                .straight(straight)
                .inner(inner)
                .outer(outer)
                .build();
    }

    @Nullable
    public ResourceLocation model(StairsShape shape) {
        return switch (shape) {
            case STRAIGHT -> straight;
            case INNER_LEFT, INNER_RIGHT -> inner;
            case OUTER_LEFT, OUTER_RIGHT -> outer;
        };
    }

    public boolean complete() {
        return straight != null
                && inner != null
                && outer != null;
    }

    public boolean isEmpty() {
        return straight == null
                && inner == null
                && outer == null;
    }

    public static final class Builder {
        private ResourceLocation straight;
        private ResourceLocation inner;
        private ResourceLocation outer;

        private Builder() {}

        public Builder straight(String model) {
            straight = parse(model, "straight");
            return this;
        }

        public Builder inner(String model) {
            inner = parse(model, "inner");
            return this;
        }

        public Builder outer(String model) {
            outer = parse(model, "outer");
            return this;
        }

        public StairModels build() {
            return new StairModels(this);
        }

        private static ResourceLocation parse(String model, String name) {
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }

            return ResourceLocation.parse(model);
        }
    }
}
