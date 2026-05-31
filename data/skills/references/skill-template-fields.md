# Skill Template Fields

Full JSON schema for WurmTweaker skill definitions.

## Modifying vs. Creating a Skill

The handler determines what to do based on whether the `id` already exists in the game:

- **Modify an existing skill**: use a vanilla skill ID (see `data/skills/references/skills.md`). Only the fields you include are changed — omitted fields are left at their vanilla values.
- **Create a new skill**: use an ID not occupied by the base game (IDs above `10095` are currently safe) and include `name`. All omitted fields get the defaults listed below.
- **Skip**: if the ID doesn't match any existing skill and `name` is missing, the entry is skipped with a warning.

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

Must be `"skill"`.

---

### `id` · Integer · Required

Numeric skill ID. Determines whether this is a modification or a new registration.

- **1–10095** — vanilla skill IDs. Use these to modify existing base-game skills.
- **> 10095** — safe range for new custom skills.

→ Full vanilla ID table: `data/skills/references/skills.md`

---

### `name` · String · Required for new skills; Optional for modifications

Display name shown in the skills panel. For modifications, including `name` renames the skill.

---

### `type` · Short · Optional (default: `4` for new skills)

Controls how the skill is categorised and how XP propagates up the skill tree.

| Value | Category | Used by |
|---|---|---|
| `0` | Sub-characteristics | Mind Logic, Body Strength, Body Control, etc. |
| `1` | Primary stats | Body, Mind, Soul (top-level only) |
| `2` | Skill groups | Swords, Fighting, Smithing, Cooking, etc. |
| `4` | Individual skills | Carpentry, Longsword, Fishing, etc. — default |

Use `4` for any new skill unless you are intentionally creating a skill group or characteristic.

---

### `difficulty` · Float · Optional (default: `1.0` for new skills)

Controls how slowly the skill advances. **Higher = harder to gain.**

Representative vanilla values:

| Range | Skill tier |
|---|---|
| 2,000–7,000 | Individual skills (Carpentry, Longsword, Fishing, etc.) |
| 4,000–20,000 | Skill groups (Swords, Smithing, Fighting, etc.) |
| 200,000 | Sub-characteristics (Mind Logic, Body Strength, etc.) |
| 300,000 | Primary stats (Body, Mind, Soul) |

For a new custom skill that trains at a typical individual-skill pace, `4000.0` is a reasonable starting point.

---

### `dependencies` · int[] · Optional (default: `[]` for new skills)

Serves two purposes at once:

1. **XP propagation** — listed skills receive bonus experience ticks whenever this skill gains XP.
2. **Visual nesting** — if a listed ID is a skill group (`type: 2`), this skill is rendered nested beneath that group in the client skills panel.

**Which entries produce nesting:**

| Dependency type | XP propagation | Visual nesting |
|---|---|---|
| Skill group (`type: 2`) | Yes | **Yes** — skill appears under that group |
| Sub-characteristic (`type: 0`) | Yes | No |
| Primary stat (`type: 1`) | Yes | No |
| Individual skill (`type: 4`) | Yes | Untested |

```json
"dependencies": [1000]
```

- `[]` — standalone skill, no propagation, no nesting
- `[104, 100]` — XP propagates to Body Control and Mind Logic; no nesting (neither is a skill group)
- `[1000]` — XP propagates to the Swords group; skill is visually nested under Swords in the panel

To place a new skill inside an existing group (e.g. under Carpentry or Fighting), list that group's ID here. Only one group entry is evaluated for nesting; listing multiple groups has no additional visual effect.

---

### `decayTime` · Long · Optional (default: `1209600000` for new skills)

**This field has no effect.** Skill decay is dead code in the current Wurm Unlimited codebase — the decay system is fully implemented internally but is never invoked at runtime. Setting any value here is accepted without error but does nothing in practice.

---

### `slowForPriests` · Boolean · Optional (default: `false`)

When `true`, reduces skill gain by ~20% for priest characters by multiplying the skill's difficulty by 1.25×.

