package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public final class Conditions {
    private final Entry<?>[] entries;

    private Conditions(Entry<?>[] entries) {
        this.entries = entries == null ? new Entry<?>[0] : entries;
    }

    public static Conditions of(Entry<?>... entries) {
        if (entries == null || entries.length == 0) {
            throw new IllegalArgumentException("Conditions.of requires at least one condition entry.");
        }
        return new Conditions(entries);
    }

    public static <T extends Comparable<T>> Conditions when(Property<T> property, T value) {
        return of(entry(property, value));
    }

    public static <T extends Comparable<T>> Entry<T> entry(Property<T> property, T value) {
        if (property == null) throw new IllegalArgumentException("Condition property cannot be null.");
        if (value == null) throw new IllegalArgumentException("Condition value cannot be null.");
        return new Entry<>(property, value);
    }

    public Conditions and(Conditions other) {
        if (other == null || other.isEmpty()) return this;
        Entry<?>[] combined = new Entry<?>[entries.length + other.entries.length];
        System.arraycopy(entries, 0, combined, 0, entries.length);
        System.arraycopy(other.entries, 0, combined, entries.length, other.entries.length);
        return new Conditions(combined);
    }

    public Conditions and(Entry<?>... extra) {
        if (extra == null || extra.length == 0) return this;
        Entry<?>[] combined = new Entry<?>[entries.length + extra.length];
        System.arraycopy(entries, 0, combined, 0, entries.length);
        System.arraycopy(extra, 0, combined, entries.length, extra.length);
        return new Conditions(combined);
    }

    public boolean isEmpty() {
        return entries.length == 0;
    }

    public Entry<?>[] entries() {
        return entries.clone();
    }

    public boolean matches(BlockState state) {
        if (state == null) return false;
        for (Entry<?> entry : entries) {
            if (entry == null) continue;
            if (!entry.matches(state)) return false;
        }
        return true;
    }

    public Entry<?> entryFor(Property<?> property) {
        if (property == null) return null;
        for (Entry<?> entry : entries) {
            if (entry != null && entry.property() == property) return entry;
        }
        return null;
    }

    public List<Property<?>> properties() {
        List<Property<?>> result = new ArrayList<>();
        for (Entry<?> entry : entries) {
            if (entry == null || entry.property() == null) continue;
            if (!result.contains(entry.property())) result.add(entry.property());
        }
        return result;
    }

    public record Entry<T extends Comparable<T>>(Property<T> property, T value) {
        public boolean matches(BlockState state) {
            return state.hasProperty(property) && state.getValue(property).equals(value);
        }
    }
}
