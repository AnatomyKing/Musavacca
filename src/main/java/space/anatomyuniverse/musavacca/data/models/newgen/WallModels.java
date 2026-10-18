package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.Nullable;

public final class WallModels {
    //? if >=1.21.11 {
    /*private final Identifier post;
    private final Identifier lowSide;
    private final Identifier tallSide;
    private final Identifier inventory;
    *///?} else {
    private final ResourceLocation post;
    private final ResourceLocation lowSide;
    private final ResourceLocation tallSide;
    private final ResourceLocation inventory;
    //?}

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
    //? if >=1.21.11 {
    /*public Identifier post() {
    *///?} else {
    public ResourceLocation post() {
    //?}
        return post;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier lowSide() {
    *///?} else {
    public ResourceLocation lowSide() {
    //?}
        return lowSide;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier tallSide() {
    *///?} else {
    public ResourceLocation tallSide() {
    //?}
        return tallSide;
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
        return post != null && lowSide != null && tallSide != null;
    }

    public boolean complete() {
        return worldComplete() && inventory != null;
    }

    public boolean isEmpty() {
        return post == null && lowSide == null && tallSide == null && inventory == null;
    }

    public static final class Builder {
        //? if >=1.21.11 {
        /*private Identifier post;
        private Identifier lowSide;
        private Identifier tallSide;
        private Identifier inventory;
        *///?} else {
        private ResourceLocation post;
        private ResourceLocation lowSide;
        private ResourceLocation tallSide;
        private ResourceLocation inventory;
        //?}

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
