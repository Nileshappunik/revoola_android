@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.cli.jvm.main


plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src/main/res", "src/main/res/layout/feed", "src/main/res/layout/more",
                    "src/main/res/layout/friend", "src/main/res/layout/start", "src/main/res/layout/overview",
                    "src/main/res/layout/common", "src/main/res/layout/activity", "src/main/res/layout/dialog"
                )
            }
        }
    }
}


dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.android.gms:play-services-fido:19.0.1")

    //facebook login
    implementation("com.facebook.android:facebook-android-sdk:17.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Fire base
    implementation ("com.google.firebase:firebase-crashlytics:17.4.0")
    implementation ("com.google.firebase:firebase-messaging:21.0.1")
    implementation ("com.google.firebase:firebase-analytics:18.0.2")
    implementation ("com.google.firebase:firebase-auth:16.0.5")
    implementation ("com.google.firebase:firebase-database-ktx:20.0.5")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.4.0")
    implementation ("com.google.android.gms:play-services-auth:20.1.0")
    implementation ("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation ("com.google.firebase:firebase-storage:20.1.0")
    implementation(platform("com.google.firebase:firebase-bom:29.3.0"))

    implementation ("com.squareup.retrofit2:retrofit:2.5.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation ("io.reactivex.rxjava2:rxjava:2.1.9")
    implementation ("io.reactivex.rxjava2:rxandroid:2.0.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:3.0.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.4.1")
    implementation ("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

  //  implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    //implementation ("com.squareup.picasso:picasso:2.71828")

    //google login
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")


    //VIDEO PLAY
    implementation ("androidx.media3:media3-exoplayer:1.0.0")
    implementation ("androidx.media3:media3-ui:1.0.0")
    implementation ("androidx.media3:media3-exoplayer-dash:1.0.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    //circle Imageview
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.caverock:androidsvg:1.4")
}

