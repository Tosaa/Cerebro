# detekt rule review

A running review of detekt's rule set for Cerebro, worked through in batches of ten.
Started 2026-09-05 against **detekt 1.23.8** (`gradle/libs.versions.toml`).

The point is to decide deliberately which rules are on, rather than inheriting
`buildUponDefaultConfig` and never looking at the rest. Each batch records what a
rule does, whether it is worth enabling *here*, and what can be tuned.

## State of play

**Review complete: all 81 enableable inactive rules assessed (81 of 115; the other 34 are inert without type resolution). 34 enabled, 3 disabled.**

### Where the work lives

This document travels with the config it describes: both live in `config/detekt/` and
`docs/`, changed together, so the rule list and `config/detekt/detekt.yml` cannot drift
apart.

Every rule that *can* be enabled has now been reviewed. The 34 inactive rules that
require type resolution are inert in this project and were not assessed individually.

### What is enabled

See **Rule status at a glance** below for the authoritative tables, generated from
`config/detekt/detekt.yml` in this same branch. In short: 19 rules explicitly
enabled, 2 explicitly disabled as duplicates, 7 tuned.

### Reviewed but NOT yet applied

These have verdicts but no config change. Decide before or alongside the remaining
batches:

- **Batch 3** — enable `DeprecatedBlockTag`, `KDocReferencesNonPublicProperty`,
  `MethodOverloading`, `ComplexInterface`.
- **Batch 5** — enable `CollapsibleIfStatements`, `UseIfInsteadOfWhen`,
  `AlsoCouldBeApply`, `DoubleNegativeLambda`, `UntilInsteadOfRangeTo`,
  `UnnecessaryBackticks`, `UnnecessaryAnnotationUseSiteTarget`, and optionally
  `UseLet` (unverified), `OptionalUnit` and `EqualsOnSignatureLine` (both duplicates).

### Open findings parked in the baseline

`config/detekt/baseline.xml` holds **11** entries, deliberately **not fixed**:

- `MaximumLineLength` x2 — `StrategyProvider.kt`.
- `TooGenericExceptionCaught` — `StrategyProvider.kt`, the broad catches.
- `ForEachOnRange` — `StrategyScreen.kt`, `(1..4).forEach`.
- `UnusedPrivateProperty` — `StrategyScreen.kt`, the dead `Int.stepName` extension.
- `UnusedPrivateMember` — `HomeScreen.kt`, a **false positive**: `StrategyOfTheDay`
  is called at line 40, but detekt cannot see `@Composable` call sites without type
  resolution.
- `BracesOnWhenStatements` — `ui/theme/Theme.kt`, inconsistent braces in a `when`.
- `ExpressionBodySyntax` x2 — `Screens.kt`, `categoryArgument` and `strategyArgument`.
- `UseDataClass` x2 — `Screens.Category` and `Screens.Strategy`.

It dropped from 14 to 11 when the duplicate rules were removed: three
`MaxLineLength` entries were duplicates of two `MaximumLineLength` ones.

### Next step

Batch 7: the 21 usable `formatting` (ktlint) rules. Given five confirmed duplicate pairs, expect much of `style`'s value to already live here. Superseded next step: — `CascadingCallWrapping`,
`DataClassContainsFunctions`, `ForbiddenImport`, `ForbiddenSuppress`,
`MultilineRawStringIndentation`, `NoTabs`, `SpacingBetweenPackageAndImports`,
`StringShouldBeRawString`, `TrailingWhitespace`, `TrimMultilineRawString`,
`UnderscoresInNumericLiterals`.

Then batches 7-8: the 21 usable `formatting` (ktlint) rules.

### Method that has been working

1. Pull the rule's real defaults from `default-detekt-config.yml` inside the
   `detekt-core` jar. Never describe a rule from memory.
2. Enable the batch temporarily and run `./gradlew detekt --rerun-tasks` to get a
   **measured** finding count against the codebase.
