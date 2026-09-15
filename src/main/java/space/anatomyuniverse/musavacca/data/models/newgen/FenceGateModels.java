package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class FenceGateModels {
    private final ResourceLocation closed;
    private final ResourceLocation open;
    private final ResourceLocation wallClosed;
    private final ResourceLocation wallOpen;

    private FenceGateModels(Builder builder) {
        closed = builder.closed;
        open = builder.open;
        wallClosed = builder.wallClosed;
        wallOpen = builder.wallOpen;

        if (isEmpty()) {
            throw new IllegalStateException("FenceGateModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FenceGateModels full(
            String closed,
            String open,
            String wallClosed,
            String wallOpen
    ) {
        return builder()
                .closed(closed)
                .open(open)
                .wallClosed(wallClosed)
                .wallOpen(wallOpen)
                .build();
    }

    @Nullable
    public ResourceLocation model(boolean open, boolean inWall) {
        if (inWall) {
            return open ? wallOpen : wallClosed;
        }

        return open ? this.open : closed;
    }

    @Nullable
    public ResourceLocation closed() {
        return closed;
    }

    public boolean complete() {
        return closed != null && open != null && wallClosed != null && wallOpen != null;
    }

    public boolean isEmpty() {
        return closed == null && open == null && wallClosed == null && wallOpen == null;
    }

    public static final class Builder {
        private ResourceLocation closed;
        private ResourceLocation open;
        private ResourceLocation wallClosed;
        private ResourceLocation wallOpen;

        private Builder() {}

        public Builder closed(String model) {
            closed = parse(model, "closed");
            return this;
        }

        public Builder open(String model) {
            open = parse(model, "open");
            return this;
        }

        public Builder wallClosed(String model) {
            wallClosed = parse(model, "wallClosed");
            return this;
        }

        public Builder wallOpen(String model) {
            wallOpen = parse(model, "wallOpen");
            return this;
        }

        public FenceGateModels build() {
            return new FenceGateModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return ResourceLocation.parse(model);
    }
}
