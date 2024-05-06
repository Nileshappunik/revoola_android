buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}
plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.google.gms.google-services") version "4.3.10" apply false
    id("com.google.firebase.crashlytics") version "2.9.1" apply false
    kotlin("android") version "1.6.10" apply false

   // kotlin("jvm") version "1.6.10"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" ) }
    }
}

/*
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}*/
