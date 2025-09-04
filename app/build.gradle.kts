@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
}

// --- Load GROQ_KEY from env -> local.properties -> default ---
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
val groqKey: String = System.getenv("GROQ_KEY")
    ?: localProps.getProperty("GROQ_KEY")
    ?: "DEFAULT_VALUE"

val revenuecatKey: String = System.getenv("REVENUECAT_ANDROID_API_KEY")
    ?: localProps.getProperty("REVENUECAT_ANDROID_API_KEY")
    ?: "DEFAULT_VALUE"

// ------------------------------------------------------------

android {
    namespace = "com.revoola"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.revoola"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-${System.getenv("VERSION_SHA")}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // ✅ Make GROQ_KEY available to all build types
        buildConfigField("String", "GROQ_KEY", "\"$groqKey\"")
        buildConfigField("String", "REVENUECAT_ANDROID_API_KEY", "\"$revenuecatKey\"")
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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

    // -------- MoEngage --------
    implementation(moengage.core)
    implementation(moengage.cardsUi)
    implementation(moengage.cardsCore)
    implementation(moengage.inapp)
    implementation(moengage.pushKit)
    implementation(moengage.richNotification)
    implementation(moengage.deviceTrigger)
    implementation(moengage.pushAmp)
    implementation(moengage.geofence)
    implementation(moengage.inboxUi)
    implementation(moengage.inboxCore)

    // -------- RevenueCat --------
    implementation("com.revenuecat.purchases:purchases:9.5.0")

    // -------- AndroidX / Google --------
    implementation("androidx.core:core:1.9.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-fido:21.1.0")

    // Facebook
    implementation("com.facebook.android:facebook-android-sdk:17.0.0")

    // (You had this; keep if you use Compose runtime)
    implementation("androidx.compose.runtime:runtime-android:1.7.8")

    // -------- Test --------
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.testng:testng:6.9.6")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // -------- Firebase --------
    implementation("com.google.firebase:firebase-inappmessaging-ktx:21.0.1")
    implementation("com.google.firebase:firebase-inappmessaging-display:21.0.1")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-crashlytics:19.3.0")
    implementation("com.google.firebase:firebase-messaging:24.1.0")
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-config-ktx:22.0.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0") // ✅ fixed spacing typo

    // -------- Networking --------
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // You already had OkHttp 4.11.0 — upgrade both to 4.12.0 for parity with my snippet:
    implementation("com.squareup.okhttp3:okhttp:4.12.0")                 // 👉 add/upgrade
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")    // 👉 upgrade

    // -------- Rx (legacy parts) --------
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:3.0.1")
    implementation("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

    // -------- Pdf View --------
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")

    // -------- Images / Media --------
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("io.github.ParkSangGwon:tedimagepicker:1.2.7")
    implementation("com.github.zhihu:Matisse:0.5.3-beta3")

    // -------- Maps / Location --------
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // -------- Media3 --------
    implementation("androidx.media3:media3-exoplayer:1.0.0")
    implementation("androidx.media3:media3-ui:1.0.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.0.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.0.0")

    // -------- Lifecycle / VM --------
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // -------- Health Connect --------
    implementation("androidx.health.connect:connect-client:1.1.0-alpha11")
    // (You had this twice; kept one entry)

    // -------- Wear / Garmin --------
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("androidx.wear:wear:1.3.0")
    implementation("com.garmin.connectiq:ciq-companion-app-sdk:2.2.0@aar")

    // -------- Branch --------
    implementation("io.branch.sdk.android:library:5.+")

    //Samsung Health Connect
    implementation (files("libs/samsung-health-data-api-1.0.0-b2.aar"))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // -------- Misc --------
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.caverock:androidsvg:1.4")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.1")

    // -------- Kotlin stdlib --------
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // 👉 ADD THESE 2 for the Groq helper:
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.json:json:20240303")


}

