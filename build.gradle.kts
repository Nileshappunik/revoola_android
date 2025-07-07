buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.1")
    }
}
plugins {
    id("com.android.application") version "8.4.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.1" apply false
    kotlin("android") version "1.9.24" apply false

}

