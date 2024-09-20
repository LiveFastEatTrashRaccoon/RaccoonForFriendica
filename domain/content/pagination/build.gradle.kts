import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "domain.lemmy.pagination"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines)

                implementation(projects.core.notifications)
                implementation(projects.core.utils)

                implementation(projects.domain.identity.repository)
                implementation(projects.domain.content.data)
                implementation(projects.domain.content.repository)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test-junit"))
                implementation(libs.mockk)
            }
        }
    }
}

android {
    namespace = "com.livefast.eattrash.raccoonforfriendica.domain.lemmy.pagination"
    compileSdk =
        libs.versions.android.targetSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
}
