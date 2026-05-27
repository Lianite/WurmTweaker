# Architecture

## Module Structure

```
WurmTweaker/
├── pom.xml                                    Maven build
├── wurmtweaker.properties                     Modloader descriptor
└── src/main/java/org/gotti/wurmtweaker/
    ├── WurmTweaker.java                       Main mod class (thin coordinator)
    └── json/
        ├── JsonLoader.java                    Scans data/ and dispatches to handlers
        └── ContentHandler.java                Interface for per-type handlers
```

Runtime data directory (not in source tree — admins create/populate this):
```
mods/wurmtweaker/
├── wurmtweaker.jar
└── data/
    ├── skills/
    │   └── *.json
    ├── creatures/
    │   └── *.json
    └── items/
        └── *.json
```

## Mod Lifecycle

```
Modloader startup
  └─ configure(Properties)        ← read wurmtweaker.properties config (data dir path, etc.)
       └─ preInit()               ← not used unless bytecode manipulation needed
            └─ init()             ← register hooks; init JsonLoader; register ContentHandlers
                 └─ onServerStarted() / onItemTemplatesCreated()
                      └─ JsonLoader.load("skills"|"creatures"|"items")
                           └─ ContentHandler.apply(definition)
                                └─ Wurm API call
```

## JsonLoader

`JsonLoader` is a generic file scanner. It:
1. Resolves the `data/<type>/` directory relative to the mod's JAR location
2. Walks all `.json` files in that directory (non-recursive)
3. Parses each file using Gson
4. Calls the registered `ContentHandler<T>` for the matching type

Error handling: parse errors are logged with filename + message, then skipped. A bad file never crashes the server.

## ContentHandler Interface

```java
public interface ContentHandler<T> {
    Class<T> getType();
    void apply(T definition);
}
```

Each phase (skills, creatures, items) provides one `ContentHandler` implementation.

## Hooks Used Per Phase

| Phase | Hook Interface | Method | Reason |
|---|---|---|---|
| Skills | TBD (see TASK-003) | TBD | Skills API not yet researched |
| Creatures | `Initable` | `init()` | Creatures must register during init via `ModCreatures` |
| Items | `ItemTemplatesCreatedListener` | `onItemTemplatesCreated()` | Templates must exist before modification |

## Dependency on Ago's Modloader

The modloader JAR is `provided` scope — it is on the classpath at runtime (the server ships it) but is NOT bundled in our JAR.

Key packages used:
- `org.gotti.wurmunlimited.modloader.interfaces` — `WurmServerMod`, listener interfaces
- `org.gotti.wurmunlimited.modsupport` — `ItemTemplateBuilder`, `CreatureTemplateBuilder`, `ModSupportDb`

## JSON Format Convention

Every JSON object must have a `"json-type"` field whose value matches the handler name:

```json
{
  "json-type": "skill",
  ...fields...
}
```

The field is named `"json-type"` — not `"type"` — to avoid collision with definition-level fields. `SkillDefinition` has its own `"type"` field for skill classification; `CreatureDefinition` has a `"types"` array for creature behavior flags; future definition classes may also use `"type"` for their own purposes.

`JsonLoader.dispatchIfMatch()` reads the `"json-type"` key and calls the registered `ContentHandler` whose `getTypeName()` matches. This allows a single JSON file to contain objects of multiple types — each is routed independently.
