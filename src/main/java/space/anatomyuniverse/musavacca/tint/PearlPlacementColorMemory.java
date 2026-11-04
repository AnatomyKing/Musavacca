package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PearlPlacementColorMemory {
    private static final Map<Long, Entry> ENTRIES = new ConcurrentHashMap<>();
    private PearlPlacementColorMemory() {}
    public static void remember(Level level, BlockPos pos, int rgb) {
        if (level == null || pos == null || !level.isClientSide() || !MusavaccaTints.hasTint(rgb)) return;
        long now = System.currentTimeMillis();
        int color = MusavaccaTints.rgb(rgb);
        long key = pos.asLong();
        Entry previous = ENTRIES.get(key);
        if (previous != null && previous.level() == level && previous.rgb() == color
                && previous.expiresAtMs() - now > 2750L) return;
        if (ENTRIES.size() >= 1024 && previous == null) {
            ENTRIES.entrySet().removeIf(entry -> now > entry.getValue().expiresAtMs());
            if (ENTRIES.size() >= 1024) ENTRIES.clear();
        }
        ENTRIES.put(key, new Entry(level, color, now + 3000L));
    }

    public static Integer get(Level level, BlockPos pos) {
        if (level == null || pos == null) return null;
        Entry entry = ENTRIES.get(pos.asLong());
        if (entry == null) return null;
        if (entry.level() != level || System.currentTimeMillis() > entry.expiresAtMs()) {
            ENTRIES.remove(pos.asLong(), entry);
            return null;
        }
        return entry.rgb();
    }

    private record Entry(Level level, int rgb, long expiresAtMs) {}
}
