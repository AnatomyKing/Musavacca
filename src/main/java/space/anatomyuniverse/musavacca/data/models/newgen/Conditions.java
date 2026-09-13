package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Conditions {
    private Conditions() {}

    public record Term<T extends Comparable<T>>(Property<T> property, T value) {
        public Term {
            Objects.requireNonNull(property, "property");
            Objects.requireNonNull(value, "value");
        }

        public boolean matches(BlockState state) {
            return state.hasProperty(property)
                    && Objects.equals(state.getValue(property), value);
        }
    }

    public static final class Match {
        private final List<Term<?>> terms;

        private Match(List<Term<?>> terms) {
            this.terms = terms;
        }

        public <T extends Comparable<T>> Match and(Property<T> property, T value) {
            ArrayList<Term<?>> copy = new ArrayList<>(terms);

            copy.add(new Term<>(property, value));

            return new Match(copy);
        }

        public Match and(Match other) {
            Objects.requireNonNull(other, "other");

            ArrayList<Term<?>> copy = new ArrayList<>(terms);

            copy.addAll(other.terms);

            return new Match(copy);
        }

        public boolean isAlways() {
            return terms.isEmpty();
        }

        public boolean matches(BlockState state) {
            for (Term<?> term : terms) {
                if (!matchesTerm(state, term)) {
                    return false;
                }
            }

            return true;
        }

        public List<Term<?>> terms() {
            return Collections.unmodifiableList(terms);
        }
    }

    public static Match always() {
        return new Match(List.of());
    }

    public static <T extends Comparable<T>> Match when(Property<T> property, T value) {
        return always()
                .and(property, value);
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private static boolean matchesTerm(BlockState state, Term<?> term) {
        Property property = term.property();

        if (!state.hasProperty(property)) {
            return false;
        }

        return Objects.equals(state.getValue(property), term.value());
    }
}
