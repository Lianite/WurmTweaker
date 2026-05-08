# TASK-002: JSON Loading Infrastructure

## Goal
A generic, reusable pipeline that scans `data/<type>/` subdirectories, parses JSON files with Gson, and dispatches parsed objects to registered `ContentHandler` implementations. No content handlers are implemented in this task — only the pipeline.

## Status
COMPLETE

## Deliverables

- [x] `src/main/java/org/gotti/wurmtweaker/json/ContentHandler.java` — handler interface
- [x] `src/main/java/org/gotti/wurmtweaker/json/JsonLoader.java` — scanner + dispatcher
- [x] Unit-testable design (JsonLoader accepts a base path, not hardcoded paths)
- [x] Error handling: bad JSON logs filename + error, does NOT throw or crash

## ContentHandler Interface

```java
package org.gotti.wurmtweaker.json;

public interface ContentHandler<T> {
    String getTypeName();          // matches data/ subdirectory name, e.g. "skills"
    Class<T> getDefinitionClass(); // Gson target class
    void apply(T definition);      // called once per parsed object
}
```

## JsonLoader Design

```java
public class JsonLoader {
    private final File baseDir;
    private final Map<String, ContentHandler<?>> handlers;

    public JsonLoader(File baseDir) { ... }
    public void registerHandler(ContentHandler<?> handler) { ... }
    public void loadAll() { ... }  // walks all registered handler type directories
}
```

`loadAll()` behavior:
1. For each registered handler, resolve `baseDir/<typeName>/`
2. If directory does not exist, log INFO and skip — not an error
3. List all `.json` files in the directory (non-recursive)
4. For each file, parse with Gson into `handler.getDefinitionClass()`
5. On parse success, call `handler.apply(definition)`
6. On parse error, log `WARN: [filename] <error message>` and continue

## JSON File Convention

Every JSON file targets one definition object. The file name is cosmetic (admins can name them whatever they want). The `"type"` field must match the handler's `getTypeName()`:

```json
{
  "type": "skill",
  "id": "blacksmithing",
  "difficulty": 1.5
}
```

## Wiring Into WurmTweaker

In `WurmTweaker.init()`:
```java
jsonLoader = new JsonLoader(dataDir);
jsonLoader.registerHandler(new SkillHandler());
// future: jsonLoader.registerHandler(new CreatureHandler());
// future: jsonLoader.registerHandler(new ItemHandler());
```

Loading is triggered from the appropriate listener (not from `init()` itself).

## Verification
- Create a test JSON file in `data/skills/test.json` with intentionally bad JSON — confirm it logs a warning and does not crash
- Create a valid JSON file — confirm `apply()` is called (add a log line temporarily)
