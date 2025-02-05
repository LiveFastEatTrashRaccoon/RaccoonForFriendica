import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mokkery)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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
            implementation(libs.compose.multiplatform.media.player)
            implementation(libs.kodein)
            implementation(libs.ktor.client.core)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transition)
            implementation(libs.voyager.tab)
            implementation(libs.voyager.kodein)

            implementation(projects.core.api)
            implementation(projects.core.appearance)
            implementation(projects.core.architecture)
            implementation(projects.core.di)
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
            implementation(projects.domain.content.usecase)
            implementation(projects.domain.identity.data)
            implementation(projects.domain.identity.repository)
            implementation(projects.domain.identity.usecase)
            implementation(projects.domain.pullnotifications)
            implementation(projects.domain.pushnotifications)
            implementation(projects.domain.urlhandler)

            implementation(projects.feature.acknowledgements)
            implementation(projects.feature.announcements)
            implementation(projects.feature.calendar)
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
            implementation(projects.feature.licences)
            implementation(projects.feature.login)
            implementation(projects.feature.manageblocks)
            implementation(projects.feature.nodeinfo)
            implementation(projects.feature.profile)
            implementation(projects.feature.report)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.shortcuts)
            implementation(projects.feature.thread)
            implementation(projects.feature.timeline)
            implementation(projects.feature.unpublished)
            implementation(projects.feature.userdetail)
            implementation(projects.feature.userlist)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
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
        versionCode = 84
        versionName = "0.4.1-beta10"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
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
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

dependencies {
    kover(projects.core.appearance)
    kover(projects.core.navigation)
    kover(projects.core.notifications)
    kover(projects.core.preferences)
    kover(projects.core.utils)
    kover(projects.domain.content.pagination)
    kover(projects.domain.content.repository)
    kover(projects.domain.content.usecase)
    kover(projects.domain.identity.repository)
    kover(projects.domain.identity.usecase)
    kover(projects.domain.pushnotifications)
    kover(projects.domain.urlhandler)
    kover(projects.feature.nodeinfo)
}

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                packages(
                    "*.resources",
                    "*.di",
                    "*.auth",
                    "*.bottomnavigation",
                    "*.core.appearance.data",
                    "*.core.appearance.theme",
                    "*.core.utils.appicon",
                    "*.core.utils.compose",
                    "*.core.utils.datetime",
                    "*.core.utils.calendar",
                    "*.core.utils.debug",
                    "*.core.utils.fs",
                    "*.core.utils.gallery",
                    "*.core.utils.imageload",
                    "*.core.utils.network",
                    "*.core.utils.share",
                    "*.core.utils.url",
                    "*.core.utils.uuid",
                    "*.core.utils.validation",
                    "*.core.utils.vibrate",
                    "*.pushnotifications.receiver",
                    "*.pushnotifications.utils",
                )
            }
        }
    }
}
