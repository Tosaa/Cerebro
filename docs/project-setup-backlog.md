# Project setup backlog

Generated from a project-setup audit performed on **2026-09-04**.

The items below are **unordered and undecided**. This is not a task list and nothing here has an
agreed answer yet. Each entry records the current situation, the decision that still has to be
made, and the risk of getting it wrong. Anyone picking one up should expect to make a judgement
call — and, in several cases, to run an experiment first.

Four mechanical fixes from the same audit are already in flight as separate pull requests
(Gradle build performance, manifest cleanup, CI hardening, repo hygiene). They are deliberately
not described here; do not duplicate them.

---

## Already settled — please do not re-litigate

These were checked during the audit and found to be correct. A future auditor should leave them
alone rather than "fixing" them.

- **Version catalog is fully adopted.** `gradle/libs.versions.toml` supplies 100% of dependencies
  and plugins. There are zero hardcoded Maven coordinates in `build.gradle.kts` or
  `app/build.gradle.kts`.
- **Repository resolution is centralised.** `settings.gradle.kts` sets
  `repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)` inside
  `dependencyResolutionManagement`, so modules cannot declare their own repositories.
- **`gradle.properties` flags are correct**: `android.useAndroidX=true`,
  `android.nonTransitiveRClass=true`, `kotlin.code.style=official`.
- **Manifest is modern.** `namespace` is declared in Gradle (`app/build.gradle.kts:8`), not as a
  legacy `package` attribute in `app/src/main/AndroidManifest.xml`. `android:exported="true"` is
  present and correct on the launcher activity, and the app declares **zero** permissions.
- **Compose setup is the correct Kotlin 2.x pairing**: the Compose BOM (`androidx-compose-bom`)
  plus the `org.jetbrains.kotlin.plugin.compose` plugin (`libs.plugins.compose.compiler`). No
  standalone `composeOptions` compiler-extension version is needed, and none should be added.
- **No secrets are committed.** `local.properties` is gitignored and untracked; the working copy
  contains only `sdk.dir`. No keystores, no `google-services.json`.

---

## Trap: AGP 9 built-in Kotlin — the missing Kotlin plugin is deliberate

**Read this before touching any build file.**

Commit `ebd734b` ("Update all dependencies to latest stable") **intentionally removed** the
`org.jetbrains.kotlin.android` plugin from both `build.gradle.kts` and `app/build.gradle.kts`,
and removed the `kotlinOptions { jvmTarget = ... }` block along with it.

The reason is recorded in that commit message: AGP 9 compiles Kotlin itself, and the standalone
Kotlin Android plugin is incompatible with AGP 9's new DSL. Under the built-in Kotlin support,
`jvmTarget` is **derived from `compileOptions.targetCompatibility`** rather than set separately.

So the current state — no `kotlin.android` plugin, no `jvmTarget`, no `kotlinOptions` — is the
correct post-migration shape, not an oversight. An auditor or tool that flags "Kotlin plugin
missing" or "jvmTarget not configured" and re-adds them would **revert a deliberate migration**
and break the build. The only Kotlin plugins that remain, correctly, are
`org.jetbrains.kotlin.plugin.serialization` and `org.jetbrains.kotlin.plugin.compose`.

This trap also bears directly on items 1 and 3 below.

---

## Open items

### 1. Java 11 vs Java 17 as the compile target

**Current situation.** `app/build.gradle.kts:30-33` sets both `sourceCompatibility` and
`targetCompatibility` to `JavaVersion.VERSION_11`. Per the AGP 9 note above, Kotlin's `jvmTarget`
follows from `targetCompatibility`, so this single setting governs both languages. The previous
value was `1.8`; commit `ebd734b` moved it to 11 as part of the AGP 9 upgrade, not as a considered
language-level decision.

Moving to 17 looks viable on paper: `minSdk` is 24, so desugaring constraints are mild, and both
the local JDK and the CI JDK are already 17 (`.github/workflows/android.yml` sets up
`java-version: '17'`).

**Decision needed.** Whether to stay on 11 or move to 17 — and, if moving, whether a Gradle
toolchain declaration (`kotlin { jvmToolchain(17) }`) is even *registered* under AGP 9's built-in
Kotlin, given the standalone Kotlin plugin is not applied and the `kotlin { }` extension may
therefore not exist in this build. That question cannot be answered from documentation alone; it
needs an experiment on a scratch branch that inspects the actual compile task configuration.

