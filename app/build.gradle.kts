plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "redtoss.creativity.cerebro"
    compileSdk = 37

    defaultConfig {
        applicationId = "redtoss.creativity.cerebro"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        checkDependencies = true
        // Cerebro is English-only by product decision, so user-facing strings live
        // inline in the Composables rather than in strings.xml. Revisit this if
        // localisation ever becomes a goal.
        disable += "HardcodedText"
        // Always a false positive here: lint spell-checks the base64 Google Fonts
        // certificate blob in res/values/font_certs.xml. Nothing to fix, ever.
        disable += "Typos"

        // Real but deferred: demoted to hints so they stay visible in the lint report
        // without gating the build. Each needs a res/ change, tracked as follow-up work.
        informational += "PrivateResource"        // app re-declares ui-text-google-fonts' cert arrays
        informational += "UnusedResources"        // leftover template colors, drawables and app_motto
        informational += "IconDuplicates"         // ic_launcher and ic_launcher_round are identical
        informational += "MonochromeLauncherIcon" // adaptive icon has no monochrome layer yet
        informational += "IconLocation"           // cerebro_logo.png lives in densityless res/drawable
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.google.fonts)
    implementation(libs.androidx.navigation)
    implementation(libs.kotlinx.serialization)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}