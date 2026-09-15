package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class FenceModels {
    private final ResourceLocation post;
    private final ResourceLocation side;
    private final ResourceLocation inventory;

    private FenceModels(Builder builder) {
        post = builder.post;
        side = builder.side;
        inventory = builder.inventory;

        if (isEmpty()) {
            throw new IllegalStateException("FenceModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FenceModels full(String post, String side, String inventory) {
        return builder().post(post).side(side).inventory(inventory).build();
    }

    @Nullable
    public ResourceLocation post() {
        return post;
    }

    @Nullable
    public ResourceLocation side() {
        return side;
    }

    @Nullable
    public ResourceLocation inventory() {
        return inventory;
    }

    public boolean worldComplete() {
        return post != null && side != null;
    }

    public boolean complete() {
        return worldComplete() && inventory != null;
    }

    public boolean isEmpty() {
        return post == null && side == null && inventory == null;
    }

    public static final class Builder {
        private ResourceLocation post;
        private ResourceLocation side;
        private ResourceLocation inventory;

        private Builder() {}

        public Builder post(String model) {
            post = parse(model, "post");
            return this;
        }

        public Builder side(String model) {
            side = parse(model, "side");
            return this;
        }

        public Builder inventory(String model) {
            inventory = parse(model, "inventory");
            return this;
        }

        public FenceModels build() {
            return new FenceModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return ResourceLocation.parse(model);
    }
}
