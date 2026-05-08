# TASK-003: Skills

## Goal
Define the skill JSON schema and data model. Implement `SkillHandler` that loads skill definitions and applies them via the Wurm skill API. The actual Wurm API call is a **stub** until the API is researched — see the Research Required section below.

## Status
COMPLETE

## Deliverables

- [ ] `src/main/java/org/gotti/wurmtweaker/skills/SkillDefinition.java` — POJO
- [ ] `src/main/java/org/gotti/wurmtweaker/skills/SkillHandler.java` — `ContentHandler<SkillDefinition>`
- [ ] `data/skills/example.json` — sample file (included in source as documentation)
- [ ] `SkillHandler.apply()` logs what it would do but does NOT call any Wurm API until research is done

## SkillDefinition Fields (Initial Proposal)

These are the fields we believe are sensible to expose. Confirm against actual Wurm API during research.

| Field | Type | Description |
|---|---|---|
| `type` | String | Always `"skill"` |
| `id` | String | Skill name as used in the game (e.g., `"Blacksmithing"`) |
| `difficulty` | Double | Multiplier or absolute value for skill gain difficulty |
| `description` | String | Override for the skill's display description |

All fields except `type` and `id` are optional. Missing fields mean "don't change this value."

## SkillHandler Stub

```java
@Override
public void apply(SkillDefinition def) {
    logger.info("Would apply skill override: " + def.getId());
    // TODO: Research required before implementing
    // Questions to answer:
    //   1. What class manages skill templates? (com.wurmonline.server.skills.SkillSystem? Skills?)
    //   2. Is there a supported modsupport API for skills, or do we need reflection?
    //   3. What is the right hook point? (ServerStartedListener? ItemTemplatesCreatedListener?)
    //   4. Are skill difficulty values a multiplier or an absolute? What is the default?
    //   5. What field names exist on the Skill/SkillTemplate class?
}
```

## Research Required Before Filling the Stub

DO NOT implement the `apply()` body until these questions are answered:

1. **What class owns skill definitions?**
   Look for: `com.wurmonline.server.skills.SkillSystem`, `Skills`, `Skill`, `SkillTemplate`

2. **Is there a modsupport API for skills?**
   Check `org.gotti.wurmunlimited.modsupport` — if a `SkillTemplateBuilder` or similar exists, use it.

3. **What hook point should trigger skill loading?**
   - If skills are initialized before `onServerStarted()`, use `ServerStartedListener`
   - If skills are part of item template initialization, use `ItemTemplatesCreatedListener`
   - If skills need bytecode manipulation, this becomes a `PreInitable` task

4. **What is the data type and range of the difficulty field?**
   Is it a `double`, `float`, or `int`? Is 1.0 the default? Is it a multiplier or absolute value?

## Example JSON

`data/skills/example.json`:
```json
{
  "type": "skill",
  "id": "Blacksmithing",
  "difficulty": 1.5
}
```

## Verification
- Drop `example.json` in the data folder, start server → log shows "Would apply skill override: Blacksmithing"
- Drop a file with invalid JSON → log shows warning, server starts normally
- Drop a file with unknown fields → Gson ignores them, no crash
