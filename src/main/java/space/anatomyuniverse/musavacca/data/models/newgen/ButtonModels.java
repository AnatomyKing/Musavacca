package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.Nullable;

//? if >=1.21.11 {
/*public record ButtonModels(Identifier normal, Identifier pressed, Identifier inventory) {
*///?} else {
public record ButtonModels(ResourceLocation normal, ResourceLocation pressed, ResourceLocation inventory) {
//?}
    public ButtonModels {
        if (normal == null && pressed == null && inventory == null) throw new IllegalStateException("ButtonModels requires at least one model");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ButtonModels full(String normal, String pressed, String inventory) {
        return builder().normal(normal).pressed(pressed).inventory(inventory).build();
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier model(boolean powered) {
    *///?} else {
    public ResourceLocation model(boolean powered) {
    //?}
        return powered ? pressed : normal;
    }

    public static final class Builder {
        //? if >=1.21.11 {
        /*private Identifier normal;
        private Identifier pressed;
        private Identifier inventory;

        *///?} else {
        private ResourceLocation normal;
        private ResourceLocation pressed;
        private ResourceLocation inventory;

        //?}
        private Builder() {}

        public Builder normal(String model) {
            normal = parse(model, "normal");
            return this;
        }

        public Builder pressed(String model) {
            pressed = parse(model, "pressed");
            return this;
        }

        public Builder inventory(String model) {
            inventory = parse(model, "inventory");
            return this;
        }

        public ButtonModels build() {
            return new ButtonModels(normal, pressed, inventory);
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
