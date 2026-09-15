package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class ButtonModels {
    private final ResourceLocation normal;
    private final ResourceLocation pressed;
    private final ResourceLocation inventory;

    private ButtonModels(Builder builder) {
        normal = builder.normal;
        pressed = builder.pressed;
        inventory = builder.inventory;

        if (isEmpty()) {
            throw new IllegalStateException("ButtonModels requires at least one model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ButtonModels full(String normal, String pressed, String inventory) {
        return builder().normal(normal).pressed(pressed).inventory(inventory).build();
    }

    @Nullable
    public ResourceLocation model(boolean powered) {
        return powered ? pressed : normal;
    }

    @Nullable
    public ResourceLocation inventory() {
        return inventory;
    }

    public boolean worldComplete() {
        return normal != null && pressed != null;
    }

    public boolean complete() {
        return worldComplete() && inventory != null;
    }

    public boolean isEmpty() {
        return normal == null && pressed == null && inventory == null;
    }

    public static final class Builder {
        private ResourceLocation normal;
        private ResourceLocation pressed;
        private ResourceLocation inventory;

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
            return new ButtonModels(this);
        }
    }

    private static ResourceLocation parse(String model, String name) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return ResourceLocation.parse(model);
    }
}