**Risk / blast radius.** Low-to-moderate, but build-wide. A wrong toolchain declaration either
fails configuration outright (loud, safe) or is silently ignored (quiet, misleading — the build
appears configured for 17 while still emitting 11 bytecode). The silent case is the dangerous
one, so any change here must be verified against actual class-file major versions rather than
against the build script text.

### 2. R8, resource shrinking, and the missing debug build type

**Current situation.** The release build type in `app/build.gradle.kts:24-29` has
`isMinifyEnabled = false` and no `isShrinkResources` setting at all, while still passing
`proguard-rules.pro` to `proguardFiles`. `app/proguard-rules.pro` is the untouched Android Studio
template — every substantive line is commented out — so it is dead configuration that looks live.

There is also **no `debug { }` build type block**, meaning no `applicationIdSuffix` and no
`versionNameSuffix`. Debug and release builds share one application ID and cannot be installed
side by side on a single device.

**Decision needed.** Whether to enable R8 and resource shrinking for release at all, and — if so —
what the verification path is, given there is no automated device testing anywhere in the project.
Separately, whether a debug suffix is wanted.

**Risk / blast radius.** This is the highest-risk item in the backlog. The models in
`app/src/main/java/redtoss/creativity/cerebro/data/` are `@Serializable` (`Strategy.kt:8`,
`Category.kt:6`) and are deserialized **at runtime** from the bundled assets
`app/src/main/assets/strategies.json` and `app/src/main/assets/improvement_strategies.json`
(loaded via `assetManager.open(...)` in `data/StrategyProvider.kt:25`). Enabling R8 without
correct kotlinx.serialization keep rules breaks JSON parsing.

The trap is that **`assembleRelease` succeeding proves nothing**: the failure surfaces only when
the app parses assets on a device, and CI (`.github/workflows/android.yml`) has no emulator. Any
decision to enable shrinking has to arrive bundled with a decision about how it gets tested.

### 3. Static analysis, and what to do with open PR #1

**Current situation.** No static analysis is wired into the build today. There is no detekt,
ktlint, or Spotless configuration anywhere in the repository, and no `config/` directory — despite
commit `6c6ca65` being titled "Fix potential detekt warnings" and despite `@Suppress` annotations
naming detekt rules being present in the source (for example `@Suppress("MagicNumber")` at
`app/src/main/java/redtoss/creativity/cerebro/ui/screens/HomeScreen.kt:28` and
`ui/screens/StrategyScreen.kt:47`). Those suppressions currently suppress nothing, because
nothing runs.

Meanwhile **PR #1** (`feature/CI_CD`, "Try to add multiple jobs depending on each other", last
touched November 2024) is still open. It adds detekt 1.23.7, a 789-line `config/detekt/detekt.yml`,
and a multi-job CI workflow.

Two things make it awkward to merge as-is:

- Its base predates the AGP 9 migration. It still applies `libs.plugins.kotlin.android` in both
  `build.gradle.kts` and `app/build.gradle.kts` — the plugin `main` has since deliberately removed
  (see the trap section above). It will not rebase cleanly.
- Its CI half is separately unsound: both cache steps use `actions/cache@v3` with `path: .`,
  caching the entire working directory. It is in any case superseded by the CI hardening PR now
  in flight.

**Decision needed.** Whether to salvage only the `detekt.yml` onto a fresh branch at a current
detekt version and close PR #1, or to rebase PR #1 wholesale. And, if detekt is adopted at all,
whether it gates CI or merely reports.

**Risk / blast radius.** Moderate. A 789-line config written against detekt 1.23.7 will not map
cleanly onto a current release, and turning on a full rule set against a never-linted codebase
produces a large first-run failure list. Whichever path is chosen, the first run should be treated
as a discovery exercise rather than a merge blocker.

### 4. Android Lint gating

**Current situation.** `app/build.gradle.kts` contains no `lint { }` block. There is no
`abortOnError`, no `warningsAsErrors`, and no `lint-baseline.xml`. Lint therefore runs only as a
side effect of `./gradlew build` with stock defaults, which is what CI invokes
(`.github/workflows/android.yml`).

**Decision needed.** Whether to gate on lint at all, and at what strictness. Turning on
`warningsAsErrors` first forces a prior choice: commit a `lint-baseline.xml` to freeze the existing
issues and gate only new ones, or fix the existing issues outright before enabling.

**Risk / blast radius.** Contained, but coupled. The largest single category of existing lint
findings is item 5 below, so this decision cannot be made independently of the localization
decision. A baseline file also carries a maintenance cost of its own — it hides issues rather than
resolving them, and tends to go stale.

