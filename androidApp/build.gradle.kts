
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    id("com.livefast.eattrash.spotless")
}

android {
    namespace = "com.livefast.eattrash.raccoonforfriendica"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.livefast.eattrash.raccoonforfriendica"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = (project.findProperty("buildNumber") as? String)?.toInt() ?: 1
        versionName = project.findProperty("versionName") as? String ?: "1.0.0"
    }
    base.archivesName = "RaccoonForFriendica"
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = File(projectDir, "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEYSTORE_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        getByName("debug") {
            resValue("string", "app_name", "Raccoon (dev)")
            applicationIdSuffix = ".dev"
        }
        getByName("release") {
            resValue("string", "app_name", "Raccoon")
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                ),
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        resValues = true
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }
    dependenciesInfo {
        includeInApk = false
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(projects.shared)
    implementation(projects.core.appearance)
    implementation(projects.core.di)
    implementation(projects.core.utils)
    implementation(projects.core.navigation)
    implementation(projects.core.persistence)
    implementation(projects.core.resources)
    implementation(projects.domain.content.repository)
    implementation(projects.domain.identity.repository)
    implementation(projects.domain.pushnotifications)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
