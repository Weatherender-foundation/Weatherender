# Releasing Weatherender

## Prerequisites

- You must be a maintainer with push access to `main` and the ability to create tags.
- CI must be green on `main`.
- Version in `pyproject.toml` must match the tag you are about to create.

---

## Release process

1. **Update version**
   Edit `pyproject.toml` → `version = "X.Y.Z"`.

2. **Update changelog**
   Add a new section at the top of `docs/CHANGELOG.md` with the date and notable changes.

3. **Commit**
   ```bash
   git add pyproject.toml docs/CHANGELOG.md
   git commit -m "chore: release vX.Y.Z"
   git push origin main
   ```

4. **Create and push the tag**
   ```bash
   git tag vX.Y.Z
   git push origin vX.Y.Z
   ```

5. **Automated publishing**
   The workflow `.github/workflows/publish-pypi.yml` will:
   - run the full test suite
   - verify that the tag matches `pyproject.toml`
   - build the wheel/sdist
   - publish to PyPI via Trusted Publishing (OIDC)

   GHCR image is published on every push to `main` by `publish-github.yml` (`latest` + commit SHA).

6. **GitHub Release**
   Optionally create a GitHub Release from the tag and paste the changelog section.

---

## Versioning policy

We follow a practical SemVer-inspired scheme:

- **MAJOR** — breaking API or major architectural changes
- **MINOR** — new features (backward-compatible)
- **PATCH** — bug fixes, documentation, dependency updates

Current series: **2.x**

---

## Rollback

If a bad version was published:

1. Yank the version on PyPI if necessary.
2. Revert the commit on `main` (prefer `git revert`, not force-push).
3. Publish a new patch version with the fix.
