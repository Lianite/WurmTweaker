# Guardrails — What NOT To Do

These rules exist to keep the project maintainable and avoid common pitfalls when working with Wurm Unlimited internals.

## Language & Build

- **Java 8 only.** No `var`, no records, no sealed classes, no `instanceof` pattern matching, no Stream collectors added after Java 8. Target bytecode: `1.8`.
- **Maven only.** Do not introduce Gradle or any other build system.
- Dependencies must be `provided` scope for anything the modloader already ships (e.g., the modloader itself, Wurm server jars). Only add `compile` scope for things we actually bundle.

## Wurm Internals

- **Never guess about Wurm's internal API.** If you don't know what class handles skills, stop and research it. Do not write `com.wurmonline.server.skills.SkillSystem.getInstance().doSomething()` based on a hunch.
- **Prefer the official modsupport API.** `ItemTemplateBuilder`, `CreatureTemplateBuilder`, `ModCreatures` etc. exist for a reason — use them before reaching for reflection.
- **Reflection is a last resort.** If the modsupport API doesn't cover a field, document WHY reflection is needed in a comment. Do not use reflection silently.
- **Do not call private methods.** If a method is private, it's private for a reason. Find a public hook or a supported modloader API.

## JSON & Data

- **Never silently swallow JSON parse errors.** Always log the filename and what went wrong.
- **Do not validate JSON in application logic.** Validate as early as possible — in the loader, before the data reaches any game API.
- **No hardcoded content IDs.** If a skill, item, or creature needs to be referenced, look up its name/id through the game's own API. Do not embed magic numbers.

## Hooks

- **Use the right hook for the job:**
  - Item template modifications → `ItemTemplatesCreatedListener.onItemTemplatesCreated()`
  - Creature registration → `Initable.init()`
  - Skill modifications → TBD (research required, see TASK-003)
  - General startup logic → `ServerStartedListener.onServerStarted()`
- **Do not do work in `configure()`.** That method is for reading properties only — no game API calls there.
- **Do not do work in `preInit()` unless you need bytecode manipulation.** It runs before classes are fully loaded.

## Code Style

- No comments explaining WHAT code does — name things well instead.
- Comments only for non-obvious WHY: a Wurm quirk, a modloader constraint, a workaround for a specific bug.
- No multi-line comment blocks.
- No `System.out.println` — use `java.util.logging.Logger`.
- Keep the main mod class (`WurmTweaker.java`) thin — delegate to handlers.

## Process

- **Complete one phase before starting the next.** Do not start Creatures (TASK-004) until Skills (TASK-003) works end-to-end.
- **Task files are the source of truth for scope.** If it's not in a task file, it's not in scope for that task.
- **No scope creep.** A bug fix doesn't need surrounding cleanup. A stub doesn't need to be fully implemented.
