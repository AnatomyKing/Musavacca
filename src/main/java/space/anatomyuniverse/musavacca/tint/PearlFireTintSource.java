package space.anatomyuniverse.musavacca.tint;

public final class PearlFireTintSource {
    private static final float EPS = 0.0005F;
    private static final float TAU = 6.2831855F;
    private static final float FULL_COLOR_LIGHTNESS_REMAINDER = 0.15F;

    private PearlFireTintSource() {}

    public static boolean supportsLayer(PearlFireTintProfiles.Profile profile, int tintIndex) {
        return profile != null && profile.supports(tintIndex);
    }

    public static int profileTint(int baseRgb, int layerIndex, PearlFireTintProfiles.Profile profile) {
        return blockTint(baseRgb, layerIndex, profile);
    }

    public static int blockTint(int baseRgb, int tintIndex, PearlFireTintProfiles.Profile profile) {
        if (!supportsLayer(profile, tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        int layerCount = profile.layerCount();
        float t = layerCount <= 1 ? 0.0F : tintIndex / (float) (layerCount - 1);

        int desired = desiredLayerRgb(TintColorUtil.rgb(baseRgb), t, profile);
        float gray = profile.grayFactor(tintIndex);

        if (gray > 0.0F && gray < 1.0F) {
            desired = unMultiplyGray(desired, gray);
        }

        return TintColorUtil.opaqueRgb(desired);
    }

    private static int desiredLayerRgb(int baseRgb, float t, PearlFireTintProfiles.Profile profile) {
        t = clamp01(t);

        float configuredWhiteCore = clamp01(profile.coreToTailLightness());
        float colorAmountTakeOver = clamp01(profile.colorAmountTakeOver());
        float whiteCore = configuredWhiteCore
                * mix(1.0F, FULL_COLOR_LIGHTNESS_REMAINDER, colorAmountTakeOver);
        float colorWhiteCore = configuredWhiteCore * (1.0F - colorAmountTakeOver);
        float jumpiness = Math.max(0.0F, profile.colorJumpiness());
        float layerContrast = Math.max(0.0F, profile.layerContrast());

        float jumpCurve = jumpiness / (1.0F + jumpiness);
        float jumpScale = jumpiness * (2.0F + (0.65F * Math.max(0.0F, jumpiness - 1.0F)));

        Oklch base = rgbToOklch(baseRgb);

        float baseL = clamp01(base.l());
        float baseC = Math.max(0.0F, base.c());
        float baseH = wrap360(base.h());
        float hasColor = baseC > EPS ? 1.0F : 0.0F;

        float core = 1.0F - smooth(t / 0.240F);
        float body = bell(t, 0.470F, 0.405F);
        float tail = smooth((t - 0.745F) / 0.255F);
        float ramp = smooth((t - 0.004F) / 0.420F);
        float bodyWeight = (body + ramp) * 0.50F;

        float tempRaw = clamp((float) Math.cos(rad(baseH - 68.0F)), -1.0F, 1.0F);
        float secondaryRaw = (float) Math.sin(rad((baseH - 68.0F) * 2.0F));

        float temp = tempRaw * hasColor;
        float secondary = secondaryRaw * hasColor;
        float vivid = (1.0F - Math.abs(tempRaw)) * hasColor;
        float warm = Math.max(0.0F, temp);
        float cool = Math.max(0.0F, -temp);

        float rgbSat = rgbSaturation(baseRgb);
        float chromaStrength = smooth(baseC / (baseC + 0.105F));
        float brightStrength = smooth((baseL - 0.50F) / 0.42F);
        float vividness = hasColor * smooth((rgbSat * 0.58F) + (chromaStrength * 0.42F));
        float softColor = hasColor * (1.0F - vividness);

        float darkness = smooth((0.690F - baseL) / 0.540F) * (0.74F + (0.26F * hasColor));
        float glow = smooth((baseL - 0.500F) / 0.430F) * (1.0F - (0.54F * darkness));

        float ink = darkness * smooth((t - 0.030F) / 0.900F);
        float inkBody = ink * (0.26F + (0.74F * bodyWeight));
        float inkTail = ink * (0.46F + (0.54F * tail));

        float glowBody = glow * (0.25F + (0.75F * bodyWeight));
        float glowTail = glow * (0.38F + (0.62F * tail));

        float gamutPressure = clamp01(
                vividness
                        * (0.40F + (0.60F * brightStrength))
                        * (0.88F + (0.18F * warm))
                        * (1.0F + (0.030F * darkness * chromaStrength))
                        * (1.0F - (0.045F * glow))
        );

        float hotFloor = mix(
                mix(0.560F, 0.455F, ink * (1.0F - (0.82F * whiteCore * core))),
                0.650F,
                glow * (1.0F - (0.55F * whiteCore * core))
        );

        float bodyFloor = mix(
                mix(0.480F, 0.245F, inkBody),
                0.560F,
                glowBody * (1.0F - (0.42F * inkBody))
        );

        float whiteBodyFloor = mix(
                mix(0.535F, 0.330F, inkBody),
                0.635F,
                glowBody * (1.0F - (0.35F * inkBody))
        );

        float tailFloor = mix(
                mix(0.300F, 0.085F, inkTail),
                0.405F,
                glowTail * (1.0F - (0.55F * inkTail))
        );

        float whiteTailFloor = mix(
                mix(0.275F, 0.070F, inkTail),
                0.445F,
                glowTail * (1.0F - (0.50F * inkTail))
        );

        float hotL = mix(
                clamp(
                        baseL
                                + ((1.0F - baseL) * (0.150F - (0.044F * gamutPressure)))
                                - (0.012F * gamutPressure)
                                - (0.026F * ink)
                                + (0.030F * glow * (1.0F - (0.65F * whiteCore * core))),
                        hotFloor,
                        0.930F
                ),
                1.0F - (0.004F * darkness),
                whiteCore
        );

        float bodyL = mix(
                clamp(
                        0.392F
                                + (baseL * 0.328F)
                                + (0.034F * cool)
                                + (0.016F * vivid)
                                + (0.006F * softColor)
                                + (0.052F * glowBody)
                                - (0.010F * gamutPressure)
                                - (0.110F * inkBody),
                        bodyFloor,
                        0.805F
                ),
                clamp(
                        0.540F
                                + (baseL * 0.218F)
                                + (0.022F * cool)
                                + (0.042F * glowBody)
                                - (0.004F * gamutPressure)
                                - (0.092F * inkBody),
                        whiteBodyFloor,
                        0.850F
                ),
                whiteCore * mix(0.52F, 0.32F, inkBody) * (1.0F - (0.18F * glowBody))
        );

        float tailL = mix(
                clamp(
                        0.360F
                                + (baseL * 0.155F)
                                + (0.026F * cool)
                                + (0.006F * softColor)
                                + (0.045F * glowTail)
                                - (0.145F * inkTail),
                        tailFloor,
                        0.625F
                ),
                clamp(
                        0.342F
                                + (baseL * 0.138F)
                                + (0.022F * cool)
                                + (0.040F * glowTail)
                                - (0.128F * inkTail),
                        whiteTailFloor,
                        0.600F
                ),
                whiteCore * mix(1.0F, 0.62F, inkTail) * (1.0F - (0.16F * glowTail))
        );

        float l = mix(
                mix(hotL, bodyL, smooth(t / 0.670F)),
                tailL,
                smooth((t - 0.625F) / 0.375F)
        );

        l += 0.018F * body * (1.0F - tail) * (1.0F - (0.35F * inkBody));
        l += 0.006F * core * (1.0F - tail) * mix(1.0F, 0.50F, whiteCore);
        l += 0.010F * bodyWeight * ((0.52F * cool) + (0.48F * vivid)) * (1.0F - (0.20F * ink));
        l += glow * (0.010F * core + 0.018F * bodyWeight + 0.014F * tail) * (1.0F - (0.50F * ink));
        l -= 0.0030F * tail * (0.58F + (0.42F * whiteCore)) * (0.88F + (0.12F * warm));
        l += 0.008F * tail * (0.42F + (0.58F * (1.0F - whiteCore))) * (0.76F + (0.24F * body));

        l -= ink
                * (0.020F + (0.078F * smooth(t / 0.760F)))
                * (1.0F - (0.78F * whiteCore * core))
                * (1.0F - (0.42F * glow));

        float contrastMask = smooth((t - 0.035F) / 0.900F)
                * (1.0F - (0.58F * whiteCore * core));

        float contrastPivot = bodyL
                - (0.045F * ink)
                + (0.032F * glow)
                + (0.010F * cool)
                + (0.008F * vivid);

        l = contrastAround(
                l,
                contrastPivot,
                layerContrast + (0.16F * ink) - (0.075F * glow),
                contrastMask
        );

        l = clamp01(l);

        float colorWhiteKill = colorWhiteCore * core;

        float hotChromaRamp = mix(
                0.72F + (0.16F * core) + (0.12F * ramp),
                smooth((t + 0.010F) / 0.455F),
                colorWhiteCore
        );

        float bodyBoost = 1.0F
                + bodyWeight
                * (0.100F + (0.150F * jumpCurve))
                * (0.78F + (0.38F * vivid) + (0.10F * inkBody) + (0.10F * glowBody))
                * (1.0F - (0.120F * gamutPressure));

        float lowChromaRescue = hasColor
                * (0.016F + (0.010F * jumpCurve))
                * (1.0F - chromaStrength)
                * bodyWeight
                * (1.0F + (0.15F * vivid) + (0.25F * inkBody) + (0.42F * glowBody));

        float tailHold = 1.0F
                + tail
                * (0.026F + (0.030F * (1.0F - colorWhiteCore)) + (0.040F * inkTail) + (0.018F * glowTail))
                * (1.0F + (0.42F * jumpCurve));

        float colorHold = 1.0F
                + (
                ((0.062F * (1.0F - core)) + ((0.034F + (0.024F * vivid)) * tail))
                        * mix(0.38F, 1.0F, chromaStrength)
        );

        float toneColorHold = 1.0F
                + ink
                * (0.075F + (0.125F * chromaStrength))
                * (0.35F + (0.65F * bodyWeight) + (0.30F * tail))
                * (1.0F - (0.45F * colorWhiteKill))
                + glow
                * hasColor
                * (0.040F + (0.075F * chromaStrength) + (0.055F * (1.0F - chromaStrength) * bodyWeight))
                * (1.0F - (0.40F * colorWhiteKill));

        float c = ((baseC * bodyBoost) + lowChromaRescue)
                * hotChromaRamp
                * (1.0F - (0.285F * colorWhiteKill))
                * tailHold
                * colorHold
                * toneColorHold
                * (1.0F - (0.014F * tail * colorWhiteCore))
                * (1.0F + (0.014F * chromaStrength * bodyWeight));

        c = Math.max(c,
                baseC
                        * hotChromaRamp
                        * (0.900F + (0.100F * body))
                        * (1.0F - (0.240F * colorWhiteKill))
                        * (1.0F - (0.006F * tail))
                        * (1.0F - (0.040F * gamutPressure * core))
                        * (1.0F + (0.095F * ink * (1.0F - core)))
                        * (1.0F + (0.060F * glow * hasColor * (1.0F - core)))
        );

        c += 0.0035F
                * softColor
                * body
                * (1.0F - core)
                * (1.0F - tail)
                * (0.45F + (0.55F * jumpCurve));

        c = Math.max(0.0F, c);

        float motion = 1.0F - (core * (0.76F + (0.07F * vivid)));

        float travel = clamp01(
                (0.098F + (0.255F * vivid * (1.0F + (0.085F * tail))))
                        * (1.0F - (0.16F * core))
                        * (0.94F + (0.085F * chromaStrength))
                        * (1.0F - (0.100F * gamutPressure))
                        * (1.0F - (0.040F * ink))
                        * (1.0F - (0.020F * glow))
        );

        float arc = mix(
                1.20F + (4.45F * temp),
                -0.52F - (4.85F * temp),
                clamp01(smooth((t - 0.18F) / 0.82F) + (0.035F * tail))
        ) * motion;

        float swing = (1.20F + (0.26F * chromaStrength))
                * bodyWeight
                * temp
                * motion
                * (1.0F - (0.52F * tail));

        float harmonic = 5.60F
                * (0.92F + (0.11F * chromaStrength))
                * secondary
                * ((0.14F * core) + (0.07F * body) - (0.36F * tail));

        float h = wrap360(baseH + (((arc + swing + harmonic) * travel * jumpScale)
                + waveJump(t, baseH, body, tail, jumpiness)));

        float anchor = 0.255F
                + (0.090F * core)
                + (0.075F * tail)
                + (0.034F * warm)
                + (0.026F * vivid)
                + (0.032F * gamutPressure * (core + body))
                + (0.065F * ink * (0.42F + (0.58F * tail)))
                + (0.030F * glow * (0.30F + (0.70F * core)));

        h = angleMix(h, baseH, anchor);

        return oklchToRgb(l, c, h);
    }

    private static float waveJump(float t, float hue, float body, float tail, float jumpiness) {
        float extra = Math.max(0.0F, jumpiness - 0.5F);

        if (extra <= 0.0F) {
            return 0.0F;
        }

        float phaseA = rad((hue * 1.6180339F) + 31.0F);
        float phaseB = rad((hue * 0.7548777F) + 127.0F);

        return extra * (
                ((float) Math.sin((t * TAU * 1.24F) + phaseA) * body * 17.0F)
                        + ((float) Math.sin((t * TAU * 1.92F) + phaseB) * tail * 20.0F)
        );
    }

    private static int unMultiplyGray(int rgb, float gray) {
        return (clamp255(Math.round(((rgb >> 16) & 0xFF) / gray)) << 16)
                | (clamp255(Math.round(((rgb >> 8) & 0xFF) / gray)) << 8)
                | clamp255(Math.round((rgb & 0xFF) / gray));
    }

    private static Oklch rgbToOklch(int rgb) {
        float r = srgbToLinear(((rgb >> 16) & 0xFF) / 255.0F);
        float g = srgbToLinear(((rgb >> 8) & 0xFF) / 255.0F);
        float b = srgbToLinear((rgb & 0xFF) / 255.0F);

        float l = 0.4122214708F * r + 0.5363325363F * g + 0.0514459929F * b;
        float m = 0.2119034982F * r + 0.6806995451F * g + 0.1073969566F * b;
        float s = 0.0883024619F * r + 0.2817188376F * g + 0.6299787005F * b;

        float l3 = (float) Math.cbrt(l);
        float m3 = (float) Math.cbrt(m);
        float s3 = (float) Math.cbrt(s);

        float okL = 0.2104542553F * l3 + 0.7936177850F * m3 - 0.0040720468F * s3;
        float okA = 1.9779984951F * l3 - 2.4285922050F * m3 + 0.4505937099F * s3;
        float okB = 0.0259040371F * l3 + 0.7827717662F * m3 - 0.8086757660F * s3;

        float c = (float) Math.hypot(okA, okB);
        float h = c <= EPS ? 0.0F : wrap360((float) Math.toDegrees(Math.atan2(okB, okA)));

        return new Oklch(okL, c, h);
    }

    private static int oklchToRgb(float l, float c, float h) {
        l = clamp01(l);
        c = Math.max(0.0F, c);
        h = wrap360(h);

        float[] direct = oklchToLinearRgb(l, c, h);

        if (inGamut(direct)) {
            return linearRgbToInt(direct[0], direct[1], direct[2]);
        }

        float bestL = l;
        float bestC = maxChroma(l, c, h);
        float bestScore = c <= EPS ? 1.0F : bestC / c;
        float maxMove = 0.020F + (0.065F * (1.0F - clamp01(bestScore)));

        for (int i = -5; i <= 5; i++) {
            if (i == 0) {
                continue;
            }

            float nextL = clamp01(l + ((i / 5.0F) * maxMove));
            float nextC = maxChroma(nextL, c, h);

            float keep = c <= EPS ? 1.0F : nextC / c;
            float move = Math.abs(nextL - l);
            float penalty = move * (nextL < l ? 2.35F : 1.75F);
            float score = keep - penalty;

            if (score > bestScore) {
                bestScore = score;
                bestL = nextL;
                bestC = nextC;
            }
        }

        float[] fitted = oklchToLinearRgb(bestL, bestC, h);
        return linearRgbToInt(fitted[0], fitted[1], fitted[2]);
    }

    private static float maxChroma(float l, float wantedC, float h) {
        if (wantedC <= 0.0F) {
            return 0.0F;
        }

        if (inGamut(oklchToLinearRgb(l, wantedC, h))) {
            return wantedC;
        }

        float low = 0.0F;
        float high = wantedC;

        for (int i = 0; i < 18; i++) {
            float mid = (low + high) * 0.5F;

            if (inGamut(oklchToLinearRgb(l, mid, h))) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return low;
    }

    private static float[] oklchToLinearRgb(float l, float c, float hDegrees) {
        float h = rad(hDegrees);
        float a = c * (float) Math.cos(h);
        float b = c * (float) Math.sin(h);

        float l3 = l + 0.3963377774F * a + 0.2158037573F * b;
        float m3 = l - 0.1055613458F * a - 0.0638541728F * b;
        float s3 = l - 0.0894841775F * a - 1.2914855480F * b;

        float ll = l3 * l3 * l3;
        float mm = m3 * m3 * m3;
        float ss = s3 * s3 * s3;

        return new float[]{
                4.0767416621F * ll - 3.3077115913F * mm + 0.2309699292F * ss,
                -1.2684380046F * ll + 2.6097574011F * mm - 0.3413193965F * ss,
                -0.0041960863F * ll - 0.7034186147F * mm + 1.7076147010F * ss
        };
    }

    private static boolean inGamut(float[] rgb) {
        return rgb != null
                && rgb.length == 3
                && Float.isFinite(rgb[0])
                && Float.isFinite(rgb[1])
                && Float.isFinite(rgb[2])
                && rgb[0] >= 0.0F && rgb[0] <= 1.0F
                && rgb[1] >= 0.0F && rgb[1] <= 1.0F
                && rgb[2] >= 0.0F && rgb[2] <= 1.0F;
    }

    private static int linearRgbToInt(float r, float g, float b) {
        return (clamp255(Math.round(linearToSrgb(clamp01(r)) * 255.0F)) << 16)
                | (clamp255(Math.round(linearToSrgb(clamp01(g)) * 255.0F)) << 8)
                | clamp255(Math.round(linearToSrgb(clamp01(b)) * 255.0F));
    }

    private static float rgbSaturation(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255.0F;
        float g = ((rgb >> 8) & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));

        return max <= 0.0F ? 0.0F : (max - min) / max;
    }

    private static float srgbToLinear(float v) {
        return v <= 0.04045F
                ? v / 12.92F
                : (float) Math.pow((v + 0.055F) / 1.055F, 2.4F);
    }

    private static float linearToSrgb(float v) {
        return v <= 0.0031308F
                ? 12.92F * v
                : (1.055F * (float) Math.pow(v, 1.0F / 2.4F)) - 0.055F;
    }

    private static float bell(float x, float center, float width) {
        return smooth(1.0F - clamp01(Math.abs(x - center) / Math.max(0.0001F, width)));
    }

    private static float smooth(float v) {
        v = clamp01(v);
        return v * v * (3.0F - (2.0F * v));
    }

    private static float mix(float a, float b, float t) {
        return a + ((b - a) * clamp01(t));
    }

    private static float contrastAround(float value, float pivot, float amount, float mask) {
        float strength = Math.max(0.0F, amount);
        return mix(value, pivot + ((value - pivot) * strength), mask);
    }

    private static float angleMix(float from, float to, float t) {
        float delta = (to - from) % 360.0F;

        if (delta > 180.0F) {
            delta -= 360.0F;
        } else if (delta < -180.0F) {
            delta += 360.0F;
        }

        return wrap360(from + (delta * clamp01(t)));
    }

    private static float wrap360(float degrees) {
        float wrapped = degrees % 360.0F;
        return wrapped < 0.0F ? wrapped + 360.0F : wrapped;
    }

    private static float rad(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    private static float clamp01(float v) {
        return Math.max(0.0F, Math.min(1.0F, v));
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int clamp255(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private record Oklch(float l, float c, float h) {}
}