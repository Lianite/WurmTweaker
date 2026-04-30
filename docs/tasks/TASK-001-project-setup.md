# TASK-001: Project Setup

## Goal
A bare Maven project that compiles cleanly and loads into Ago's modloader without errors. No game logic yet — just the scaffolding.

## Status
COMPLETE

## Deliverables

- [x] `pom.xml` — Java 8 target, modloader as `provided` dependency, Gson as `compile` dependency
- [x] `src/main/java/org/gotti/wurmtweaker/WurmTweaker.java` — main mod class
- [x] `wurmtweaker.properties` — modloader descriptor file
- [x] Project compiles with `mvn clean package`

## Implementation Notes

### pom.xml requirements
- `<source>1.8</source>` and `<target>1.8</target>` in maven-compiler-plugin
- Modloader dependency: `org.gotti.wurmunlimited:modlauncher` at `provided` scope
- Gson dependency: `com.google.code.gson:gson` at `compile` scope (bundle it)
- Output JAR: `wurmtweaker.jar`

### WurmTweaker.java
Implements:
- `WurmServerMod` — required base interface
- `Configurable` — to read `wurmtweaker.properties` config (e.g., data dir path)
- `Initable` — to register content handlers and hooks during `init()`
- `ServerStartedListener` — for post-boot logic

The class should be thin: configure data directory path in `configure()`, wire up handlers in `init()`, trigger loading in the appropriate listener methods.

On first boot, if the `data/` directory doesn't exist, log a warning but don't crash.

### wurmtweaker.properties
```properties
classname=org.gotti.wurmtweaker.WurmTweaker
classpath=wurmtweaker.jar
```

Optionally include a configurable `dataDir` property defaulting to `mods/wurmtweaker/data`.

## Modloader JAR Note

The modloader JAR is not in any public Maven repo. For local builds, install it manually:

```bash
mvn install:install-file -Dfile=/path/to/modlauncher.jar \
  -DgroupId=org.gotti.wurmunlimited -DartifactId=modlauncher \
  -Dversion=0.47 -Dpackaging=jar
```

The real JAR lives in your Wurm server's `mods/` directory as `modlauncher.jar` (or similar).
A compile-only stub with the required interfaces is installed in the local Maven repo for CI/dev use.

## Verification
- `mvn clean package` produces `target/wurmtweaker.jar` (258K, includes bundled Gson)
- JAR contains `WurmTweaker.class`, `ContentHandler.class`, `JsonLoader.class`
- Modloader interfaces are `provided` (not bundled) — correct
- No Wurm game API calls — purely framework wiring
