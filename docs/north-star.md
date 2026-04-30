# North Star — Design Philosophy

## The CDDA Model

Cataclysm: Dark Days Ahead defines almost all game content — items, monsters, recipes, mutations, professions — in JSON files. Non-programmers can mod the game by editing those files. The game engine reads them at startup and builds its internal state from them.

WurmTweaker applies that same philosophy to Wurm Unlimited server administration:

> **Content is data. Data lives in JSON. Code only loads and applies data.**

## What This Means in Practice

### JSON is the interface
Server admins never touch Java. They drop a `.json` file in the right folder and restart. That's it.

### One JSON file = one unit of content
Each file should be focused and self-describing. A `longsword.json` file describes the longsword. A `mining.json` file describes the mining skill. No mega-files with hundreds of entries.

### The engine never hard-codes content
If a skill property, creature stat, or item attribute can be expressed in JSON, it should be. The Java code is a generic loader — it should have no opinions about what a "good" sword weight is.

### Validation over silent failure
If a JSON file has an unknown field or a bad value, log an error that tells the admin exactly what is wrong and in which file. Never silently ignore bad input.

### Additive by default
Drop a file in, the change applies. Remove the file, the default is restored. No database migrations, no cleanup scripts.

## Example Vision

A server admin wants to make the Blacksmithing skill harder to train. They create:

`mods/wurmtweaker/data/skills/blacksmithing.json`
```json
{
  "type": "skill",
  "id": "blacksmithing",
  "difficulty": 1.5
}
```

Restart the server. Done. No SQL, no code, no reflection.

## Guiding Questions

When making any design decision, ask:
1. Could a non-programmer admin understand this JSON file without reading any documentation?
2. If the admin deletes this file, does the game return to vanilla behavior?
3. Is this change expressed as data, or did we sneak logic into the JSON?
