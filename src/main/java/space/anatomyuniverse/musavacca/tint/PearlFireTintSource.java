package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintSource {
    private static final float CHROMA_EPSILON = 0.0005F;

    // Flame shape
    private static final float CORE_END = 0.21F;
    private static final float BODY_CENTER = 0.45F;
    private static final float BODY_WIDTH = 0.28F;
    private static final float TAIL_START = 0.74F;

    // Lightness
    private static final float CORE_BRIGHTNESS = 0.046F;
    private static final float MID_BRIGHTNESS = 0.041F;
    private static final float TAIL_DARKNESS = 0.030F;

    // Chroma
    private static final float CHROMA_GAIN = 0.13F;
    private static final float CHROMA_RESCUE = 0.026F;
    private static final float COLOR_HOLD = 0.10F;
    private static final float CHROMA_PRESERVE = 0.18F;

    // Hue
    private static final float HUE_CENTER = 68.0F;
    private static final float HUE_TRAVEL = 0.34F;
    private static final float SECONDARY_SWING = 7.6F;
    private static final float HUE_ANCHOR = 0.21F;

    private PearlFireTintSource() {}

    public static boolean supportsLayer(PearlFireTintProfiles.Profile profile, int tintIndex) {
        return profile != null && profile.supports(tintIndex);
    }

    public static int blockTint(int baseRgb, int tintIndex) {
        return blockTint(baseRgb, tintIndex, PearlFireTintProfiles.FIRE_BLOCK);
    }

    public static int blockTint(int baseRgb, int tintIndex, PearlFireTintProfiles.Profile profile) {
        if (profile == null || !profile.supports(tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        int layerCount = profile.layerCount();
        float t = layerCount <= 1 ? 0.0F : tintIndex / (float) (layerCount - 1);

        int desired = desiredLayerRgb(TintColorUtil.rgb(baseRgb), t);
        float gray = profile.grayFactor(tintIndex);

        if (gray > 0.0F && gray < 1.0F) {
            int r = (desired >> 16) & 0xFF;
            int g = (desired >> 8) & 0xFF;
            int b = desired & 0xFF;

            desired = (clamp255(Math.round(r / gray)) << 16)
                    | (clamp255(Math.round(g / gray)) << 8)
                    | clamp255(Math.round(b / gray));
        }

        return TintColorUtil.opaqueRgb(desired);
    }

    private static int desiredLayerRgb(int baseRgb, float t) {
        if (t <= 0.00001F) {
            return 0xFFFFFF;
        }

        Oklch base = rgbToOklch(baseRgb);

        float core = 1.0F - smooth01(t / CORE_END);
        float body = bell(t, BODY_CENTER, BODY_WIDTH);
        float tail = smooth01((t - TAIL_START) / (1.0F - TAIL_START));
        float ramp = smooth01((t - 0.03F) / 0.26F);

        float baseL = base.l();
        float baseC = base.c();

        if (baseC <= CHROMA_EPSILON) {
            float l = 1.0F - ((1.0F - lerp(0.09F, 0.45F, baseL))
                    * (float) Math.pow(t, lerp(0.56F, 1.24F, baseL)));
            l += CORE_BRIGHTNESS * core * (0.66F + (0.34F * (1.0F - baseL)));
            l += 0.013F * core * body;
            l -= TAIL_DARKNESS * tail * (0.68F + (0.22F * (1.0F - baseL)));

            float anchorL = 1.0F - ((1.0F - lerp(0.17F, 0.45F, baseL))
                    * (float) Math.pow(t, lerp(0.64F, 1.18F, baseL)));
            l = lerp(l, anchorL, 0.23F + (0.10F * body) + (0.09F * tail));

            return oklchToRgbGamutFit(l, 0.0F, 0.0F);
        }

        float h0 = base.hDegrees();
        float temp = clamp((float) Math.cos(Math.toRadians(h0 - HUE_CENTER)), -1.0F, 1.0F);
        float vivid = 1.0F - Math.abs(temp);
        float secondary = (float) Math.sin(Math.toRadians((h0 - HUE_CENTER) * 2.0F));
        float warm = Math.max(0.0F, temp);
        float cool = Math.max(0.0F, -temp);
        float richness = smooth01(baseC / (baseC + 0.18F));

        float shadowL = lerp(0.12F, 0.38F, baseL) - (0.011F * tail * (0.55F + (0.38F * warm)));
        float l = 1.0F - ((1.0F - clamp01(shadowL))
                * (float) Math.pow(t, lerp(0.54F, 1.28F, baseL)));
        l += CORE_BRIGHTNESS * core * (0.66F + (0.34F * (1.0F - baseL)));
        l += 0.013F * core * body;
        l += MID_BRIGHTNESS * (0.62F * body + 0.38F * ramp) * ((0.62F * cool) + (0.38F * vivid));
        l -= TAIL_DARKNESS * tail * ((0.70F + (0.18F * (1.0F - baseL))) + (0.22F * warm));

        float anchorL = 1.0F - ((1.0F - lerp(0.18F, 0.46F, baseL))
                * (float) Math.pow(t, lerp(0.64F, 1.18F, baseL)));
        l = lerp(l, anchorL, 0.21F + (0.10F * body) + (0.08F * tail) + (0.04F * warm) + (0.03F * vivid));

        float normC = smooth01(baseC / (baseC + 0.12F));
        float bodyWeight = (0.60F * body) + (0.40F * ramp);

        float c = (
                baseC * (1.00F + (CHROMA_GAIN * bodyWeight * (0.72F + (0.42F * vivid))) + (0.02F * tail))
                        + (CHROMA_RESCUE * (1.0F - normC) * bodyWeight * (1.0F + (0.16F * vivid)))
        ) * ramp
                * (1.0F - (0.05F * tail))
                * (1.0F - (0.68F * core))
                * (1.0F + (0.06F * core * body))
                * (1.0F + (
                ((COLOR_HOLD * (1.0F - core)) + ((0.065F + (0.040F * vivid)) * tail))
                        * lerp(0.42F, 1.0F, normC)
        ))
                * (1.0F + (0.03F * richness * bodyWeight));
        c = Math.max(0.0F, c);

        float preserveC = baseC
                * ramp
                * (0.94F + (0.08F * body))
                * (1.0F - (0.56F * core))
                * (1.0F - (0.03F * tail));
        c = lerp(c, Math.max(c, preserveC), CHROMA_PRESERVE + (0.05F * tail) + (0.06F * warm));

        float motion = 1.0F - (core * (0.82F + (0.10F * vivid)));
        float arc = lerp(
                1.5F + (5.6F * temp),
                -0.7F - (6.2F * temp),
                clamp01(smooth01((t - 0.21F) / 0.79F) + (0.06F * tail))
        ) * motion;

        float swing = (1.75F + (0.35F * richness)) * bodyWeight * temp * motion * (1.0F - (0.68F * tail));
        float harmonic = SECONDARY_SWING * (0.92F + (0.14F * richness)) * secondary
                * ((0.20F * core) + (0.09F * body) - (0.56F * tail));
        float travel = clamp01((0.12F + (HUE_TRAVEL * vivid * (1.0F + (0.12F * tail))))
                * (1.0F - (0.22F * core))
                * (0.94F + (0.10F * richness)));

        float h = wrapDegrees360(h0 + ((arc + swing + harmonic) * travel));
        h = lerpAngleDegrees(h, h0, HUE_ANCHOR + (0.12F * core) + (0.12F * tail) + (0.06F * warm));

        return oklchToRgbGamutFit(l, c, h);
    }

    private static Oklch rgbToOklch(int rgb) {
        float r = srgbToLinear(((rgb >> 16) & 0xFF) / 255.0F);
        float g = srgbToLinear(((rgb >> 8) & 0xFF) / 255.0F);
        float b = srgbToLinear((rgb & 0xFF) / 255.0F);

        Oklab lab = linearSrgbToOklab(r, g, b);
        float c = (float) Math.hypot(lab.a(), lab.b());

        return new Oklch(
                lab.l(),
                c,
                c <= CHROMA_EPSILON ? 0.0F : wrapDegrees360((float) Math.toDegrees(Math.atan2(lab.b(), lab.a())))
        );
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
        float h = (float) Math.toRadians(hDegrees);
        return oklabToLinearSrgb(new Oklab(
                l,
                c * (float) Math.cos(h),
                c * (float) Math.sin(h)
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
                4.0767416621F * l - 3.3077115913F * m + 0.2309699292F * s,
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
        return (clamp255(Math.round(linearToSrgb(clamp01(r)) * 255.0F)) << 16)
                | (clamp255(Math.round(linearToSrgb(clamp01(g)) * 255.0F)) << 8)
                | clamp255(Math.round(linearToSrgb(clamp01(b)) * 255.0F));
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

    private static float lerpAngleDegrees(float from, float to, float t) {
        float delta = (to - from) % 360.0F;
        if (delta > 180.0F) {
            delta -= 360.0F;
        } else if (delta < -180.0F) {
            delta += 360.0F;
        }
        return wrapDegrees360(from + (delta * clamp01(t)));
    }

    private static float wrapDegrees360(float degrees) {
        float wrapped = degrees % 360.0F;
        return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
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

//package space.anatomyuniverse.musavacca.tint;
//
//public final class PearlFireTintSource {
//    private static final float CHROMA_EPSILON = 0.0005F;
//
//    private static final float CORE_END = 0.21F;
//    private static final float BODY_CENTER = 0.44F;
//    private static final float BODY_WIDTH = 0.27F;
//    private static final float TAIL_START = 0.74F;
//
//    private static final float CORE_BRIGHTNESS = 0.046F;
//    private static final float MID_BRIGHTNESS = 0.036F;
//    private static final float TAIL_DARKNESS = 0.032F;
//
//    private static final float CHROMA_GAIN = 0.13F;
//    private static final float CHROMA_RESCUE = 0.026F;
//    private static final float COLOR_HOLD = 0.12F;
//    private static final float CHROMA_PRESERVE = 0.18F;
//
//    private static final float HUE_CENTER = 68.0F;
//    private static final float HUE_TRAVEL = 0.27F;
//    private static final float SECONDARY_SWING = 6.2F;
//    private static final float CORE_STABILITY = 0.80F;
//    private static final float CORE_SOFTEN = 0.14F;
//    private static final float TAIL_SETTLE = 0.66F;
//    private static final float HUE_ANCHOR = 0.18F;
//
//    private PearlFireTintSource() {}
//
//    public static boolean supportsLayer(PearlFireTintProfiles.Profile profile, int tintIndex) {
//        return profile != null && profile.supports(tintIndex);
//    }
//
//    public static int blockTint(int baseRgb, int tintIndex) {
//        return blockTint(baseRgb, tintIndex, PearlFireTintProfiles.FIRE_BLOCK);
//    }
//
//    public static int blockTint(int baseRgb, int tintIndex, PearlFireTintProfiles.Profile profile) {
//        if (profile == null || !profile.supports(tintIndex)) {
//            return TintColorUtil.NO_TINT;
//        }
//
//        int layerCount = profile.layerCount();
//        float t = layerCount <= 1 ? 0.0F : tintIndex / (float) (layerCount - 1);
//
//        int desired = desiredLayerRgb(TintColorUtil.rgb(baseRgb), t);
//        float gray = profile.grayFactor(tintIndex);
//
//        if (gray > 0.0F && gray < 1.0F) {
//            int r = (desired >> 16) & 0xFF;
//            int g = (desired >> 8) & 0xFF;
//            int b = desired & 0xFF;
//
//            desired = (clamp255(Math.round(r / gray)) << 16)
//                    | (clamp255(Math.round(g / gray)) << 8)
//                    | clamp255(Math.round(b / gray));
//        }
//
//        return TintColorUtil.opaqueRgb(desired);
//    }
//
//    private static int desiredLayerRgb(int baseRgb, float t) {
//        if (t <= 0.00001F) {
//            return 0xFFFFFF;
//        }
//
//        Oklch base = rgbToOklch(baseRgb);
//        float core = 1.0F - smooth01(t / CORE_END);
//        float body = bell(t, BODY_CENTER, BODY_WIDTH);
//        float tail = smooth01((t - TAIL_START) / (1.0F - TAIL_START));
//        float baseL = base.l();
//
//        if (base.c() <= CHROMA_EPSILON) {
//            float l = 1.0F - ((1.0F - lerp(0.05F, 0.44F, baseL))
//                    * (float) Math.pow(t, lerp(0.50F, 1.40F, baseL)));
//            l += CORE_BRIGHTNESS * core * (0.62F + (0.38F * (1.0F - baseL)));
//            l += 0.014F * core * body;
//            l -= TAIL_DARKNESS * tail * (0.74F + (0.26F * (1.0F - baseL)));
//
//            float vanillaL = 1.0F - ((1.0F - lerp(0.10F, 0.40F, baseL))
//                    * (float) Math.pow(t, lerp(0.58F, 1.28F, baseL)));
//            l = lerp(l, vanillaL, 0.18F + (0.08F * body));
//
//            return oklchToRgbGamutFit(l, 0.0F, 0.0F);
//        }
//
//        float h0 = base.hDegrees();
//        float temp = clamp((float) Math.cos(Math.toRadians(h0 - HUE_CENTER)), -1.0F, 1.0F);
//        float vivid = 1.0F - Math.abs(temp);
//        float secondary = (float) Math.sin(Math.toRadians((h0 - HUE_CENTER) * 2.0F));
//        float warm = Math.max(0.0F, temp);
//        float cool = Math.max(0.0F, -temp);
//
//        float shadowL = lerp(0.10F, 0.35F, baseL) - (0.013F * tail * (0.55F + (0.45F * warm)));
//        float l = 1.0F - ((1.0F - clamp01(shadowL))
//                * (float) Math.pow(t, lerp(0.50F, 1.40F, baseL)));
//        l += CORE_BRIGHTNESS * core * (0.62F + (0.38F * (1.0F - baseL)));
//        l += 0.014F * core * body;
//        l += MID_BRIGHTNESS * body * ((0.74F * cool) + (0.45F * vivid));
//        l -= TAIL_DARKNESS * tail * ((0.76F + (0.24F * (1.0F - baseL))) + (0.34F * warm));
//
//        float vanillaL = 1.0F - ((1.0F - lerp(0.14F, 0.42F, baseL))
//                * (float) Math.pow(t, lerp(0.58F, 1.28F, baseL)));
//        l = lerp(l, vanillaL, 0.12F + (0.08F * body) + (0.06F * tail) + (0.03F * warm));
//
//        float baseC = base.c();
//        float normC = smooth01(baseC / (baseC + 0.12F));
//        float c = (
//                baseC * (0.98F + (body * (CHROMA_GAIN + (0.08F * vivid))) + (0.02F * tail))
//                        + (CHROMA_RESCUE * (1.0F - normC) * body * (1.0F + (0.18F * vivid)))
//        ) * smooth01((t - 0.03F) / 0.24F)
//                * (1.0F - (0.07F * tail))
//                * (1.0F - (0.74F * core))
//                * (1.0F + (0.08F * core * body))
//                * (1.0F + (
//                ((COLOR_HOLD * (1.0F - core)) + ((0.075F + (0.045F * vivid)) * tail))
//                        * lerp(0.36F, 1.0F, normC)
//        ));
//        c = Math.max(0.0F, c);
//
//        float preserveC = baseC
//                * smooth01((t - 0.04F) / 0.26F)
//                * (0.94F + (0.08F * body))
//                * (1.0F - (0.58F * core))
//                * (1.0F - (0.03F * tail));
//        c = lerp(c, Math.max(c, preserveC), CHROMA_PRESERVE + (0.04F * tail) + (0.05F * warm));
//
//        float motion = 1.0F - (core * (CORE_STABILITY + (CORE_SOFTEN * vivid)));
//        float arc = lerp(
//                2.0F + (7.0F * temp),
//                -1.0F - (8.0F * temp),
//                clamp01(smooth01((t - 0.20F) / 0.80F) + (0.08F * tail))
//        ) * motion;
//        float swing = 2.4F * body * temp * motion * (1.0F - (TAIL_SETTLE * tail));
//        float harmonic = SECONDARY_SWING * secondary * ((0.24F * core) + (0.10F * body) - (0.70F * tail));
//        float travel = clamp01((0.18F + (HUE_TRAVEL * vivid * (1.0F + (0.18F * tail))))
//                * (1.0F - (0.28F * core)));
//
//        float h = wrapDegrees360(h0 + ((arc + swing + harmonic) * travel));
//        h = lerpAngleDegrees(h, h0, HUE_ANCHOR + (0.08F * core) + (0.08F * tail) + (0.04F * warm));
//
//        return oklchToRgbGamutFit(l, c, h);
//    }
//
//    private static Oklch rgbToOklch(int rgb) {
//        float r = srgbToLinear(((rgb >> 16) & 0xFF) / 255.0F);
//        float g = srgbToLinear(((rgb >> 8) & 0xFF) / 255.0F);
//        float b = srgbToLinear((rgb & 0xFF) / 255.0F);
//
//        Oklab lab = linearSrgbToOklab(r, g, b);
//        float c = (float) Math.hypot(lab.a(), lab.b());
//
//        return new Oklch(
//                lab.l(),
//                c,
//                c <= CHROMA_EPSILON ? 0.0F : wrapDegrees360((float) Math.toDegrees(Math.atan2(lab.b(), lab.a())))
//        );
//    }
//
//    private static int oklchToRgbGamutFit(float l, float c, float hDegrees) {
//        l = clamp01(l);
//        c = Math.max(0.0F, c);
//        hDegrees = wrapDegrees360(hDegrees);
//
//        float[] rgb = oklchToLinearSrgb(l, c, hDegrees);
//        if (!isInSrgbGamut(rgb)) {
//            float low = 0.0F;
//            float high = c;
//
//            for (int i = 0; i < 24; i++) {
//                float mid = (low + high) * 0.5F;
//                if (isInSrgbGamut(oklchToLinearSrgb(l, mid, hDegrees))) {
//                    low = mid;
//                } else {
//                    high = mid;
//                }
//            }
//
//            rgb = oklchToLinearSrgb(l, low, hDegrees);
//        }
//
//        return linearSrgbToInt(rgb[0], rgb[1], rgb[2]);
//    }
//
//    private static float[] oklchToLinearSrgb(float l, float c, float hDegrees) {
//        float h = (float) Math.toRadians(hDegrees);
//        return oklabToLinearSrgb(new Oklab(l, c * (float) Math.cos(h), c * (float) Math.sin(h)));
//    }
//
//    private static Oklab linearSrgbToOklab(float r, float g, float b) {
//        float l = 0.4122214708F * r + 0.5363325363F * g + 0.0514459929F * b;
//        float m = 0.2119034982F * r + 0.6806995451F * g + 0.1073969566F * b;
//        float s = 0.0883024619F * r + 0.2817188376F * g + 0.6299787005F * b;
//
//        float l3 = (float) Math.cbrt(l);
//        float m3 = (float) Math.cbrt(m);
//        float s3 = (float) Math.cbrt(s);
//
//        return new Oklab(
//                0.2104542553F * l3 + 0.7936177850F * m3 - 0.0040720468F * s3,
//                1.9779984951F * l3 - 2.4285922050F * m3 + 0.4505937099F * s3,
//                0.0259040371F * l3 + 0.7827717662F * m3 - 0.8086757660F * s3
//        );
//    }
//
//    private static float[] oklabToLinearSrgb(Oklab c) {
//        float l3 = c.l() + 0.3963377774F * c.a() + 0.2158037573F * c.b();
//        float m3 = c.l() - 0.1055613458F * c.a() - 0.0638541728F * c.b();
//        float s3 = c.l() - 0.0894841775F * c.a() - 1.2914855480F * c.b();
//
//        float l = l3 * l3 * l3;
//        float m = m3 * m3 * m3;
//        float s = s3 * s3 * s3;
//
//        return new float[]{
//                4.0767416621F * l - 3.3077115913F * m + 0.2309699292F * s,
//                -1.2684380046F * l + 2.6097574011F * m - 0.3413193965F * s,
//                -0.0041960863F * l - 0.7034186147F * m + 1.7076147010F * s
//        };
//    }
//
//    private static boolean isInSrgbGamut(float[] rgb) {
//        return rgb != null
//                && rgb.length == 3
//                && Float.isFinite(rgb[0]) && Float.isFinite(rgb[1]) && Float.isFinite(rgb[2])
//                && rgb[0] >= 0.0F && rgb[0] <= 1.0F
//                && rgb[1] >= 0.0F && rgb[1] <= 1.0F
//                && rgb[2] >= 0.0F && rgb[2] <= 1.0F;
//    }
//
//    private static int linearSrgbToInt(float r, float g, float b) {
//        return (clamp255(Math.round(linearToSrgb(clamp01(r)) * 255.0F)) << 16)
//                | (clamp255(Math.round(linearToSrgb(clamp01(g)) * 255.0F)) << 8)
//                | clamp255(Math.round(linearToSrgb(clamp01(b)) * 255.0F));
//    }
//
//    private static float srgbToLinear(float c) {
//        return c <= 0.04045F ? c / 12.92F : (float) Math.pow((c + 0.055F) / 1.055F, 2.4);
//    }
//
//    private static float linearToSrgb(float c) {
//        return c <= 0.0031308F ? 12.92F * c : 1.055F * (float) Math.pow(c, 1.0 / 2.4) - 0.055F;
//    }
//
//    private static float bell(float x, float center, float width) {
//        float v = 1.0F - clamp01(Math.abs(x - center) / Math.max(0.0001F, width));
//        return v * v * (3.0F - (2.0F * v));
//    }
//
//    private static float smooth01(float t) {
//        t = clamp01(t);
//        return t * t * (3.0F - (2.0F * t));
//    }
//
//    private static float lerp(float a, float b, float t) {
//        return a + ((b - a) * clamp01(t));
//    }
//
//    private static float lerpAngleDegrees(float from, float to, float t) {
//        float delta = (to - from) % 360.0F;
//        if (delta > 180.0F) {
//            delta -= 360.0F;
//        } else if (delta < -180.0F) {
//            delta += 360.0F;
//        }
//        return wrapDegrees360(from + (delta * clamp01(t)));
//    }
//
//    private static float wrapDegrees360(float degrees) {
//        float wrapped = degrees % 360.0F;
//        return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
//    }
//
//    private static float clamp01(float value) {
//        return Math.max(0.0F, Math.min(1.0F, value));
//    }
//
//    private static float clamp(float value, float min, float max) {
//        return Math.max(min, Math.min(max, value));
//    }
//
//    private static int clamp255(int value) {
//        return Math.max(0, Math.min(255, value));
//    }
//
//    private record Oklab(float l, float a, float b) {}
//    private record Oklch(float l, float c, float hDegrees) {}
//}