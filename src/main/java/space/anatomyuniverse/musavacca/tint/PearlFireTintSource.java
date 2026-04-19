package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintSource {
    public static final int LAYER_COUNT = 32;

    private static final float CHROMA_EPSILON = 0.0005F;

    private static final float CORE_END = 0.19F;
    private static final float BODY_CENTER = 0.43F;
    private static final float BODY_WIDTH = 0.25F;
    private static final float TAIL_START = 0.72F;

    private static final float CORE_BRIGHTNESS = 0.060F;
    private static final float MID_BRIGHTNESS = 0.050F;
    private static final float TAIL_DARKNESS = 0.047F;

    private static final float CHROMA_GAIN = 0.18F;
    private static final float CHROMA_RESCUE = 0.034F;
    private static final float COLOR_HOLD = 0.17F;

    private static final float HUE_TRAVEL = 0.36F;
    private static final float SECONDARY_SWING = 8.4F;
    private static final float CORE_STABILITY = 0.74F;
    private static final float CORE_SOFTEN = 0.18F;
    private static final float TAIL_SETTLE = 0.58F;

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

        float t = tintIndex / (float) (LAYER_COUNT - 1);
        int desiredRgb = desiredLayerRgb(TintColorUtil.rgb(baseRgb), t);

        float grayFactor = SOURCE_GRAY_BY_LAYER[tintIndex] / 255.0F;
        if (grayFactor <= 0.0F) {
            return TintColorUtil.opaqueRgb(desiredRgb);
        }

        int r = (desiredRgb >> 16) & 0xFF;
        int g = (desiredRgb >> 8) & 0xFF;
        int b = desiredRgb & 0xFF;

        int tintR = clamp255(Math.round(r / grayFactor));
        int tintG = clamp255(Math.round(g / grayFactor));
        int tintB = clamp255(Math.round(b / grayFactor));

        return TintColorUtil.opaqueRgb((tintR << 16) | (tintG << 8) | tintB);
    }

    private static int desiredLayerRgb(int baseRgb, float t) {
        if (t <= 0.00001F) {
            return 0xFFFFFF;
        }

        Oklch base = rgbToOklch(baseRgb);

        float core = 1.0F - smooth01(t / CORE_END);
        float body = bell(t, BODY_CENTER, BODY_WIDTH);
        float tail = smooth01((t - TAIL_START) / (1.0F - TAIL_START));

        float baseL = base.l();

        if (base.c() <= CHROMA_EPSILON) {
            float shadowL = lerp(0.02F, 0.48F, baseL);
            float l = 1.0F - ((1.0F - shadowL) * (float) Math.pow(t, lerp(0.35F, 1.65F, baseL)));
            l += CORE_BRIGHTNESS * core * (0.70F + (0.30F * (1.0F - baseL)));
            l += 0.021F * core * body;
            l -= TAIL_DARKNESS * tail * (0.88F + (0.12F * (1.0F - baseL)));
            return oklchToRgbGamutFit(clamp01(l), 0.0F, 0.0F);
        }

        float h0 = base.hDegrees();
        float temp = clamp((float) Math.cos(Math.toRadians(h0 - 70.0F)), -1.0F, 1.0F);
        float vivid = 1.0F - Math.abs(temp);
        float secondary = (float) Math.sin(Math.toRadians((h0 - 70.0F) * 2.0F));
        float warm = Math.max(0.0F, temp);
        float cool = Math.max(0.0F, -temp);

        float shadowL = lerp(0.08F, 0.38F, baseL) - (0.019F * tail * (0.60F + (0.40F * warm)));
        float l = 1.0F - ((1.0F - clamp01(shadowL)) * (float) Math.pow(t, lerp(0.35F, 1.65F, baseL)));
        l += CORE_BRIGHTNESS * core * (0.70F + (0.30F * (1.0F - baseL)));
        l += 0.021F * core * body;
        l += MID_BRIGHTNESS * body * ((0.92F * cool) + (0.56F * vivid));
        l -= TAIL_DARKNESS * tail * ((0.88F + (0.12F * (1.0F - baseL))) + (0.60F * warm));

        float baseC = base.c();
        float normalizedBaseC = smooth01(baseC / (baseC + 0.12F));
        float c = (
                baseC * (1.02F + (body * (CHROMA_GAIN + (0.13F * vivid))) + (0.04F * tail))
                        + (CHROMA_RESCUE * (1.0F - normalizedBaseC) * body * (1.0F + (0.28F * vivid)))
        ) * smooth01((t - 0.02F) / 0.22F)
                * (1.0F - (0.09F * tail))
                * (1.0F - (0.82F * core))
                * (1.0F + (0.12F * core * body))
                * (1.0F + (
                ((COLOR_HOLD * (1.0F - core)) + ((0.115F + (0.065F * vivid)) * tail))
                        * lerp(0.32F, 1.0F, normalizedBaseC)
        ));
        c = Math.max(0.0F, c);

        float motion = 1.0F - (core * (CORE_STABILITY + (CORE_SOFTEN * vivid)));
        float arcT = clamp01(smooth01((t - 0.18F) / 0.82F) + (0.12F * tail));
        float arc = lerp(2.5F + (9.5F * temp), -1.5F - (10.5F * temp), arcT) * motion;
        float swing = 3.2F * body * temp * motion * (1.0F - (TAIL_SETTLE * tail));
        float harmonic = SECONDARY_SWING * secondary * ((0.30F * core) + (0.13F * body) - (0.86F * tail));
        float travel = clamp01((0.24F + (HUE_TRAVEL * vivid * (1.0F + (0.28F * tail)))) * (1.0F - (0.36F * core)));
        float h = wrapDegrees360(h0 + ((arc + swing + harmonic) * travel));

        return oklchToRgbGamutFit(clamp01(l), c, h);
    }

    private static Oklch rgbToOklch(int rgb) {
        float r = srgbToLinear(((rgb >> 16) & 0xFF) / 255.0F);
        float g = srgbToLinear(((rgb >> 8) & 0xFF) / 255.0F);
        float b = srgbToLinear((rgb & 0xFF) / 255.0F);

        Oklab lab = linearSrgbToOklab(r, g, b);
        float c = (float) Math.hypot(lab.a(), lab.b());
        float h = c <= CHROMA_EPSILON ? 0.0F : wrapDegrees360((float) Math.toDegrees(Math.atan2(lab.b(), lab.a())));

        return new Oklch(lab.l(), c, h);
    }

    private static int oklchToRgbGamutFit(float l, float c, float hDegrees) {
        l = clamp01(l);
        c = Math.max(0.0F, c);
        hDegrees = wrapDegrees360(hDegrees);

        float[] rgb = oklchToLinearSrgb(l, c, hDegrees);
        if (!isInSrgbGamut(rgb)) {
            float low = 0.0F;
            float high = c;

            for (int i = 0; i < 24; i++) {
                float mid = (low + high) * 0.5F;
                if (isInSrgbGamut(oklchToLinearSrgb(l, mid, hDegrees))) {
                    low = mid;
                } else {
                    high = mid;
                }
            }

            rgb = oklchToLinearSrgb(l, low, hDegrees);
        }

        return linearSrgbToInt(rgb[0], rgb[1], rgb[2]);
    }

    private static float[] oklchToLinearSrgb(float l, float c, float hDegrees) {
        float hRad = (float) Math.toRadians(hDegrees);
        return oklabToLinearSrgb(new Oklab(
                l,
                c * (float) Math.cos(hRad),
                c * (float) Math.sin(hRad)
        ));
    }

    private static Oklab linearSrgbToOklab(float r, float g, float b) {
        float l = 0.4122214708F * r + 0.5363325363F * g + 0.0514459929F * b;
        float m = 0.2119034982F * r + 0.6806995451F * g + 0.1073969566F * b;
        float s = 0.0883024619F * r + 0.2817188376F * g + 0.6299787005F * b;

        float l3 = (float) Math.cbrt(l);
        float m3 = (float) Math.cbrt(m);
        float s3 = (float) Math.cbrt(s);

        return new Oklab(
                0.2104542553F * l3 + 0.7936177850F * m3 - 0.0040720468F * s3,
                1.9779984951F * l3 - 2.4285922050F * m3 + 0.4505937099F * s3,
                0.0259040371F * l3 + 0.7827717662F * m3 - 0.8086757660F * s3
        );
    }

    private static float[] oklabToLinearSrgb(Oklab c) {
        float l3 = c.l() + 0.3963377774F * c.a() + 0.2158037573F * c.b();
        float m3 = c.l() - 0.1055613458F * c.a() - 0.0638541728F * c.b();
        float s3 = c.l() - 0.0894841775F * c.a() - 1.2914855480F * c.b();

        float l = l3 * l3 * l3;
        float m = m3 * m3 * m3;
        float s = s3 * s3 * s3;

        return new float[]{
                +4.0767416621F * l - 3.3077115913F * m + 0.2309699292F * s,
                -1.2684380046F * l + 2.6097574011F * m - 0.3413193965F * s,
                -0.0041960863F * l - 0.7034186147F * m + 1.7076147010F * s
        };
    }

    private static boolean isInSrgbGamut(float[] rgb) {
        return rgb != null
                && rgb.length == 3
                && Float.isFinite(rgb[0]) && Float.isFinite(rgb[1]) && Float.isFinite(rgb[2])
                && rgb[0] >= 0.0F && rgb[0] <= 1.0F
                && rgb[1] >= 0.0F && rgb[1] <= 1.0F
                && rgb[2] >= 0.0F && rgb[2] <= 1.0F;
    }

    private static int linearSrgbToInt(float r, float g, float b) {
        int ri = clamp255(Math.round(linearToSrgb(clamp01(r)) * 255.0F));
        int gi = clamp255(Math.round(linearToSrgb(clamp01(g)) * 255.0F));
        int bi = clamp255(Math.round(linearToSrgb(clamp01(b)) * 255.0F));
        return (ri << 16) | (gi << 8) | bi;
    }

    private static float srgbToLinear(float c) {
        return c <= 0.04045F ? c / 12.92F : (float) Math.pow((c + 0.055F) / 1.055F, 2.4);
    }

    private static float linearToSrgb(float c) {
        return c <= 0.0031308F ? 12.92F * c : 1.055F * (float) Math.pow(c, 1.0 / 2.4) - 0.055F;
    }

    private static float bell(float x, float center, float width) {
        float v = 1.0F - clamp01(Math.abs(x - center) / Math.max(0.0001F, width));
        return v * v * (3.0F - (2.0F * v));
    }

    private static float smooth01(float t) {
        t = clamp01(t);
        return t * t * (3.0F - (2.0F * t));
    }

    private static float lerp(float a, float b, float t) {
        return a + ((b - a) * clamp01(t));
    }

    private static float wrapDegrees360(float degrees) {
        float wrapped = degrees % 360.0F;
        return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private record Oklab(float l, float a, float b) {}
    private record Oklch(float l, float c, float hDegrees) {}
}