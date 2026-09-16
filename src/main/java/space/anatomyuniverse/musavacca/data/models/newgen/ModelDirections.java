package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.Direction;

import java.util.List;

public final class ModelDirections {
    private static final List<Direction> HORIZONTAL = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    );

    private ModelDirections() {}

    public static List<Direction> horizontal() {
        return HORIZONTAL;
    }
}

