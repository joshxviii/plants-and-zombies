#!/usr/bin/env python3
"""
compare translation files against en_us.json.

example use
python .github/scripts/check_lang.py
 or
python .github/scripts/check_lang.py es_es.json
"""

import json
import sys
from pathlib import Path

LANG_DIR = Path("src/main/resources/assets/plantz/lang")
BASE_FILE = LANG_DIR / "en_us.json"

def load_json(path: Path) -> dict:
    try:
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        if not isinstance(data, dict):
            print(f"❌ {path.name}: root must be a JSON object")
            sys.exit(1)
        return data
    except json.JSONDecodeError as e:
        print(f"❌ {path.name}: invalid JSON – {e}")
        sys.exit(1)
    except Exception as e:
        print(f"❌ {path.name}: {e}")
        sys.exit(1)

def main() -> None:
    if not BASE_FILE.exists():
        print(f"❌ Base file not found: {BASE_FILE}")
        sys.exit(1)

    base = load_json(BASE_FILE)
    base_keys = set(base.keys())
    print(f"✅ Loaded {BASE_FILE.name} ({len(base_keys)} keys)\n")

    if len(sys.argv) > 1: # get files to check from cl
        files_to_check = [Path(f"src/main/resources/assets/plantz/lang/{f}") for f in sys.argv[1:] if Path(f"src/main/resources/assets/plantz/lang/{f}").name != "en_us.json"]
    else: # if no arguments are present, check everything
        files_to_check = sorted(p for p in LANG_DIR.glob("*.json") if p.name != "en_us.json")

    if not files_to_check:
        print("No translation files to check.")
        return

    has_errors = False

    for path in files_to_check:
        if not path.exists():
            print(f"❌ {path}: file does not exist")
            has_errors = True
            continue

        keys = set(load_json(path).keys())

        missing = sorted(base_keys - keys)
        extra = sorted(keys - base_keys)

        if not missing and not extra:
            print(f"✅ {path.name}: complete ({len(keys)} keys)")
            continue

        has_errors = True
        print(f"❌ {path.name}:")

        if missing:
            print(f"   Missing keys ({len(missing)}):")
            for key in missing: print(f"      \"{key}\"")
        if extra:
            print(f"   Unused keys ({len(extra)}):")
            for key in extra: print(f"      \"{key}\"")
        print()

    if has_errors:
        sys.exit(1)

    print("All language checks passed! :)")

if __name__ == "__main__":
    main()