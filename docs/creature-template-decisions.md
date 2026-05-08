# Creature Template — Design Decisions

This document records non-obvious design decisions made while modelling creature templates in JSON. Read this before changing the schema for creatures.

## Combat block structure

All combat-related fields are nested under a single `"combat"` key. This includes ratings, group behaviour, armor, and attacks.

### Why one block

The Wurm `CreatureTemplate` constructor takes damage values (`handDam`, `kickDam`, `biteDam`, `headDam`, `breathDam`) as flat parameters alongside combat ratings, but they are all combat concerns. Grouping them avoids the impression that damage is a physical property of the creature rather than a combat stat.

### `armor` as a child of `combat`

`naturalArmour` and `armourType` live under `combat.armor`, not at the top level or in their own sibling key. Armor affects how much damage the creature absorbs during combat — it belongs in the combat context.

### `damageType` at the `combat` level

`combatDamageType` from the source API is surfaced as `combat.damageType`. It applies to the creature's attacks regardless of which attack system is active (legacy or new), so it sits above `combat.attacks` rather than inside it.

## Two attack systems — legacy vs new

Wurm has two distinct combat systems for creatures. The `combat.attacks` block makes this explicit.

### Legacy system

`combat.attacks.legacy` maps directly to the constructor parameters `handDam`, `kickDam`, `biteDam`, `headDam`, `breathDam`. These are always present in the template regardless of which system is active.

### New system

When `combat.attacks.useNewSystem` is `true`, the game engine ignores the legacy damage values at runtime and uses the `combat.attacks.primary` and `combat.attacks.secondary` lists instead. The `AttackAction` objects in those lists are passed to `CreatureTemplate.addPrimaryAttack()` and `addSecondaryAttack()` via `setUsesNewAttacks(true)`.

**Primary attacks** are used on every hit roll. **Secondary attacks** are a separate, less frequent slot — typically charged or special moves with a higher damage multiplier.

### Why keep `legacy` even when `useNewSystem` is true

The legacy fields are always stored in the `CreatureTemplate` struct. Omitting them from the JSON when `useNewSystem: true` would hide information the engine still holds and would make the fallback behaviour invisible if the flag were ever removed. The seal is the canonical example of a creature that carries both — its legacy values (`bite: 5.0`, `kick: 8.0`) are dead at runtime but documented here for traceability.

### `values` object fields

Each `AttackAction` carries a `values` object whose keys map directly to the `AttackValues` constructor:

| Field | Type | Notes |
|---|---|---|
| `baseDamage` | float | Base damage dealt per hit |
| `criticalChance` | float | Probability of a critical hit |
| `baseSpeed` | float | Base attack speed |
| `attackReach` | int | How far the attack can reach |
| `weightGroup` | int | Weight class grouping for the attack |
| `damageType` | int (byte) | Wound type — matches `combatDamageType` conventions |
| `usesWeapon` | boolean | Whether the attack uses an equipped weapon |
| `rounds` | int | Number of combat rounds the attack occupies |
| `waitUntilNextAttack` | float | Delay multiplier before the next attack can fire |
