// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/HexColorLcg.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HexColorLcg {
    private HexColorLcg() {}

    public static final int NO_COLOR = -1;

    private static final long MASK = 0xFFFFFFFFL;
    private static final long MULTIPLIER = 1664525L;
    private static final long INCREMENT = 1013904223L;
    private static final long SHARED_SEED = 0x4D595DF4L;

    private static long clientState = SHARED_SEED;
    private static long serverState = SHARED_SEED;

    private static final int MAX_PENDING_CLIENT_PREDICTIONS = 2048;

    private static final Map<Long, Integer> PENDING_CLIENT_COLORS = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
            return size() > MAX_PENDING_CLIENT_PREDICTIONS;
        }
    };

    public static long snapshotClientState() {
        return clientState;
    }

    public static void restoreClientState(long snapshot) {
        clientState = snapshot & MASK;
    }

    public static int nextServerHexColor() {
        serverState = advance(serverState);
        return rgbFromState(serverState);
    }

    public static int nextClientHexColor() {
        clientState = advance(clientState);
        return rgbFromState(clientState);
    }

    public static void reserveClientPlacementPrediction(BlockPos pos) {
        int hex = nextClientHexColor();
        PENDING_CLIENT_COLORS.put(pos.asLong(), hex);
    }

    public static int getClientPlacementPrediction(BlockPos pos) {
        Integer hex = PENDING_CLIENT_COLORS.get(pos.asLong());
        return hex != null ? hex : NO_COLOR;
    }

    public static void clearClientPrediction(BlockPos pos) {
        PENDING_CLIENT_COLORS.remove(pos.asLong());
    }

    private static long advance(long state) {
        return (state * MULTIPLIER + INCREMENT) & MASK;
    }

    private static int rgbFromState(long state) {
        return (int) ((state >>> 8) & 0xFFFFFFL);
    }
}