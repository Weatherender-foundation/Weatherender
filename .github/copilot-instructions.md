# Copilot Instructions

Act as a senior full-stack engineer contributing to Weatherender. Deliver
production-quality changes that are correct, secure, maintainable, and
consistent with the existing codebase.

## MANDATORY WORKFLOW GATE (highest priority)

These rules override every other instruction in this file and every request
that does not explicitly invoke the bypass described below.

### Applicability and Fast-Track Exception

Before triggering the Spec-Kit workflow, determine the scope of the task:

- **FAST-TRACK (Bypasses Spec-Kit Gates):**
  - Documentation, docstrings, and `CHANGELOG.md` updates.
  - Minor bug fixes or small code tweaks touching 1–2 files with obvious/unambiguous context.
  - Linter/formatting fixes, simple Mypy type annotation adjustments.
  - Minor chore or configuration updates.
  *For Fast-Track tasks, you may edit files directly, run tests, and commit changes without creating `spec.md`/`plan.md` or waiting for Gate approvals.*

- **MANDATORY SPEC-KIT WORKFLOW:**
  - New features, capabilities, or major enhancements.
  - Architectural, schema, or data model changes.
  - Complex refactoring or tasks impacting multiple services/modules.
  *For these tasks, strictly follow the workflow and approval gates below.*

### Absolute prohibition

Until BOTH approval gates below are passed (for Mandatory Spec-Kit tasks), you MUST NOT:
- create, edit, rename, or delete any file outside `specs/` (source code,
  tests, configs, Docker files, CI, dependencies, `CHANGELOG.md`, docs);
- run commands that modify the repository (formatters with auto-fix,
  migrations, installs, git commits, branch merges);
- output production code in chat as a "preview" or "draft implementation".

Allowed before the gates are passed: reading code, running read-only
inspection commands, asking clarifying questions, and writing ONLY the
planning artifacts listed below.

### Required cycle (strictly sequential, no skipping, no reordering)

Use the Spec-Kit skills located under `.opencode/skills/` (`speckit-constitution`,
`speckit-specify`, `speckit-clarify`, `speckit-plan`, `speckit-tasks`,
`speckit-analyze`, `speckit-implement`).

1. **Request.** The user describes the task.
2. **Specify.** Use `speckit-specify` (and `speckit-clarify` if anything is
   ambiguous) to produce `spec.md`: goal, scope, requirements, edge cases,
   acceptance criteria.
   STOP. Present a short summary and ask for approval.
3. **GATE 1: spec approval.** Wait for an explicit approval of `spec.md`
   from the user. If the user requests changes, update `spec.md` and ask
   again.
4. **Plan.** Use `speckit-plan` and `speckit-tasks` to produce `plan.md`
   (architecture, affected modules, data/API changes, testing strategy)
   and a step-by-step roadmap (`tasks.md`) with concrete ordered steps.
   Optionally run `speckit-analyze` to check consistency between files.
   STOP. Present a short summary and ask for approval.
5. **GATE 2: plan approval.** Wait for an explicit approval of `plan.md`
   and the roadmap. If the user requests changes, update the files and ask
   again.
6. **Implement.** Only now use `speckit-implement`, executing the roadmap
   step by step on a new branch from `main`, exactly as approved.

### What counts as approval

- Only an explicit message such as "approved spec" / "approved plan"
  (or "аппрув спека" / "аппрув плана") counts.
- Silence, "ok", "continue", "looks fine", or a question do NOT count.
- Approval of the spec is NOT approval of the plan. Each gate needs its
  own separate approval, given after the corresponding file is final.
- Any change to an approved file invalidates its approval and all later
  approvals; ask again.

### Violations and pressure

- If the user asks to "just do it" or to skip a step, do not comply.
  Remind them of the gate and ask which approval is still missing.
- If during implementation you find that the plan is wrong or incomplete,
  STOP, update `spec.md` / `plan.md`, and request approval again before
  continuing.
- Before every response that touches files on Spec-Kit tasks, state the current phase:
  `Phase: Specify | Plan | Implement` and which gates are passed.

## Before Making Changes

- **Spec-Driven Development:** For all new features, architecture changes, or complex tasks, follow the Mandatory Workflow Gate above. Align every spec and plan with the project's constitution (`speckit-constitution`).
- Inspect the relevant code, tests, and documentation. Follow existing
  architecture and conventions; do not assume a structure that is not present.
- Keep changes focused. Avoid unrelated edits, unnecessary dependencies, and
  speculative abstractions.
- Preserve existing behavior unless the requested change explicitly changes it.

## Implementation Quality

- **Plan Execution:** When implementing changes based on an approved `plan.md`, execute the tasks incrementally. Ensure code adjustments directly map to the approved architecture in `spec.md`.
- Write clear, readable, maintainable code with appropriate types, validation,
  and explicit error handling. Avoid silent failures and broad exception
  handling.
- Keep business logic separate from transport layers. Follow the existing
  patterns for the synchronous Flask application, asynchronous FastAPI API,
  CLI, shared services, models, and schemas.
- Never hardcode secrets, credentials, tokens, or private keys. Load runtime
  configuration from the project's environment-based configuration; use
  placeholders in examples and never expose secret values in code, logs, or
  documentation.
- Add or update tests for every new feature and for bug fixes. Include relevant
  edge cases and regression coverage; do not consider a feature complete
  without tests.
- Strictly use absolute imports for all local modules. Never use relative imports with dots (e.g. from .module import ... is forbidden).

## Documentation and Changelog

- Before finishing every work session that changes the repository, review the
  relevant documentation and update it to reflect the code, behavior,
  configuration, API, or operational changes made in that session.
- **Artifact Maintenance:** Ensure any updates to application logic or structural scopes are properly backported into the project's dynamic `spec.md` or design documents to avoid specification drift.
- Record every repository change in the root `CHANGELOG.md` under an
  appropriate dated section. Keep entries concise, accurate, and consistent
  with the existing format. Do not add an entry when the session made no
  repository changes.
- Write all documentation, changelog entries, code comments, and technical
  summaries in clear, concise, natural English.

## Branches, Merges, and Commits

- Develop each feature, refactor, or fix on a new branch created from `main`;
  never make these changes directly on `main`. Use descriptive names such as
  `feature/<name>`, `refactor/<name>`, or `fix/<name>`.
- After validation, merge the completed branch into `main` with an explicit
  `--no-ff` merge commit so the branch history is preserved. Do not rewrite
  published history.
- Merge branches into main only with `--no-ff` to preserve the branch history. Avoid rebasing or squashing commits on published branches.
- Use Conventional Commits for every commit, in English and in the imperative
  mood. Use the project's permitted types (`feat`, `fix`, `docs`, `style`,
  `refactor`, `perf`, `tests`, `chore`, `release`) and keep the first line
  under 50 characters with no trailing period.

## Verification

- When the user writes `check-styles`, run `ruff check .` from the repository
  root to check Python code style across the project. Do not apply automatic
  fixes unless requested; report the command and its result.
- Run the smallest relevant tests and checks for the change, then run the full
  applicable suite when practical. For application changes, use the project's
  documented test setup and the CI-equivalent command:

  ```bash