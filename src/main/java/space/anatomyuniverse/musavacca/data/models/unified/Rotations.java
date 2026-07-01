package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public final class Rotations {
    private final Property<?> property;
    private final Rule[] rules;

    private Rotations(Property<?> property, Rule[] rules) {
        this.property = property;
        this.rules = rules == null ? new Rule[0] : rules;
    }

    public static Rotations of(Property<?> property, Rule... rules) {
        if (property == null || rules == null || rules.length == 0) {
            throw new IllegalArgumentException("Rotations.of requires a property and at least one rotation rule.");
        }
        return new Rotations(property, rules);
    }

    public static Rotations facing(EnumProperty<Direction> property) {
        return of(property,
                when(Direction.UP, 0, 0),
                when(Direction.DOWN, 180, 0),
                when(Direction.NORTH, 90, 0),
                when(Direction.SOUTH, 90, 180),
                when(Direction.WEST, 90, 270),
                when(Direction.EAST, 90, 90));
    }

    public static Rotations horizontalFacing(EnumProperty<Direction> property) {
        return of(property,
                when(Direction.NORTH, 0, 0),
                when(Direction.EAST, 0, 90),
                when(Direction.SOUTH, 0, 180),
                when(Direction.WEST, 0, 270));
    }

    public static Rotations horizontalFacingMirrored(EnumProperty<Direction> property) {
        return of(property,
                when(Direction.NORTH, 0, 180),
                when(Direction.EAST, 0, 270),
                when(Direction.SOUTH, 0, 0),
                when(Direction.WEST, 0, 90));
    }

    public static Rotations axis(EnumProperty<Direction.Axis> property) {
        return of(property,
                when(Direction.Axis.Y, 0, 0),
                when(Direction.Axis.Z, 90, 0),
                when(Direction.Axis.X, 90, 90));
    }

    public static Rule when(Comparable<?> value, int xDeg, int yDeg) {
        if (value == null) throw new IllegalArgumentException("Rotation value cannot be null.");
        return new Rule(value, normalize(xDeg), normalize(yDeg));
    }

    public boolean enabled() {
        return property != null && rules.length > 0;
    }

    public Property<?> property() {
        return property;
    }

    public Rule[] rules() {
        return rules.clone();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Rotation forState(BlockState state) {
        if (!enabled() || state == null || !state.hasProperty((Property) property)) return Rotation.ZERO;
        Comparable<?> value = (Comparable<?>) state.getValue((Property) property);
        for (Rule rule : rules) {
            if (rule != null && rule.value().equals(value)) return new Rotation(rule.xDeg(), rule.yDeg());
        }
        return Rotation.ZERO;
    }

    private static int normalize(int deg) {
        int normalized = Math.floorMod(deg, 360);
        return switch (normalized) {
            case 0, 90, 180, 270 -> normalized;
            default -> throw new IllegalArgumentException("Unsupported model rotation: " + deg);
        };
    }

    public record Rule(Comparable<?> value, int xDeg, int yDeg) {
        public Direction direction() {
            return value instanceof Direction direction ? direction : null;
        }
    }

    public record Rotation(int xDeg, int yDeg) {
        public static final Rotation ZERO = new Rotation(0, 0);
    }
}