**Epic/Challenge servers only.** This feature only activates when the server is configured as Epic or Challenge mode — on Freedom servers, the priest check is never populated and this flag has no effect regardless of its value.

**Characteristics only.** The flag only applies to skills with `type: 0` — the seven Body/Mind/Soul characteristics (Mind Logic, Mind Speed, Body Strength, Body Stamina, Body Control, Soul Strength, Soul Depth). Setting it on any other skill type has no effect.

No vanilla skill has this enabled. It is a fully functional feature for Epic/Challenge servers that was never switched on in the base game.

---

### `fightSkill` · Boolean · Optional (default: `false`)

When `true`, grants a +0.25 stamina modifier bonus to skill gain for players who worship a warrior deity with more than 20 faith and at least 20 favor. This is the flag's only effect.

Note that `fightSkill` is **not** what controls whether a skill can be gained near enemies — that is `ignoresEnemies`. The two flags are independent and set separately.

Vanilla skills with `fightSkill = true` are the 11 broader combat skills: Shields (1002), Fighting (1023), Weaponless fighting (10052), Aggressive/Defensive/Normal fighting (10053–10055), Taunting (10057), Shield bashing (10058), and the three bow skills (10079–10081). Weapon-specific skills (longsword, large axe, etc.) have `ignoresEnemies = true` but `fightSkill = false`.

---

### `thieverySkill` · Boolean · Optional (default: `false`)

When `true`, automatically sets `ignoresEnemies: true` and enables `tickTime` for new skills. These are the flag's only effects. You can override `ignoresEnemies` by explicitly including `"ignoresEnemies": false` in the same definition.

The four vanilla thievery skills fall into two groups:

| Skill | tickTime | Behavior |
|---|---|---|
| Thievery (1028) | `0` | `ignoresEnemies` only — gains on every qualifying check |
| Traps (10084) | `0` | Same |
| Stealing (10075) | `600000` (10 min) | Cooldown between gains; larger single-gain events |
| Lock picking (10076) | `600000` (10 min) | Same |

---

### `ignoresEnemies` · Boolean · Optional (default: `false`, unless `thieverySkill: true`)

When `true`, bypasses the 20% skill gain penalty applied when the player has recently been near a hostile creature (~15 minutes on a standard server). When `false`, any skill gains while enemies are nearby are multiplied by 0.8.

All combat skills — including weapon-specific skills like longsword and large axe — have this set to `true`. Crafting and production skills have `false`.

If `thieverySkill: true` is set and `ignoresEnemies` is omitted, it is automatically forced to `true`. Explicitly including `"ignoresEnemies": false` in the same definition will override this.

---

### `tickTime` · Long · Optional (default: `0`)

Minimum cooldown in milliseconds between consecutive skill gain ticks. `0` = no cooldown; the skill can tick on every qualifying action. XP is never granted automatically on a timer — the player must always perform the action.

When a cooldown is active, the action still resolves normally (the player succeeds or fails the task), but no skill XP is awarded until the cooldown window has elapsed since the last gain. The cooldown clock resets only when a gain actually fires, not on every action attempt.

To compensate for suppressed intermediate gains, skills with a non-zero `tickTime` can deliver a proportionally larger single-gain event than untimed skills.

| Value | Effective cooldown |
|---|---|
| `0` | No cooldown (vanilla default for most skills) |
| `600000` | 10 minutes between gains (Stealing, Lock picking) |

> **Note:** `tickTime` only takes effect when combined with `thieverySkill: true` for newly created skills. For modifications to existing skills, any non-zero value is applied normally.

---

## Addendum: Creating New Skills and Cooking Affinity Calculators

Adding a new skill to the game modifies the internal skill array, which **will break any existing third-party cooking affinity calculators**. Temporary affinities from food are determined by mapping a calculation result onto the ordered skill array — the result is an index into that array. When new skills are inserted, every index at or after the insertion point shifts, meaning foods that previously granted an affinity for one skill will now resolve to a different skill entirely.

There is no workaround for this on the calculator side without knowledge of your server's exact skill list. A companion mod is planned that will expose the server's current skill list in-game, allowing affinity-from-food calculations to account for server-specific additions dynamically.
