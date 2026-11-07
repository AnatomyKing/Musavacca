package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

//? if >=1.21.11 {
/*public record FenceModels(Identifier post, Identifier side, Identifier inventory) {
*///?} else {
public record FenceModels(ResourceLocation post, ResourceLocation side, ResourceLocation inventory) {
//?}
    public FenceModels {
        if (post == null && side == null && inventory == null) throw new IllegalStateException("FenceModels requires at least one model");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FenceModels full(String post, String side, String inventory) {
        return builder().post(post).side(side).inventory(inventory).build();
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
            return new FenceModels(post, side, inventory);
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