3. **Probe every rule with a deliberate violation.** A green run cannot distinguish
   "found nothing" from "never ran". This caught
   `RedundantVisibilityModifierRule` ignoring top-level declarations and left `UseLet`
   correctly marked unverified.
4. Check for an active `formatting` twin before enabling a `style` rule — three
   duplicate pairs have been confirmed so far.
5. Record the verdict here, then revert the probe config.

---

## Rule status at a glance

Generated from `config/detekt/detekt.yml`. Covers every rule the project makes an
explicit decision about; the remaining rules follow detekt's defaults via
`buildUponDefaultConfig = true`.

### Enabled — turned on, were off by default (34)

| Rule | Set | Why |
| --- | --- | --- |
| `UnconditionalJumpStatementInLoop` | potential-bugs | Loop exits on iteration one |
| `CastToNullableType` | potential-bugs | `as Foo?` meant as `as? Foo` |
| `MissingPackageDeclaration` | potential-bugs | File without a package line |
| `LateinitUsage` | potential-bugs | Runtime crash risk; none in code today |
| `NotImplementedDeclaration` | exceptions | `TODO()` reaching release |
| `UnnecessaryPartOfBinaryExpression` | performance | `a && a` |
| `GlobalCoroutineUsage` | coroutines | `GlobalScope` leaks |
| `CognitiveComplexMethod` | complexity | Nesting-weighted complexity; exempts Composables |
| `LambdaParameterNaming` | naming | camelCase or `_` |
| `OutdatedDocumentation` | comments | KDoc must match its signature |
| `BracesOnIfStatements` | style | Brace policy |
| `BracesOnWhenStatements` | style | Brace policy |
| `MandatoryBracesLoops` | style | Brace policy |
| `ClassOrdering` | style | Member ordering |
| `UnnecessaryParentheses` | style | Redundant parens |
| `DataClassShouldBeImmutable` | style | `var` in a data class |
| `RedundantVisibilityModifierRule` | style | Explicit `public` (members only) |
| `ExpressionBodySyntax` | style | `= expr` over `{ return expr }` |
| `UseDataClass` | style | Data-only classes |
| `TrailingCommaOnCallSite` | formatting | Trailing commas on calls — house style |
| `EnumWrapping` | formatting | — |
| `IfElseBracing` | formatting | — |
| `IfElseWrapping` | formatting | — |
| `NoBlankLineInList` | formatting | — |
| `NoEmptyFirstLineInClassBody` | formatting | — |
| `NoSingleLineBlockComment` | formatting | — |
| `StringTemplateIndent` | formatting | — |
| `TryCatchFinallySpacing` | formatting | — |
| `DiscouragedCommentLocation` | formatting | — |
| `NoConsecutiveComments` | formatting | — |
| `ParameterListSpacing` | formatting | — |
| `TypeArgumentListSpacing` | formatting | — |
| `TypeParameterListSpacing` | formatting | — |
| `ContextReceiverMapping` | formatting | `maxLineLength` set to 140, not its default 120 |

### Disabled — turned off, were on by default (2)

| Rule | Set | Why |
| --- | --- | --- |
| `MaxLineLength` | style | **Duplicate** of `formatting/MaximumLineLength`, which keeps the 140 limit |
| `WildcardImport` | style | **Duplicate** of `formatting/NoWildcardImports`, which keeps the test excludes |

### Tuned — left active, defaults adjusted (7)

| Rule | Set | Adjustment |
| --- | --- | --- |
| `CyclomaticComplexMethod` | complexity | `ignoreSimpleWhenEntries` |
| `LongMethod` | complexity | `ignoreAnnotated: ['Composable']` |
| `FunctionNaming` | naming | `ignoreAnnotated: ['Composable']` |
| `MagicNumber` | style | `ignoreAnnotated: ['Composable','Preview']`, `ignorePropertyDeclaration` |
| `ReturnCount` | style | `excludeGuardClauses` |
| `MaximumLineLength` | formatting | `maxLineLength: 140`, matching `.editorconfig` |
| `NoWildcardImports` | formatting | excludes test sources |

