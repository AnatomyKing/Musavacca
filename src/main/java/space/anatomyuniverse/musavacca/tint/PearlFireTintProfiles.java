package space.anatomyuniverse.musavacca.tint;

// wicked fire with E74E8C
// 0.85F,   // coreToTailLightness
// 6.385F,  // colorJumpiness
// 0.0F,    // colorAmountTakeOver
// 0.0F,    // vibrancyDarkening
// 1.18F    // layerContrast

/*
 * PearlFireTintProfiles settings:
 *
 * coreToTailLightness
 *   0.0 = no white/light core influence
 *   1.0 = strongest white-hot highlights and core-to-tail lightness travel
 *   >1.0 = overdriven white-core influence; affects more of the palette
 *
 * colorJumpiness
 *   0.0 = hue stays close to the selected color
 *   0.5 = smooth magical hue movement
 *   >0.5 = enables increasingly strong wave-like hue jumps
 *
 * colorAmountTakeOver
 *   0.0       = exact original procedural, index-driven fire behavior
 *   0.01–0.19 = transition between procedural fire and grayscale palette behavior
 *   >=0.20     = fully gray-value-driven layer positioning
 *   Higher values also make the authored grayscale palette and selected hue
 *   override more of the expressive procedural fire result(coreToTailLightness).
 *
 * vibrancyDarkening
 *   0.0 = no additional dark/vivid-color deepening
 *   1.0 = strongest darkening and deepest proportional palette shadows
 *
 * layerContrast
 *   0.0 = heavily compresses/flattens layer differences
 *   1.0 = neutral layer spacing
 *   >1.0 = increasingly separates highlights, body colors and shadows
 */

/*
 * coreToTailLightness: 0=no white core, 1=maximum white-hot core
 * colorJumpiness:      0=fixed hue, 0.5=smooth movement, >0.5=extra hue waves
 * colorAmountTakeOver: 0=procedural, 0.01–0.19=hybrid, >=0.20=palette-driven
 * vibrancyDarkening:   0=no extra darkening, 1=maximum vivid/shadow deepening
 * layerContrast:       0=flat, 1=neutral, >1=stronger layer separation
 */

public final class PearlFireTintProfiles {

    public static final Profile FIRE_BLOCK = of(
            settings(
                    1.0F,   // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.0F,   // colorAmountTakeOver
                    0.86F   // layerContrast
            ),
            255, 252, 249, 246, 243, 240, 237, 234,
            231, 229, 226, 223, 220, 217, 214, 211,
            208, 205, 203, 201, 199, 197, 195, 193,
            191, 189, 187, 185, 183, 181, 179, 177
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

    public static final Profile IMBUED_POTASSIUM_ITEMS = of(
            settings(
                    1.67F,
                    0.16F,
                    0.90F,
                    0.05F,
                    1.03F
            ),
            255, 250, 236, 206, 180, 138, 97, 72,
            50
    );

    public static final Profile IMBUED_POTASSIUM_ARMOR = of(
            settings(
                    1.0F,
                    0.20F,
                    0.76F,
                    0.36F,
                    1.22F
            ),
            245, 234, 218, 204, 186, 163, 124
    );

    public static final Profile IMBUED_POTASSIUM_HELMET = of(
            settings(
                    1.0F,   // coreToTailLightness
                    0.20F,  // colorJumpiness
                    0.76F,  // colorAmountTakeOver
                    0.36F,  // vibrancyDarkening
                    1.22F   // layerContrast
            ),
            245, 234, 218, 204, 186, 163, 124,
            77, 64, 57, 43
    );

    public static final Profile SIM_CARD_TINT = of(
            settings(
                    1.25F,  // coreToTailLightness
                    0.64F,  // colorJumpiness
                    0.36F,  // colorAmountTakeOver
                    0.34F,  // vibrancyDarkening
                    0.47F   // layerContrast
            ),
            255, 234, 210, 184, 174, 138, 112
    );

    public static final Profile PORTAL_BLOCK = of(
            settings(
                    2F,   // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.62F,  // colorAmountTakeOver
                    0.27F,  // vibrancyDarkening
                    0.86F   // layerContrast
            ),
            255, 247, 242, 232, 225, 217, 207, 203,
            199, 195, 189, 181, 171, 164, 157
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
                    0.60F,  // coreToTailLightness
                    0.5F,   // colorJumpiness
                    0.21F,  // colorAmountTakeOver
                    0.36F   // layerContrast
            ),
            244, 228, 202
    );

    public static final Profile GLITHER_PARTICLE = of(
            settings(
                    0.80F,
                    0.5F,
                    0.0F,
                    1.21F
            ),
            255, 240, 199, 184
    );

    public static final Profile PEARL_FLAME = of(
            settings(
                    0.80F,
                    0.5F,
                    0.0F,
                    1.21F
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

    public static Profile of(
            Settings settings,
            int... grayValues
    ) {
        if (settings == null) {
            throw new IllegalArgumentException(
                    "settings must not be null"
            );
        }

        if (grayValues == null || grayValues.length == 0) {
            throw new IllegalArgumentException(
                    "grayValues must not be empty"
            );
        }

        float[] grayFactors =
                new float[grayValues.length];

        for (int i = 0; i < grayValues.length; i++) {
            grayFactors[i] =
                    clamp01(grayValues[i] / 255.0F);
        }

        return new Profile(
                settings,
                grayFactors
        );
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
            coreToTailLightness =
                    Math.max(0.0F, coreToTailLightness);

            colorJumpiness =
                    Math.max(0.0F, colorJumpiness);

            colorAmountTakeOver =
                    clamp01(colorAmountTakeOver);

            vibrancyDarkening =
                    clamp01(vibrancyDarkening);

            layerContrast =
                    Math.max(0.0F, layerContrast);
        }
    }

    public record Profile(
            Settings settings,
            float[] grayFactors
    ) {
        public Profile {
            if (settings == null) {
                throw new IllegalArgumentException(
                        "settings must not be null"
                );
            }

            if (
                    grayFactors == null
                            || grayFactors.length == 0
            ) {
                throw new IllegalArgumentException(
                        "grayFactors must not be empty"
                );
            }

            grayFactors = grayFactors.clone();

            for (
                    int i = 0;
                    i < grayFactors.length;
                    i++
            ) {
                grayFactors[i] =
                        clamp01(grayFactors[i]);
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
            return tintIndex >= 0
                    && tintIndex < grayFactors.length;
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
        return Math.max(
                0.0F,
                Math.min(1.0F, value)
        );
    }
}
