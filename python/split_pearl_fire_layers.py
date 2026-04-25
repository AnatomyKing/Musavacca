from __future__ import annotations

from pathlib import Path
from PIL import Image

INPUT_FILES = [
    "pearl_fire_0.png",
    "pearl_fire_1.png",
]

EXPECTED_LAYER_COUNT = 32


def open_rgba(path: Path) -> Image.Image:
    return Image.open(path).convert("RGBA")


def validate_grayscale(img: Image.Image, image_name: str) -> None:
    px = img.load()

    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            if r != g or g != b:
                raise ValueError(
                    f"{image_name}: non-grayscale pixel at ({x}, {y}) -> {(r, g, b, a)}"
                )


def collect_shared_palette(images: list[tuple[Path, Image.Image]]) -> list[int]:
    values: set[int] = set()

    for path, img in images:
        validate_grayscale(img, path.name)
        px = img.load()

        for y in range(img.height):
            for x in range(img.width):
                r, g, b, a = px[x, y]
                if a != 0:
                    values.add(r)

    palette = sorted(values, reverse=True)  # lightest -> darkest

    if len(palette) != EXPECTED_LAYER_COUNT:
        raise ValueError(
            f"Expected {EXPECTED_LAYER_COUNT} grayscale values across the source textures, "
            f"but found {len(palette)}.\nPalette: {palette}"
        )

    return palette


def split_image_into_layers(path: Path, img: Image.Image, palette: list[int]) -> None:
    src = img.load()

    for layer_index, gray in enumerate(palette):
        out = Image.new("RGBA", img.size, (0, 0, 0, 0))
        dst = out.load()

        for y in range(img.height):
            for x in range(img.width):
                r, g, b, a = src[x, y]
                if a != 0 and r == gray:
                    dst[x, y] = (r, g, b, a)

        out_path = path.with_name(f"{path.stem}_{layer_index}.png")
        out.save(out_path)

    print(f"{path.name} -> wrote {len(palette)} layers")


def main() -> None:
    base_dir = Path(__file__).resolve().parent

    paths = [base_dir / name for name in INPUT_FILES]
    for path in paths:
        if not path.exists():
            raise FileNotFoundError(f"Missing input image: {path}")

    images = [(path, open_rgba(path)) for path in paths]
    palette = collect_shared_palette(images)

    print("Detected shared SOURCE_GRAY_BY_LAYER (lightest -> darkest):")
    print(palette)

    for path, img in images:
        split_image_into_layers(path, img, palette)

    print("\nDone.")
    print("Generated:")
    print("  pearl_fire_0_0.png ... pearl_fire_0_31.png")
    print("  pearl_fire_1_0.png ... pearl_fire_1_31.png")


if __name__ == "__main__":
    main()