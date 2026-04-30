# TASK-001: Project Setup

## Goal
A bare Maven project that compiles cleanly and loads into Ago's modloader without errors. No game logic yet — just the scaffolding.

## Status
IN PROGRESS (skeleton created, needs compile verification)

## Deliverables

- [ ] `pom.xml` — Java 8 target, modloader as `provided` dependency, Gson as `compile` dependency
- [ ] `src/main/java/org/gotti/wurmtweaker/WurmTweaker.java` — main mod class
- [ ] `wurmtweaker.properties` — modloader descriptor file
- [ ] Project compiles with `mvn clean package`

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

## Verification
- `mvn clean package` produces `target/wurmtweaker.jar` with no errors
- No Wurm game API calls yet — this is purely framework wiring
