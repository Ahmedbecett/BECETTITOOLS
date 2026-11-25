#!/usr/bin/env python3
"""Verify that every user-visible Android string exists and is really translated for ar and fr.

Fails when:
  * a string in res/values/strings.xml is missing from values-ar or values-fr
  * an Arabic resource contains no Arabic characters
  * a French resource contains Arabic characters
  * the launcher label (app_name) is not localized
  * the tool counter in the UI disagrees with the real size of the tool catalog
"""
from __future__ import annotations

import re
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "app/src/main/res"


def read_strings(path: Path) -> dict[str, str]:
    text = path.read_text(encoding="utf-8")
    return dict(re.findall(r'<string name="([^"]+)">(.*?)</string>', text, re.S))


def unescape(value: str) -> str:
    return value.replace("\\'", "'").replace('\\"', '"')


def script_counts(text: str) -> tuple[int, int]:
    arabic = latin = 0
    for char in text:
        if not char.isalpha():
            continue
        name = unicodedata.name(char, "")
        if name.startswith("ARABIC"):
            arabic += 1
        elif name.startswith("LATIN"):
            latin += 1
    return arabic, latin


def catalog_size() -> int:
    source = (ROOT / "app/src/main/java/com/becetti/tools/domain/models/ToolModels.kt").read_text(encoding="utf-8")
    catalog = source.split("object ToolCatalog")[1]
    tools = len(re.findall(r"ToolItem\(", catalog))
    routes = (ROOT / "app/src/main/java/com/becetti/tools/core/navigation/Routes.kt").read_text(encoding="utf-8")
    extra_screens = len(re.findall(r"object WifiTransfer : Route", routes))
    return tools + extra_screens


def main() -> int:
    en = read_strings(RES / "values/strings.xml")
    ar = read_strings(RES / "values-ar/strings.xml")
    fr = read_strings(RES / "values-fr/strings.xml")

    errors: list[str] = []
    for name in en:
        if name not in ar:
            errors.append(f"values-ar is missing string '{name}'")
        if name not in fr:
            errors.append(f"values-fr is missing string '{name}'")

    for lang, table in (("ar", ar), ("fr", fr)):
        for name, raw in table.items():
            value = unescape(raw)
            arabic, latin = script_counts(value)
            if lang == "ar" and arabic == 0:
                errors.append(f"values-ar/{name} contains no Arabic text: {value!r}")
            if lang == "fr" and arabic > 0:
                errors.append(f"values-fr/{name} contains Arabic text: {value!r}")
            if lang == "fr" and latin == 0:
                errors.append(f"values-fr/{name} contains no Latin text: {value!r}")

    for lang in ("ar", "fr"):
        if "app_name" not in (ar if lang == "ar" else fr):
            errors.append(f"{lang}: launcher label app_name is not localized")

    # Tool counts shown in the UI must match the catalog that is actually shipped.
    tools = catalog_size()
    strings_kt = (ROOT / "app/src/main/java/com/becetti/tools/core/localization/AppStrings.kt").read_text(encoding="utf-8")
    for label, pattern in (("searchHint", r'searchHint = "([^"]*)"'),
                           ("toolsCountLabel", r'toolsCountLabel = "([^"]*)"')):
        for value in re.findall(pattern, strings_kt):
            numbers = re.findall(r"\d+", value)
            if numbers and str(tools) not in numbers:
                errors.append(f"AppStrings.{label} says {numbers} but the catalog ships {tools} tools: {value!r}")

    print(f"English strings: {len(en)} · Arabic: {len(ar)} · French: {len(fr)} · tools in catalog: {tools}")
    print(f"Arabic launcher label: {ar.get('app_name', '<missing>')}")
    print(f"French launcher label: {fr.get('app_name', '<missing>')}")

    if errors:
        print("\nLOCALIZATION CHECK FAILED:")
        for error in errors:
            print(f"  - {error}")
        return 1
    print("\nPASS: every string is present and genuinely translated in Arabic and French.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
