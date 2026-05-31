---
type: reference
tags:
  - reference
  - skills
related:
  - "[[Skill IDs]]"
  - "[[TASK-003 Skills]]"
---

# Skill Template Fields

Full JSON schema for WurmTweaker skill definitions. Derived from `SkillTemplate.java`, `SkillList.java`, `SkillSystem.java`, and `SkillHandler.java`.

## Source Files

| File | Role |
|---|---|
| `refs/SkillTemplate.java` | Authoritative field definitions and constructor signatures |
| `refs/SkillList.java` | `TYPE_*` constants and `skillArray` |
| `refs/SkillSystem.java` | `templates` map and `addSkillTemplate()` |
| `src/.../skills/SkillHandler.java` | Apply logic, reflection targets, constructor selection |

## Modification vs. Creation

Unlike the creature handler, the skill handler has no fixed ID boundary. The routing decision is purely runtime:

- **Modification**: `SkillSystem.templates.get(def.id)` returns a non-null template → apply only the non-null fields via reflection.
- **Creation**: `templates.get(def.id)` returns `null` AND `def.name != null` → build a new `SkillTemplate` via one of three package-private constructors and register it.
- **Skip**: `templates.get(def.id)` returns `null` AND `def.name == null` → log warning, skip.

In practice: use a known vanilla ID (from [[Skill IDs]]) to modify an existing skill. Use an ID above `10095` to safely create a new one — IDs 1–10095 are occupied by the base game.

### Quick-reference: required fields by use case

| Field | Modify existing skill | Create new skill |
|---|---|---|
| `json-type` | Required | Required |
| `id` | Required | Required |
| `name` | Optional (renames skill) | **Required** |
| All other fields | Optional | Optional (each has a default) |

---

## Fields

### `json-type` · String · Required

Must be `"skill"`. Any other value logs a warning and skips the entry.

---

### `id` · Integer · Required

Numeric skill ID. Determines the routing path:

- **1–10095** — vanilla skill IDs. If the ID exists in `SkillSystem.templates`, the modification path runs.
- **> 10095** — safe range for new custom skills (currently unoccupied in the base game).

→ Full vanilla ID table: [[Skill IDs]] / `data/creatures/references/skills.md`

---

### `name` · String · Required for new skills; Optional for modifications

Display name shown in the skills panel and examination text.

For modifications: if present, renames the skill. The handler also updates `SkillSystem.skillNames` and the `namesToSkill` name-lookup map so the new name resolves correctly everywhere.

---

### `type` · Short · Optional (default: `4` for new skills)

Controls how XP is distributed up the dependency tree and how the skill is categorised in the skills panel.

| Value | Constant | Used by |
|---|---|---|
| `0` | `TYPE_BASIC` | Sub-characteristics (Mind Logic, Body Strength, etc.) |
| `1` | `TYPE_MEMORY` | Top-level stats (Body, Mind, Soul) |
| `2` | `TYPE_ENHANCING` | Skill groups (Swords, Fighting, Smithing, etc.) |
| `4` | `TYPE_NORMAL` | Individual skills (Carpentry, Longsword, etc.) — default |

Set via reflection (`TYPE_FIELD`) for modifications. Passed directly to the constructor for new skills.

---

### `difficulty` · Float · Optional (default: `1.0` for new skills)

Controls how slowly the skill advances. **Higher = harder to gain.** The value is divided internally by `difficultyDivider` (1.0 on normal servers, 50.0 on challenge servers).

Representative vanilla values:

| Range | Skill tier |
|---|---|
| 2,000–7,000 | Individual skills (Carpentry, Longsword, Fishing, etc.) |
| 4,000–20,000 | Skill groups (Swords, Smithing, Fighting, etc.) |
| 200,000 | Sub-characteristics (Mind Logic, Body Strength, etc.) |
| 300,000 | Top-level stats (Body, Mind, Soul) |

`SkillTemplate` exposes a public `setDifficulty()` setter — no reflection required for this field on modifications.

---

### `dependencies` · int[] · Optional (default: `[]` for new skills)

Serves two distinct purposes simultaneously:

1. **XP propagation** — listed skills receive bonus experience ticks whenever this skill gains XP.
2. **Visual nesting in the skills panel** — if a listed ID is a `TYPE_ENHANCING` (type 2) skill, this skill is rendered nested beneath that group in the client UI.