### Reviewed and deliberately NOT enabled

| Rule | Set | Reason |
| --- | --- | --- |
| `UnusedImports` | style | Duplicate of `formatting/NoUnusedImports` |
| `NoTabs` | style | Duplicate of `formatting/Indentation`; also `.editorconfig` |
| `TrailingWhitespace` | style | Exact duplicate of `formatting/NoTrailingSpaces` |
| `OptionalUnit` | style | Duplicate of `formatting/NoUnitReturn` |
| `EqualsOnSignatureLine` | style | Overlaps `formatting/NoLineBreakBeforeAssignment` |
| `ForbiddenClassName` | naming | No-op: empty `forbiddenName` |
| `VariableMinLength` | naming | No-op: minimum is 1 |
| `ForbiddenImport` | style | No-op: empty `imports` |
| `ForbiddenSuppress` | style | No-op: empty `rules`. Useful if configured |
| `ThrowingExceptionInMain` | exceptions | Not applicable: Android has no `main()` |
| `VariableMaxLength` | naming | Never fires at 64 chars |
| `FunctionMinLength` / `FunctionMaxLength` | naming | Arbitrary limits; declined |
| `StringLiteralDuplication` | complexity | Conflicts with the English-only inline-strings decision |
| `UndocumentedPublicClass` / `Function` / `Property` | comments | Library rules; app has zero KDoc by choice |
| `AbsentOrWrongFileLicense` | comments | `LICENSE` file is sufficient; no per-file headers |
| `EndOfSentenceFormat` | comments | Prose styling only |
| `CommentOverPrivateFunction` / `Property` | comments | Would penalise explanatory comments this repo wants |
| `LabeledExpression` | complexity | Would flag idiomatic `return@async` |
| `UseLet` | style | Could not be made to fire; unverified |
| `FunctionName` | formatting | Duplicate of `naming/FunctionNaming`; all 20 findings were Composables |
| `ClassName` | formatting | Duplicate of `naming/ClassNaming` |
| `PropertyName` | formatting | Wants SCREAMING_SNAKE for `ui/theme` colour tokens; Compose uses PascalCase |
| `FunctionSignature` | formatting | Default `maxLineLength: 120` conflicts with the project's 140 |
| `MultilineExpressionWrapping` | formatting | 24 findings; not adopted as house style |
| `TrailingCommaOnDeclarationSite` | formatting | Produces a bare `;` on its own line in enums with a body; only 4 of 45 findings |

### Pending — reviewed, verdict "enable", not yet applied

Batch 3: `DeprecatedBlockTag`, `KDocReferencesNonPublicProperty`, `MethodOverloading`,
`ComplexInterface`.

Batch 5: `CollapsibleIfStatements`, `UseIfInsteadOfWhen`, `AlsoCouldBeApply`,
`DoubleNegativeLambda`, `UntilInsteadOfRangeTo`, `UnnecessaryBackticks`,
`UnnecessaryAnnotationUseSiteTarget`.

Batch 6: `MultilineRawStringIndentation`, `SpacingBetweenPackageAndImports`,
`StringShouldBeRawString`, `UnderscoresInNumericLiterals`, `CascadingCallWrapping`,
`TrimMultilineRawString`, `DataClassContainsFunctions`.

Batch 7 has been **applied**: 15 rules enabled, and trailing commas adopted on
call sites only, auto-corrected across the codebase.

### Android Lint, for completeness

Configured in `app/build.gradle.kts` with `warningsAsErrors = true`.

| Check | State | Why |
| --- | --- | --- |
| `HardcodedText` | disabled | English-only by product decision |
| `Typos` | disabled | Spell-checks the base64 Google Fonts cert blob |
| `GradleDependency` | informational | Non-deterministic: fires when anything newer publishes |
| `AndroidGradlePluginVersion` | informational | Same |
| `PrivateResource` | informational | `font_certs.xml` re-declares library arrays |
| `UnusedResources` | informational | Leftover template resources |
| `IconDuplicates` | informational | `ic_launcher` and `ic_launcher_round` identical |
| `MonochromeLauncherIcon` | informational | No themed-icon layer |
| `IconLocation` | informational | `cerebro_logo.png` in densityless `res/drawable` |

