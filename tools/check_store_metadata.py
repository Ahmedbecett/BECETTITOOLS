#!/usr/bin/env python3
"""Validate the Arabic/French store listing against Softonic field limits and language rules.

Checks per listing:
  * title <= 50 characters, short description <= 80, full description <= 4000
  * Arabic listing is Arabic script, French listing is Latin script with French accents
  * no English boilerplate left in either listing
  * tool count mentioned in the text matches the real tool catalog size
  * no placeholder/demo wording

Exit code 0 when every check passes.
"""
from __future__ import annotations

import json
import re
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
METADATA = ROOT / "docs/store/softonic-v1.1.0-metadata.json"
CATALOG = ROOT / "app/src/main/java/com/becetti/tools/domain/models/ToolModels.kt"

# English words that must never appear in an Arabic or French listing.
ENGLISH_MARKERS = [
    "the ", " and ", " with ", " your ", " you ", "tools for", "smart tools",
    "everything you need", "download now", "free app",
]

PLACEHOLDER_MARKERS = [
    "lorem ipsum", "tbd", "todo", "placeholder", "sample text", "xxx", "coming soon",
]


def real_tool_count() -> int:
    """Count tools in ToolCatalog.all plus the extra Wi-Fi transfer screen."""
    source = CATALOG.read_text(encoding="utf-8")
    catalog = source.split("object ToolCatalog")[1]
    items = len(re.findall(r"ToolItem\(", catalog))
    routes = (ROOT / "app/src/main/java/com/becetti/tools/core/navigation/Routes.kt").read_text(encoding="utf-8")
    extra = len(re.findall(r"object WifiTransfer : Route", routes))
    return items + extra


def script_of(text: str) -> dict[str, int]:
    counts: dict[str, int] = {}
    for char in text:
        if not char.isalpha():
            continue
        name = unicodedata.name(char, "")
        script = name.split(" ")[0]
        counts[script] = counts.get(script, 0) + 1
    return counts


def main() -> int:
    data = json.loads(METADATA.read_text(encoding="utf-8"))
    limits = data["limits"]
    problems: list[str] = []
    notes: list[str] = []

    tools = real_tool_count()
    notes.append(f"tool catalog size detected in source: {tools}")

    for lang in data["supported_languages"]:
        listing = data["listings"][lang]
        print(f"\n--- {lang} ({listing['locale_name']}) ---")

        for field, limit in (("title", limits["title"]),
                             ("short_description", limits["short_description"]),
                             ("description", limits["description"])):
            value = listing[field]
            state = "PASS" if len(value) <= limit else "FAIL"
            print(f"  {field}: {len(value)}/{limit} chars  {state}")
            if len(value) > limit:
                problems.append(f"{lang}.{field} is {len(value)} chars (limit {limit})")
        print(f"  title: {listing['title']}")

        combined = " ".join([listing["title"], listing["short_description"], listing["description"]])
        scripts = script_of(combined)
        arabic = scripts.get("ARABIC", 0)
        latin = scripts.get("LATIN", 0)
        print(f"  letters by script: arabic={arabic}, latin={latin}")
        if lang == "ar":
            if arabic < latin:
                problems.append("ar listing is not mainly Arabic script")
        else:
            if arabic > 0:
                problems.append("fr listing contains Arabic script")
            if not re.search(r"[àâçéèêëîïôûùüÿœæ]", combined, re.I):
                problems.append("fr listing has no French accented characters")
        if lang == "ar" and arabic <= latin:
            problems.append("ar listing has more Latin than Arabic letters")

        lowered = combined.lower()
        for marker in ENGLISH_MARKERS:
            if marker in lowered:
                problems.append(f"{lang}: English wording detected: {marker!r}")
        for marker in PLACEHOLDER_MARKERS:
            if marker in lowered:
                problems.append(f"{lang}: placeholder wording detected: {marker!r}")

        if str(tools) not in combined:
            problems.append(f"{lang}: listing does not mention the real tool count {tools}")

        print(f"  language/placeholder/tool-count checks: "
              f"{'PASS' if not any(p.startswith(lang) for p in problems) else 'FAIL'}")

    print("\n=== RESULT ===")
    for note in notes:
        print(f"  note: {note}")
    if problems:
        for problem in problems:
            print(f"  FAIL: {problem}")
        return 1
    print("  PASS: Arabic and French listings match declared languages and field limits")
    return 0


if __name__ == "__main__":
    sys.exit(main())
