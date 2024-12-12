buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}
plugins {
    id("com.android.application") version "8.4.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    kotlin("android") version "1.9.10" apply false

}

/*allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" )}
        maven { url = uri("https://repo.moengage.com/") }

    }
}*/

// OLD
//id("com.google.gms.google-services") version "4.3.10" apply false
//id("com.google.firebase.crashlytics") version "2.9.1" apply false

// classpath("com.google.gms:google-services:4.3.15")
// id("com.android.application") version "7.2.2" apply false old
// kotlin("android") version "1.6.10" apply false old
//  kotlin("android") version "1.8.0" apply false old second
// kotlin("jvm") version "1.6.10"



/*
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}*/
