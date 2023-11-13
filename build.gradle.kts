// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    kotlin("kapt") version "1.8.0" apply false
    kotlin("plugin.parcelize") version "1.8.0" apply false
    kotlin("plugin.serialization") version "1.8.0" apply false

}