**Nesting rules (empirically confirmed):**
- `TYPE_NORMAL` (4) depending on a `TYPE_ENHANCING` (2) → nested under that group in the skills panel. This is the standard parent–child relationship.
- `TYPE_NORMAL` (4) depending on `TYPE_MEMORY` (1) or `TYPE_BASIC` (0) → XP propagation occurs but no visual nesting. The skill appears at the top level.
- `TYPE_NORMAL` depending on another `TYPE_NORMAL` → XP propagation expected but visual nesting behaviour is untested.

Only the **first** `TYPE_ENHANCING` entry in the array is evaluated for nesting; multiple group entries in one dependency list are not supported by the vanilla UI.

Examples:
- Carpentry: `[104, 100]` — XP propagates to Body Control and Mind Logic; neither is TYPE_ENHANCING so Carpentry appears at the top level within its tab
- Longsword: `[1000]` — XP propagates to Swords group; since Swords is TYPE_ENHANCING, Longsword is nested under Swords in the UI

Set via reflection (`DEPENDENCIES_FIELD`) for modifications.

---

### `decayTime` · Long · Optional (default: `1209600000` for new skills)

**This field has no effect. Skill decay is entirely dead code in the current Wurm Unlimited codebase.**

The constructor clamps the stored value to a minimum of `1L`, and `DECAY_TIME_FIELD` reflection applies it on modification — but the value is never read at runtime.

**Why it does nothing — the broken call chain:**

```
Skills.checkDecay()      [Skills.java:353]  — iterates all skills, calls mem.checkDecay() / other.checkDecay()
  └─ Skill.checkDecay()  [Skill.java:589]   — EMPTY METHOD; body is {}
       └─ decay(saved)   [Skill.java:592]   — private, implemented, never called
            └─ alterSkill(..., decay=true)
```

- `Skill.checkDecay()` (Skill.java:589–590) has an empty body — it does nothing.
- `Skill.decay()` (Skill.java:592–621) is fully implemented but private and has no callers anywhere in the codebase.
- `Skills.checkDecay()` (Skills.java:353) is itself never invoked from any timer, scheduler, login, or logout handler — it has no callers either.

**What decay would do if it worked:**

| Skill type | Base decrease per tick | Affinity mitigation | `saved` (affinity) halves it? |
|---|---|---|---|
| Memory (`type 1`) | `-(100 − knowledge) / (difficulty × knowledge)` | n/a | n/a |
| Characteristic (`type 0`) | `−0.1` | `+0.05` per affinity level | Yes |
| Other (`type 2+`) | `−0.25` | `+0.025` per affinity level | Yes |

Hard floor in `alterSkill()` with `decay=true` (Skill.java:859–876): if `knowledge <= 70.0`, the decay tick returns immediately. Player skills at or below 70 would never decay even if the system worked. Creatures have no such floor.

Set via reflection (`DECAY_TIME_FIELD`) for modifications.

---

### `slowForPriests` · Boolean · Optional (default: `false`)

When `true`, multiplies the skill's difficulty by `1.25×` in the gain formula, reducing priest skill gain by ~20% (difficulty is in the denominator of the gain calculation).

**Three conditions that must all be true simultaneously:**

1. **Skill `type` must be `0`** (characteristic) — only the seven characteristics qualify: Mind Logic (100), Mind Speed (101), Body Strength (102), Body Stamina (103), Body Control (104), Soul Strength (105), Soul Depth (106). No weapon, crafting, or religion skill can ever be affected even if the flag were set on them.
2. **Player must be a priest** — specifically, `Skills.priest` must be `true`.
3. **`isPriestSlowskillgain` must be `true`** on the template.

**Epic/Challenge servers only:** `Skills.priest` is only populated in one place (`DbPlayerInfo.java:2298–2306`), and only when the server is configured as Epic or Challenge:

```java
if (Servers.localServer.isChallengeOrEpicServer() && this.realdeath == 0) {
    skills.priest = this.isPriest;
}
```

On Freedom servers, `Skills.priest` stays `false` regardless of whether the player is actually a priest — the flag has no effect on Freedom.

**Code path (SkillSystem.java:58–64):**

```java
public static float getDifficultyFor(final int skillNum, final boolean priest) {
    final SkillTemplate template = SkillSystem.templates.get(skillNum);
    if (template.getType() == 0 && priest && template.isPriestSlowskillgain) {
        return template.getDifficulty() * 1.25f;
    }
    return template.getDifficulty();
}
```

