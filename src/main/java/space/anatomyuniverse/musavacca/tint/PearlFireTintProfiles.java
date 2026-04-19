package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintProfiles {
    public static final Profile FIRE_BLOCK = of(
            255, 251, 247, 244, 240, 236, 233, 229,
            225, 221, 217, 213, 210, 205, 202, 199,
            195, 191, 188, 184, 180, 176, 173, 169,
            165, 161, 157, 153, 150, 146, 142, 139
    );

    public static final Profile TORCH_BLOCK = of(
            255, 203, 110
    );

    private PearlFireTintProfiles() {}

    public static Profile of(int... grayValues) {
        if (grayValues == null || grayValues.length == 0) {
            throw new IllegalArgumentException("grayValues must not be empty");
        }

        float[] grayFactors = new float[grayValues.length];
        for (int i = 0; i < grayValues.length; i++) {
            grayFactors[i] = clamp01(grayValues[i] / 255.0F);
        }

        return new Profile(grayFactors);
    }

    public record Profile(float[] grayFactors) {
        public Profile {
            if (grayFactors == null || grayFactors.length == 0) {
                throw new IllegalArgumentException("grayFactors must not be empty");
            }

            grayFactors = grayFactors.clone();
            for (int i = 0; i < grayFactors.length; i++) {
                grayFactors[i] = clamp01(grayFactors[i]);
            }
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
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}