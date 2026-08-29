package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PearlPlacementColorMemory {
    private static final long ENTRY_LIFETIME_MS = 1500L;
    private static final int MAX_ENTRIES_BEFORE_PURGE = 1024;

    private static final Map<Long, Entry> ENTRIES = new ConcurrentHashMap<>();

    private PearlPlacementColorMemory() {}

    public static void remember(Level level, BlockPos pos, int rgb) {
        if (level == null || pos == null || !level.isClientSide()) {
            return;
        }

        rememberClientPos(pos, rgb);
    }

    public static Integer get(BlockAndTintGetter level, BlockPos pos) {
        return get(pos);
    }

    public static Integer get(Level level, BlockPos pos) {
        return get(pos);
    }

    public static Integer get(BlockPos pos) {
        if (pos == null) {
            return null;
        }

        Entry entry = ENTRIES.get(pos.asLong());
        if (entry == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        if (now > entry.expiresAtMs()) {
            ENTRIES.remove(pos.asLong());
            return null;
        }

        return entry.rgb();
    }

    public static void clear(BlockAndTintGetter level, BlockPos pos) {
        clear(pos);
    }

    public static void clear(Level level, BlockPos pos) {
        clear(pos);
    }

    public static void clear(BlockPos pos) {
        if (pos != null) {
            ENTRIES.remove(pos.asLong());
        }
    }

    public static void clearAll() {
        ENTRIES.clear();
    }

    private static void rememberClientPos(BlockPos pos, int rgb) {
        long now = System.currentTimeMillis();

        if (ENTRIES.size() >= MAX_ENTRIES_BEFORE_PURGE) {
            purgeExpired(now);

            if (ENTRIES.size() >= MAX_ENTRIES_BEFORE_PURGE) {
                ENTRIES.clear();
            }
        }

        ENTRIES.put(
                pos.asLong(),
                new Entry(
                        TintColorUtil.rgb(rgb),
                        now + ENTRY_LIFETIME_MS
                )
        );
    }

    private static void purgeExpired(long now) {
        ENTRIES.entrySet().removeIf(entry -> now > entry.getValue().expiresAtMs());
    }

    private record Entry(int rgb, long expiresAtMs) {}
}

