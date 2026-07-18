package space.anatomyuniverse.musavacca.tint;

// wicked fire with E74E8C
// 0.85F,   // coreToTailLightness
// 6.385F,  // colorJumpiness
// 0.0F,    // colorAmountTakeOver
// 1.18F    // layerContrast

public final class PearlFireTintProfiles {

    public static final Profile FIRE_BLOCK = of(
            settings(
                    1.0F,   // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    1.21F   // layerContrast
            ),
            255, 251, 247, 244, 240, 236, 233, 229,
            225, 221, 217, 213, 210, 205, 202, 199,
            195, 191, 188, 184, 180, 176, 173, 169,
            165, 161, 157, 153, 150, 146, 142, 139
    );

    public static final Profile FLINT_AND_PEARL = of(
            settings(
                    1.0F,   // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    1.21F   // layerContrast
            ),
            252, 242, 232, 222, 211, 201, 192, 182,
            172, 162, 151, 142, 0
    );

    public static final Profile IMBUED_POTASSIUM_TOOLS = of(
            settings(
                    0.82F,  // coreToTailLightness
                    0.64F,  // colorJumpiness
                    0.36F,   // colorAmountTakeOver
                    0.47F   // layerContrast
            ),
            250, 237, 223, 207, 193, 180, 166, 152,
            139
    );

    public static final Profile IMBUED_POTASSIUM_ARMOR = of(
            settings(
                    0.62F,  // coreToTailLightness
                    0.32F,  // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    0.98F   // layerContrast
            ),
            239, 223, 204, 186, 164, 145, 117
    );

    public static final Profile IMBUED_POTASSIUM_HELMET = of(
            settings(
                    0.62F,  // coreToTailLightness
                    0.32F,  // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    0.98F   // layerContrast
            ),
            239, 223, 204, 186, 164, 145, 117, 87,
            75, 68, 54
    );

    public static final Profile SIM_CARD_TINT = of(
            settings(
                    0.82F,  // coreToTailLightness
                    0.64F,  // colorJumpiness
                    0.36F,   // colorAmountTakeOver
                    0.47F   // layerContrast
            ),
            255, 234, 210, 184, 174, 138, 112
    );

    public static final Profile PORTAL_BLOCK = of(
            settings(
                    0.82F,  // coreToTailLightness
                    0.64F,  // colorJumpiness
                    0.36F,   // colorAmountTakeOver
                    0.47F   // layerContrast
            ),
            255, 247, 240, 229, 221, 213, 205, 195,
            188, 180, 173, 165, 153, 146, 139
    );

    public static final Profile PORTAL_GLYPH_PARTICLE = of(
            settings(
                    0.13F,  // coreToTailLightness
                    0.12F,  // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    0.36F   // layerContrast
            ),
            244, 228, 202, 186
    );

    public static final Profile IMBUED_POTASSIUM = of(
            settings(
                    0.80F,  // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.21F,   // colorAmountTakeOver
                    0.36F   // layerContrast
            ),
            244, 228, 202
    );

    public static final Profile GLITHER_PARTICLE = of(
            settings(
                    0.48F,  // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    1.21F   // layerContrast
            ),
            255, 240, 199, 184
    );

    public static final Profile PEARL_FLAME = of(
            settings(
                    0.80F,  // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    1.21F   // layerContrast
            ),
            255, 228, 196, 167
    );

    private PearlFireTintProfiles() {}

    public static Settings settings(
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float layerContrast
    ) {
        return new Settings(
                coreToTailLightness,
                colorJumpiness,
                colorAmountTakeOver,
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
            float layerContrast
    ) {
        public Settings {
            coreToTailLightness = clamp01(coreToTailLightness);
            colorJumpiness = Math.max(0.0F, colorJumpiness);
            colorAmountTakeOver = clamp01(colorAmountTakeOver);
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

        public float layerContrast() {
            return settings.layerContrast();
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}