### No duplicates remain

Verified with a probe: every defect is now reported exactly once. Five
style/formatting duplicate pairs were identified in total; three were active and
have been resolved, two were never enabled.

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

## Batch 3 — the rest of `comments`, and `complexity`

Nine rules, which exhausts both rule sets. After this only `style` (31) and
`formatting` (21) remain.

| # | Rule | Set | Verdict |
| --- | --- | --- | --- |
| 21 | `DeprecatedBlockTag` | comments | **Enable** |
| 22 | `KDocReferencesNonPublicProperty` | comments | **Enable** |
| 23 | `MethodOverloading` | complexity | Enable |
| 24 | `ComplexInterface` | complexity | Enable |
| 25 | `EndOfSentenceFormat` | comments | Skip |
| 26 | `AbsentOrWrongFileLicense` | comments | Skip |
| 27 | `CommentOverPrivateFunction` | comments | **Skip** |
| 28 | `CommentOverPrivateProperty` | comments | **Skip** |
| 29 | `LabeledExpression` | complexity | **Skip** |

### 21. `DeprecatedBlockTag` — comments
Flags the KDoc block tag `@deprecated`, which Kotlin ignores entirely — the
`@Deprecated` *annotation* is the real mechanism. A `@deprecated` tag therefore
silently does nothing while looking like it works. **Config:** none beyond universal.
**Verdict: enable.** Zero cost, and the failure mode is invisible.

### 22. `KDocReferencesNonPublicProperty` — comments
Flags KDoc on a public declaration that `[references]` a private or internal member,
which a reader of the public API cannot see. **Config:** `excludes` (test sources
already excluded by default). **Verdict: enable.** Free today, since there is no KDoc.

### 23. `MethodOverloading` — complexity
Flags more than `threshold` overloads of the same function name. Heavy overloading is
usually better expressed with default arguments in Kotlin. **Config:** `threshold`
(default 6). **Verdict: enable.** Six is generous and nothing here approaches it.

### 24. `ComplexInterface` — complexity
Flags interfaces declaring more than `threshold` members, as a proxy for a type doing
too much. **Config:** `threshold` (10), `includeStaticDeclarations` (false),
`includePrivateDeclarations` (false), `ignoreOverloaded` (false).
**Verdict: enable.** The only interface in the codebase is `Screens`
(`ui/screens/Screens.kt:13`), well under the limit. Worth noting that if the routes
are ever refactored into explicit constants, that file grows — so this rule may start
to have an opinion later, which is arguably the point.

### 25. `EndOfSentenceFormat` — comments
Requires the first sentence of a KDoc block to end with `.`, `?` or `!`.
**Config:** `endOfSentenceFormat` (a regex). **Verdict: skip.** Pure prose styling
with no correctness value, and it only bites once documentation exists.

### 26. `AbsentOrWrongFileLicense` — comments
Requires every file to begin with a license header matching a template.
**Config:** `licenseTemplateFile` (`license.template`), `licenseTemplateIsRegex`.
**Verdict: skip.** The repo has an Apache-2.0 `LICENSE` file and zero per-file
headers, which is a perfectly normal arrangement. Enabling this means adding a header
to all ~25 source files and maintaining a template, for no practical gain on a single
application repo. Reconsider only if per-file headers become a requirement.

### 27-28. `CommentOverPrivateFunction` / `CommentOverPrivateProperty` — comments
Flag *any* comment above a private function or property, on the argument that private
implementation should be self-explanatory. **Config:** none beyond universal.
**Verdict: skip both — actively harmful here.** This codebase deliberately uses
explanatory comments on non-obvious internals, and the same instinct produced the
comments in `proguard-rules.pro` and `detekt.yml` that record *why* a decision was
made. Penalising that is backwards: the answer to a confusing private function is
rarely to delete the sentence explaining it.

