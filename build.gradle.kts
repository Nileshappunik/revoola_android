buildscript {
    dependencies {
       // classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.gms:google-services:4.4.2")
    }
}
plugins {
    id("com.android.application") version "8.4.2" apply false
   // id("com.android.application") version "7.2.2" apply false old
    id("com.google.gms.google-services") version "4.3.10" apply false
    id("com.google.firebase.crashlytics") version "2.9.1" apply false
   // kotlin("android") version "1.6.10" apply false old
    kotlin("android") version "1.8.0" apply false

   // kotlin("jvm") version "1.6.10"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" )}
        maven { url = uri("https://repo.moengage.com/") }

    }
}

/*
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}*/
