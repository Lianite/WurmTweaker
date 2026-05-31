---
id: TASK-007
title: Initial Containers Support
status: Planned
phase: 3
tags:
  - task
  - planned
related:
  - "[[TASK-005 Items]]"
  - "[[Item Template Fields]]"
---

# TASK-007: Initial Containers Support

## Goal

Expose `setInitialContainers()` to the JSON schema so admins can define fixed child-container slots on items (e.g. give a custom lunchbox three food compartments, or a custom still its own boiler and condenser). This is an enhancement to the Phase 3 item system.

## Background

Several vanilla items use `setInitialContainers()` on `ItemTemplate` to spawn fixed child-container slots when the item is first created in the world. These slots are tied to the item's lifecycle — not player-placed sub-items. The pattern always accompanies `setContainerSize(0, 0, 0)` + itemType `180` (`ITEM_TYPE_USES_SPECIFIED_CONTAINER_VOLUME`), which suppresses dimension-based volume computation.

### Vanilla items that use this pattern

| templateId | Name | Child containers |
|---|---|---|
| 1178 | still | boiler (1284) + condenser (1285) |
| 1277 | larder | ice box (1278) + food shelf ×N (1279) |
| 1296 | lunchbox | thermos (1294) + food compartment ×2 (1295) |
| 1297 | picnic basket | thermos + food compartments |
| 1117 | alchemist's cupboard | internal shelves |
| 1119 | storage unit | internal shelves |
| 1316 | bulk container unit | BSB slots |
| 1341 | tackle box | internal slots |
| 1432 | chicken coop | internal slots |

## The `InitialContainer` Class

**Package:** `com.wurmonline.server.items`

**File:** `refs/InitialContainer.java` (copy in repo for reference)

All fields and getters are public; **constructors are package-private**:

```java
public class InitialContainer {
    final int    templateId;
    final String name;
    final byte   material;

    // package-private — cannot call from org.gotti.wurmtweaker.items without reflection
    InitialContainer(int aTemplateId, String aName) { ... }
    InitialContainer(int aTemplateId, String aName, byte aMaterial) { ... }

    public int    getTemplateId() { ... }
    public String getName()       { ... }
    public byte   getMaterial()   { ... }
}
```

## The `setInitialContainers()` Method

On `com.wurmonline.server.items.ItemTemplate`:

```java
// package-private — cannot call directly from org.gotti.wurmtweaker.items
void setInitialContainers(InitialContainer[] containers)
```

Must be invoked via `ReflectionUtil.callPrivateMethod()`.

## Implementation Plan

### JSON Schema Addition

Add `initialContainers` array field to `ItemDefinition`:

```java
public static class InitialContainerDef {
    public Integer templateId;  // required — child item's template ID
    public String  slotName;    // required — display name for the slot
    public Integer material;    // optional — byte material; defaults to 0 (no override)
}

public InitialContainerDef[] initialContainers;
```

Example JSON:

```json
{
  "json-type": "item",
  "identifier": "myplugin:mylunchbox",
  "containerSize": { "x": 0, "y": 0, "z": 0 },
  "itemTypes": [180, ...],
  "initialContainers": [
    { "templateId": 1294, "slotName": "thermos" },
    { "templateId": 1295, "slotName": "food compartment" },
    { "templateId": 1295, "slotName": "food compartment" }
  ]
}
```

### Instantiating `InitialContainer` via Reflection

`InitialContainer` constructors are package-private so `new InitialContainer(...)` fails from our package. Use `getDeclaredConstructor`:

```java
// Two-arg constructor (no material override)
Constructor<?> ctor2 = InitialContainer.class
        .getDeclaredConstructor(int.class, String.class);
ctor2.setAccessible(true);
InitialContainer ic = (InitialContainer) ctor2.newInstance(templateId, slotName);

// Three-arg constructor (with material override)
Constructor<?> ctor3 = InitialContainer.class
        .getDeclaredConstructor(int.class, String.class, byte.class);
ctor3.setAccessible(true);
InitialContainer ic = (InitialContainer) ctor3.newInstance(templateId, slotName, (byte)materialValue);
```

Cache both `Constructor` objects statically to avoid `getDeclaredConstructor` overhead per item.

### Calling `setInitialContainers()` via ReflectionUtil

```java
Method setter = ReflectionUtil.getMethod(ItemTemplate.class, "setInitialContainers");
ReflectionUtil.callPrivateMethod(template, setter, (Object) containersArray);
```

Note: the `(Object)` cast is required so Java does not treat the array as varargs.

### Handler Integration

In `ItemHandler.applyItem()`, after the containerSize block:

```java
if (def.initialContainers != null && def.initialContainers.length > 0) {
    applyInitialContainers(template, def.initialContainers);
}
```

The `applyInitialContainers()` method builds the `InitialContainer[]` array using the reflection constructors above, then calls the method setter.

## Caveats and Risks

### Modification Path Is Risky

`setInitialContainers()` is called by the server at item-creation time — it tells the engine what child containers to spawn when a **new** instance of this item is created. It does **not** retroactively add containers to items already in the world.

If an admin changes `initialContainers` on an existing vanilla template (e.g. adds a third food compartment to the lunchbox), **existing lunchboxes in the world are unaffected**. New lunchboxes will have the modified slots. This divergence can confuse players — document clearly.

If an admin **removes** a container slot from a template already used in the world, items already spawned will be orphaned — the child container item still exists in the DB but is no longer accessible through the parent. This is data loss.

**Recommended scope for initial implementation:** creation path only (items with `identifier`). For modification path, require an explicit `"overrideInitialContainers": true` flag and log a prominent warning at apply time.

### itemType 180 Is Required

Any item using `initialContainers` with `containerSize {0,0,0}` must also have itemType `180` (`ITEM_TYPE_USES_SPECIFIED_CONTAINER_VOLUME`) in `itemTypes`. Without it, the engine computes volume from `dimensions` (x*y*z), which may not be zero and would allow items to be placed directly into the container bypassing the slot structure.

## Deliverables

- [ ] `InitialContainerDef` nested class in `ItemDefinition.java`
- [ ] `applyInitialContainers()` private method in `ItemHandler.java`
- [ ] Integration into `ItemHandler.applyItem()` (creation path first, then modification path with safety flag)
- [ ] Update `data/items/example.json` with an `initialContainers` example
- [ ] Update `obsidian/wurmtweaker/Item Template Fields.md` schema doc with the new field
