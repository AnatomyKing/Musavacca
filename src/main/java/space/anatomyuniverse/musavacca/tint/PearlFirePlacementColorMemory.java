// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/PearlFirePlacementColorMemory.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PearlFirePlacementColorMemory {
    private static final long ENTRY_LIFETIME_MS = 1500L;
    private static final int MAX_ENTRIES_BEFORE_PURGE = 256;

    private static final Map<Long, Entry> ENTRIES = new ConcurrentHashMap<>();

    private PearlFirePlacementColorMemory() {}

    public static void remember(BlockPos pos, int rgb) {
        if (ENTRIES.size() >= MAX_ENTRIES_BEFORE_PURGE) {
            purgeExpired();
        }

        ENTRIES.put(
                pos.asLong(),
                new Entry(TintColorUtil.rgb(rgb), System.currentTimeMillis() + ENTRY_LIFETIME_MS)
        );
    }

    public static Integer get(BlockPos pos) {
        Entry entry = ENTRIES.get(pos.asLong());
        if (entry == null) {
            return null;
        }

        if (System.currentTimeMillis() > entry.expiresAtMs()) {
            ENTRIES.remove(pos.asLong());
            return null;
        }

        return entry.rgb();
    }

    public static void clear(BlockPos pos) {
        ENTRIES.remove(pos.asLong());
    }

    private static void purgeExpired() {
        long now = System.currentTimeMillis();
        ENTRIES.entrySet().removeIf(e -> now > e.getValue().expiresAtMs());
    }

    private record Entry(int rgb, long expiresAtMs) {}
}