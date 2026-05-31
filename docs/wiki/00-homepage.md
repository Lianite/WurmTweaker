# WurmTweaker

WurmTweaker is a server-side mod for **Wurm Unlimited** built on [Ago's WurmServerModLauncher](https://github.com/ago1024/WurmServerModLauncher). It lets server administrators customize skills, creatures, and items by dropping JSON files into a folder and restarting the server — no SQL, no code, no recompilation required.

---

## What It Does

| Content Type | What you can change |
|---|---|
| **Skills** | Difficulty, experience rates, tick timing, priest restrictions, and more |
| **Creatures** | Stats, combat behavior, drops, spawn groups, and more |
| **Items** | Weights, dimensions, materials, values, combat stats, decay, containers, and more — including entirely new items |

The philosophy is simple: **content is data, data lives in JSON, code only loads and applies it.** Drop a file in to apply a change. Remove the file to restore the vanilla value.

---

## How It Works

At server startup, WurmTweaker scans the `data/` folder inside the mod directory and loads every `.json` file it finds. Each file describes one or more changes to skills, creatures, or items. The mod applies them using the modloader's official APIs.

Parse errors and unknown fields are logged clearly and skipped — a bad JSON file never crashes the server.

There is also an option in the wurmtweaker.properties file to specify your own directory to load JSON data from.

---

## Installation

1. Download the latest `wurmtweaker-dist.zip` from the releases page and extract it into your server's `mods/` directory
2. Start the server — WurmTweaker logs which files it loaded

The distribution includes a `data/` folder with reference documentation and `.json.example` files showing every vanilla item and creature in WurmTweaker's JSON format.

---

## Quick Example

```json
{
  "json-type": "item",
  "templateId": 21,
  "difficulty": 35.0,
  "weight": 2800
}
```

Drop this in `data/items/` and restart. The longsword's crafting difficulty and weight change. Every other field stays vanilla. Only the fields you include are touched.

---

## Documentation

- **[[Skills]]** — JSON field reference for skill customization
- **[[Creatures]]** — JSON field reference for creature customization
- **[[Items]]** — JSON field reference for item customization, including creating new items
- **[[Item Types]]** — Full `itemTypes` flag table
- **[[Item Materials]]** — Material ID table
- **[[Skill IDs]]** — Vanilla skill ID table
- **[[Spawn Tiles]]** — Vanilla Spawning Locations
- **[[Creature Types]]** — Vanilla Creature Type table
