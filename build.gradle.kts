plugins {
    id("com.android.application") version "7.2.2" apply false
    kotlin("android") version "1.6.10" apply false
   // kotlin("jvm") version "1.6.10"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

/*
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}*/
