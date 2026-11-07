package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

//? if >=1.21.11 {
/*public record WallModels(Identifier post, Identifier lowSide, Identifier tallSide, Identifier inventory) {
*///?} else {
public record WallModels(ResourceLocation post, ResourceLocation lowSide, ResourceLocation tallSide, ResourceLocation inventory) {
//?}
    public WallModels {
        if (post == null && lowSide == null && tallSide == null && inventory == null) throw new IllegalStateException("WallModels requires at least one model");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static WallModels full(String post, String lowSide, String tallSide, String inventory) {
        return builder().post(post).lowSide(lowSide).tallSide(tallSide).inventory(inventory).build();
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
            return new WallModels(post, lowSide, tallSide, inventory);
        }
    }

    //? if >=1.21.11 {
    /*private static Identifier parse(String model, String name) {
    *///?} else {
    private static ResourceLocation parse(String model, String name) {
    //?}
        if (model == null || model.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        //? if >=1.21.11 {
        /*return Identifier.parse(model);
        *///?} else {
        return ResourceLocation.parse(model);
        //?}
    }
}
