from __future__ import annotations

import argparse
import glob
import sys
from pathlib import Path
from typing import Iterable, List, Set

from PIL import Image


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Extract unique grayscale layer values from one or more PNG textures, "
            "format them as Java-style comma-separated rows, and copy the result to the clipboard."
        )
    )
    parser.add_argument(
        "--texture",
        nargs="+",
        required=True,
        help=(
            "One or more texture paths or glob patterns. "
            'Examples: --texture pearl_fire_0.png or --texture "texture_1_*.png"'
        ),
    )
    parser.add_argument(
        "--per-line",
        type=int,
        default=8,
        help="How many values to put on each output line. Default: 8",
    )
    return parser.parse_args()


def resolve_patterns(patterns: Iterable[str]) -> List[Path]:
    matched: List[Path] = []
    seen: Set[Path] = set()

    for pattern in patterns:
        globbed = glob.glob(pattern, recursive=True)

        if not globbed:
            direct = Path(pattern)
            if direct.exists():
                globbed = [str(direct)]

        for item in globbed:
            path = Path(item).resolve()
            if path.is_file() and path.suffix.lower() == ".png" and path not in seen:
                seen.add(path)
                matched.append(path)

    matched.sort()
    return matched


def collect_grayscale_values(image_path: Path) -> Set[int]:
    img = Image.open(image_path).convert("RGBA")
    px = img.load()

    values: Set[int] = set()

    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]

            if a == 0:
                continue

            if not (r == g == b):
                raise ValueError(
                    f"Found non-grayscale pixel in {image_path.name} at ({x}, {y}): {(r, g, b, a)}"
                )

            values.add(r)

    return values


def format_values(values: List[int], per_line: int) -> str:
    if not values:
        return ""

    lines: List[str] = []

    for start in range(0, len(values), per_line):
        chunk = values[start:start + per_line]
        line = ", ".join(str(v) for v in chunk)
        if start + per_line < len(values):
            line += ","
        lines.append(line)

    return "\n".join(lines)


def copy_to_clipboard(text: str) -> None:
    try:
        import pyperclip  # type: ignore

        pyperclip.copy(text)
        return
    except Exception:
        pass

    try:
        import tkinter as tk

        root = tk.Tk()
        root.withdraw()
        root.clipboard_clear()
        root.clipboard_append(text)
        root.update()
        root.destroy()
        return
    except Exception as exc:
        raise RuntimeError(
            "Could not copy to clipboard automatically. "
            "Install pyperclip with: pip install pyperclip"
        ) from exc


def main() -> int:
    args = parse_args()
    texture_paths = resolve_patterns(args.texture)

    if not texture_paths:
        print("No matching PNG files found.", file=sys.stderr)
        return 1

    all_values: Set[int] = set()

    for texture_path in texture_paths:
        values = collect_grayscale_values(texture_path)
        all_values.update(values)

    sorted_values = sorted(all_values, reverse=True)
    formatted = format_values(sorted_values, args.per_line)

    print(formatted)
    print()

    try:
        copy_to_clipboard(formatted)
        print("Copied grayscale values to clipboard.")
    except RuntimeError as exc:
        print(str(exc), file=sys.stderr)
        print("The values were still printed above, so you can copy them manually.", file=sys.stderr)

    print(f"Matched {len(texture_paths)} file(s):")
    for path in texture_paths:
        print(f" - {path}")

    print(f"Found {len(sorted_values)} unique grayscale value(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())