### 5. Hardcoded UI strings and localization

**Current situation.** `app/src/main/res/values/strings.xml` holds only three entries
(`app_name`, `app_motto`, `about_screen_app_description`), while 16+ user-visible literals are
hardcoded directly in Compose code:

- `app/src/main/java/redtoss/creativity/cerebro/ui/screens/AppBar.kt:45-59` — the five dropdown
  items "About", "Library", "Settings", "Unlock all", "New Strategy" (plus the `"Menu icon"`
  content description at line 65)
- `ui/screens/HomeScreen.kt:45,81` — "Categories", "Strategy of the day:"
- `ui/screens/StrategyScreen.kt:59,60,114,122-144` — "Edit", "Finished", "Preview", the four
  `"Step $index: ..."` editor labels and the "Category" fallback; plus the tab-title strings at
  lines 159-163
- `ui/screens/AboutScreen.kt:17` — "About"
- English `contentDescription` values in `ui/layouts/cards/StrategyCard.kt:32,46` and
  `ui/layouts/cards/StrategyPreviewCard.kt:38`

There are no `values-<locale>` folders at all — `app/src/main/res/` has a single `values/`
directory.

**Decision needed.** Whether localization is actually a product goal for this app. That single
answer determines everything else: if it is, this is a substantial extraction refactor across
every screen plus a translation workflow; if it is not, this is a non-issue and the lint checks
that flag it should simply be turned off.

**Risk / blast radius.** The refactor itself is low-risk and mechanical, but it touches nearly
every UI file, so it conflicts with any concurrent UI work. The real cost is ongoing rather than
one-off: adopting localization means every future string goes through resources and translations
have to be kept current.

### 6. README and LICENSE

**Current situation.** Neither `README.md` nor `LICENSE` exists in the repository. There is no
description of the project anywhere in the tree.

**Decision needed.** The LICENSE choice is the maintainer's alone — it is a legal and distribution
decision, not a technical one, and nobody else should make it. Whether the project is intended to
be public, contributable, or published to an app store all feed into it.

**Note for whoever writes the README.** The `about_screen_app_description` string in
`app/src/main/res/values/strings.xml` already contains a full prose description of the app and
would serve as a starting point.

**Risk / blast radius.** None technically. Adding a license retroactively to a project that has
already accepted outside contributions is harder than doing it up front, so the cost of deferring
grows slowly over time.

### 7. Smaller items — batchable once the larger questions are settled

None of these are urgent on their own, and several depend on decisions above. Listed for the
record so they are not rediscovered from scratch:

- **No `[bundles]` in `gradle/libs.versions.toml`**, despite obvious groupings — the Compose UI
  artifacts and the androidTest artifacts are each declared item by item in
  `app/build.gradle.kts:44-64`.
- **Duplicated theme packages.** Both `app/src/main/java/redtoss/creativity/cerebro/ui/theme/` and
  `ui/theme2/` exist with parallel `Color.kt` / `Theme.kt` / `Type.kt`. `MainActivity.kt` imports
  *both* `ui.theme.CerebroTheme` (line 18) and `ui.theme2.CosyAppTheme` (line 19) but only uses
  `CosyAppTheme` (line 27), so `ui/theme/` appears to be dead — needs confirmation before any
  deletion.
- **Tests are template-only.** `app/src/test/java/redtoss/creativity/cerebro/ExampleUnitTest.kt`
  and `app/src/androidTest/java/redtoss/creativity/cerebro/ExampleInstrumentedTest.kt` are the
  unmodified Android Studio stubs. Real coverage is zero and there is no JaCoCo or Kover setup.
  This is what makes item 2's verification problem hard.
- **No `values-night/themes.xml`.** `app/src/main/res/values/themes.xml` defines a single
  `Theme.Cerebro` parented off the framework `android:Theme.Material.Light.NoActionBar`, so the
  pre-Compose window is permanently light regardless of the system dark-mode setting.
- **Unused template colors.** `app/src/main/res/values/colors.xml` still carries the generated
  `purple_200` / `purple_500` / `purple_700` and `teal_200` / `teal_700` entries, none of which
  are referenced.
- **No baseline profile.** There is no baseline-profile module or generator; startup performance
  is unmeasured.
- **Release builds are unsigned.** `app/build.gradle.kts` declares no `signingConfigs` block at
  all, so `assembleRelease` produces an unsigned APK. This becomes blocking only if distribution
  is ever a goal, which ties back to item 6.
