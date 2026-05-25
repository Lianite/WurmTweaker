# TASK-006: Full Spawn Control System

## Goal

Replace the convenience `spawns` shorthand (TASK-004c) with a proper CDDA-style spawn authoring system: a dedicated `json-type: "spawn-group"` that is fully decoupled from creature definitions. Also adds the missing capability from TASK-004c: removing and reweighting vanilla spawn entries.

## Status

BACKLOG

## Background

TASK-004c embeds spawn config inside creature definitions for convenience. This was an intentional short-term trade-off: it couples two concerns (what a creature is vs. where it appears) and cannot express cross-creature spawn groups. The full system described here is the intended end state.

CDDA (Cataclysm: Dark Days Ahead) is the design reference. Their `monstergroup` JSON type defines groups of monsters with weights, and those groups are referenced by map generators and regional overrides. Creature definitions contain no spawn information — the two concerns are completely separate.

## Scope

### In scope
- `json-type: "spawn-group"` — new JSON type processed by `SpawnGroupHandler`
- Mixing vanilla and custom creatures in the same spawn group
- Adding custom creatures to tiles that already have vanilla encounters
- Removing specific creatures from vanilla encounter pools
- Reweighting (adjusting chance) of existing vanilla encounters
- Deprecation of the `spawns` array inside creature JSON (with warning)

### Out of scope (defer further)
- Conditional spawning (time of day, season, kingdom, server type)
- Spawn density / total population caps
- Named spawn group references (one group referencing another)

### Sea creature spawning (in scope — requires separate hook)

Sea creatures (Blue Whale, Dolphin, Octopus, Huge Shark, Sea Serpent) are spawned by `Zone.java`, not `SpawnTable`. This is a completely separate code path that the current TASK-004c system cannot reach. Full sea creature control is explicitly in scope for this task and requires a Javassist hook on `Zone` methods. See the "Sea Creature Spawning" section under Key Technical Findings.

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

| Field | Type | Notes |
|---|---|---|
| `id` | String | Unique identifier for this group (not used at runtime, for human reference) |
| `tile` | String | Named tile type — same names as TASK-004c |
| `elevation` | String | Named elevation — same names as TASK-004c |
| `entries[]` | Array | Creatures to add. `creature` = template ID, `count` = group size, `chance` = relative weight |
| `remove[]` | int[] | Template IDs to remove from this tile/elevation pool entirely |
| `reweight[]` | Array | Adjust the chance weight of an existing entry. Reconstructs the pool with the new weight |

## Key Technical Findings

### What exists (leverage these)

- **`EncounterBuilder`** (`refs/WurmServerModLauncher-develop/.../modsupport/creatures/EncounterBuilder.java`)
  Existing modlauncher API. Handles adding to existing `EncounterType` or creating a new one. Uses reflection to call the private `SpawnTable.addTileType()`. **TASK-004c already uses this for additions.** Reuse for `entries[]`.

- **`ModCreatures.init()` hook on `SpawnTable.createEncounters()`** (`ModCreatures.java` lines 155–174)
  Already registered by our `WurmTweaker.init()`. Fires after vanilla encounters are built. `SpawnGroupHandler` can either plug into this same hook (via its own `ModCreature` registrations) or register its own separate hook on the same method. The separate hook approach keeps `SpawnGroupHandler` independent of creature registration.

- **`SpawnTable.getType(tiletype, elevation)`** (`SpawnTable.java` line 54, public static)
  Returns the `EncounterType` for a given tile/elevation combination, or null. Use this to locate the target pool for `remove[]` and `reweight[]` operations.

- **`SpawnTable.addTileType(EncounterType)`** (`SpawnTable.java` line 27, package-private static)
  `EncounterBuilder` already accesses this via reflection. The same reflection pattern applies here.

### What requires reflection (the hard part)

**`EncounterType` internal state** (`refs/EncounterType.java` lines 26–27):
```java
private final LinkedList<Integer> chances;
private final LinkedList<Encounter> encounters;
```

These are private final fields with no public remove or replace API. `addEncounter()` (line 37) only appends. To implement `remove[]` and `reweight[]`:

1. Get the `EncounterType` via `SpawnTable.getType()`
2. Access `chances` and `encounters` via `Field.setAccessible(true)` + reflection
3. For **remove**: iterate `encounters`, find entries where `Encounter.getTypes()` contains the target template ID, remove them from both lists, rebuild `sumchance`
4. For **reweight**: same find, then replace the corresponding entry in `chances` and rebuild `sumchance`
5. `sumchance` is also private (`EncounterType.java` line 27) — must be recalculated and written back

