package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.jetbrains.annotations.Nullable;

public final class FenceGateModels {
    //? if >=1.21.11 {
    /*private final Identifier closed;
    private final Identifier open;
    private final Identifier wallClosed;
    private final Identifier wallOpen;
    *///?} else {
    private final ResourceLocation closed;
    private final ResourceLocation open;
    private final ResourceLocation wallClosed;
    private final ResourceLocation wallOpen;
    //?}

    private FenceGateModels(Builder builder) {
        closed = builder.closed;
        open = builder.open;
        wallClosed = builder.wallClosed;
        wallOpen = builder.wallOpen;

        if (isEmpty()) {
            throw new IllegalStateException("FenceGateModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FenceGateModels full(
            String closed,
            String open,
            String wallClosed,
            String wallOpen
    ) {
        return builder()
                .closed(closed)
                .open(open)
                .wallClosed(wallClosed)
                .wallOpen(wallOpen)
                .build();
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier model(boolean open, boolean inWall) {
    *///?} else {
    public ResourceLocation model(boolean open, boolean inWall) {
    //?}
        if (inWall) {
            return open ? wallOpen : wallClosed;
        }

        return open ? this.open : closed;
    }

    @Nullable
    //? if >=1.21.11 {
    /*public Identifier closed() {
    *///?} else {
    public ResourceLocation closed() {
    //?}
        return closed;
    }

    public boolean complete() {
        return closed != null && open != null && wallClosed != null && wallOpen != null;
    }

    public boolean isEmpty() {
        return closed == null && open == null && wallClosed == null && wallOpen == null;
    }

    public static final class Builder {
        //? if >=1.21.11 {
        /*private Identifier closed;
        private Identifier open;
        private Identifier wallClosed;
        private Identifier wallOpen;
        *///?} else {
        private ResourceLocation closed;
        private ResourceLocation open;
        private ResourceLocation wallClosed;
        private ResourceLocation wallOpen;
        //?}

        private Builder() {}

        public Builder closed(String model) {
            closed = parse(model, "closed");
            return this;
        }

        public Builder open(String model) {
            open = parse(model, "open");
            return this;
        }

        public Builder wallClosed(String model) {
            wallClosed = parse(model, "wallClosed");
            return this;
        }

        public Builder wallOpen(String model) {
            wallOpen = parse(model, "wallOpen");
            return this;
        }

        public FenceGateModels build() {
            return new FenceGateModels(this);
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
