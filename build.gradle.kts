// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    kotlin("android") version "2.1.0" apply false  // Apply false, because you don't want it applied here directly
    alias(libs.plugins.android.application) apply false
    // Remove the duplicate Kotlin plugin alias declaration
    // alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
