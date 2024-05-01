@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.myfirstapp"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.myfirstapp"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-${System.getenv("VERSION_SHA")}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


/*kotlin {
    //jvmToolchain(17)
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}*/
/*tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"

    }
}*/

android {
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.android.gms:play-services-fido:19.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.google.firebase:firebase-crashlytics:17.4.0")
    implementation ("com.google.firebase:firebase-messaging:21.0.1")
    implementation ("com.google.firebase:firebase-analytics:18.0.2")
    implementation (platform("com.google.firebase:firebase-bom:29.3.0"))

    implementation ("com.squareup.retrofit2:retrofit:2.5.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation ("io.reactivex.rxjava2:rxjava:2.1.9")
    implementation ("io.reactivex.rxjava2:rxandroid:2.0.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:3.0.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.4.1")
    implementation ("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

    //google login
    implementation("com.google.android.gms:play-services-auth:19.0.0")

    //facebook login


    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    //circle Imageview
    implementation ("de.hdodenhof:circleimageview:3.1.0")
}

