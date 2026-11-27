package space.anatomyuniverse.musavacca.tint;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public final class PearlFireTintProfiles {
    public static final Settings PORTAL_BLOCK = settings(2.0F, 0.5F, 0.62F, 0.27F, 0.86F);

    public static final Settings FIRE_BLOCK = settings(1.0F, 0.5F, 0.0F, 0.86F);

    public static final Settings PEARL_CANDLE = settings(1.0F, 0.5F, 0.0F, 0.86F);
    public static final Settings PEARL_CANDLE_CAKE = settings(1.0F, 0.5F, 0.0F, 0.86F);
    public static final Settings PEARL_CAMPFIRE = settings(1.0F, 0.5F, 0.0F, 0.86F);

    private PearlFireTintProfiles() {}

    public static Settings forFlame(BlockState state) {
        if (state.is(BlockTags.CAMPFIRES)) return PEARL_CAMPFIRE;
        if (state.is(BlockTags.CANDLE_CAKES)) return PEARL_CANDLE_CAKE;
        return PEARL_CANDLE;
    }

    public static Settings settings(float coreToTailLightness, float colorJumpiness, float colorAmountTakeOver, float layerContrast) {
        return settings(coreToTailLightness, colorJumpiness, colorAmountTakeOver, 0.0F, layerContrast);
    }

    public static Settings settings(
            float coreToTailLightness, float colorJumpiness, float colorAmountTakeOver, float vibrancyDarkening, float layerContrast) {
        return new Settings(coreToTailLightness, colorJumpiness, colorAmountTakeOver, vibrancyDarkening, layerContrast);
    }

    public record Settings(
            float coreToTailLightness, float colorJumpiness, float colorAmountTakeOver, float vibrancyDarkening, float layerContrast) {
        public Settings(float coreToTailLightness, float colorJumpiness, float colorAmountTakeOver, float layerContrast) {
            this(coreToTailLightness, colorJumpiness, colorAmountTakeOver, 0.0F, layerContrast);
        }

        public Settings {
            coreToTailLightness = Math.max(0.0F, coreToTailLightness);
            colorJumpiness = Math.max(0.0F, colorJumpiness);
            colorAmountTakeOver = clamp01(colorAmountTakeOver);
            vibrancyDarkening = clamp01(vibrancyDarkening);
            layerContrast = Math.max(0.0F, layerContrast);
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
