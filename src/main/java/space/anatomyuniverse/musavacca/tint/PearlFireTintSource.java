// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/PearlFireTintSource.java
package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintSource {
    public static final int LAYER_COUNT = 32;

    private static final float CHROMA_EPSILON = 0.0005F;

    // Fire-shaping knobs
    private static final float CORE_END = 0.19F;
    private static final float BODY_CENTER = 0.43F;
    private static final float BODY_WIDTH = 0.25F;
    private static final float TAIL_START = 0.72F;

    private static final float MID_CHROMA_BOOST = 0.18F;
    private static final float LOW_CHROMA_RESCUE = 0.035F;
    private static final float COOL_MID_LIGHTNESS_BOOST = 0.045F;
    private static final float WARM_TAIL_DARKEN = 0.030F;

    private static final float WARM_HIGHLIGHT_SHIFT = 14.0F;
    private static final float COOL_HIGHLIGHT_SHIFT = -8.0F;
    private static final float WARM_SHADOW_SHIFT = -14.0F;
    private static final float COOL_SHADOW_SHIFT = 10.0F;

    /*
     * Actual grayscale source levels in pearl_fire_0 / pearl_fire_1,
     * lightest -> darkest.
     */
    private static final int[] SOURCE_GRAY_BY_LAYER = new int[]{
            255, 250, 245, 241, 236, 231, 227, 222,
            217, 213, 207, 203, 199, 193, 189, 185,
            180, 175, 171, 166, 161, 156, 152, 147,
            142, 138, 133, 128, 124, 119, 114, 110
    };

    static {
        if (SOURCE_GRAY_BY_LAYER.length != LAYER_COUNT) {
            throw new IllegalStateException(
                    "Expected " + LAYER_COUNT + " source gray values, got " + SOURCE_GRAY_BY_LAYER.length
            );
        }
    }

    private PearlFireTintSource() {}

    public static int blockTint(int baseRgb, int tintIndex) {
        if (tintIndex < 0 || tintIndex >= LAYER_COUNT) {
            return TintColorUtil.NO_TINT;
        }

        float t = tintIndex / (float) (LAYER_COUNT - 1); // 0 = hottest/lightest, 1 = darkest
        int desiredRgb = computeDesiredLayerRgb(TintColorUtil.rgb(baseRgb), t);
        int compensatedRgb = compensateForMinecraftMultiply(desiredRgb, tintIndex);
        return TintColorUtil.opaqueRgb(compensatedRgb);
    }

    private static int computeDesiredLayerRgb(int baseRgb, float t) {
        if (t <= 0.00001F) {
            return 0xFFFFFF; // always white-hot on the brightest layer
        }

        Oklch base = rgbToOklch(baseRgb);
        boolean chromatic = base.c > CHROMA_EPSILON;

        float baseL = base.l;
        float baseC = chromatic ? base.c : 0.0F;
        float baseH = chromatic ? base.hDegrees : 0.0F;

        // +1 = warm/yellow-orange side, -1 = cool/blue-cyan side
        float temperature = chromatic ? clamp(cosDeg(baseH - 70.0F), -1.0F, 1.0F) : 0.0F;

        float coreFade = smooth01(t / CORE_END);                     // 0 in core, 1 outside core
        float core = 1.0F - coreFade;
        float body = bell(t, BODY_CENTER, BODY_WIDTH);               // mid flame body
        float tail = smooth01((t - TAIL_START) / (1.0F - TAIL_START));

        float lightness = computeLightness(baseL, chromatic, temperature, body, tail, t);

        float chroma;
        float hueDegrees;

        if (chromatic) {
            float normalizedBaseC = smooth01(baseC / (baseC + 0.12F));

            // Core starts almost white, body blooms richer, tail keeps some color.
            float bodyTargetC = baseC * (1.06F + (MID_CHROMA_BOOST * body) + (0.05F * tail));
            float lowChromaLift = LOW_CHROMA_RESCUE * (1.0F - normalizedBaseC) * body;
            float chromaEnvelope = smooth01((t - 0.02F) / 0.22F) * (1.0F - 0.10F * tail);

            chroma = Math.max(0.0F, (bodyTargetC + lowChromaLift) * chromaEnvelope);

            // Highlights drift warmer for warm hues and cooler for cool hues.
            // Shadows drift redder for warm hues and violet/deeper-blue for cool hues.
            float highlightShift = lerp(COOL_HIGHLIGHT_SHIFT, WARM_HIGHLIGHT_SHIFT, (temperature + 1.0F) * 0.5F);
            float shadowShift = lerp(COOL_SHADOW_SHIFT, WARM_SHADOW_SHIFT, (temperature + 1.0F) * 0.5F);
            float hueArc = lerp(highlightShift, shadowShift, smooth01((t - 0.18F) / 0.82F));

            // Tiny extra bend in the colorful body so the flame feels more alive.
            float bodySwing = 3.5F * body * temperature;

            hueDegrees = wrapDegrees360(baseH + hueArc + bodySwing);
        } else {
            // White/gray/black stay achromatic
            chroma = 0.0F;
            hueDegrees = 0.0F;
        }

        return oklchToRgbGamutFit(lightness, chroma, hueDegrees);
    }

    private static float computeLightness(
            float baseL,
            boolean chromatic,
            float temperature,
            float body,
            float tail,
            float t
    ) {
        /*
         * Dark inputs collapse faster so black fire still reads dark.
         * Bright inputs keep a longer bright head so white/light colors stay luminous.
         */
        float shadowL = chromatic
                ? lerp(0.08F, 0.38F, baseL)
                : lerp(0.02F, 0.48F, baseL);

        float exponent = lerp(0.35F, 1.65F, baseL);

        float L = 1.0F - ((1.0F - shadowL) * (float) Math.pow(t, exponent));

        // Cool hues often look better with a slightly brighter inner body.
        L += COOL_MID_LIGHTNESS_BOOST * body * Math.max(0.0F, -temperature);

        // Warm hues usually look nicer with a slightly denser/darker tail.
        L -= WARM_TAIL_DARKEN * tail * Math.max(0.0F, temperature);

        return clamp01(L);
    }

    private static int compensateForMinecraftMultiply(int desiredRgb, int tintIndex) {
        float grayFactor = SOURCE_GRAY_BY_LAYER[tintIndex] / 255.0F;
        if (grayFactor <= 0.0F) {
            return desiredRgb;
        }

        int desiredR = (desiredRgb >> 16) & 0xFF;
        int desiredG = (desiredRgb >> 8) & 0xFF;
        int desiredB = desiredRgb & 0xFF;

        int tintR = clamp255(Math.round(desiredR / grayFactor));
        int tintG = clamp255(Math.round(desiredG / grayFactor));
        int tintB = clamp255(Math.round(desiredB / grayFactor));

        return (tintR << 16) | (tintG << 8) | tintB;
    }

    // -------------------------------------------------------------------------
    // Oklab / Oklch
    // -------------------------------------------------------------------------

    private static Oklch rgbToOklch(int rgb) {
        float r = srgbToLinear(((rgb >> 16) & 0xFF) / 255.0F);
        float g = srgbToLinear(((rgb >> 8) & 0xFF) / 255.0F);
        float b = srgbToLinear((rgb & 0xFF) / 255.0F);

        Oklab lab = linearSrgbToOklab(r, g, b);

        float c = (float) Math.hypot(lab.a, lab.b);
        float hDegrees = c <= CHROMA_EPSILON
                ? 0.0F
                : wrapDegrees360((float) Math.toDegrees(Math.atan2(lab.b, lab.a)));

        return new Oklch(lab.l, c, hDegrees);
    }

    private static int oklchToRgbGamutFit(float l, float c, float hDegrees) {
        l = clamp01(l);
        c = Math.max(0.0F, c);
        hDegrees = wrapDegrees360(hDegrees);

        float[] direct = oklchToLinearSrgb(l, c, hDegrees);
        if (isInSrgbGamut(direct)) {
            return linearSrgbToInt(direct[0], direct[1], direct[2]);
        }

        // Simple chroma backoff: keep L and h, reduce C until it fits sRGB.
        float low = 0.0F;
        float high = c;
        float best = 0.0F;

        for (int i = 0; i < 24; i++) {
            float mid = (low + high) * 0.5F;
            float[] test = oklchToLinearSrgb(l, mid, hDegrees);

            if (isInSrgbGamut(test)) {
                best = mid;
                low = mid;
            } else {
                high = mid;
            }
        }

        float[] fitted = oklchToLinearSrgb(l, best, hDegrees);
        return linearSrgbToInt(fitted[0], fitted[1], fitted[2]);
    }

    private static float[] oklchToLinearSrgb(float l, float c, float hDegrees) {
        float hRad = (float) Math.toRadians(hDegrees);
        float a = c * (float) Math.cos(hRad);
        float b = c * (float) Math.sin(hRad);

        Oklab lab = new Oklab(l, a, b);
        return oklabToLinearSrgb(lab);
    }

    private static Oklab linearSrgbToOklab(float r, float g, float b) {
        float l = 0.4122214708F * r + 0.5363325363F * g + 0.0514459929F * b;
        float m = 0.2119034982F * r + 0.6806995451F * g + 0.1073969566F * b;
        float s = 0.0883024619F * r + 0.2817188376F * g + 0.6299787005F * b;

        float l_ = (float) Math.cbrt(l);
        float m_ = (float) Math.cbrt(m);
        float s_ = (float) Math.cbrt(s);

        return new Oklab(
                0.2104542553F * l_ + 0.7936177850F * m_ - 0.0040720468F * s_,
                1.9779984951F * l_ - 2.4285922050F * m_ + 0.4505937099F * s_,
                0.0259040371F * l_ + 0.7827717662F * m_ - 0.8086757660F * s_
        );
    }

    private static float[] oklabToLinearSrgb(Oklab c) {
        float l_ = c.l + 0.3963377774F * c.a + 0.2158037573F * c.b;
        float m_ = c.l - 0.1055613458F * c.a - 0.0638541728F * c.b;
        float s_ = c.l - 0.0894841775F * c.a - 1.2914855480F * c.b;

        float l = l_ * l_ * l_;
        float m = m_ * m_ * m_;
        float s = s_ * s_ * s_;

        return new float[]{
                +4.0767416621F * l - 3.3077115913F * m + 0.2309699292F * s,
                -1.2684380046F * l + 2.6097574011F * m - 0.3413193965F * s,
                -0.0041960863F * l - 0.7034186147F * m + 1.7076147010F * s
        };
    }

    private static boolean isInSrgbGamut(float[] linearRgb) {
        return linearRgb != null
                && linearRgb.length == 3
                && isFinite(linearRgb[0]) && isFinite(linearRgb[1]) && isFinite(linearRgb[2])
                && linearRgb[0] >= 0.0F && linearRgb[0] <= 1.0F
                && linearRgb[1] >= 0.0F && linearRgb[1] <= 1.0F
                && linearRgb[2] >= 0.0F && linearRgb[2] <= 1.0F;
    }

    private static boolean isFinite(float value) {
        return !Float.isNaN(value) && !Float.isInfinite(value);
    }

    private static int linearSrgbToInt(float r, float g, float b) {
        int ri = clamp255(Math.round(linearToSrgb(clamp01(r)) * 255.0F));
        int gi = clamp255(Math.round(linearToSrgb(clamp01(g)) * 255.0F));
        int bi = clamp255(Math.round(linearToSrgb(clamp01(b)) * 255.0F));
        return (ri << 16) | (gi << 8) | bi;
    }

    private static float srgbToLinear(float c) {
        if (c <= 0.04045F) {
            return c / 12.92F;
        }
        return (float) Math.pow((c + 0.055F) / 1.055F, 2.4);
    }

    private static float linearToSrgb(float c) {
        if (c <= 0.0031308F) {
            return 12.92F * c;
        }
        return 1.055F * (float) Math.pow(c, 1.0 / 2.4) - 0.055F;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static float bell(float x, float center, float width) {
        float d = Math.abs(x - center) / Math.max(0.0001F, width);
        float v = 1.0F - clamp01(d);
        return v * v * (3.0F - 2.0F * v);
    }

    private static float cosDeg(float degrees) {
        return (float) Math.cos(Math.toRadians(degrees));
    }

    private static float wrapDegrees360(float degrees) {
        float wrapped = degrees % 360.0F;
        return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
    }

    private static float smooth01(float t) {
        t = clamp01(t);
        return t * t * (3.0F - (2.0F * t));
    }

    private static float lerp(float a, float b, float t) {
        return a + ((b - a) * clamp01(t));
    }

    private static float clamp01(float value) {
        return clamp(value, 0.0F, 1.0F);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private record Oklab(float l, float a, float b) {}
    private record Oklch(float l, float c, float hDegrees) {}
}