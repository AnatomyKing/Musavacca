package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
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
            this.terms = List.copyOf(terms);
        }

        public <T extends Comparable<T>> Match and(Property<T> property, T value) {
            Objects.requireNonNull(property, "property");
            Objects.requireNonNull(value, "value");

            for (Term<?> term : terms) {
                if (term.property() == property) {
                    if (Objects.equals(term.value(), value)) {
                        return this;
                    }

                    throw new IllegalArgumentException(
                            "Condition already requires " + property.getName()
                                    + "=" + term.value()
                                    + "; cannot also require " + value
                    );
                }
            }

            ArrayList<Term<?>> copy = new ArrayList<>(terms);
            copy.add(new Term<>(property, value));
            return new Match(copy);
        }

        public Match and(Match other) {
            Objects.requireNonNull(other, "other");

            Match result = this;

            for (Term<?> term : other.terms) {
                result = andUnchecked(result, term);
            }

            return result;
        }

        public boolean allows(Property<?> property, Comparable<?> value) {
            Objects.requireNonNull(property, "property");
            Objects.requireNonNull(value, "value");

            for (Term<?> term : terms) {
                if (term.property() == property
                        && !Objects.equals(term.value(), value)) {
                    return false;
                }
            }

            return true;
        }

        public boolean contains(Property<?> property) {
            Objects.requireNonNull(property, "property");

            for (Term<?> term : terms) {
                if (term.property() == property) {
                    return true;
                }
            }

            return false;
        }

        public Match without(Property<?>... properties) {
            Objects.requireNonNull(properties, "properties");

            if (properties.length == 0 || terms.isEmpty()) {
                return this;
            }

            ArrayList<Term<?>> filtered = new ArrayList<>(terms.size());

            outer:
            for (Term<?> term : terms) {
                for (Property<?> property : properties) {
                    if (term.property() == Objects.requireNonNull(property, "property")) {
                        continue outer;
                    }
                }

                filtered.add(term);
            }

            return filtered.size() == terms.size()
                    ? this
                    : new Match(filtered);
        }

        public boolean isAlways() {
            return terms.isEmpty();
        }

        public boolean matches(BlockState state) {
            for (Term<?> term : terms) {
                if (!term.matches(state)) {
                    return false;
                }
            }

            return true;
        }

        public List<Term<?>> terms() { return terms; }

        @Override public boolean equals(Object other) {
            return other instanceof Match match && terms.size() == match.terms.size()
                    && terms.containsAll(match.terms);
        }

        @Override public int hashCode() {
            return terms.stream().mapToInt(Term::hashCode).sum();
        }
    }

    private static final Match ALWAYS = new Match(List.of());

    public static Match always() { return ALWAYS; }

    public static <T extends Comparable<T>> Match when(Property<T> property, T value) {
        return always()
                .and(property, value);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Match andUnchecked(Match match, Term<?> term) {
        return match.and((Property) term.property(), (Comparable) term.value());
    }

}
