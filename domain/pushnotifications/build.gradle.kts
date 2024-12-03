import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mokkery)
}

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
            baseName = "domain.pushnotifications"
            isStatic = true
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.bouncycastle)
                implementation(libs.unifiedpush)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)

                implementation(libs.koin.core)
                api(libs.koin.annotations)
                implementation(libs.ktor.client.core)

                implementation(projects.core.api)
                implementation(projects.core.appearance)
                implementation(projects.core.l10n)
                implementation(projects.core.persistence)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
                implementation(projects.domain.content.repository)
                implementation(projects.domain.identity.data)
                implementation(projects.domain.identity.repository)
                implementation(projects.domain.pullnotifications)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
    add("kspAndroid", libs.koin.ksp)
    add("kspIosX64", libs.koin.ksp)
    add("kspIosArm64", libs.koin.ksp)
    add("kspIosSimulatorArm64", libs.koin.ksp)
}

android {
    namespace = "com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
}

kotlin.sourceSets.commonMain.configure {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
