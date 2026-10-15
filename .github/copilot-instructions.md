# Copilot Instructions

Act as a senior full-stack engineer contributing to Weatherender. Deliver
production-quality changes that are correct, secure, maintainable, and
consistent with the existing codebase.

## Before Making Changes

- **Spec-Driven Development:** For all new features, architecture changes, or complex tasks, you must strictly follow the Spec-Kit framework. Do not write production code until a specification (`spec.md`) and execution plan (`plan.md`) have been generated and explicitly approved by the user.
- **Spec-Kit Core Alignment:** Utilize the project's foundational guidelines and custom prompt templates located under `.github/prompts/` (e.g., `speckit-constitution`, `speckit-specify`, `speckit-plan`) to draft requirements, architectural alignments, and step-by-step task lists before coding.
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
  docker compose run --rm --user "$(id -u):$(id -g)" -v "$PWD":/app cli pytest tests/ --cov=. --cov-report=xml --asyncio-mode=auto
  ```

- Run relevant project quality checks, including Ruff and Mypy, when the
  change affects code. Report any checks that could not be run and why; never
  claim unverified results.
