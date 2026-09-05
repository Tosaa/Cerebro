# detekt rule review

A running review of detekt's rule set for Cerebro, worked through in batches of ten.
Started 2026-09-05 against **detekt 1.23.8** (`gradle/libs.versions.toml`).

The point is to decide deliberately which rules are on, rather than inheriting
`buildUponDefaultConfig` and never looking at the rest. Each batch records what a
rule does, whether it is worth enabling *here*, and what can be tuned.

## How to resume

Pick the next unreviewed batch from the queue at the bottom, review those ten, move
them into a "Reviewed" section with a verdict, and update the progress line.

**Progress: 20 of 115 inactive rules reviewed.**

---

## Inventory

detekt 1.23.8 as configured here ships **287 rules** across ten rule sets:

| Rule set | Rules | Rule set | Rules |
| --- | ---: | --- | ---: |
| style | 87 | complexity | 15 |
| formatting (ktlint) | 77 | exceptions | 14 |
| potential-bugs | 35 | comments | 10 |
| naming | 21 | coroutines | 7 |
| empty-blocks | 15 | performance | 6 |

- **172 are active by default** — these are already running, via `buildUponDefaultConfig = true`.
- **115 are inactive by default** — the subject of this review.
- `config/detekt/detekt.yml` currently tunes **9** rules and enables **none** that were off.

## The catch: 67 rules do nothing here

**67 of the 287 rules require type resolution**, and detekt runs without it in this
project — 1.23.8 embeds `kotlin-compiler-embeddable:2.0.21` against the project's
Kotlin 2.4.10, so only parsing happens, not type checking.

Those 67 rules are inert: enabling them changes nothing, silently. **34 of the 115
inactive rules fall in this group**, so only **81 inactive rules are actually
enableable**. Each batch below marks them.

This also means some *active* rules under-report. `UnusedPrivateMember` is the one
already observed misfiring — it cannot see `@Composable` call sites, which is why
`HomeScreen.kt:79` sits in the baseline as a false positive.

Revisit when the `dev.detekt` 2.0 coordinates leave alpha.

## Universal options

Every rule accepts these, whether or not the default config lists them:

- `active` — on/off.
- `excludes` / `includes` — glob paths, e.g. `['**/test/**']`.
- `ignoreAnnotated` — skip anything carrying the given annotation. **Not documented
  in the default config**, but it works on any rule; this project already relies on
  it for `LongMethod`, `FunctionNaming` and `MagicNumber` with `['Composable']`.

Rule-specific options are listed per rule below.

---

## Batch 1 — inactive correctness rules that would actually work

Chosen as the highest-value slice: everything correctness-adjacent that is off by
default *and* is not blocked by type resolution.

| # | Rule | Set | Verdict |
| --- | --- | --- | --- |
| 1 | `UnconditionalJumpStatementInLoop` | potential-bugs | **Enable** |
| 2 | `CastToNullableType` | potential-bugs | **Enable** |
| 3 | `MissingPackageDeclaration` | potential-bugs | **Enable** |
| 4 | `NotImplementedDeclaration` | exceptions | **Enable** |
| 5 | `UnnecessaryPartOfBinaryExpression` | performance | **Enable** |
| 6 | `GlobalCoroutineUsage` | coroutines | **Enable** (with a caveat) |
| 7 | `LateinitUsage` | potential-bugs | Enable to hold the line |
| 8 | `CognitiveComplexMethod` | complexity | Enable, needs tuning |
| 9 | `StringLiteralDuplication` | complexity | **Skip** |
| 10 | `ThrowingExceptionInMain` | exceptions | **Skip** — not applicable |

### 1. `UnconditionalJumpStatementInLoop` — potential-bugs
Flags a `break`, `continue` or `return` that always runs on the first iteration,
making the loop body run exactly once. Nearly always a real bug or dead logic.
**Config:** none beyond the universal options. **Verdict: enable.** No false-positive
risk, catches a genuine mistake.

### 2. `CastToNullableType` — potential-bugs
Flags `x as Foo?`, which is almost always a confusion with the safe cast `x as? Foo`.
The nullable-type cast still throws on a type mismatch; only the safe cast returns null.
**Config:** none beyond universal. **Verdict: enable.** Cheap, and the failure mode is
a surprise `ClassCastException`.

