# APK build workflow — frozen-baseline contract

`.github/workflows/build-native-apk.yml` used to be unsafe for a preserved release:

| Before | Risk |
|---|---|
| `push: branches: ["arena/354c3e44-becettitools", "main"]` | every push to `main` started a build |
| `cp … ./BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` | the fresh build was written **over the frozen v1.0.0 filename** in the repo root |
| `sha256sum … > ./BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256` | the published checksum was overwritten too |
| `stefanzweifel/git-auto-commit-action` with `file_pattern: "BECETTI-TOOLS*.apk*"` | the replaced binary was **committed and pushed** automatically |
| `git push origin HEAD:arena/…` on failure | even a failed build wrote to the repository |
| artifact name `BECETTI-TOOLS-v1.0.0-debug-native` | not tied to the version being built |

The v1.0.0 APK (`SHA-256 460235a2…22632`) is the approved, published baseline. It must never be
rebuilt in place, and no automation may replace it.

## What the workflow does now

**Triggers**

| Event | Behaviour |
|---|---|
| Push to any branch, including `main` | **Nothing runs.** There is no `push.branches` trigger. |
| Push of a tag matching `v*` | Guard → build → optional release attach (versioned artifact). |
| Manual `workflow_dispatch` | Same, with `variant` (`debug`/`release`) and an opt-in `publish_release` input. |

**Jobs**

1. **`guard`** — read-only. Verifies the frozen APK's SHA-256 against the pinned value *and*
   against the committed `.sha256` file; reads `versionName`/`versionCode` from
   `app/build.gradle.kts` (read-only); computes a versioned artifact name; refuses to continue if
   that name would collide with the frozen filename; flags `is_frozen_baseline=true` when
   `versionName == 1.0.0`.
2. **`build`** — `contents: read`. JDK + Android SDK, the existing no-WebView check, then
   `assembleDebug`/`assembleRelease`. The APK is copied to `dist/BECETTI-TOOLS-v<version>-<variant>-<shortSha>.apk`
   with a matching `.sha256`, uploaded as a workflow artifact, and the frozen file is re-checked
   afterwards (checksum + `git status`). Nothing is written outside `dist/` and `/tmp`.
3. **`publish`** — `contents: write`, and only when the baseline is **not** frozen and the ref is a
   tag (or `publish_release` was set). Attaches the versioned APK to an **existing** release and
   skips any asset whose name already exists — `gh release upload` is called **without
   `--clobber`**, so an existing asset can never be replaced. No release is auto-created.

## Protections, in order

1. No branch trigger → a push to `main` cannot start a build at all.
2. No `git commit`/`git push`/auto-commit step anywhere → the workflow cannot write to the repo.
3. Output path is always `dist/<versioned name>`; a hard check aborts if the basename equals
   `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`.
4. The frozen APK's checksum is asserted **before** and **after** the build, and the worktree is
   checked for modifications to it.
5. `versionName == 1.0.0` marks the run as frozen-baseline: the artifact is explicitly a
   CI-only validation build and the `publish` job is skipped.
6. `guard` fails the whole run if the frozen APK is missing or its digest has drifted.

## Producing a future versioned APK

1. Bump `versionName`/`versionCode` in a reviewed change to `app/build.gradle.kts` (never in this
   workflow).
2. Merge, then either push a tag (`v1.1.0`) or run the workflow manually and pick `variant`.
3. Download the artifact `BECETTI-TOOLS-v1.1.0-debug-<sha>` from the run — or, if a release for
   that tag already exists, let the `publish` job attach it.
4. Update the landing page and release notes from the **new** artifact's checksum; the v1.0.0
   download link and checksum on the site stay untouched.

## Things this workflow will not do

- Rebuild, re-sign or overwrite `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`.
- Commit APKs back into the repository (future binaries live as workflow artifacts / release
  assets, not as Git objects).
- Publish anything without a tag or an explicit manual input.
- Replace an existing release asset.
