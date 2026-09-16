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
        if (level == null || pos == null || !level.isClientSide()) {
            return;
        }

        long now = System.currentTimeMillis();
        purgeExpired(now);

        if (ENTRIES.size() >= MAX_ENTRIES) {
            ENTRIES.clear();
        }

        ENTRIES.put(
                pos.asLong(),
                new Entry(
                        MusavaccaTints.rgb(rgb),
                        now + ENTRY_LIFETIME_MS
                )
        );
    }

    public static Integer get(BlockPos pos) {
        if (pos == null) {
            return null;
        }

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
        if (pos != null) {
            ENTRIES.remove(pos.asLong());
        }
    }

    private static void purgeExpired(long now) {
        ENTRIES.entrySet().removeIf(entry -> now > entry.getValue().expiresAtMs());
    }

    private record Entry(int rgb, long expiresAtMs) {}
}
