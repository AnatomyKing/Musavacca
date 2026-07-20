#!/usr/bin/env python3
"""Create Minecraft tint-safe grayscale carrier textures from PNG files.

For every pixel, the output gray is max(red, green, blue). This is the
smallest neutral carrier that retains enough per-channel headroom to rebuild
the original RGB color after Minecraft multiplies the texture by a tint.

Examples:
    python make_tint_grayscale.py armor.png
    python make_tint_grayscale.py armor_1.png armor_2.png helmet.png
    python make_tint_grayscale.py armor.png --gray-count 7
    python make_tint_grayscale.py textures/ --recursive
    python make_tint_grayscale.py armor.png --output-dir converted --overwrite

Requires Pillow:
    python -m pip install Pillow
"""

from __future__ import annotations

import argparse
from collections import Counter
from pathlib import Path
from typing import Iterable

from PIL import Image, UnidentifiedImageError


OUTPUT_SUFFIX = "_tint_grayscale"


def pixel_data(image: Image.Image) -> Iterable[tuple[int, int, int, int]]:
    """Support current and older Pillow versions without resampling pixels."""
    if hasattr(image, "get_flattened_data"):
        return image.get_flattened_data()
    return image.getdata()


def find_pngs(inputs: list[Path], recursive: bool) -> list[Path]:
    found: list[Path] = []

    for supplied in inputs:
        path = supplied.expanduser().resolve()

        if path.is_file():
            if path.suffix.lower() != ".png":
                raise ValueError(f"Not a PNG file: {path}")
            found.append(path)
            continue

        if path.is_dir():
            iterator = path.rglob("*.png") if recursive else path.glob("*.png")
            found.extend(
                candidate.resolve()
                for candidate in iterator
                if not candidate.stem.endswith(OUTPUT_SUFFIX)
            )
            continue

        raise FileNotFoundError(f"Input does not exist: {path}")

    # Preserve input order while preventing accidental duplicate conversion.
    return list(dict.fromkeys(found))


def destination_for(source: Path, output_dir: Path | None) -> Path:
    filename = f"{source.stem}{OUTPUT_SUFFIX}.png"
    return (output_dir / filename) if output_dir else source.with_name(filename)


def tint_safe_quantization(
        counts: Counter[int],
        requested_count: int | None,
) -> dict[int, int]:
    """Map carrier values to an exact, tint-safe number of gray levels.

    Every reduced value is mapped upward to the maximum member of its cluster.
    Therefore the output carrier is never darker than max(R, G, B), and no RGB
    headroom is lost. For two or more levels, the darkest value is kept as its
    own endpoint; the brightest endpoint is always retained automatically.
    """
    values = sorted(counts)

    if not values:
        if requested_count is not None:
            raise ValueError("cannot quantize an image with no visible pixels")
        return {}

    if requested_count is None or requested_count == len(values):
        return {value: value for value in values}

    if requested_count > len(values):
        raise ValueError(
            f"requested {requested_count} gray values, but the image only has "
            f"{len(values)} visible carrier values"
        )

    if requested_count == 1:
        brightest = values[-1]
        return {value: brightest for value in values}

    # Keep the darkest endpoint exactly, then optimally split the remaining
    # values into contiguous clusters. Cluster cost is weighted by pixel count.
    darkest = values[0]
    remaining = values[1:]
    cluster_count = requested_count - 1
    item_count = len(remaining)

    prefix_weight = [0]
    prefix_weighted_value = [0]
    prefix_weighted_square = [0]

    for value in remaining:
        weight = counts[value]
        prefix_weight.append(prefix_weight[-1] + weight)
        prefix_weighted_value.append(
            prefix_weighted_value[-1] + (weight * value)
        )
        prefix_weighted_square.append(
            prefix_weighted_square[-1] + (weight * value * value)
        )

    def cluster_cost(start: int, end: int) -> int:
        """Squared error for [start, end), represented by its maximum."""
        representative = remaining[end - 1]
        weight = prefix_weight[end] - prefix_weight[start]
        weighted_value = (
            prefix_weighted_value[end] - prefix_weighted_value[start]
        )
        weighted_square = (
            prefix_weighted_square[end] - prefix_weighted_square[start]
        )
        return (
            (representative * representative * weight)
            - (2 * representative * weighted_value)
            + weighted_square
        )

    infinity = float("inf")
    costs = [
        [infinity] * (item_count + 1)
        for _ in range(cluster_count + 1)
    ]
    previous = [
        [-1] * (item_count + 1)
        for _ in range(cluster_count + 1)
    ]
    costs[0][0] = 0

    for clusters in range(1, cluster_count + 1):
        for end in range(clusters, item_count + 1):
            for start in range(clusters - 1, end):
                candidate = costs[clusters - 1][start]

                if candidate == infinity:
                    continue

                candidate += cluster_cost(start, end)

                if candidate < costs[clusters][end]:
                    costs[clusters][end] = candidate
                    previous[clusters][end] = start

    boundaries: list[tuple[int, int]] = []
    end = item_count

    for clusters in range(cluster_count, 0, -1):
        start = previous[clusters][end]

        if start < 0:
            raise RuntimeError("failed to construct grayscale quantization")

        boundaries.append((start, end))
        end = start

    mapping = {darkest: darkest}

    for start, end in reversed(boundaries):
        representative = remaining[end - 1]

        for value in remaining[start:end]:
            mapping[value] = representative

    return mapping