No vanilla skill has this flag enabled. It is a fully functional feature for Epic/Challenge servers that was never switched on in the base game. Applied via the public `setIsSlowForPriests()` setter — no reflection required.

---

### `fightSkill` · Boolean · Optional (default: `false`)

When `true`, grants a +0.25 stamina modifier bonus when a player gains this skill, provided they worship a warrior deity with more than 20 faith and at least 20 favor. `staminaMod` feeds into `advanceMultiplicator`, making this a meaningful gain boost for qualifying players.

**This is the only runtime effect of `fightSkill`.** It has no connection to PvP, training restrictions, or enemy presence — those behaviours are controlled by `ignoresEnemies`.

**Only read site (Skill.java:907):**

```java
else if (player2.getDeity().isWarrior()
      && player2.getFaith() > 20.0f
      && player2.getFavor() >= 20.0f
      && this.isFightingSkill()) {
    staminaMod += 0.25f;
}
```

**`fightSkill` and `ignoresEnemies` are independent.** Weapon-specific skills (longsword, large axe, two-handed sword, etc.) have `ignoresEnemies = true` but `fightSkill = false` — they bypass the enemy presence penalty without receiving the warrior deity bonus.

Only 11 broader combat skills have `fightSkill = true`:

| Skill | fightSkill | ignoresEnemies |
|---|---|---|
| Shields (1002) | true | true |
| Fighting (1023) | true | true |
| Weaponless fighting (10052) | true | true |
| Aggressive fighting (10053) | true | true |
| Defensive fighting (10054) | true | true |
| Normal fighting (10055) | true | true |
| Taunting (10057) | true | true |
| Shield bashing (10058) | true | true |
| Short bow (10079) | true | true |
| Long bow (10080) | true | true |
| Medium bow (10081) | true | true |

**Constructor note:** `SkillTemplate` has three anonymous constructor overloads. The combat skill overload takes `(boolean fightingSkill, boolean ignoreEnemy)` — both flags are passed independently. `fightSkill = true` does **not** imply `ignoresEnemies = true` (unlike `thieverySkill`, which does).

Applied for modifications by WurmTweaker's skill handler.

---

### `thieverySkill` · Boolean · Optional (default: `false`)

When `true`, routes new skill creation through the thievery constructor overload, which accepts `tickTime` directly — the only constructor that wires `tickTime` at construction time. The constructor also sets `ignoresEnemies = true` unconditionally (SkillTemplate.java:62–64).

**Vanilla constructor behavior:** `ignoresEnemies` is set unconditionally in the constructor body with no per-call override mechanism — there is no way to pass `false` through the same constructor call. WurmTweaker's handler applies `ignoresEnemies` post-construction via reflection, which is why `"ignoresEnemies": false` in a JSON definition still takes effect despite the constructor always writing `true`.

`isThieverySkill()` is defined in `SkillSystem.java:86–89` but is never called anywhere in the codebase. The flag is pure metadata — its only runtime consequences are what the constructor bakes in at creation time.

**Vanilla thievery skills split into two distinct groups:**

| Skill | tickTime | Cooldown | Behavior |
|---|---|---|---|
| Thievery (1028) | `0L` | None | `ignoresEnemies` only — gains on every qualifying check |
| Traps (10084) | `0L` | None | Same |
| Stealing (10075) | `600000L` | 10 min | Gains suppressed between cooldown windows; higher `times` cap |
| Lock picking (10076) | `600000L` | 10 min | Same |

The parent skill Thievery and the passive skill Traps carry no cooldown. The 10-minute gate applies only to the two active-use skills.

Set via reflection (`THIEVERY_SKILL_FIELD`) for modifications.

---

### `ignoresEnemies` · Boolean · Optional (default: `false`, unless `thieverySkill: true`)

When `true`, bypasses the 0.8× skill gain multiplier that applies when the player has recently been near a hostile creature.

**Code path (Skill.java:916–918):**

```java
if (player2.getEnemyPresense() > Player.minEnemyPresence && !this.ignoresEnemy()) {
    advanceMultiplicator *= 0.800000011920929;
}
```

`minEnemyPresence` corresponds to approximately 15 minutes of enemy presence on a standard server (30 seconds on test servers). All combat and thievery skills — including weapon-specific skills that have `fightSkill = false` — have `ignoresEnemies = true`. Crafting and production skills have `false`.

**Auto-set rule:** if `thieverySkill: true` is present and `ignoresEnemies` is omitted, WurmTweaker's handler forces `ignoresEnemies = true` post-construction via reflection (mirroring the constructor). An explicit `ignoresEnemies: false` in the JSON definition still takes effect because the handler applies it after construction — the constructor always writes `true`, but WurmTweaker overwrites it afterward.

