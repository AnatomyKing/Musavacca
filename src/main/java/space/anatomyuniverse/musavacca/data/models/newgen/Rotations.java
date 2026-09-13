package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Objects;

public final class Rotations {
    private Rotations() {}

    public enum Type {
        BRICKS,
        LOG,
        FURNACE,
        SHULKER_BOX,
        CRAFTER
    }

    public record Case(Comparable<?> value, int x, int y) {}

    public record Spec(Type type, Property<?> property) {
        public Spec {
            Objects.requireNonNull(type, "type");

            if (type != Type.BRICKS && property == null) {
                throw new IllegalArgumentException(type + " requires a block-state property");
            }
        }

        public List<Case> cases() {
            return switch (type) {
                case BRICKS ->
                        List.of(new Case(null, 0, 0));

                case LOG ->
                        List.of(
                                new Case(Direction.Axis.Y, 0, 0), 
                                new Case(Direction.Axis.Z, 90, 0), 
                                new Case(Direction.Axis.X, 90, 90)
                        );

                case FURNACE ->
                        List.of(
                                new Case(Direction.NORTH, 0, 0), 
                                new Case(Direction.EAST, 0, 90), 
                                new Case(Direction.SOUTH, 0, 180), 
                                new Case(Direction.WEST, 0, 270)
                        );

                /*
                 * Canonical model points UP.
                 *
                 * This is the same six-direction mapping used by
                 * BananaPearlChapiter.
                 */
                case SHULKER_BOX ->
                        List.of(
                                new Case(Direction.UP, 0, 0), 
                                new Case(Direction.DOWN, 180, 0), 
                                new Case(Direction.NORTH, 90, 0), 
                                new Case(Direction.SOUTH, 90, 180), 
                                new Case(Direction.WEST, 90, 270), 
                                new Case(Direction.EAST, 90, 90)
                        );

                /*
                 * Canonical model is NORTH_UP.
                 */
                case CRAFTER ->
                        List.of(
                                new Case(FrontAndTop.DOWN_EAST, 90, 90), 
                                new Case(FrontAndTop.DOWN_NORTH, 90, 0), 
                                new Case(FrontAndTop.DOWN_SOUTH, 90, 180), 
                                new Case(FrontAndTop.DOWN_WEST, 90, 270), 

                                new Case(FrontAndTop.EAST_UP, 0, 90), 
                                new Case(FrontAndTop.NORTH_UP, 0, 0), 
                                new Case(FrontAndTop.SOUTH_UP, 0, 180), 
                                new Case(FrontAndTop.WEST_UP, 0, 270), 

                                new Case(FrontAndTop.UP_EAST, 270, 270), 
                                new Case(FrontAndTop.UP_NORTH, 270, 180), 
                                new Case(FrontAndTop.UP_SOUTH, 270, 0), 
                                new Case(FrontAndTop.UP_WEST, 270, 90)
                        );
            };
        }
    }

    public static Spec bricks() {
        return new Spec(Type.BRICKS, null);
    }

    public static Spec log() {
        return log(BlockStateProperties.AXIS);
    }

    public static Spec log(Property<Direction.Axis> property) {
        return new Spec(Type.LOG, property);
    }

    public static Spec furnace() {
        return furnace(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static Spec furnace(Property<Direction> property) {
        return new Spec(Type.FURNACE, property);
    }

    public static Spec shulkerBox() {
        return shulkerBox(BlockStateProperties.FACING);
    }

    public static Spec shulkerBox(Property<Direction> property) {
        return new Spec(Type.SHULKER_BOX, property);
    }

    public static Spec crafter() {
        return crafter(BlockStateProperties.ORIENTATION);
    }

    public static Spec crafter(Property<FrontAndTop> property) {
        return new Spec(Type.CRAFTER, property);
    }
}
