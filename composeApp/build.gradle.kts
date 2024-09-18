import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.splashscreen)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.coil)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.lyricist)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transition)
            implementation(libs.voyager.tab)

            implementation(projects.core.api)
            implementation(projects.core.appearance)
            implementation(projects.core.architecture)
            implementation(projects.core.commonui.components)
            implementation(projects.core.commonui.content)
            implementation(projects.core.l10n)
            implementation(projects.core.navigation)
            implementation(projects.core.notifications)
            implementation(projects.core.persistence)
            implementation(projects.core.preferences)
            implementation(projects.core.resources)
            implementation(projects.core.utils)

            implementation(projects.domain.content.data)
            implementation(projects.domain.content.pagination)
            implementation(projects.domain.content.repository)
            implementation(projects.domain.identity.data)
            implementation(projects.domain.identity.repository)
            implementation(projects.domain.identity.usecase)

            implementation(projects.feature.circles)
            implementation(projects.feature.composer)
            implementation(projects.feature.directmessages)
            implementation(projects.feature.drawer)
            implementation(projects.feature.entrydetail)
            implementation(projects.feature.explore)
            implementation(projects.feature.favorites)
            implementation(projects.feature.followrequests)
            implementation(projects.feature.gallery)
            implementation(projects.feature.hashtag)
            implementation(projects.feature.imagedetail)
            implementation(projects.feature.inbox)
            implementation(projects.feature.login)
            implementation(projects.feature.manageblocks)
            implementation(projects.feature.nodeinfo)
            implementation(projects.feature.profile)
            implementation(projects.feature.report)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.thread)
            implementation(projects.feature.timeline)
            implementation(projects.feature.unpublished)
            implementation(projects.feature.userdetail)
            implementation(projects.feature.userlist)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.livefast.eattrash.raccoonforfriendica"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

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
        versionCode = 27
        versionName = "0.1.0-alpha27"
    }
    base.archivesName = "RaccoonForFriendica"
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}