### 3. `MissingPackageDeclaration` — potential-bugs
Flags files with no `package` line. **Config:** `excludes`. **Verdict: enable.** Zero
noise in a normal source tree; costs nothing.

### 4. `NotImplementedDeclaration` — exceptions
Flags `TODO()`, which throws `NotImplementedError` at runtime. Stops a stub reaching
a release build. **Config:** none beyond universal. **Verdict: enable.** The codebase
has none today, so it starts clean and stays that way.

### 5. `UnnecessaryPartOfBinaryExpression` — performance
Flags redundant operands such as `a && a` or `a || true`, where part of the expression
cannot affect the result. Usually a copy-paste slip. **Config:** none beyond universal.
**Verdict: enable.**

### 6. `GlobalCoroutineUsage` — coroutines
Flags `GlobalScope.launch` / `GlobalScope.async`. Such coroutines outlive any lifecycle
and leak. **Config:** none beyond universal. **Verdict: enable** — but note it only
matches `GlobalScope` literally. It would **not** catch
`StrategyProvider.kt:94`'s `CoroutineScope(Dispatchers.IO).async { … }`, which creates
an unstructured scope that is never cancelled and carries the same hazard. Worth fixing
separately; the rule is not a substitute for that.

### 7. `LateinitUsage` — potential-bugs
Flags `lateinit var`, which trades compile-time null safety for a runtime
`UninitializedPropertyAccessException`. **Config:** `excludes`, `ignoreOnClassesPattern`.
**Verdict: enable to hold the line.** The codebase currently has zero `lateinit`, so
enabling costs nothing today and prevents drift. Note this is an opinionated rule —
`lateinit` is idiomatic in Activity/Fragment code, so if the app grows in that
direction, `ignoreOnClassesPattern` is the escape hatch.

### 8. `CognitiveComplexMethod` — complexity
Measures *cognitive* complexity — nesting-weighted — rather than cyclomatic branch
counting, so it tracks how hard code is to read rather than how many paths it has.
**Config:** `threshold` (default 15). **Verdict: enable, but tune.** Compose screens
will need `ignoreAnnotated: ['Composable']`, exactly as `LongMethod` already does,
or declarative UI trees will trip it for no benefit. Set the threshold after seeing
the first run.

### 9. `StringLiteralDuplication` — complexity
Flags the same string literal repeated more than `threshold` times (default 3).
**Config:** `threshold`, `excludeStringsWithLessThan5Characters`, `ignoreStringsRegex`,
`ignoreAnnotation`, `excludes`. **Verdict: skip.** It exists to push literals into
constants, which collides directly with the standing decision that Cerebro is
English-only and keeps UI strings inline in the Composables. Enabling it would
re-litigate a settled choice.

### 10. `ThrowingExceptionInMain` — exceptions
Flags a `main()` function that throws. **Config:** none beyond universal.
**Verdict: skip — not applicable.** An Android app has no `main()`; the rule can never
fire here. Listed only so it is not revisited.

---

## Batch 2 — naming (all 6 usable) and the documentation question (4 of 10)

`comments` has 10 usable rules; the four that decide policy are here, the remaining
six are in batch 3.

| # | Rule | Set | Verdict |
| --- | --- | --- | --- |
| 11 | `LambdaParameterNaming` | naming | **Enable** |
| 12 | `FunctionMinLength` | naming | Optional |
| 13 | `FunctionMaxLength` | naming | Optional |
| 14 | `ForbiddenClassName` | naming | **No-op at defaults** |
| 15 | `VariableMinLength` | naming | **No-op at defaults** |
| 16 | `VariableMaxLength` | naming | **Skip** |
| 17 | `OutdatedDocumentation` | comments | **Enable** |
| 18 | `UndocumentedPublicClass` | comments | **Skip** |
| 19 | `UndocumentedPublicFunction` | comments | **Skip** |
| 20 | `UndocumentedPublicProperty` | comments | **Skip** |

### Two rules that do nothing at their defaults

