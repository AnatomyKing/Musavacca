package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class PressurePlateModels {
    private final ResourceLocation up;
    private final ResourceLocation down;

    private PressurePlateModels(Builder builder) {
        up = builder.up;
        down = builder.down;

        if (isEmpty()) {
            throw new IllegalStateException("PressurePlateModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PressurePlateModels full(String up, String down) {
        return builder().up(up).down(down).build();
    }

    @Nullable
    public ResourceLocation model(boolean powered) {
        return powered ? down : up;
    }

    @Nullable
    public ResourceLocation up() {
        return up;
    }

    public boolean complete() {
        return up != null && down != null;
    }

    public boolean isEmpty() {
        return up == null && down == null;
    }

    public static final class Builder {
        private ResourceLocation up;
        private ResourceLocation down;

        private Builder() {}

        public Builder up(String model) {
            up = parse(model, "up");
            return this;
        }

        public Builder down(String model) {
            down = parse(model, "down");
            return this;
        }

        public PressurePlateModels build() {
            return new PressurePlateModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return ResourceLocation.parse(model);
    }
}

