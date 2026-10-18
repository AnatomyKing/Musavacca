package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.Nullable;

public final class FenceModels {
    //? if >=1.21.11 {
    /*private final Identifier post;
    private final Identifier side;
    private final Identifier inventory;
    *///?} else {
    private final ResourceLocation post;
    private final ResourceLocation side;
    private final ResourceLocation inventory;
    //?}

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
    //? if >=1.21.11 {
    /*public Identifier post() {
    *///?} else {
    public ResourceLocation post() {
    //?}
        return post;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier side() {
    *///?} else {
    public ResourceLocation side() {
    //?}
        return side;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier inventory() {
    *///?} else {
    public ResourceLocation inventory() {
    //?}
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
        //? if >=1.21.11 {
        /*private Identifier post;
        private Identifier side;
        private Identifier inventory;
        *///?} else {
        private ResourceLocation post;
        private ResourceLocation side;
        private ResourceLocation inventory;
        //?}

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
