package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

public final class DoorModels {
    private final ResourceLocation bottomLeft;
    private final ResourceLocation bottomLeftOpen;
    private final ResourceLocation bottomRight;
    private final ResourceLocation bottomRightOpen;
    private final ResourceLocation topLeft;
    private final ResourceLocation topLeftOpen;
    private final ResourceLocation topRight;
    private final ResourceLocation topRightOpen;

    private DoorModels(Builder builder) {
        this.bottomLeft = builder.bottomLeft;
        this.bottomLeftOpen = builder.bottomLeftOpen;
        this.bottomRight = builder.bottomRight;
        this.bottomRightOpen = builder.bottomRightOpen;
        this.topLeft = builder.topLeft;
        this.topLeftOpen = builder.topLeftOpen;
        this.topRight = builder.topRight;
        this.topRightOpen = builder.topRightOpen;

        if (isEmpty()) {
            throw new IllegalStateException("DoorModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DoorModels lower(
            String bottomLeft,
            String bottomLeftOpen,
            String bottomRight,
            String bottomRightOpen
    ) {
        return builder()
                .bottomLeft(bottomLeft)
                .bottomLeftOpen(bottomLeftOpen)
                .bottomRight(bottomRight)
                .bottomRightOpen(bottomRightOpen)
                .build();
    }

    public static DoorModels full(
            String bottomLeft,
            String bottomLeftOpen,
            String bottomRight,
            String bottomRightOpen,
            String topLeft,
            String topLeftOpen,
            String topRight,
            String topRightOpen
    ) {
        return builder()
                .bottomLeft(bottomLeft)
                .bottomLeftOpen(bottomLeftOpen)
                .bottomRight(bottomRight)
                .bottomRightOpen(bottomRightOpen)
                .topLeft(topLeft)
                .topLeftOpen(topLeftOpen)
                .topRight(topRight)
                .topRightOpen(topRightOpen)
                .build();
    }

    @Nullable
    public ResourceLocation model(
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open
    ) {
        if (half == DoubleBlockHalf.LOWER) {
            if (hinge == DoorHingeSide.LEFT) {
                return open
                        ? bottomLeftOpen
                        : bottomLeft;
            }

            return open
                    ? bottomRightOpen
                    : bottomRight;
        }

        if (hinge == DoorHingeSide.LEFT) {
            return open
                    ? topLeftOpen
                    : topLeft;
        }

        return open
                ? topRightOpen
                : topRight;
    }

    public boolean isEmpty() {
        return bottomLeft == null
                && bottomLeftOpen == null
                && bottomRight == null
                && bottomRightOpen == null
                && topLeft == null
                && topLeftOpen == null
                && topRight == null
                && topRightOpen == null;
    }

    public static final class Builder {
        private ResourceLocation bottomLeft;
        private ResourceLocation bottomLeftOpen;
        private ResourceLocation bottomRight;
        private ResourceLocation bottomRightOpen;
        private ResourceLocation topLeft;
        private ResourceLocation topLeftOpen;
        private ResourceLocation topRight;
        private ResourceLocation topRightOpen;

        private Builder() {}

        public Builder bottomLeft(String model) {
            this.bottomLeft = parse(model, "bottomLeft");
            return this;
        }

        public Builder bottomLeftOpen(String model) {
            this.bottomLeftOpen = parse(model, "bottomLeftOpen");
            return this;
        }

        public Builder bottomRight(String model) {
            this.bottomRight = parse(model, "bottomRight");
            return this;
        }

        public Builder bottomRightOpen(String model) {
            this.bottomRightOpen = parse(model, "bottomRightOpen");
            return this;
        }

        public Builder topLeft(String model) {
            this.topLeft = parse(model, "topLeft");
            return this;
        }

        public Builder topLeftOpen(String model) {
            this.topLeftOpen = parse(model, "topLeftOpen");
            return this;
        }

        public Builder topRight(String model) {
            this.topRight = parse(model, "topRight");
            return this;
        }

        public Builder topRightOpen(String model) {
            this.topRightOpen = parse(model, "topRightOpen");
            return this;
        }

        public DoorModels build() {
            return new DoorModels(this);
        }

        private static ResourceLocation parse(String model, String name) {
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException(name + " must not be blank");
            }

            return ResourceLocation.parse(model);
        }
    }
}

