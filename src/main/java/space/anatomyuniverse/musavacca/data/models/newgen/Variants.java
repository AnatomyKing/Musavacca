package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Variants {
    private Variants() {}

    public record Option(int rotationX, int rotationY, int weight) {
        public Option {
            rotationX = ModelTransforms.quarterTurn(rotationX);
            rotationY = ModelTransforms.quarterTurn(rotationY);

            if (weight <= 0) {
                throw new IllegalArgumentException("Variant weight must be greater than zero");
            }
        }

        public Option rotateX(int degrees) {
            return new Option(degrees, rotationY, weight);
        }

        public Option rotateY(int degrees) {
            return new Option(rotationX, degrees, weight);
        }

        public Option weight(int weight) {
            return new Option(rotationX, rotationY, weight);
        }
    }

    public record Set(List<Option> options) {
        public Set {
            Objects.requireNonNull(options, "options");

            if (options.isEmpty()) {
                throw new IllegalArgumentException("Variants requires at least one option");
            }

            for (Option option : options) {
                Objects.requireNonNull(option, "option");
            }

            options = List.copyOf(options);
        }
    }

    public static Option option() {
        return new Option(0, 0, 1);
    }

    private static final Set SINGLE = of(option());

    public static Set single() { return SINGLE; }

    public static Set randomY() {
        return of(
                option(),
                option().rotateY(90),
                option().rotateY(180),
                option().rotateY(270)
        );
    }

    public static Set of(Option... options) {
        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("Variants.of(...) requires at least one option");
        }

        return new Set(Arrays.asList(options));
    }

}

