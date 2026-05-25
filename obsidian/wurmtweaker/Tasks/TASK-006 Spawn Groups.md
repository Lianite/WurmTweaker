---
id: TASK-006
title: Full Spawn Control System
status: Backlog
phase: 4
tags:
  - task
  - backlog
related:
  - "[[TASK-004c Creature Spawns]]"
  - "[[Spawn Tiles]]"
---

# TASK-006: Full Spawn Control System

## Goal

CDDA-style spawn authoring: a dedicated `json-type: "spawn-group"` completely decoupled from creature definitions. Also adds vanilla spawn modification (remove, reweight) which TASK-004c intentionally defers.

## Scope Notes

Sea creature spawning (Blue Whale, Dolphin, Octopus, Huge Shark, Sea Serpent) is **in scope** but requires a separate Javassist hook on `Zone.java` — it is not reachable via `SpawnTable` or `EncounterBuilder`. See the Sea Creature section under Key Technical Findings.

## Design Reference

[Cataclysm: Dark Days Ahead monstergroup JSON](https://github.com/CleverRaven/Cataclysm-DDA) — creature definitions carry no spawn data; spawn groups are a separate concern entirely.

## JSON Shape

```json
{
  "json-type": "spawn-group",
  "id": "deep_forest_predators",
  "tile": "tree",
  "elevation": "ground",
  "entries": [
    { "creature": 11,  "count": 1, "chance": 5 },
    { "creature": 200, "count": 2, "chance": 3 }
  ],
  "remove": [10],
  "reweight": [
    { "creature": 14, "chance": 1 }
  ]
}
```

## Key Technical Findings

### Available hooks (no new bytecode work needed for additions)

- **`EncounterBuilder`** — modlauncher API, handles adding to existing or new `EncounterType`. Already used in TASK-004c. Located at `refs/WurmServerModLauncher-develop/.../modsupport/creatures/EncounterBuilder.java`.
- **`SpawnTable.getType(tiletype, elevation)`** — public static, returns the `EncounterType` for a slot. Use this to locate the target pool for `remove`/`reweight`. Located at `refs/SpawnTable.java` line 54.
- **`ModCreatures.init()` hook** — already registered on `SpawnTable.createEncounters()`. A second hook on the same method chains cleanly. Located at `refs/WurmServerModLauncher-develop/.../modsupport/creatures/ModCreatures.java` lines 155–174.

### Sea Creature Spawning — Separate Zone-based System

Discovered 2026-05-25 via `refs/Zone.java` investigation. Sea creatures completely bypass `SpawnTable` and are driven by hardcoded logic in `Zone.getRandomSeaCreatureId()` and `Zone.spawnSeaCreature()`. No existing mod hook covers this path.

**Hardcoded sea creature IDs:**

| ID | Constant | How selected |
|---|---|---|
| 70 | `SEA_SERPENT_CID` | Rare override (< 4 exist + 1/86400 chance per zone tick) |
| 71 | `SHARK_HUGE_CID` | "Sea hunter" path — unconditional when `seaHunters < 500` |
| 97 | `BLUE_WHALE_CID` | Main pool via `getOpenSpawnSlotsForCreatureType()` |
| 99 | `DOLPHIN_CID` | Main pool via `getOpenSpawnSlotsForCreatureType()` |
| 100 | `OCTOPUS_CID` | Main pool via `getOpenSpawnSlotsForCreatureType()` |

**Spawn condition:** Surface tile with height < −200 (deep water). `Creature.doNew()` called directly — no `EncounterType` or `Encounter` objects involved.

**Hook needed:** Javassist `insertAfter` on `Zone.getRandomSeaCreatureId()` — if it returns 0 (all vanilla slots full), call a static helper that picks from registered custom sea creature IDs using `maxPercentOfCreatures` as the population cap.

**Proposed JSON shape** (elevation `"deep-water"`, no `tile`):
```json
{ "json-type": "spawn-group", "elevation": "deep-water",
  "entries": [{ "creature": 200, "count": 1, "chance": 3 }] }
```
`SpawnGroupHandler` routes these to the Zone hook instead of `EncounterBuilder`. Needs validation against the population cap system before implementation.

Full details in `docs/tasks/TASK-006-spawn-groups.md`.

### The hard part: remove and reweight require reflection

`EncounterType` (`refs/EncounterType.java` lines 26–27) has no remove API:

```java
private final LinkedList<Integer> chances;    // cumulative weights
private final LinkedList<Encounter> encounters;
private int sumchance;
```

`addEncounter()` (line 37) only appends. To remove or reweight:
1. Get `EncounterType` via `SpawnTable.getType()`
2. Access fields via `Field.setAccessible(true)`
3. Find entries where `Encounter.getTypes()` (`refs/Encounter.java` line 23) contains the target template ID
4. Mutate both lists in tandem; rebuild `sumchance` (it's a running cumulative total — take the last `chances` value after rebuilding, or recalculate from scratch)

### Interop constraint

`remove`/`reweight` targeting vanilla creature IDs (≤ 119) is safe — vanilla entries are always present before any mod hook. Targeting entries added by other mods is unsupported (no guaranteed ordering). Document this clearly.

## Transition from TASK-004c

1. The `spawns` array in creature JSON becomes **deprecated** — log a warning, keep it working.
2. Both surfaces call `EncounterBuilder` at runtime — no data migration needed.
3. Final cleanup: remove `spawns` field and `SpawnEntry` POJO once deprecation period ends.

## Files to Create/Modify

| File | Change |
|---|---|
| `src/.../creatures/SpawnGroupDefinition.java` | New POJO |
| `src/.../creatures/SpawnGroupHandler.java` | New handler + hook |
| `src/.../creatures/CreatureDefinition.java` | `@Deprecated` on `spawns` field |
| `src/.../creatures/CreatureHandler.java` | Deprecation warning when `spawns` present |
| `src/.../WurmTweaker.java` | Register `SpawnGroupHandler` |

## Full Refactor Scope

The spawn system has two distinct layers: **spawn data** (what spawns where, currently baked into compiled constants) and **spawn engine** (the decision-making code that reads that data). JSON consolidation targets the data layer first; the engine must then be refactored to read from JSON instead of hardcoded values.

### Layer 1: Spawn Data — Primary JSON Targets

These are hardcoded tables that become JSON. Pure data currently dressed as code — the straightforward wins.

| Current location | Lines | What it defines |
|---|---|---|
| `SpawnTable.createEncounters()` | `SpawnTable.java:94–287` | Tile + elevation → encounter lists with weights |
| `Zone.getRandomSeaCreatureId()` | `Zone.java:492–518` | Sea creature pool (97/99/100), sea serpent probability (1/86400), population caps |
| Sea hunter priority logic | `Zone.java:413–422` | Shark hardcoded as priority spawn when pool < 500 |
| Per-template spawn limits | `CreatureTemplate.java:196–198` | `maxPercentOfCreatures`, `maxPopulationOfCreatures` per species |

### Layer 2: Spawn Engine — Code That Needs Refactoring

This logic stays in Java but must be rewritten to read from the JSON tables rather than hardcoding decisions.

- **`Zone.spawnCreature()` (`Zone.java:650–878`)** — the kingdom/den/creatureSpawn/SpawnTable decision tree is a long chain of if/else. Needs to become a single table lookup.
- **`Zone.maySpawnCreatureTemplate()` (`Zone.java:880–899`)** — gating logic (agg %, typed count, population caps) reads from template fields. Once template data moves to JSON, this reads from the loaded definitions instead.
- **`Creatures.java` counters** — `seaMonsters` and `seaHunters` are hardcoded category names. A generalized system replaces these with a `Map<String, Integer>` keyed by spawn category defined in JSON. This is the most invasive change: these counters are touched in `addCreature()` and `removeCreature()` on every creature lifecycle event.

### Layer 3: External Spawners — Out of Scope

Approximately 12 callsites call `Creature.doNew()` directly outside the Zone poll cycle: guard towers, spells, rift events, egg hatching, village events, tutorials, etc. These are triggered events, not world population management — they stay as direct `doNew()` calls and are not part of this refactor.

The **Dens system** (`Dens.java`) is semi-independent — it already persists to its own database table and has its own lifecycle. Treat as a separate task if needed.

### Implementation Phases

Ordered by impact vs. risk. Steps 1–2 can ship independently and deliver the majority of the benefit.

1. **Define the JSON schema** — tile type → encounter list, sea creature pool, per-template caps, spawn category names
2. **Replace `SpawnTable.java`** with a JSON loader that exposes the same `getRandomEncounter()` interface — all existing callers stay unchanged. (~70% of benefit, low risk: `SpawnTable` is already well-isolated)
3. **Replace `Zone.getRandomSeaCreatureId()` and the sea hunter priority logic** with a table lookup using the same schema
4. **Migrate per-template caps** out of `CreatureTemplateCreator.java` into JSON
5. **Replace `seaMonsters`/`seaHunters` counters** with generic category counters driven by spawn category definitions — highest-risk step in the entire refactor

## Creature Den Bug Investigation (In-World Item Dens)

The in-world den item (template ID 521, `Zone.creatureSpawn`) is separate from the `Dens.java` database system. It is a placeable item that tells a zone to use a specific creature template when its random spawn tick fires. The system is fundamentally broken in settled areas due to how spawn location is chosen — this is a bug to fix in the full revamp, not a configuration issue.

### The Core Problem

When `Zone.poll()` decides to spawn from the den item, it picks a **random tile anywhere in the zone** as the spawn point — not the tile the den item is sitting on. In a settled area where most tiles have been visited (and are therefore tracked as `VolaTile` entries), the random picker almost always lands on a blocked tile and the spawn silently aborts. The den item's own coordinates are irrelevant to where creatures appear; they could spawn anywhere in the zone. Only cave dens use the item's actual position.

### The Eight Failure Gates

Working through `Zone.spawnCreature()` in order:

**Gate 1 — spawnSeed throttle** (`Zone.java:130, 444`)

```java
this.spawnSeed = Zones.worldTileSizeX / 200;
if (Server.rand.nextInt(this.spawnSeed) == 0) { ... }
```

On a 2048 map `spawnSeed = 10`; on a 4096 map `spawnSeed = 20`. Only 1-in-10 (or 1-in-20) poll ticks attempt any spawn at all.

**Gate 2 — Kingdom spawn preemption** (`Zone.java:404–412, 725`)

Inside kingdom influence, `lSpawnKingdom` has a 1-in-50 chance (PvE) or 1-in-20 (PvP) of being true per eligible tick. When it fires, `spawnCreature()` takes the kingdom guard branch — the den item path is skipped entirely that tick.

**Gate 3 — Random tile hits an existing VolaTile** (`Zone.java:464–483`) — *most impactful in settled areas*

```java
final VolaTile t3 = this.getTileOrNull(tx, ty);
if (t3 != null) {
    if (lSpawnKingdom && ...) { this.spawnCreature(...); }
    // otherwise: nothing. Den is ignored.
} else {
    if (Villages.getVillage(tx, ty, ...) == null) {
        this.spawnCreature(...);
    }
}
```

A `VolaTile` exists for any tile ever visited by a player, or containing structures, fences, or creatures. In a settled zone, the majority of tiles are already tracked. When the random picker lands on one, the spawn silently aborts unless it happens to also be a kingdom tick.

**Gate 4 — Village deed check** (`Zone.java:471`)

Even if the random tile has no `VolaTile`, if it falls within a deeded village `Villages.getVillage()` returns non-null and no spawn occurs. Kingdom influence zones typically have nearby deeds covering significant tile area.

**Gate 5 — Player proximity check** (`Zone.java:450–463`)

If any player is within 10 tiles of the randomly chosen tile, the entire `poll()` method returns immediately — no spawn, no retry.

**Gate 6 — `numberOfTyped` global cap** (`Zone.java:428, 888`)

Den item spawns use `typed = true`. Two independent checks both gate on this:

```java
// Outer gate (Zone.java:428):
doSpawn = (this.creatures < 60 && getNumberOfTyped() < maxTypedCreatures);

// Inner gate in maySpawnCreatureTemplate (Zone.java:888):
if (typed && getNumberOfTyped() >= maxTypedCreatures) return false;
```

If the server has hit `maxTypedCreatures` globally, the outer gate disables spawning. Even if the zone's creature count re-enables `doSpawn`, `maySpawnCreatureTemplate` still blocks the actual call.

**Gate 7 — 1-in-10 SpawnTable override** (`Zone.java:750`)

```java
if (this.creatureSpawn != null && Server.rand.nextInt(10) != 0) {
    // use den  — 90% of eligible ticks
} else {
    // SpawnTable — 10% of eligible ticks, den ignored
}
```

10% of otherwise-eligible ticks bypass the den and roll from SpawnTable instead.

**Gate 8 — `maySpawnCreatureTemplate` per-species limits** (`Zone.java:884–899`)

Final check even if all prior gates pass:
- Global agg% exceeded — monsters are blocked if the server's aggressive creature ratio is above `percentAggCreatures`
- Per-species world cap — `maxPercentOfCreatures` (e.g. 1% for mountain lion); blocked if already at limit
- Nice creature overflow — blocked if `numberOfNice > maxCreatures / 2`

### Fix Direction for the Revamp

The correct behavior for a placed den item is to spawn the creature **near the item's own tile**, not at a random zone tile. The random tile selection is an artifact of the zone poll design that predates placed dens. Fixing this means the revamp's spawn engine should:

1. When `creatureSpawn != null`, pick the spawn coordinates from a small radius around `creatureSpawn.getTileX() / getTileY()` rather than a random zone tile
2. Remove the 1-in-10 SpawnTable override (Gate 7) — a placed den should always use its configured template
3. The remaining gates (proximity, caps, deed check) are legitimate and should be preserved