### 29. `LabeledExpression` — complexity
Flags labelled expressions such as `return@async`, treating labels as a complexity
smell. **Config:** `ignoredLabels`. **Verdict: skip.** `StrategyProvider.kt:96` and
`:110` use `return@async` inside a coroutine builder, where the label is the only way
to return a value from the lambda. Enabling this would flag idiomatic, correct code,
and the workaround would be to list `async` in `ignoredLabels` — at which point the
rule is doing nothing useful.

---

## Batch 4 — `style`, part 1 of 4

`style` is the largest set: 46 inactive rules, of which **15 need type resolution** and
are inert here, leaving 31 to review. These ten were each enabled temporarily and run
against the real codebase, so the "findings" column is measured, not predicted.

| # | Rule | Findings | Verdict |
| --- | --- | ---: | --- |
| 30 | `BracesOnIfStatements` | 0 | **Enable** |
| 31 | `MandatoryBracesLoops` | 0 | **Enable** |
| 32 | `ClassOrdering` | 0 | **Enable** |
| 33 | `UnnecessaryParentheses` | 0 | **Enable** |
| 34 | `DataClassShouldBeImmutable` | 0 | **Enable** |
| 35 | `BracesOnWhenStatements` | 1 | Enable, one fix |
| 36 | `RedundantVisibilityModifierRule` | 0 | Enable, but see the gap |
| 37 | `ExpressionBodySyntax` | 2 | Optional |
| 38 | `UseDataClass` | 2 | Optional |
| 39 | `UnusedImports` | 0 | **Skip — duplicate** |

### The five free ones: 30-34
All measured zero findings.

- **`BracesOnIfStatements`** — `singleLine: 'never'`, `multiLine: 'always'` by default,
  which the codebase already satisfies (`ui/theme/Theme.kt:45` is a braceless
  single-line if, exactly as the default wants).
- **`MandatoryBracesLoops`** — braces required on loop bodies. **Config:** none.
- **`ClassOrdering`** — enforces properties → initialisers → constructors → methods →
  companion. **Config:** none. Verified live with a probe: it correctly flagged a
  property declared after a method.
- **`UnnecessaryParentheses`** — **Config:** `allowForUnclearPrecedence` (false), which
  can be set true to permit clarifying parentheses.
- **`DataClassShouldBeImmutable`** — flags `var` in a data class. `Strategy` is all
  `val`, so this is purely preventive.

### 35. `BracesOnWhenStatements` — 1 finding
`singleLine: 'necessary'`, `multiLine: 'consistent'`. Flags
`ui/theme/Theme.kt:42` — a `when` whose branches inconsistently use braces. One
trivial fix. **Verdict: enable.**

### 36. `RedundantVisibilityModifierRule` — 0 findings, with a caveat
Flags an explicit `public`, which is already the default. **Config:** none.

Measured behaviour: it fires on `public class` and on `public fun` **members**, but
**not on top-level declarations**. So it does *not* catch
`ui/screens/Screens.kt:11`'s `public fun NavHostController.navigateToScreen(...)` —
the one genuinely redundant modifier in the codebase. **Verdict: enable anyway**
(free, and it covers members), but do not expect it to find that case.

### 37. `ExpressionBodySyntax` — 2 findings
Prefers `fun f() = expr` over `fun f() { return expr }`. Flags
`Screens.kt:37` and `:58` (`categoryArgument`, `strategyArgument`).
**Config:** `includeLineWrapping` (false) — set true to also flag bodies that wrap
across lines. **Verdict: optional.** Pure preference; the two fixes are trivial if
the concise form is wanted.

### 38. `UseDataClass` — 2 findings
Flags classes that only hold data. Hits `Screens.Category` (`:25`) and
`Screens.Strategy` (`:42`), the two parameterised route classes.
**Config:** `allowVars` (false). **Verdict: optional.** Converting them is harmless
and would add `equals`/`hashCode`/`copy`, but it is a source change for little gain,
and both classes may be rewritten anyway when the routes stop being derived from
class names.

