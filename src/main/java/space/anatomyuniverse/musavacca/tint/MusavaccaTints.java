package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.level.block.entity.BlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MusavaccaTints {
    public static final int NO_TINT = -1;
    public static final int RANDOM_TINT = -2;
    public static final int DEFAULT_TINT = 0xD5CD49;
    public static final String HEX_COLOR_KEY = "hex_color";
    public static final String MULTI_HEX_COLOR_KEY = "multi_hex_color";
    private MusavaccaTints() {}
    public interface HexSource {
        int getHexColor();
        default boolean hasHexColor() {
            return hasTint(getHexColor());
        }
    }

    public interface MultiHexSource {
        int getHexColor(int index);
    }

    public static boolean hasTint(int color) {
        return color != NO_TINT && color != RANDOM_TINT;
    }

    public static int rgb(int color) {
        return color & 0xFFFFFF;
    }

    public static int resolve(int color) {
        if (color == NO_TINT) return NO_TINT;
        if (color == RANDOM_TINT) return randomRgb();
        return rgb(color);
    }

    public static int stored(int color) {
        return color == NO_TINT || color == RANDOM_TINT ? NO_TINT : rgb(color);
    }

    public static int orDefault(int color) {
        return color == NO_TINT ? DEFAULT_TINT : resolve(color);
    }

    public static int randomRgb() {
        return ThreadLocalRandom.current().nextInt(0x1000000);
    }

    public static int opaqueRgb(int color) {
        return hasTint(color) ? 0xFF000000 | rgb(color) : NO_TINT;
    }

    public static int multiColor(List<Integer> colors, int index) {
        if (colors == null || index < 0 || index >= colors.size()) return NO_TINT;
        Integer color = colors.get(index);
        return color == null ? NO_TINT : stored(color);
    }

    public static String formatHex(int color) {
        if (color == NO_TINT) return "NO_TINT";
        if (color == RANDOM_TINT) return "RANDOM_TINT";
        return String.format("#%06X", rgb(color));
    }

    public static int blockEntityHexColor(BlockEntity blockEntity) {
        if (blockEntity == null) return NO_TINT;
        if (blockEntity instanceof HexSource source) return source.hasHexColor() ? stored(source.getHexColor()) : NO_TINT;
        Integer color = blockEntity.collectComponents().get(ModDataComponents.HEX_COLOR.get());
        return color == null ? NO_TINT : stored(color);
    }

}
