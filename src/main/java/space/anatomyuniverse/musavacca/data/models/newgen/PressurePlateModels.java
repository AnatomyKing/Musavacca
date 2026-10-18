package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.Nullable;

public final class PressurePlateModels {
    //? if >=1.21.11 {
    /*private final Identifier up;
    private final Identifier down;
    *///?} else {
    private final ResourceLocation up;
    private final ResourceLocation down;
    //?}

    private PressurePlateModels(Builder builder) {
        up = builder.up;
        down = builder.down;

        if (isEmpty()) {
            throw new IllegalStateException("PressurePlateModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PressurePlateModels full(String up, String down) {
        return builder().up(up).down(down).build();
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier model(boolean powered) {
    *///?} else {
    public ResourceLocation model(boolean powered) {
    //?}
        return powered ? down : up;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier up() {
    *///?} else {
    public ResourceLocation up() {
    //?}
        return up;
    }

    public boolean complete() {
        return up != null && down != null;
    }

    public boolean isEmpty() {
        return up == null && down == null;
    }

    public static final class Builder {
        //? if >=1.21.11 {
        /*private Identifier up;
        private Identifier down;
        *///?} else {
        private ResourceLocation up;
        private ResourceLocation down;
        //?}

        private Builder() {}

        public Builder up(String model) {
            up = parse(model, "up");
            return this;
        }

        public Builder down(String model) {
            down = parse(model, "down");
            return this;
        }

        public PressurePlateModels build() {
            return new PressurePlateModels(this);
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
