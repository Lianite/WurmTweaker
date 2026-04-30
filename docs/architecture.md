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

Every JSON file must have a `"type"` field matching the content directory name:

```json
{
  "type": "skill",
  ...fields...
}
```

This allows mixed-type files in the future and makes files self-describing.