### 39. `UnusedImports` — skip, it is a duplicate
**Verdict: skip.** `formatting/NoUnusedImports` is already active and does the same
job — the detekt-autocorrect pass removed six unused imports through it. Enabling the
`style` twin means the same import reported twice.

This project already demonstrates the problem: `style/MaxLineLength` and
`formatting/MaximumLineLength` are both on, and the baseline carried entries from
**both** for the same lines of `StrategyProvider.kt`. Worth treating as a general
rule — check for a `formatting` twin before enabling a `style` rule.

---

## Batch 5 — `style`, part 2 of 4

**All ten measured zero findings against the codebase.** Nine were confirmed live by
probe; one could not be triggered and is marked unverified.

| # | Rule | Live? | Verdict |
| --- | --- | --- | --- |
| 40 | `CollapsibleIfStatements` | confirmed | **Enable** |
| 41 | `UseIfInsteadOfWhen` | confirmed | **Enable** |
| 42 | `AlsoCouldBeApply` | confirmed | **Enable** |
| 43 | `DoubleNegativeLambda` | confirmed | **Enable** |
| 44 | `UntilInsteadOfRangeTo` | confirmed | **Enable** |
| 45 | `UnnecessaryBackticks` | confirmed | **Enable** |
| 46 | `UnnecessaryAnnotationUseSiteTarget` | confirmed | **Enable** |
| 47 | `UseLet` | **unverified** | Enable, unproven |
| 48 | `OptionalUnit` | confirmed | Duplicate - see below |
| 49 | `EqualsOnSignatureLine` | confirmed | Duplicate - see below |

### 40-46: the seven clean ones
All confirmed firing on deliberate violations, all silent on the real codebase.

- **`CollapsibleIfStatements`** - nested `if` that could be merged. **Config:** none.
- **`UseIfInsteadOfWhen`** - a `when` with only two branches reads better as `if`.
  **Config:** `ignoreWhenContainingVariableDeclaration` (false).
- **`AlsoCouldBeApply`** - an `also` block whose every statement starts with `it` is
  really an `apply`. **Config:** none.
- **`DoubleNegativeLambda`** - `none { !it... }` is `all { it... }`. **Config:**
  `negativeFunctions`, `negativeFunctionNameParts`.
- **`UntilInsteadOfRangeTo`** - `0..n - 1` should be `0 until n`. **Config:** none.
- **`UnnecessaryBackticks`** - backticked identifiers that need no escaping.
- **`UnnecessaryAnnotationUseSiteTarget`** - `@property:` on something that is not a
  constructor parameter.

### 47. `UseLet` - unverified
Should suggest `?.let` in place of an `if (x != null)` block. **Config:** none.
It did **not** fire on a probe written to trigger it, and it is not on the
type-resolution list, so the reason is unclear - possibly a narrower trigger than
expected. **Verdict: harmless to enable, but do not count it as coverage** until it
has been seen to fire.

### 48-49: two more style/formatting duplicates
Both confirmed live, both redundant with `formatting` rules that are **already active**:

- **`OptionalUnit`** - on the probe, `formatting/NoUnitReturn` flagged the *same line*,
  reported twice, once by each rule.
- **`EqualsOnSignatureLine`** - overlapped `formatting/NoLineBreakBeforeAssignment` and
  `FunctionStartOfBodySpacing` on the same construct.

This is the third confirmed instance of the pattern, after
`UnusedImports`/`NoUnusedImports` and `MaxLineLength`/`MaximumLineLength`.

**The general rule this establishes: before enabling anything from `style`, check
whether an active `formatting` rule already covers it.** The `formatting` set is
ktlint, it is on by default, and it overlaps `style` heavily.

---

## Batch 6 - `style`, part 3 of 3 (completes the rule set)

The last eleven usable `style` rules. **All measured zero findings** against the
codebase; seven were confirmed live by probe, two are no-ops at their defaults, and
two stayed silent for explainable reasons.

