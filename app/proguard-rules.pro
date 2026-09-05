# Cerebro R8 / ProGuard configuration.
#
# Applied on top of AGP's proguard-android-optimize.txt (see app/build.gradle.kts)
# and the consumer rules shipped by our dependencies. Keep this file minimal:
# every keep rule is a hole in the shrinker, so nothing goes in here without a
# comment saying what breaks without it.

# -- Crash readability -------------------------------------------------------
# Keep the original line numbers in stack traces so a Play Console / logcat
# crash can be mapped back to source via app/build/outputs/mapping/release/mapping.txt.
# Not in proguard-android-optimize.txt, which only keeps the reflection-related
# attributes (AnnotationDefault, RuntimeVisibleAnnotations, Signature, ...).
-keepattributes SourceFile,LineNumberTable

# Having kept LineNumberTable, replace the real source file name with the
# constant "SourceFile" so class file names are not leaked in traces.
-renamesourcefileattribute SourceFile

# -- kotlinx.serialization ---------------------------------------------------
# Intentionally empty. The models in data/ are @Serializable and are parsed from
# assets/strategies.json at runtime, which is the classic thing R8 breaks — but
# no explicit keep rule turned out to be necessary here, so none is added.
#
# Verified against app/build/outputs/mapping/release/ on the first minified build:
#   * mapping.txt keeps Strategy$$serializer as a class; the only member usage.txt
#     reports removed from it is Compose's `$stable` int field, not serialize/
#     deserialize/getDescriptor.
#   * seeds.txt shows Category (a @Serializable enum) retaining values(),
#     valueOf(String) and Companion.serializer(), which is what protects
#     name-based enum deserialization.
# The kotlinx-serialization runtime ships its own consumer rules and they are
# doing the work. Re-check this if the models gain polymorphic or contextual
# serializers, which reflect far more aggressively.

# -- Navigation routes -------------------------------------------------------
# Screens.name derives every navigation route from `this.javaClass.simpleName`
# (ui/screens/Screens.kt). R8 renaming those classes rewrites the start
# destination and every composable() route into short obfuscated names that no
# longer agree with each other, so the app launches on an arbitrary screen and
# navigation silently does nothing. Keeping the names restores the routes.
#
# This is a workaround for fragile code, not a fix: routes should be explicit
# string constants rather than reflected class names. Remove these rules once
# Screens.kt no longer reflects on its own class names.
-keepnames class redtoss.creativity.cerebro.ui.screens.Screens
-keepnames class redtoss.creativity.cerebro.ui.screens.Screens$**
