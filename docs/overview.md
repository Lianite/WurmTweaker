# WurmTweaker — Project Overview

WurmTweaker is a server-side mod for **Wurm Unlimited** built on Ago's modloader (WurmServerModLauncher). Its purpose is to let server administrators customize game content — skills, creatures, and items — without writing or compiling any code. Customizations are defined in **JSON files** that the mod reads at startup.

## Problem It Solves

Wurm Unlimited exposes very little in-game tooling for server customization. The options are:
- Edit source code and recompile (not possible without access to closed source)
- Use the modloader + reflection hacks (fragile, no structure)
- Use SQL tables as a config layer (the old WurmTweaker approach — clunky, no schema, no validation)

WurmTweaker replaces all of that with a clean JSON-based content layer, similar to how CDDA (Cataclysm: Dark Days Ahead) defines all its game content in JSON files that non-programmers can read and edit.

## What It Does

| Phase | Feature | Status |
|---|---|---|
| 1 | Skill customization | Planned |
| 2 | Creature customization | Planned |
| 3 | Item customization | Planned |

## How It Works

1. The mod loads via Ago's modloader when the Wurm Unlimited server starts
2. It scans the `mods/wurmtweaker/data/` directory for JSON files organized by type
3. Each JSON file defines one or more content overrides (e.g., a skill with modified difficulty)
4. The mod applies those overrides using the modloader's official APIs where available, and reflection only as a last resort

## Key Reference Documents

| Document | When to read it |
|---|---|
| `docs/architecture.md` | Module structure, hook lifecycle, JSON conventions |
| `docs/guardrails.md` | What NOT to do — API research rules |
| `docs/north-star.md` | Design philosophy |
| `docs/creature-template-decisions.md` | Schema decisions for creature JSON — read before changing the `combat` block or attack structure |

## Non-Goals

- This is NOT a client-side mod
- This does NOT add new game mechanics — it only customizes existing ones
- This does NOT modify core game files
