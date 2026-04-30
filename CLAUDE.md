# WurmTweaker

A Java 8 server-side mod for Wurm Unlimited built on Ago's modloader. Admins customize game content — skills, creatures, items — by dropping JSON files in a `data/` folder. No SQL, no reflection hacks, no code changes required.

See `docs/overview.md` for the full project description and `docs/north-star.md` for the design philosophy.

## Rules

- **Java 8 only.** No language features from Java 9+.
- **Never guess Wurm internals.** If you don't know the API, research it first. See `docs/guardrails.md`.
- **One phase at a time.** Skills → Creatures → Items. Don't start a phase until the previous one works end-to-end.
- **Tasks live in `docs/tasks/`.** Read the task file before implementing anything.

## Build

```bash
mvn clean package
```

Output: `target/wurmtweaker.jar`

Deploy by copying the JAR to `mods/wurmtweaker/` on the Wurm Unlimited server.

## Modloader

This mod targets **Ago's WurmServerModLauncher** (ago1024/WurmServerModLauncher).

Descriptor file: `wurmtweaker.properties` (goes in server's `mods/` folder alongside the `wurmtweaker/` directory).

Available hook interfaces (all in `org.gotti.wurmunlimited.modloader.interfaces`):

| Interface | Method | When to use |
|---|---|---|
| `WurmServerMod` | `init()`, `preInit()` | Base interface — all mods implement this |
| `Configurable` | `configure(Properties)` | Read `.properties` config only — no game API here |
| `Initable` | `init()` | Register hooks, wire up handlers |
| `PreInitable` | `preInit()` | Bytecode manipulation only |
| `ServerStartedListener` | `onServerStarted()` | Post-boot logic |
| `ItemTemplatesCreatedListener` | `onItemTemplatesCreated()` | Item template modification |
| `PlayerLoginListener` | `onPlayerLogin(Player)` | Player connect |
| `ServerPollListener` | (each tick) | Polling logic |
| `ServerShutdownListener` | — | Cleanup on halt |

modsupport helpers (`org.gotti.wurmunlimited.modsupport`):
- `ItemTemplateBuilder` — preferred API for item template modification
- `CreatureTemplateBuilder` — preferred API for creature registration
- `ModCreatures.addCreature()` — registers a creature with the modloader
- `ModSupportDb` — SQLite access (avoid — prefer JSON)

## Data Directory Layout

```
mods/wurmtweaker/
├── wurmtweaker.jar
└── data/
    ├── skills/       ← Phase 1: *.json files defining skill overrides
    ├── creatures/    ← Phase 2: *.json files defining creature customizations
    └── items/        ← Phase 3: *.json files defining item overrides
```

## JSON Format

Every file defines one content override. The `"type"` field must match the subfolder name:

```json
{
  "type": "skill",
  "id": "Blacksmithing",
  "difficulty": 1.5
}
```

## Phase Roadmap

| Task | Description | Status |
|---|---|---|
| TASK-001 | Maven project skeleton + modloader wiring | In Progress |
| TASK-002 | JSON loading infrastructure | Pending |
| TASK-003 | Skills — data model + stub (API research required) | Pending |
| TASK-004 | Creatures — via `CreatureTemplateBuilder` | Planned |
| TASK-005 | Items — via `ItemTemplateBuilder` + reflection fallback | Planned |

## Key Files

| File | Purpose |
|---|---|
| `docs/overview.md` | What this project is and why |
| `docs/north-star.md` | Design philosophy (CDDA model) |
| `docs/guardrails.md` | What NOT to do |
| `docs/architecture.md` | Module structure, hook lifecycle, JSON conventions |
| `docs/tasks/TASK-00N-*.md` | Detailed task specs |
| `pom.xml` | Maven build |
| `wurmtweaker.properties` | Modloader descriptor |
| `src/main/java/org/gotti/wurmtweaker/WurmTweaker.java` | Main mod class |
| `src/main/java/org/gotti/wurmtweaker/json/JsonLoader.java` | Generic JSON pipeline |
