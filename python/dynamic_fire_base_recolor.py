from __future__ import annotations

import argparse
import colorsys
from pathlib import Path
from typing import List, Tuple

from PIL import Image

RGB = Tuple[int, int, int]


TONE_GAMMA = 0.97

HOT_START = 0.82
HOT_CURVE = 1.75


LOWMID_HUE_SHIFT = -0.016
MID_HUE_SHIFT = -0.006
HOT_HUE_SHIFT = 0.012
CORE_HUE_SHIFT = 0.000

MIN_BASE_SATURATION = 0.34
SATURATION_BOOST = 1.18

CORE_DESAT_START = 0.80
CORE_DESAT_END = 1.00
CORE_WHITE_BLEND_START = 0.90
CORE_WHITE_BLEND_END = 1.00
CORE_WHITE_BLEND_MAX = 0.82

SHADOW_DENSITY = 1.05


NEAR_GRAY_THRESHOLD = 6

HSV_STOPS = [
    (0.00, SHADOW_HUE_SHIFT, 0.34, 0.025),
    (0.10, SHADOW_HUE_SHIFT, 0.78, 0.12),
    (0.24, LOWMID_HUE_SHIFT, 1.05, 0.28),
    (0.42, MID_HUE_SHIFT,    1.22, 0.52),
    (0.62, MID_HUE_SHIFT,    1.12, 0.78),
    (0.78, HOT_HUE_SHIFT,    0.88, 0.93),
    (0.90, HOT_HUE_SHIFT,    0.34, 1.00),
    (1.00, CORE_HUE_SHIFT,   0.00, 1.00),
]



def clamp(value: float, low: float = 0.0, high: float = 1.0) -> float:
    return max(low, min(high, value))


def smoothstep(edge0: float, edge1: float, x: float) -> float:
    if edge0 == edge1:
        return 0.0
    t = clamp((x - edge0) / (edge1 - edge0))
    return t * t * (3.0 - 2.0 * t)


def lerp(a: float, b: float, t: float) -> float:
    t = clamp(t)
    return a * (1.0 - t) + b * t


def mix_rgb(a: RGB, b: RGB, t: float) -> RGB:
    t = clamp(t)
    return tuple(round(a[i] * (1.0 - t) + b[i] * t) for i in range(3))


def wrap01(value: float) -> float:
    return value % 1.0


def shortest_hue_delta(a: float, b: float) -> float:
    delta = (b - a) % 1.0
    if delta > 0.5:
        delta -= 1.0
    return delta


def lerp_hue(a: float, b: float, t: float) -> float:
    return wrap01(a + shortest_hue_delta(a, b) * clamp(t))


def hex_to_rgb(hex_color: str) -> RGB:
    hex_color = hex_color.strip().lstrip("#")
    if len(hex_color) != 6:
        raise ValueError(f"Expected a 6-digit hex color, got: {hex_color!r}")
    return tuple(int(hex_color[i:i + 2], 16) for i in (0, 2, 4))


def rgb_to_hex(rgb: RGB) -> str:
    return "#{:02X}{:02X}{:02X}".format(*rgb)


def rgb255_to_hsv(rgb: RGB) -> Tuple[float, float, float]:
    r, g, b = rgb
    return colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)


def hsv_to_rgb255(h: float, s: float, v: float) -> RGB:
    r, g, b = colorsys.hsv_to_rgb(wrap01(h), clamp(s), clamp(v))
    return (
        round(r * 255.0),
        round(g * 255.0),
        round(b * 255.0),
    )


def is_near_grayscale(r: int, g: int, b: int, threshold: int = NEAR_GRAY_THRESHOLD) -> bool:
    return abs(r - g) <= threshold and abs(g - b) <= threshold and abs(r - b) <= threshold