Worth knowing as a general trap: a rule can be enabled and still never fire.

- **`ForbiddenClassName`** — bans class names containing given substrings, but
  `forbiddenName: []` is empty by default. Turning it on changes nothing until the
  list is filled. Only worth enabling alongside an actual taboo, e.g.
  `forbiddenName: ['Manager', 'Util', 'Helper']`.
- **`VariableMinLength`** — `minimumVariableNameLength: 1`, so no name can violate it.
  It would need raising to 2 or 3 to mean anything, and then it starts objecting to
  `i` and `it`, which are idiomatic. Leave it off.

### 11. `LambdaParameterNaming` — naming
Requires lambda parameters to match `[a-z][A-Za-z0-9]*|_` — camelCase, or `_` for
deliberately unused. **Config:** `parameterPattern`. **Verdict: enable.** Low noise,
and the `_` allowance means it does not fight idiomatic code.

### 12. `FunctionMinLength` — naming
Flags function names shorter than `minimumFunctionNameLength` (default 3), catching
`f`, `fn`. **Config:** `minimumFunctionNameLength`. **Verdict: optional.** Harmless
here — Composable names are long — but it catches little in practice.

### 13. `FunctionMaxLength` — naming
Flags names longer than `maximumFunctionNameLength` (default 30). **Config:**
`maximumFunctionNameLength`. **Verdict: optional, leaning skip.** The limit is
arbitrary, and descriptive Composable names are a feature rather than a defect.

### 16. `VariableMaxLength` — naming
Flags variable names longer than 64 characters. **Config:**
`maximumVariableNameLength`. **Verdict: skip.** Effectively never fires; pure config
noise.

### 17. `OutdatedDocumentation` — comments
Checks that KDoc `@param` / `@property` tags actually match the declaration —
right names, right count, and optionally right order. **Config:**
`matchTypeParameters` (true), `matchDeclarationsOrder` (true),
`allowParamOnConstructorProperties` (false). **Verdict: enable.**

This is the one documentation rule worth having. It only fires where KDoc already
exists, so with zero KDoc in the codebase today it costs nothing — but it means any
documentation written later cannot silently drift out of sync with its signature.
Consider `allowParamOnConstructorProperties: true` if `@param` on constructor
properties is preferred over `@property`.

### 18–20. `UndocumentedPublicClass` / `UndocumentedPublicFunction` / `UndocumentedPublicProperty` — comments
Require KDoc on every public class, function and property.
**Config (all three):** `excludes` (already excludes test source sets),
plus `searchInNestedClass` / `searchInInnerClass` / `searchInInnerObject` /
`searchInInnerInterface` / `ignoreDefaultCompanionObject` for the class rule, and
`searchProtectedFunction` / `searchProtectedProperty` for the other two.

**Verdict: skip all three.** Cerebro has **zero KDoc** in `app/src/main/java`, against
23 top-level public declarations and 24 `@Composable` functions — and Composables are
public by default. Enabling these would demand KDoc on essentially the whole UI layer
at once.

More fundamentally, these rules exist for **libraries with a public API consumed by
strangers**. Cerebro is an application: its "public" surface is public only because
Kotlin defaults that way, not because anyone calls it from outside. The cost is a
large mechanical documentation burden; the benefit is close to zero.

The pairing to remember: **do not mandate documentation, but do keep whatever
documentation gets written honest** — skip 18–20, enable `OutdatedDocumentation`.

---

## Queue

Remaining inactive rules to review, in suggested order:

| Batch | Contents | Count |
| --- | --- | ---: |
| 3 | `comments` remainder: AbsentOrWrongFileLicense, CommentOverPrivateFunction, CommentOverPrivateProperty, DeprecatedBlockTag, EndOfSentenceFormat, KDocReferencesNonPublicProperty | 6 |
| 4 | `complexity` remainder: ComplexInterface, LabeledExpression, MethodOverloading | 3 |
| 5–8 | `style` (31 usable) — the largest block, opinionated | 31 |
| 9–10 | `formatting` / ktlint (21 usable) | 21 |
| — | The 34 inactive rules blocked by type resolution, recorded but not enableable | 34 |
