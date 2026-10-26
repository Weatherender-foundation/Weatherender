---
name: using-agent-skills
description: Discovers and invokes agent skills. Use when starting a session, or when you need to decide which skill or workflow applies to the piece of work at hand.
---

# Using Agent Skills

## Overview

Agent Skills is a collection of engineering workflow skills organized by development phase. This meta-skill helps you discover and apply the right skill for your current task.

## Skill Discovery

When a task arrives, identify the development phase and apply the corresponding skill:

```
Task arrives
    │
    ├── Spec-Kit Methodology & Workflow
    │   ├── Project constitution & principles? ─→ speckit-constitution
    │   ├── Draft new spec (PRD/Feature)? ──────→ speckit-specify
    │   ├── Clarify ambiguous spec requirements? → speckit-clarify
    │   ├── Validate spec quality & completeness? → speckit-checklist
    │   ├── Analyze codebase impact & risk? ─────→ speckit-analyze
    │   ├── Create technical plan & architecture? → speckit-plan
    │   ├── Align on tech stack / options? ──────→ speckit-converge
    │   ├── Break down plan into actionable tasks? → speckit-tasks
    │   ├── Convert tasks into GitHub Issues? ────→ speckit-taskstoissues
    │   └── Execute task-by-task implementation? → speckit-implement
    │
    ├── Engineering Practices
    │   ├── End-to-end spec-driven flow? ────────→ spec-driven-development
    │   ├── Designing APIs, schemas & contracts? ──→ api-and-interface-design
    │   ├── Writing tests / TDD cycle? ──────────→ test-driven-development
    │   └── Code review, quality & refactoring? ─→ code-review-and-quality
    │
    └── Routing & Navigation
        └── Discover & route skills? ────────────→ using-agent-skills
```

## Core Operating Behaviors

These behaviors apply at all times, across all skills. They are non-negotiable.

### 1. Surface Assumptions

Before implementing anything non-trivial, explicitly state your assumptions:

```
ASSUMPTIONS I'M MAKING:
1. [assumption about requirements]
2. [assumption about architecture]
3. [assumption about scope]
→ Correct me now or I'll proceed with these.
```

Don't silently fill in ambiguous requirements. Surface uncertainty early — it's cheaper than rework.

### 2. Manage Confusion Actively

When you encounter inconsistencies, conflicting requirements, or unclear specifications:

1. **STOP.** Do not proceed with a guess.
2. Name the specific confusion.
3. Present the tradeoff or ask the clarifying question.
4. Wait for resolution before continuing.

### 3. Push Push Back When Warranted

When an approach has clear problems:
- Point out the issue directly
- Explain the concrete downside
- Propose an alternative
- Accept the human's decision if they override with full information

### 4. Enforce Simplicity

Actively resist overcomplicating. Prefer the boring, obvious solution. Cleverness is expensive.

### 5. Maintain Scope Discipline

Touch only what you're asked to touch. Surgical precision, not unsolicited renovation.

### 6. Verify, Don't Assume

Every task is complete only when verified with passing tests, linter checks, or runtime evidence.

---

## Lifecycle Sequence

For feature development, the standard sequence using your active skills is:

```
1. speckit-constitution  → Verify alignment with project architecture & rules
2. speckit-specify       → Write spec.md (requirements & scope)
3. speckit-clarify       → Resolve ambiguous points (if any)
4. speckit-checklist     → Validate spec completeness
5. speckit-analyze       → Analyze impact on existing codebase
6. speckit-plan          → Draft technical plan.md & architecture
7. speckit-tasks         → Break down implementation into concrete tasks
8. speckit-implement     → Implement tasks step-by-step
9. test-driven-development → Ensure regression and feature coverage
10. code-review-and-quality → Conduct quality check before PR/merge
```

---

## Quick Reference

| Category | Skill | One-Line Summary |
|----------|-------|-----------------|
| Spec-Kit | speckit-constitution | Define and enforce non-negotiable project rules and standards |
| Spec-Kit | speckit-specify | Create clear functional specifications (spec.md) |
| Spec-Kit | speckit-clarify | Identify and resolve requirement gaps before coding |
| Spec-Kit | speckit-checklist | Audit specification quality against completion criteria |
| Spec-Kit | speckit-analyze | Evaluate impact, risks, and dependencies across codebase |
| Spec-Kit | speckit-plan | Design technical architecture and execution strategy (plan.md) |
| Spec-Kit | speckit-converge | Compare and decide between architectural trade-offs |
| Spec-Kit | speckit-tasks | Decompose plan into ordered, atomic execution tasks |
| Spec-Kit | speckit-taskstoissues | Sync planned tasks with issue trackers |
| Spec-Kit | speckit-implement | Execute approved tasks systematically |
| Practice | spec-driven-development | End-to-end specification-first workflow manager |
| Practice | api-and-interface-design | Design clean, stable API contracts and data models |
| Practice | test-driven-development | Red-Green-Refactor test cycle |
| Practice | code-review-and-quality | Multi-axis code review and quality validation |
| Meta | using-agent-skills | Discover, route, and invoke active agent skills |