| # | Rule | Live? | Verdict |
| --- | --- | --- | --- |
| 50 | `MultilineRawStringIndentation` | confirmed | **Enable** |
| 51 | `SpacingBetweenPackageAndImports` | confirmed | **Enable** |
| 52 | `StringShouldBeRawString` | confirmed | **Enable** |
| 53 | `UnderscoresInNumericLiterals` | confirmed | **Enable** |
| 54 | `CascadingCallWrapping` | confirmed | **Enable** |
| 55 | `TrimMultilineRawString` | untriggered | Enable |
| 56 | `DataClassContainsFunctions` | untriggered | Enable |
| 57 | `NoTabs` | confirmed | **Skip - duplicate** |
| 58 | `TrailingWhitespace` | confirmed | **Skip - duplicate** |
| 59 | `ForbiddenImport` | n/a | **No-op at defaults** |
| 60 | `ForbiddenSuppress` | n/a | **No-op at defaults** |

### 50-54: the five clean enables

- **`MultilineRawStringIndentation`** - raw-string content must line up with its
  opening quotes. **Config:** `indentSize` (4), `trimmingMethods`.
- **`SpacingBetweenPackageAndImports`** - exactly one blank line between them.
- **`StringShouldBeRawString`** - a string with more than `maxEscapedCharacterCount`
  escapes reads better as a raw string. **Config:** `maxEscapedCharacterCount` (2),
  `ignoredCharacters`.
- **`UnderscoresInNumericLiterals`** - `1234567` should be `1_234_567`. **Config:**
  `acceptableLength` (4), `allowNonStandardGrouping`. The colour literals in
  `ui/theme2` are excluded from detekt anyway, and `ui/theme` produced no findings.
- **`CascadingCallWrapping`** - if one call in a chain is wrapped, all should be.
  **Config:** `includeElvis` (true). Distinct from the active
  `formatting/ChainWrapping`: only this one fired on the probe chain.

### 55-56: enable, but untriggered by the probe

- **`TrimMultilineRawString`** - requires `.trimIndent()` / `.trimMargin()` on
  multiline raw strings. The probe used `.trimIndent()`, so it correctly stayed
  silent. **Config:** `trimmingMethods`.
- **`DataClassContainsFunctions`** - flags member functions on a data class.
  `Strategy` has none (`serialized()` is an extension), so nothing could trigger it.
  **Config:** `conversionFunctionPrefix`, `allowOperators` (false).

Their silence is explained rather than mysterious, unlike `UseLet` in batch 5.

### 57-58: two more duplicates, one exact

- **`TrailingWhitespace`** - `formatting/NoTrailingSpaces` flagged the **same line and
  the same column** on the probe. A textbook exact duplicate.
- **`NoTabs`** - `formatting/Indentation` flagged the same tab character. Tabs are
  additionally ruled out by `indent_style = space` in `.editorconfig`, so this is
  covered three times over.

**Skip both.** That brings the confirmed style/formatting duplicate count to five pairs.

### 59-60: two more no-ops at defaults

- **`ForbiddenImport`** - `imports: []`, `forbiddenPatterns: ''`.
- **`ForbiddenSuppress`** - `rules: []`.

Neither can fire until configured, exactly like `ForbiddenClassName` in batch 2.

`ForbiddenSuppress` is the more interesting as a *policy* tool: it can ban suppressing
specific rules, so `rules: ['MagicNumber']` would stop `@Suppress("MagicNumber")` being
used to sidestep a decision already taken in this config. Worth revisiting if
suppressions start accumulating.

### `style` is now complete

46 inactive rules: 15 inert (type resolution), 31 reviewed across batches 4-6. Of those
31 - 10 enabled, 7 more recommended, 5 duplicates, 4 no-ops, the rest optional.

---

## Batch 7 — `formatting` (ktlint), all 21 (completes the review)

**109 findings across 7 rules; the other 14 are silent.** Eight of the silent ones were
confirmed live by probe, six could not be triggered.

