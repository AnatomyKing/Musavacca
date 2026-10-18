package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public final class SlabModels {
    //? if >=1.21.11 {
    /*private final Identifier bottom;
    private final Identifier top;
    private final Identifier doubleModel;
    *///?} else {
    private final ResourceLocation bottom;
    private final ResourceLocation top;
    private final ResourceLocation doubleModel;
    //?}

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
    //? if >=1.21.11 {
    /*public Identifier model(SlabType type) {
    *///?} else {
    public ResourceLocation model(SlabType type) {
    //?}
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
        //? if >=1.21.11 {
        /*private Identifier bottom;
        private Identifier top;
        private Identifier doubleModel;
        *///?} else {
        private ResourceLocation bottom;
        private ResourceLocation top;
        private ResourceLocation doubleModel;
        //?}

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

    //? if >=1.21.11 {
    /*private static Identifier parse(String model, String name) {
    *///?} else {
    private static ResourceLocation parse(String model, String name) {
    //?}
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }

        //? if >=1.21.11 {
        /*return Identifier.parse(model);
        *///?} else {
        return ResourceLocation.parse(model);
        //?}
    }
}
