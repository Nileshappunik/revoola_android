pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()

    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add RevenueCat's repository
       // maven { url = uri("https://github.com/RevenueCat/purchases-android/releases" )}
        maven { url = uri("https://jitpack.io" )}
    }
    versionCatalogs {
        create("moengage"){
            from("com.moengage:android-dependency-catalog:4.5.0")
        }
    }
}

include(":app")