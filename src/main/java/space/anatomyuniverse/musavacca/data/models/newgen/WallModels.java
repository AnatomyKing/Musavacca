package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class WallModels {
    private final ResourceLocation post;
    private final ResourceLocation lowSide;
    private final ResourceLocation tallSide;
    private final ResourceLocation inventory;

    private WallModels(Builder builder) {
        post = builder.post;
        lowSide = builder.lowSide;
        tallSide = builder.tallSide;
        inventory = builder.inventory;

        if (isEmpty()) {
            throw new IllegalStateException("WallModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static WallModels full(
            String post,
            String lowSide,
            String tallSide,
            String inventory
    ) {
        return builder()
                .post(post)
                .lowSide(lowSide)
                .tallSide(tallSide)
                .inventory(inventory)
                .build();
    }

    @Nullable
    public ResourceLocation post() {
        return post;
    }

    @Nullable
    public ResourceLocation lowSide() {
        return lowSide;
    }

    @Nullable
    public ResourceLocation tallSide() {
        return tallSide;
    }

    @Nullable
    public ResourceLocation inventory() {
        return inventory;
    }

    public boolean worldComplete() {
        return post != null && lowSide != null && tallSide != null;
    }

    public boolean complete() {
        return worldComplete() && inventory != null;
    }

    public boolean isEmpty() {
        return post == null && lowSide == null && tallSide == null && inventory == null;
    }

    public static final class Builder {
        private ResourceLocation post;
        private ResourceLocation lowSide;
        private ResourceLocation tallSide;
        private ResourceLocation inventory;

        private Builder() {}

        public Builder post(String model) {
            post = parse(model, "post");
            return this;
        }

        public Builder lowSide(String model) {
            lowSide = parse(model, "lowSide");
            return this;
        }

        public Builder tallSide(String model) {
            tallSide = parse(model, "tallSide");
            return this;
        }

        public Builder inventory(String model) {
            inventory = parse(model, "inventory");
            return this;
        }

        public WallModels build() {
            return new WallModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return ResourceLocation.parse(model);
    }
}