Set via reflection (`IGNORES_ENEMIES_FIELD`) for modifications.

---

### `tickTime` · Long · Optional (default: `0`)

Minimum cooldown in milliseconds between consecutive skill gain ticks. `0` = no cooldown; the skill can tick on every qualifying action. The player must always perform the skill's action — XP is never granted automatically on a timer.

**Full runtime mechanism (five steps):**

**Step 1 — `touch()` is disabled for timed skills (Skill.java:575–579):**

```java
public void touch() {
    if (SkillSystem.getTickTimeFor(this.getNumber()) <= 0L) {
        this.lastUsed = System.currentTimeMillis();
    }
}
```

For `tickTime > 0`, `touch()` is a no-op and never updates `lastUsed`. For untimed skills, `touch()` advances `lastUsed` on every skill check.

**Step 2 — `mayUpdateTimedSkill()` detects cooldown (Skill.java:585–587):**

```java
boolean mayUpdateTimedSkill() {
    return System.currentTimeMillis() - this.lastUsed < SkillSystem.getTickTimeFor(this.getNumber());
}
```

Returns `true` when still inside the cooldown window since the last gain. For `tickTime = 0`, this always returns `false` (no cooldown).

**Step 3 — `dryRun` gate in `checkAdvance()` (Skill.java:703–706, 773–780):**

```java
if (!dryRun) {
    dryRun = this.mayUpdateTimedSkill();
}
// ... rolls skill check, calculates power ...
if (!dryRun) {
    this.doSkillGainNew(check, power, learnMod, times, skillDivider);
}
```

If still in cooldown, `dryRun` is forced `true` and `doSkillGainNew` is never called. The skill check still rolls — the action succeeds or fails normally — but no XP is awarded.

**Step 4 — `lastUsed` advances only on actual gains (Skill.java:852):**

```java
// inside alterSkill(), inside the hasSkillGain block:
this.lastUsed = System.currentTimeMillis();
```

The cooldown clock resets only when a gain fires, not on every action attempt.

**Step 5 — higher `times` cap for timed skills (Skill.java:850):**

```java
times = Math.min(
    (SkillSystem.getTickTimeFor(this.getNumber()) > 0L || this.getNumber() == 10033) ? 100.0f : 30.0f,
    times
);
```

Timed skills accept up to `100` for `times` vs `30` for untimed. Since `times` feeds directly into `advanceMultiplicator`, timed skills can deliver a proportionally larger single-gain event to compensate for the suppressed intermediate checks.

**Vanilla thievery skills:**

| Skill | tickTime | Cooldown | times cap |
|---|---|---|---|
| Thievery (1028) | `0L` | None | 30 |
| Traps (10084) | `0L` | None | 30 |
| Stealing (10075) | `600000L` | 10 min | 100 |
| Lock picking (10076) | `600000L` | 10 min | 100 |

**Creation path note:** `tickTime` is only wired at construction time via the thievery constructor overload (`thieverySkill: true`). For new skills using either of the other two constructor overloads, the field defaults to `0` regardless of what the definition specifies.

Set via reflection (`TICK_TIME_FIELD`) for modifications.

---

## Implementation Notes

- **No modsupport `SkillTemplateBuilder`** exists. All field access uses raw reflection against `SkillTemplate` fields and package-private constructors. No `private final` stripping is required because `SkillTemplate` fields are not final — they are package-private primitives.
- **`SkillList.skillArray`** is `static final` and must be un-finaled at class init time before new skills can be appended to it. This is done in the static initialiser block of `SkillHandler`.
- **`addSkillTemplate()`** in `SkillSystem` is a private static method; invoked via reflection.
- **Name rename side-effects:** renaming a vanilla skill also updates `SkillSystem.skillNames` (id→name map) and removes/re-inserts the entry in `SkillSystem.namesToSkill` (lowercase name→id map). Renaming is safe as long as no other mod or server code holds a hard-coded name string for that skill.
- **`difficulty` and challenge servers:** the `SkillTemplate` constructor applies `difficulty / difficultyDivider` at construction time, where `difficultyDivider = 50` on challenge servers. For modifications via `setDifficulty()`, the raw value is stored — no divider is applied at modification time. This means the effective difficulty of modified skills on challenge servers matches the stored value directly, not the divided version.
