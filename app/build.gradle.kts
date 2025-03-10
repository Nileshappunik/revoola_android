@file:Suppress("UnstableApiUsage")


plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    id ("kotlin-kapt")
    id ("kotlin-parcelize")
}

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
       // sourceCompatibility = JavaVersion.VERSION_1_8 Old
       // targetCompatibility = JavaVersion.VERSION_1_8  Old

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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

    //MoEngage SDK for Use InApp purchase and PushNotification
    // core moengage features
    implementation(moengage.core)
    // optionally add this to use the cards feature
    implementation(moengage.cardsUi)
    // optionally add this if you are using the core module of cards
    implementation(moengage.cardsCore)
    // optionally add this to use the InApp feature
    implementation(moengage.inapp)
    // optionally add this to use the Huaewi PushKit feature
    implementation(moengage.pushKit)
    // optionally add this to use the Push Templates feature
    implementation(moengage.richNotification)
    // optionally add this to use the Device Trigger feature
    implementation(moengage.deviceTrigger)
    // optionally add this to use the Push Amp feature
    implementation(moengage.pushAmp)
    // optionally add this to use the geofence feature
    implementation(moengage.geofence)
    // optionally add this to use the Inbox UI feature
    implementation(moengage.inboxUi)
    // optionally add this if you are using the core module of Inbox
    implementation(moengage.inboxCore)
   /* // MoEngage Analytics (Required for event tracking)
    implementation("com.moengage:moe-android-sdk:13.05.00")*/


    //RevenueCat
    implementation ("com.revenuecat.purchases:purchases:7.0.0")
    implementation ("com.revenuecat.purchases:purchases-store-amazon:7.0.0")

    implementation("androidx.core:core:1.9.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.android.gms:play-services-fido:21.1.0")


    //facebook login
    implementation("com.facebook.android:facebook-android-sdk:17.0.0")
    implementation("androidx.compose.runtime:runtime-android:1.7.8")


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.testng:testng:6.9.6")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //inappMessage Firebase
    implementation("com.google.firebase:firebase-inappmessaging-ktx:21.0.1")
    implementation("com.google.firebase:firebase-inappmessaging-display:21.0.1")

    // Fire base
    implementation ("com.google.firebase:firebase-crashlytics:19.3.0")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.google.firebase:firebase-analytics:22.1.2")
    implementation ("com.google.firebase:firebase-auth:23.1.0")
    implementation ("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation ("com.google.firebase:firebase-storage:21.0.1")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation ("com.google.firebase:firebase-config-ktx:22.0.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:3.0.1")
    implementation ("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

    //Glide Use for ViewImage
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    //5 Image Select to Gallery
    implementation ("io.github.ParkSangGwon:tedimagepicker:1.2.7")
    // 5 Image Select to Gallery
    implementation ("com.github.zhihu:Matisse:0.5.3-beta3")

    //google login
    //implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.google.android.gms:play-services-auth:21.   3.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    //VIDEO PLAY
    implementation ("androidx.media3:media3-exoplayer:1.0.0")
    implementation ("androidx.media3:media3-ui:1.0.0")
    implementation ("androidx.media3:media3-exoplayer-dash:1.0.0")
    implementation ("androidx.media3:media3-exoplayer-hls:1.0.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    //Branch.io Use for Share
    implementation("io.branch.sdk.android:library:5.+")

    //circle Imageview
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.caverock:androidsvg:1.4")

    //Health Connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha11")

    //Watch Data Sync
    implementation ("com.google.android.gms:play-services-wearable:19.0.0")
    implementation ("androidx.wear:wear:1.3.0")

    //view model
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("androidx.activity:activity-ktx:1.8.2")

    //Health Connect
    implementation ("androidx.health.connect:connect-client:1.1.0-alpha11")

    //Samsung Health Connect
    implementation (files("libs/samsung-health-data-api-1.0.0-b2.aar"))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")


   // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
   // testImplementation("io.mockk:mockk:1.13.5")
    //implementation("com.samsung.android.sdk.healthdata:health-data:1.6.1")

}

