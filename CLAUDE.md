# WurmTweaker

A Java 8 server-side mod for Wurm Unlimited built on Ago's modloader. Admins customize game content — skills, creatures, items — by dropping JSON files in a `data/` folder. No SQL, no reflection hacks, no code changes required.

Before starting any work, read:
- `obsidian/wurmtweaker/Design/Overview.md` — what this project is and why
- `obsidian/wurmtweaker/Design/North Star.md` — design philosophy; every decision should align with this

## Rules

- **Java 8 only.** No language features from Java 9+.
- **Never guess Wurm internals.** If you don't know the API, research it first. Ask the user to provide source files — never decompile JARs.
- **One phase at a time.** Skills → Creatures → Items. Don't start a phase until the previous one works end-to-end.
- **Tasks live in `obsidian/wurmtweaker/Tasks/`.** Read the task file before implementing anything.

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

Every JSON object must have a `"json-type"` field whose value matches the handler name (`"skill"`, `"creature"`, `"item"`). This routes each object to the correct handler.

The field is `"json-type"` — not `"type"` — to avoid collision with definition-level fields (e.g. `SkillDefinition` has its own `"type"` field for skill classification; `CreatureDefinition` has `"types"` for creature flags).

```json
{
  "json-type": "skill",
  "id": 102,
  "difficulty": 1.5
}
```

## Obsidian Vault

An Obsidian knowledge vault lives at `obsidian/wurmtweaker/` in the repo root. It documents design decisions, reference tables, and tasks.

- **Tasks:** `obsidian/wurmtweaker/Tasks/` — one file per task (TASK-001 through TASK-007+)
- Task files use YAML frontmatter (`id`, `title`, `status`, `phase`, `tags`, `related`) followed by markdown sections (Goal, Deliverables, Research Findings, etc.)
- When creating a new task, check the existing files to find the next available task number


## Key Files

| File | Purpose |
|---|---|
| `obsidian/wurmtweaker/Design/Overview.md` | What this project is and why |
| `obsidian/wurmtweaker/Design/North Star.md` | Design philosophy — read before making any decisions |
| `obsidian/wurmtweaker/Design/Architecture.md` | Module structure, hook lifecycle, JSON conventions |
| `obsidian/wurmtweaker/Design/Guardrails.md` | What NOT to do |
| `pom.xml` | Maven build |
| `wurmtweaker.properties` | Modloader descriptor |
| `src/main/java/org/gotti/wurmtweaker/WurmTweaker.java` | Main mod class |
| `src/main/java/org/gotti/wurmtweaker/json/JsonLoader.java` | Generic JSON pipeline |
