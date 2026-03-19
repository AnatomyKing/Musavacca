
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public final class HexColorLcg {
    public static final int NO_COLOR = -1;

    private static final long MASK = 0xFFFFFFFFL;
    private static final long A = 1664525L;
    private static final long C = 1013904223L;
    private static final long SEED = 0x4D595DF4L;

    private static long clientState = SEED;
    private static long serverState = SEED;

    private static final Map<Long, Integer> PREDICTED = new HashMap<>();

    private HexColorLcg() {}

    public static long predict(BlockPos pos, Integer savedHex) {
        long snapshot = clientState;
        clientState = next(clientState);
        PREDICTED.put(pos.asLong(), savedHex != null ? rgb(savedHex) : rgbFromState(clientState));
        return snapshot;
    }

    public static void rollback(BlockPos pos, long snapshot) {
        clientState = snapshot & MASK;
        PREDICTED.remove(pos.asLong());
    }

    public static int get(BlockPos pos) {
        return PREDICTED.getOrDefault(pos.asLong(), NO_COLOR);
    }

    public static void clear(BlockPos pos) {
        PREDICTED.remove(pos.asLong());
    }

    public static int nextServerColor() {
        serverState = next(serverState);
        return rgbFromState(serverState);
    }

    public static int rgb(int value) {
        return value & 0xFFFFFF;
    }

    private static int rgbFromState(long state) {
        return (int) ((state >>> 8) & 0xFFFFFFL);
    }

    private static long next(long state) {
        return (state * A + C) & MASK;
    }
}