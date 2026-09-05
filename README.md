# Cerebro

*Think in new ways.*

Cerebro is a small Android app for sharpening creative thinking and finding a
way into complex problems. It collects a library of categorised thinking
strategies — short prompts and techniques you can apply to whatever you are
stuck on — so you can step outside your usual framing, question assumptions and
work through a situation from a different angle. You can also add your own
strategies to the library.

## What it does

Strategies are grouped into six categories: **Perspective**, **Experimentation**,
**Clarity**, **Mindset**, **Decision Making** and **Improvement**. Each strategy
has a title, a one-line summary and a longer explanation.

The app is a single-activity Compose app with these screens:

| Screen | Source | What it shows |
| --- | --- | --- |
| Home | [`ui/screens/HomeScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/HomeScreen.kt) | The category grid, the entry point into the app |
| Category | [`ui/screens/CategoryScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/CategoryScreen.kt) | Every strategy in one category |
| Strategy | [`ui/screens/StrategyScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/StrategyScreen.kt) | A single strategy in full |
| Library | [`ui/screens/LibraryScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/LibraryScreen.kt) | All strategies, bundled and custom, sorted by title |
| New Strategy | [`ui/screens/StrategyScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/StrategyScreen.kt) | A four-step editor (title, short description, long description, category) with a preview before saving |
| About | [`ui/screens/AboutScreen.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/AboutScreen.kt) | App description |

Library, About and New Strategy are reached from the overflow menu in the top
app bar ([`ui/screens/AppBar.kt`](app/src/main/java/redtoss/creativity/cerebro/ui/screens/AppBar.kt)).

The bundled strategies ship as a JSON asset and are parsed with
kotlinx.serialization. Strategies you create yourself are written to a private
file in the app's internal storage, then merged with the bundled ones — see
[`data/StrategyProvider.kt`](app/src/main/java/redtoss/creativity/cerebro/data/StrategyProvider.kt).

## Tech stack

- **Kotlin**, single Gradle module (`:app`)
- **Jetpack Compose** with **Material 3**, via the Compose BOM
- **Navigation Compose** for screen navigation
- **kotlinx.serialization** (JSON) for the strategy data
- **Gradle** 9.7.1 (wrapper included), Android Gradle Plugin

Exact library versions live in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml), which is the single
source of truth for the dependency versions — this README deliberately does not
duplicate them.

## Requirements

- **JDK 17** to build (this is what CI uses; see
  [`.github/workflows/android.yml`](.github/workflows/android.yml))
- **Android SDK** with the platform matching `compileSdk` (currently 37)
- **Android 7.0 (API 24)** or newer to run — `minSdk` is 24, `targetSdk` is 37

The Android SDK location is read from `local.properties` at the repo root. That
file is machine-specific and gitignored, so create it yourself if you are not
building through Android Studio:

```properties
sdk.dir=/path/to/your/Android/sdk
```

## Build and run

```bash
# Debug APK
./gradlew assembleDebug

# Full check: assembles both variants, runs unit tests and Android Lint
./gradlew build
```

`assembleDebug` writes the APK to `app/build/outputs/apk/debug/app-debug.apk`.
Install it on a connected device or running emulator with either:

```bash
./gradlew installDebug
# or
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Lint reports from `./gradlew build` end up in `app/build/reports/`.

## Project structure

```
app/src/main/
├── assets/                     # bundled strategy JSON
├── java/redtoss/creativity/cerebro/
│   ├── MainActivity.kt         # single activity, sets up Compose content
│   ├── data/                   # Strategy, Category, StrategyProvider, editor state
│   └── ui/
│       ├── layouts/            # cards and lists
│       ├── screens/            # the screens above + navigation graph (RootUi.kt, Screens.kt)
│       ├── theme/              # Compose theme
│       └── theme2/             # a second Compose theme
└── res/                        # strings, drawables, icons
```

Application ID and namespace are both `redtoss.creativity.cerebro`.

Note that two theme packages exist side by side (`ui/theme` and `ui/theme2`);
both are present in the source tree.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for the
full text.
