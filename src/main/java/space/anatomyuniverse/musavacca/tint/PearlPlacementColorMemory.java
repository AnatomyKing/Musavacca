package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class PearlPlacementColorMemory {
    private static final long ENTRY_LIFETIME_MS = 1500L;
    private static final int MAX_ENTRIES = 1024;

    private static final Map<Long, Entry> ENTRIES = new ConcurrentHashMap<>();

    private PearlPlacementColorMemory() {}

    public static void remember(Level level, BlockPos pos, int rgb) {
        if (level == null || pos == null || !level.isClientSide()
                || !MusavaccaTints.hasTint(rgb)) {
            return;
        }

        long now = System.currentTimeMillis();
        int color = MusavaccaTints.rgb(rgb);
        long key = pos.asLong();
        Entry previous = ENTRIES.get(key);
        if (previous != null && previous.level() == level && previous.rgb() == color
                && previous.expiresAtMs() - now > ENTRY_LIFETIME_MS - 250L) {
            return;
        }
        if (ENTRIES.size() >= MAX_ENTRIES && previous == null) {
            ENTRIES.entrySet().removeIf(entry -> now > entry.getValue().expiresAtMs());
            if (ENTRIES.size() >= MAX_ENTRIES) {
                ENTRIES.clear();
            }
        }

        ENTRIES.put(key, new Entry(level, color, now + ENTRY_LIFETIME_MS));
    }

    public static Integer get(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }

        Entry entry = ENTRIES.get(pos.asLong());
        if (entry == null) {
            return null;
        }

        if (entry.level() != level || System.currentTimeMillis() > entry.expiresAtMs()) {
            ENTRIES.remove(pos.asLong(), entry);
            return null;
        }

        return entry.rgb();
    }

    public static void clear(BlockPos pos) {
        if (pos != null) {
            ENTRIES.remove(pos.asLong());
        }
    }

    private record Entry(Level level, int rgb, long expiresAtMs) {}
}
