package space.anatomyuniverse.musavacca.data.models.engine.core;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.unified.Conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class EngineConditions {
    private EngineConditions() {}

    public static boolean matches(BlockState state, Conditions conditions) {
        return conditions == null || conditions.matches(state);
    }

    public static boolean has(Conditions conditions) {
        return conditions != null && !conditions.isEmpty();
    }

    public static <T> List<Property<?>> properties(T[] entries, Function<T, Conditions> getter) {
        List<Property<?>> result = new ArrayList<>();
        if (entries == null || getter == null) return result;
        for (T entry : entries) {
            if (entry == null) continue;
            Conditions conditions = getter.apply(entry);
            if (conditions == null || conditions.isEmpty()) continue;
            for (Property<?> property : conditions.properties()) {
                if (property != null && !result.contains(property)) result.add(property);
            }
        }
        return result;
    }
}
