package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintProfiles {
    public static final Profile FIRE_BLOCK = of(
            settings(1.0F, 0.5F, 0.0F, 0.86F),
            255, 252, 249, 246, 243, 240, 237, 234,
            231, 229, 226, 223, 220, 217, 214, 211,
            208, 205, 203, 201, 199, 197, 195, 193,
            191, 189, 187, 185, 183, 181, 179, 177
    );

    public static final Profile FLINT_AND_PEARL = of(
            settings(1.0F, 0.5F, 0.0F, 1.21F),
            252, 242, 232, 222, 211, 201, 192,
            182, 172, 162, 151, 142, 0
    );

    public static final Profile PORTAL_BLOCK = of(
            settings(2.0F, 0.5F, 0.62F, 0.27F, 0.86F),
            255, 247, 242, 232, 225, 217, 207, 203,
            199, 195, 189, 181, 171, 164, 157
    );

    public static final Profile PORTAL_GLYPH_PARTICLE = of(
            settings(0.13F, 0.12F, 0.0F, 0.36F),
            244, 228, 202, 186
    );

    public static final Profile GLITHER_PARTICLE = of(
            settings(0.80F, 0.5F, 0.0F, 1.21F),
            255, 240, 199, 184
    );

    public static final Profile PEARL_FLAME = of(
            settings(0.80F, 0.5F, 0.0F, 1.21F),
            255, 228, 196, 167
    );

    public static final Profile SIM_CARD_TINT = of(
            settings(1.25F, 0.64F, 0.36F, 0.34F, 0.47F),
            255, 234, 210, 184, 174, 138, 112
    );

    private PearlFireTintProfiles() {}

    public static Settings settings(
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float layerContrast
    ) {
        return settings(
                coreToTailLightness,
                colorJumpiness,
                colorAmountTakeOver,
                0.0F,
                layerContrast
        );
    }

    public static Settings settings(
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float vibrancyDarkening,
            float layerContrast
    ) {
        return new Settings(
                coreToTailLightness,
                colorJumpiness,
                colorAmountTakeOver,
                vibrancyDarkening,
                layerContrast
        );
    }

    public static Profile of(Settings settings, int... grayValues) {
        if (settings == null) {
            throw new IllegalArgumentException("settings must not be null");
        }
        if (grayValues == null || grayValues.length == 0) {
            throw new IllegalArgumentException("grayValues must not be empty");
        }

        float[] grayFactors = new float[grayValues.length];
        for (int i = 0; i < grayValues.length; i++) {
            grayFactors[i] = clamp01(grayValues[i] / 255.0F);
        }

        return new Profile(settings, grayFactors);
    }

    public record Settings(
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float vibrancyDarkening,
            float layerContrast
    ) {
        public Settings(
                float coreToTailLightness,
                float colorJumpiness,
                float colorAmountTakeOver,
                float layerContrast
        ) {
            this(
                    coreToTailLightness,
                    colorJumpiness,
                    colorAmountTakeOver,
                    0.0F,
                    layerContrast
            );
        }

        public Settings {
            coreToTailLightness = Math.max(0.0F, coreToTailLightness);
            colorJumpiness = Math.max(0.0F, colorJumpiness);
            colorAmountTakeOver = clamp01(colorAmountTakeOver);
            vibrancyDarkening = clamp01(vibrancyDarkening);
            layerContrast = Math.max(0.0F, layerContrast);
        }
    }

    public record Profile(Settings settings, float[] grayFactors) {
        public Profile {
            if (settings == null) {
                throw new IllegalArgumentException("settings must not be null");
            }
            if (grayFactors == null || grayFactors.length == 0) {
                throw new IllegalArgumentException("grayFactors must not be empty");
            }

            grayFactors = grayFactors.clone();
            for (int i = 0; i < grayFactors.length; i++) {
                grayFactors[i] = clamp01(grayFactors[i]);
            }
        }

        @Override
        public float[] grayFactors() {
            return grayFactors.clone();
        }

        public int layerCount() {
            return grayFactors.length;
        }

        public boolean supports(int tintIndex) {
            return tintIndex >= 0 && tintIndex < grayFactors.length;
        }

        public float grayFactor(int tintIndex) {
            return grayFactors[tintIndex];
        }

        public float coreToTailLightness() {
            return settings.coreToTailLightness();
        }

        public float colorJumpiness() {
            return settings.colorJumpiness();
        }

        public float colorAmountTakeOver() {
            return settings.colorAmountTakeOver();
        }

        public float vibrancyDarkening() {
            return settings.vibrancyDarkening();
        }

        public float layerContrast() {
            return settings.layerContrast();
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