The defining property of this set: **17 of the 21 carry `autoCorrect: true`**, so
`detekt --auto-correct` fixes them mechanically. Enabling them is cheap in a way the
`style` rules were not.

### Skip — 3

| Rule | Findings | Why |
| --- | ---: | --- |
| `FunctionName` | 20 | **Duplicate** of active `naming/FunctionNaming`, and every finding is a `@Composable`. Enabling it would undo the `ignoreAnnotated: ['Composable']` exemption that exists precisely to allow PascalCase Composables. |
| `ClassName` | 0 | **Duplicate** of active `naming/ClassNaming` — both fired on the same class in a probe. Sixth confirmed duplicate pair. |
| `PropertyName` | 9 | Demands SCREAMING_SNAKE_CASE for immutable properties. All nine are `ui/theme/Color.kt` colour tokens, where Compose convention is PascalCase. Conflicts with the framework, and that package appears dead anyway. |

### House-style decision — 3

Auto-correctable, so the cost is one `--auto-correct` run, but they change how the
codebase looks:

| Rule | Findings | Question |
| --- | ---: | --- |
| `TrailingCommaOnCallSite` | 41 | Do you want trailing commas on calls? **Adopted.** |
| `TrailingCommaOnDeclarationSite` | 4 | ...and on declarations? **Declined** — see below. |
| `MultilineExpressionWrapping` | 24 | Multiline expressions must start on a new line. **Declined.** |

`TrailingCommaOnDeclarationSite` was enabled, then dropped after seeing its output. On
an enum whose entries are followed by a body it formats the trailing comma as
`Improvement,` followed by a bare `;` on its own line. Valid Kotlin, but unpleasant to
read, and the rule accounted for only 4 of the 45 trailing-comma findings — so the
call-site rule carries almost all the value without the artefact.

### Needs config alignment first — 1

`FunctionSignature` (9 findings) ships `maxLineLength: 120`, contradicting the project's
140 in `.editorconfig` and `formatting/MaximumLineLength`. Enabling it as-is introduces a
third, inconsistent line-length number. Set it to 140 or leave it off.

`ContextReceiverMapping` carries the same 120 default, though it finds nothing (no
context receivers in the codebase).

### Enable — 14

Zero or trivial findings, all auto-correctable.

**Confirmed live by probe (8):** `EnumWrapping`, `IfElseBracing`, `IfElseWrapping`,
`NoEmptyFirstLineInClassBody`, `NoSingleLineBlockComment`, `StringTemplateIndent`,
`TryCatchFinallySpacing`, plus `NoBlankLineInList` (2 trivial findings).

**Untriggered by the probe (6):** `ContextReceiverMapping`, `DiscouragedCommentLocation`,
`NoConsecutiveComments`, `ParameterListSpacing`, `TypeArgumentListSpacing`,
`TypeParameterListSpacing`. Their silence is explained — the codebase has no context
receivers, no consecutive comments, and no explicit type-argument lists to misformat.

### Tested and disproven: the `ktlint_code_style` shortcut

These 21 are largely the rules ktlint enables only under its `ktlint_official` code
style, which suggested a shortcut: set `ktlint_code_style = ktlint_official` in
`.editorconfig` and get the coherent set instead of 21 individual switches.

**That does not work.** Adding it to `.editorconfig` and re-running produced **zero**
additional findings. detekt-formatting gates these rules on its own `active:` flags and
does not consult ktlint's code-style setting. They must be enabled individually in
`config/detekt/detekt.yml`.

### The review is now complete

81 enableable inactive rules assessed across seven batches. The remaining 34 require
type resolution and are inert here, so there is nothing to decide about them until
detekt 2.0 makes type resolution practical.

---

## Queue

Remaining inactive rules to review, in suggested order:

| Batch | Contents | Count |
| --- | --- | ---: |
| 6–7 | `style` remainder — 11 rules across two batches | 11 |
| 9–10 | `formatting` / ktlint (21 usable) | 21 |
| — | The 34 inactive rules blocked by type resolution, recorded but not enableable | 34 |
