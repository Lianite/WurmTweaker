# WurmTweaker

A Java 8 server-side mod for Wurm Unlimited built on Ago's modloader. Server admins customize game content — skills, creatures, items — by dropping JSON files in a `data/` folder. No SQL, no reflection hacks, no code changes.

---

## Tasks

| ID | Title | Phase | Status |
|---|---|---|---|
| [[TASK-001 Project Setup\|TASK-001]] | Project Setup | 1 | Complete |
| [[TASK-002 JSON Infrastructure\|TASK-002]] | JSON Loading Infrastructure | 1 | Complete |
| [[TASK-003 Skills\|TASK-003]] | Skills | 1 | Complete |
| [[TASK-004 Creatures\|TASK-004]] | Creatures | 2 | Complete |
| [[TASK-004b Creatures Java Support\|TASK-004b]] | Creatures — Java Support | 2 | Complete |
| [[TASK-005 Items\|TASK-005]] | Items | 3 | Planned |

---

## Design

- [[Overview]] — What this project is and why it exists
- [[North Star]] — Design philosophy (the CDDA model)
- [[Architecture]] — Module structure, hook lifecycle, JSON conventions
- [[Guardrails]] — What NOT to do
- [[Creature Template Decisions]] — Schema decisions for the creature JSON — read before changing combat block or attack structure

---

## Reference

- [[Skill IDs]] — All skill constant names and numeric IDs
- [[Creature Types]] — All creature type flag IDs and their effects

---

## Phase Roadmap

| Phase | Focus | Status |
|---|---|---|
| 1 | Skills | Complete |
| 2 | Creatures | Complete |
| 3 | Items | Planned |

## Build

```bash
mvn clean package
```

Output: `target/wurmtweaker.jar` → deploy to `mods/wurmtweaker/` on the server.
