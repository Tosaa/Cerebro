# Project setup backlog

Generated from a project-setup audit on **2026-09-04**, and **revised the same day** once the
open questions were answered.

This document is deliberately **not merged into `main`** — it is a parked reference so the
outstanding work is written down somewhere rather than living only in a chat transcript.

Most of the original audit has since been resolved. What follows is: what was decided and why,
then the items that are genuinely still open.

---

## Decisions taken

Recorded so they are not re-opened without a reason.

| Question | Decision | Where it landed |
| --- | --- | --- |
| Java 11 or 17? | **Java 17** | `app/build.gradle.kts` `compileOptions` |
| Localization a goal? | **No — English-only.** UI strings stay inline in the Composables | lint `disable += "HardcodedText"` |
| Lint gating | **`abortOnError` + `warningsAsErrors`**, with justified exemptions | `lint { }` in `app/build.gradle.kts` |
| detekt config | **Fresh 57-line config**, not the salvaged 789-line one | `config/detekt/detekt.yml` |
| Old PR #1 (`feature/CI_CD`) | **Closed unmerged.** Branch `origin/feature/CI_CD` still exists if anything is ever wanted from it | — |
| License | **Apache-2.0**, `Copyright 2026 Tosaa` — chosen over MIT for the patent grant and to match the AndroidX/Kotlin/Compose stack | `LICENSE` |

Also shipped from the original audit: Gradle build-performance flags and wrapper SHA-256 pinning,
manifest cleanup (including a real `roundIcon` bug fix), CI hardening plus Dependabot, and
`.gitignore` / `.editorconfig` consolidation.

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

### Measured, not assumed: `jvmToolchain` does not control the target here

When the Java target was moved to 17, `kotlin { jvmToolchain(17) }` was tested directly under
AGP 9.4.0. The extension **is** registered — AGP's built-in Kotlin applies `KotlinBaseApiPlugin`,
giving `:app` a `KotlinAndroidProjectExtension` — and adding it configures and builds with no
warning at all. That is exactly what makes it dangerous.

The decisive test: `compileOptions` was set back to `VERSION_11` while leaving
`jvmToolchain(17)` in place. The result was still bytecode **major version 55** (Java 11). The
toolchain only selects the JDK that *runs* the compiler; AGP overrides `jvmTarget` from
`compileOptions.targetCompatibility` regardless. So `jvmToolchain` here would be decorative and
actively misleading, and it was deliberately not adopted.

Verify any future target change with `javap -v <class> | grep major` (61 = Java 17), never with a
green build alone. Note the class output path under AGP 9 is
`app/build/intermediates/built_in_kotlinc/{debug,release}/compile{Debug,Release}Kotlin/classes/`,
**not** `app/build/tmp/kotlin-classes/`.

---

## Still open

### 1. R8 and resource shrinking — in progress

The only item from the original audit still being actively worked. `app/build.gradle.kts` has
`isMinifyEnabled = false` on `release` and no `isShrinkResources`, so `app/proguard-rules.pro`
(100% commented-out template) is dead configuration.

**The risk:** the `@Serializable` models in
`app/src/main/java/redtoss/creativity/cerebro/data/` are deserialized at runtime from
`app/src/main/assets/strategies.json`. Wrong keep rules mean R8 strips the generated
`$$serializer` members and the app fails when loading data. `assembleRelease` succeeding proves
nothing — the failure only appears on a device, and CI has no emulator.

**Decided approach:** enable R8 + `shrinkResources`, add kotlinx.serialization keep rules, add a
`debug` build type with an `applicationIdSuffix` so both variants can coexist, and set
`signingConfig = signingConfigs.getByName("debug")` on `release` **as an interim measure** so the
APK is installable at all. That last line is not shippable to Play and must be replaced before
any real distribution.

Verification is manual: install the release build and walk Home → Library → Category → Strategy.

### 2. Real release signing

Follows directly from the above. There is no `signingConfigs` block; release is debug-signed as a
stopgap. Proper signing needs a keystore, credentials read from a gitignored
`keystore.properties` with an env-var fallback for CI, and somewhere safe to keep the keystore.
Blocking only if distribution becomes a goal.

### 3. detekt's 121-entry baseline

`config/detekt/baseline.xml` freezes 121 findings so the build could go green without editing
source. Roughly 106 are mechanically auto-correctable formatting. **About 15 are real code
issues** and deserve individual attention:

- `data/StrategyProvider.kt:32` — a `catch (e: Exception)` that **swallows the exception
  entirely**. The most substantive of the set.
- Two further generic `catch (e: Exception)` blocks in the same file.
- 6 dead imports, 9 of them in `MainActivity.kt` alone; 2 dead private members.
- `ui/screens/StrategyScreen.kt:66` — `(1..4).forEach` where a plain loop reads better.

The baseline shrinks as these are fixed; it is not meant to be permanent.

### 4. detekt runs without type resolution

detekt 1.23.8 is the newest stable under the `io.gitlab.arturbosch.detekt` coordinates, but it
embeds `kotlin-compiler-embeddable:2.0.21` while the project is on Kotlin 2.4.10. It works only
because analysis runs **without type resolution** — detekt parses but does not type-check.

Two consequences: the subset of detekt rules that require type information is not running at all,
and genuinely post-2.0 Kotlin syntax would fail to parse. The successor `dev.detekt` 2.0.0
coordinates are still alpha. Worth revisiting when 2.0 stabilises.

### 5. Dead and questionable assets

Found incidentally while writing the README; none acted on.

- **`app/src/main/assets/improvement_strategies.json` is loaded by nothing.** Only
  `strategies.json` is read by `data/StrategyProvider.kt`. Either dead weight in the APK or an
  unfinished feature.
- **`ui/theme/` appears dead.** `MainActivity.kt` imports `ui.theme.CerebroTheme` (line 18) but
  wraps the app in `ui.theme2.CosyAppTheme` (line 27). Both packages carry parallel
  `Color.kt` / `Theme.kt` / `Type.kt`. Confirm before deleting.
- **`app/src/main/res/values/font_certs.xml` re-declares** the
  `com_google_android_gms_fonts_certs{,_dev,_prod}` arrays that `ui-text-google-fonts` already
  ships and marks private — this is what Lint's `PrivateResource` flags. Likely deletable, but
  font resolution must be verified afterwards.

### 6. Lint issues demoted to `informational`

Kept visible in the lint report rather than hidden, each needing a `res/` change:
`PrivateResource` (see above), `UnusedResources` (12 leftover template resources),
`IconDuplicates` (`ic_launcher` and `ic_launcher_round` are byte-identical),
`MonochromeLauncherIcon` (no themed-icon layer — a small real gap on Android 13+), `IconLocation`.

### 7. Smaller items — batchable

- **No `[bundles]` in `gradle/libs.versions.toml`**, despite obvious groupings — the Compose UI
  and androidTest artifacts are each declared item by item in `app/build.gradle.kts`.
- **Tests are template-only.** `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt` are the
  unmodified Android Studio stubs. Real coverage is zero, and there is no JaCoCo or Kover. This
  is precisely what makes item 1's verification manual.
- **No `values-night/themes.xml`.** `res/values/themes.xml` defines a single `Theme.Cerebro`
  parented off the framework `android:Theme.Material.Light.NoActionBar`, so the pre-Compose
  window is permanently light regardless of system dark mode.
- **Unused template colors** — `purple_200/500/700`, `teal_200/700` in `res/values/colors.xml`.
- **No baseline profile.** Startup performance is unmeasured.
- **Four Dependabot PRs are open and unreviewed** (#8–#11), all major-version bumps of GitHub
  Actions.
