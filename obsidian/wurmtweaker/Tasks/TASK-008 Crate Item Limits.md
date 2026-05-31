---
id: TASK-008
title: Crate Item Limits
status: backlog
phase: items
tags: [items, hardcoded, crates, documentation]
related: [TASK-005, TASK-007]
---

## Goal

Document and optionally expose the hardcoded item limits for small and large crates, which are baked directly into `Item.java` and cannot be changed via item template fields.

## Background

Crate capacity limits are **not** template fields — they are compile-time constants in `Item.java`:

```java
// Item.java:233-234
public static final int MAX_CONTAINED_ITEMS_ITEMCRATE_SMALL = 150;
public static final int MAX_CONTAINED_ITEMS_ITEMCRATE_LARGE = 300;
```

Applied in two places:

- `getRemainingCrateSpace()` (Item.java:3861–3864) — branches on template ID 852 (large crate) vs. all others (small crate limit)
- Inline at Item.java:4734:
  ```java
  final int storageSpace = (this.template.templateId == 852) ? 300 : 150;
  ```

Template ID 852 = large crate. Everything else defaults to 150.

There is no `maxItemCount` or `maxItemWeight` field for crates — the limits are entirely outside the template system.

## Deliverables

- [ ] Add a note to `data/items/references/item-template-fields.md` (and Obsidian reference) warning admins that crate limits are hardcoded and cannot be overridden via JSON
- [ ] Decide whether WurmTweaker should expose these as configurable values (requires bytecode patching via `PreInitable`/javassist — not a JSON field)
- [ ] If exposing: design the config surface (`.properties` key? new JSON field on crate templates?), implement, and test

## Research Findings

- Constants are `public static final int` — not reflectively settable at runtime without bytecode manipulation
- Two independent call sites must both be patched for the change to be consistent
- Scope is narrow: only affects template IDs that reach `getRemainingCrateSpace()` or the inline branch (i.e., actual crate items, not generic containers)