The `sumchance` field is `private int sumchance`. After any structural change, rebuild it: iterate the `chances` list and take the last value (it's a running cumulative sum, not a simple total — see `addEncounter()` at line 38).

### Sea Creature Spawning — Entirely Separate System

Sea creatures in Wurm are spawned by `Zone.java`, completely bypassing `SpawnTable`. This was discovered during TASK-004c research (Zone.java investigation, 2026-05-25).

#### How it works

`Zone.poll()` evaluates two flags per zone tick when the zone is on the surface with fewer than 20 creatures:
- `spawnSeaHunter` — true if `getNumberOfSeaHunters() < 500`
- `spawnSeaCreature` — true if not spawning a sea hunter and no kingdom creature is being spawned

When either flag is set (and no land spawn is happening), `Zone.spawnSeaCreature(boolean spawnSeaHunter)` is called. It picks a tile at random within the zone and only proceeds if the tile height is **< −200** (deep water). It then calls `Creature.doNew()` directly.

**The spawn selection is hardcoded in `Zone.getRandomSeaCreatureId()`:**

| Template ID | Constant | Selection method |
|---|---|---|
| 70 | `SEA_SERPENT_CID` | Rare override: only if `getNumberOfSeaMonsters() < 4` AND 1-in-86400 chance. Returns 0 (no spawn) otherwise. |
| 71 | `SHARK_HUGE_CID` | Always used when `spawnSeaHunter = true`. Not part of `getRandomSeaCreatureId()`. |
| 97 | `BLUE_WHALE_CID` | Main pool — selected if `getOpenSpawnSlotsForCreatureType(97) > 0` |
| 99 | `DOLPHIN_CID` | Main pool — selected if `getOpenSpawnSlotsForCreatureType(99) > 0` |
| 100 | `OCTOPUS_CID` | Main pool — selected if `getOpenSpawnSlotsForCreatureType(100) > 0` |

`getRandomSeaCreatureId()` builds a map of creature ID → open slots, removes IDs with 0 slots, and picks randomly among those remaining. Returns 0 (no spawn) if all slots are full.

#### No existing mod hook

`ModCreatures.init()` hooks only `CreatureTemplateCreator.createCreatureTemplates()` and `SpawnTable.createEncounters()`. Neither touches `Zone`. The `EncounterBuilder` / `SpawnTable` approach has no effect on sea creature spawns.

#### What is needed to hook this

A Javassist bytecode hook on `Zone.getRandomSeaCreatureId()` using `ExprEditor` or `insertAfter`. The clean approach:

```
insertAfter on Zone.getRandomSeaCreatureId()
  if ($_  == 0) {
      $_ = WurmTweakerSeaSpawns.getCustomSeaCreatureId();
  }
```

`getCustomSeaCreatureId()` would implement the same open-slot check using `maxPercentOfCreatures` from the `CreatureTemplate` (since custom IDs have no entry in the vanilla slots system), then pick randomly among registered custom sea creature IDs that have room.

The `spawnSeaHunter` path (ID 71) is harder to extend cleanly — it is not selected through `getRandomSeaCreatureId()` at all, but chosen unconditionally in `spawnSeaCreature()` when the sea hunter flag is set. Supporting a custom "sea hunter" equivalent would require a separate hook on `spawnSeaCreature()` itself.

#### JSON shape for sea creatures (proposed)

Sea creatures need a different JSON shape since they have no tile/elevation pool — they spawn anywhere in deep water:

```json
{
  "json-type": "spawn-group",
  "id": "open_ocean",
  "elevation": "deep-water",
  "entries": [
    { "creature": 200, "count": 1, "chance": 3 }
  ]
}
```

When `elevation` is `"deep-water"` and no `tile` is specified, `SpawnGroupHandler` routes to the `Zone`-based sea spawn hook rather than `EncounterBuilder`. The `chance` value becomes a relative weight among all registered custom sea creatures (same semantics as the land system).

This shape is a proposal only — it needs validation against the `getOpenSpawnSlotsForCreatureType` population cap system before implementation.

### Interop constraint

Other mods (e.g., mods using `ModCreature.addEncounters()`) also inject into these lists. Timing matters: our hook on `createEncounters()` fires after vanilla, but the order relative to other mods' `addEncounters()` calls depends on registration order. For `remove[]` and `reweight[]` targeting **vanilla** entries only, this is safe — vanilla entries are always present before any mod hook fires. For removing entries added by other mods, there's no guarantee.

**Design decision for the task:** Document that `remove[]` and `reweight[]` are best-effort for mod-added entries. Target vanilla creature IDs (≤ 119) with confidence; targeting other mods' creatures is unsupported.

## Transition from TASK-004c

When this task is implemented:

1. `SpawnEntry` (the `spawns` array in creature JSON) will be **deprecated**. Loading a creature JSON with a `spawns` block will log a deprecation warning but still work.
2. The deprecation warning should name the equivalent `spawn-group` JSON as the preferred approach.
3. The embedded `spawns` entries are internally equivalent — at runtime both call `EncounterBuilder`. No migration of existing data is needed.
4. In a future cleanup pass, the `spawns` field and `SpawnEntry` POJO can be removed entirely.

## Architecture

```
WurmTweaker.preInit()
  └─ [new] Javassist hook on Zone.getRandomSeaCreatureId()
       └─ insertAfter: if result == 0, call WurmTweakerSeaSpawns.getCustomSeaCreatureId()

WurmTweaker.init()
  └─ [new] SpawnGroupHandler registered with JsonLoader for type "spawn-group"
  └─ jsonLoader.loadType("spawn-group")
       └─ SpawnGroupHandler.apply(def)  ← partitions defs by type:
            tile+elevation defs → queued for SpawnTable hook
            deep-water defs    → registered with WurmTweakerSeaSpawns
  └─ ModCreatures.init()  ← registers SpawnTable.createEncounters() hook

[server boot]
  └─ SpawnTable.createEncounters()
       └─ [existing ModCreatures hook: addEncounters() for each ModCreature]
       └─ [new hook registered by SpawnGroupHandler]
            └─ for each tile+elevation SpawnGroupDefinition:
                 add entries via EncounterBuilder
                 remove entries via reflection on EncounterType
                 reweight entries via reflection on EncounterType

  └─ Zone.poll() [per zone tick, ongoing]
       └─ Zone.getRandomSeaCreatureId()  ← vanilla runs first
            └─ [Javassist insertAfter: if 0, ask WurmTweakerSeaSpawns]
                 └─ WurmTweakerSeaSpawns picks from registered deep-water entries
```

Two hooks on `SpawnTable.createEncounters()` is fine — the modlauncher chains invocation handlers. The `Zone` hook is registered in `preInit()` via Javassist (bytecode phase), same pattern as `CreatureDbHooks`.

## Files to Create/Modify

| File | Change |
|---|---|
| `src/.../creatures/SpawnGroupDefinition.java` | New POJO for `spawn-group` JSON |
| `src/.../creatures/SpawnGroupHandler.java` | New `ContentHandler` + hook registration |
| `src/.../creatures/CreatureDefinition.java` | Deprecate `spawns` field (keep it, add `@Deprecated`) |
| `src/.../creatures/CreatureHandler.java` | Log deprecation warning when `spawns` is present |
| `src/.../WurmTweaker.java` | Register `SpawnGroupHandler` with `JsonLoader`; register Zone hook in preInit() |
| `src/.../creatures/WurmTweakerSeaSpawns.java` | New — static registry of custom sea creature IDs + selection logic |
| `obsidian/Reference/Spawn Tiles.md` | Already created in TASK-004c — no changes needed |

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

### In-World Den Item Bug

The placed den item (`creatureSpawn`, template ID 521) has a fundamental design bug: when it fires, the spawn location is chosen from a **random tile anywhere in the zone**, not near the item's actual position. In settled areas where most tiles are tracked as `VolaTile` entries or covered by deed, the random picker almost always lands on a blocked tile and the spawn silently aborts. The item appears intact but effectively never fires.

Eight independent gates can each independently prevent a den spawn from occurring (detailed in `obsidian/wurmtweaker/Tasks/TASK-006 Spawn Groups.md`). The two most impactful in practice are Gate 3 (random tile hits an existing `VolaTile`) and Gate 4 (deed coverage), which compound in any settled kingdom area.

**Fix required by this task:** When `creatureSpawn != null`, pick spawn coordinates from a small radius around the item's own tile coordinates rather than from a random zone tile. Remove the 1-in-10 SpawnTable override that bypasses the den even on eligible ticks. The remaining gates (proximity checks, population caps, deed guard) are correct behavior and should be preserved.

### Implementation Phases

Ordered by impact vs. risk. Steps 1–2 can ship independently and deliver the majority of the benefit.

1. **Define the JSON schema** — tile type → encounter list, sea creature pool, per-template caps, spawn category names
2. **Replace `SpawnTable.java`** with a JSON loader that exposes the same `getRandomEncounter()` interface — all existing callers stay unchanged. (~70% of benefit, low risk: `SpawnTable` is already well-isolated)
3. **Replace `Zone.getRandomSeaCreatureId()` and the sea hunter priority logic** with a table lookup using the same schema
4. **Migrate per-template caps** out of `CreatureTemplateCreator.java` into JSON
5. **Replace `seaMonsters`/`seaHunters` counters** with generic category counters driven by spawn category definitions — highest-risk step in the entire refactor

## Verification

1. A `spawn-group` JSON adds a custom creature to `tree/ground`; confirm it appears in the pool
2. `remove: [11]` (troll) on `tree/ground` removes trolls from that tile's pool
3. `reweight: [{creature: 64, chance: 1}]` reduces horse frequency on steppe
4. A creature JSON with `spawns` still works but logs a deprecation warning
5. A mod using `EncounterBuilder` in its own `addEncounters()` is unaffected
6. A `spawn-group` with `elevation: "deep-water"` registers a custom sea creature; confirm it can spawn in deep water via the Zone hook
7. With all vanilla sea creature slots full, the Zone hook is still consulted for custom entries