def convert(
        source: Path,
        destination: Path,
        requested_count: int | None,
) -> tuple[int, list[int]]:
    with Image.open(source) as opened:
        rgba = opened.convert("RGBA")

    pixels = list(pixel_data(rgba))
    carrier_counts = Counter(
        max(red, green, blue)
        for red, green, blue, alpha in pixels
        if alpha > 0
    )
    mapping = tint_safe_quantization(carrier_counts, requested_count)

    converted: list[tuple[int, int, int, int]] = []
    used_grays: set[int] = set()

    for red, green, blue, alpha in pixels:
        if alpha == 0:
            converted.append((0, 0, 0, 0))
            continue

        carrier = mapping[max(red, green, blue)]
        converted.append((carrier, carrier, carrier, alpha))
        used_grays.add(carrier)

    output = Image.new("RGBA", rgba.size)
    output.putdata(converted)

    destination.parent.mkdir(parents=True, exist_ok=True)
    output.save(destination, format="PNG", optimize=True)

    return len(carrier_counts), sorted(used_grays, reverse=True)


def positive_integer(value: str) -> int:
    parsed = int(value)

    if parsed < 1:
        raise argparse.ArgumentTypeError("must be at least 1")

    return parsed


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Convert PNGs into tint-safe grayscale carrier textures using "
            "gray = max(R, G, B), while preserving every alpha value."
        )
    )
    parser.add_argument(
        "inputs",
        nargs="+",
        type=Path,
        help="One or more PNG files or directories",
    )
    parser.add_argument(
        "-r",
        "--recursive",
        action="store_true",
        help="Search supplied directories recursively",
    )
    parser.add_argument(
        "-n",
        "--gray-count",
        type=positive_integer,
        help=(
            "Reduce each image to exactly this many visible gray values; "
            "cannot exceed the image's original carrier count"
        ),
    )
    parser.add_argument(
        "-o",
        "--output-dir",
        type=Path,
        help="Write every result into this directory instead of beside its source",
    )
    parser.add_argument(
        "-f",
        "--overwrite",
        action="store_true",
        help="Replace an existing _tint_grayscale.png result",
    )
    return parser


def main() -> int:
    args = build_parser().parse_args()
    output_dir = args.output_dir.expanduser().resolve() if args.output_dir else None

    try:
        sources = find_pngs(args.inputs, args.recursive)
    except (FileNotFoundError, ValueError) as error:
        print(f"ERROR: {error}")
        return 2

    if not sources:
        print("No PNG files found.")
        return 1

    failures = 0

    for source in sources:
        destination = destination_for(source, output_dir)

        if destination.exists() and not args.overwrite:
            print(f"SKIPPED: {destination} already exists (use --overwrite)")
            continue

        try:
            original_count, gray_values = convert(
                source,
                destination,
                args.gray_count,
            )
        except (OSError, UnidentifiedImageError, ValueError) as error:
            failures += 1
            print(f"FAILED: {source}: {error}")
            continue

        print(f"CREATED: {destination}")
        if len(gray_values) != original_count:
            print(f"REDUCED: {original_count} -> {len(gray_values)} gray values")
        print(f"GRAY VALUES ({len(gray_values)}): {', '.join(map(str, gray_values))}")

        if 0 in gray_values:
            print("NOTE: visible gray 0 remains permanently black under multiplication.")

    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