def build_lut(base_hex: str) -> List[RGB]:
    """
    Build a 256-entry grayscale -> colored fire LUT.

    This version is more stylized and closer to the look of crisp game fire:
    - shadow darkness comes from value shaping
    - color richness comes from stronger mid saturation
    - the upper end loses saturation toward white
    - a small hue slide helps the flame feel less flat
    """
    base_rgb = hex_to_rgb(base_hex)
    base_h, base_s, _base_v = rgb255_to_hsv(base_rgb)

    base_s = max(base_s, MIN_BASE_SATURATION)
    base_s = clamp(base_s * SATURATION_BOOST)

    expanded_stops: List[Tuple[float, float, float, float]] = []
    for pos, hue_shift, sat_mul, val in HSV_STOPS:
        h = wrap01(base_h + hue_shift)
        s = clamp(base_s * sat_mul)
        v = clamp(val)
        expanded_stops.append((pos, h, s, v))

    lut: List[RGB] = []

    for gray in range(256):
        t = gray / 255.0

        t = t ** TONE_GAMMA

        if t < 0.5:
            shadow_t = t / 0.5
            shadow_t = shadow_t ** SHADOW_DENSITY
            t = shadow_t * 0.5

        if t > HOT_START:
            t = HOT_START + ((t - HOT_START) / (1.0 - HOT_START)) ** HOT_CURVE * (1.0 - HOT_START)

        chosen = None
        for i in range(len(expanded_stops) - 1):
            p0, h0, s0, v0 = expanded_stops[i]
            p1, h1, s1, v1 = expanded_stops[i + 1]
            if t <= p1:
                local_t = smoothstep(p0, p1, t)
                h = lerp_hue(h0, h1, local_t)
                s = lerp(s0, s1, local_t)
                v = lerp(v0, v1, local_t)
                chosen = (h, s, v)
                break

        if chosen is None:
            _, h, s, v = expanded_stops[-1]
        else:
            h, s, v = chosen

        core_desat = smoothstep(CORE_DESAT_START, CORE_DESAT_END, t)
        s = lerp(s, 0.0, core_desat)

        rgb = hsv_to_rgb255(h, s, v)

        core_white = smoothstep(CORE_WHITE_BLEND_START, CORE_WHITE_BLEND_END, t)
        if core_white > 0.0:
            rgb = mix_rgb(rgb, (255, 255, 255), core_white * CORE_WHITE_BLEND_MAX)

        lut.append(rgb)

    return lut


def recolor_grayscale_fire(input_path: str | Path, output_path: str | Path, base_hex: str) -> None:
    image = Image.open(input_path).convert("RGBA")
    out = Image.new("RGBA", image.size)

    src = image.load()
    dst = out.load()
    lut = build_lut(base_hex)

    for y in range(image.height):
        for x in range(image.width):
            r, g, b, a = src[x, y]

            if a == 0:
                dst[x, y] = (0, 0, 0, 0)
                continue

            if is_near_grayscale(r, g, b):
                gray = round(0.2126 * r + 0.7152 * g + 0.0722 * b)
                nr, ng, nb = lut[gray]
                dst[x, y] = (nr, ng, nb, a)
            else:
                dst[x, y] = (r, g, b, a)

    out.save(output_path)


def save_lut_preview(lut: List[RGB], output_path: str | Path, scale_x: int = 2, height: int = 48) -> None:
    preview = Image.new("RGBA", (len(lut) * scale_x, height), (0, 0, 0, 0))
    px = preview.load()

    for i, color in enumerate(lut):
        for x in range(i * scale_x, (i + 1) * scale_x):
            for y in range(height):
                px[x, y] = (*color, 255)

    preview.save(output_path)


def print_key_samples(lut: List[RGB]) -> None:
    sample_points = [0, 32, 64, 96, 128, 160, 192, 224, 255]
    print("Key grayscale -> fire colors:")
    for gray in sample_points:
        print(f"{gray:3d}: {rgb_to_hex(lut[gray])}")


def resolve_output_name(input_path: Path, out_dir: Path, base_hex: str) -> Path:
    suffix = base_hex.strip().lstrip("#").upper()
    return out_dir / f"{input_path.stem}_{suffix}.png"


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Recolor one or more grayscale fire textures using a higher-quality hue-driven fire ramp."
    )
    parser.add_argument("--hex", required=True, help='Base hex color, for example "#00D6E8"')
    parser.add_argument(
        "--inputs",
        nargs="+",
        required=True,
        help="One or more grayscale fire PNGs, for example base_fire_0.png base_fire_1.png",
    )
    parser.add_argument(
        "--out-dir",
        default=".",
        help="Directory where recolored PNGs will be written",
    )
    parser.add_argument(
        "--preview",
        default="fire_lut_preview.png",
        help="Where to save the LUT preview PNG",
    )
    args = parser.parse_args()

    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    lut = build_lut(args.hex)
    print_key_samples(lut)
    save_lut_preview(lut, args.preview)

    for input_str in args.inputs:
        input_path = Path(input_str)
        output_path = resolve_output_name(input_path, out_dir, args.hex)
        recolor_grayscale_fire(input_path, output_path, args.hex)
        print(f"Saved recolored texture to: {output_path}")

    print(f"\nSaved LUT preview to: {args.preview}")


if __name__ == "__main__":
    main()