from __future__ import annotations

import sys
from pathlib import Path
from typing import List, Set

from PIL import Image

INPUT_FILES = [
    "pearl_portal.png",
]

PER_LINE = 8


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
    base_dir = Path(__file__).resolve().parent

    texture_paths = [base_dir / name for name in INPUT_FILES]

    for path in texture_paths:
        if not path.exists():
            print(f"Missing input image: {path}", file=sys.stderr)
            return 1

        if path.suffix.lower() != ".png":
            print(f"Input file is not a PNG: {path}", file=sys.stderr)
            return 1

    all_values: Set[int] = set()

    for texture_path in texture_paths:
        values = collect_grayscale_values(texture_path)
        all_values.update(values)

    sorted_values = sorted(all_values, reverse=True)
    formatted = format_values(sorted_values, PER_LINE)

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