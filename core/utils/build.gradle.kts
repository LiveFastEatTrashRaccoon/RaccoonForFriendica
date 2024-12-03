import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Properties

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
            baseName = "core.utils"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)

                implementation(libs.koin.core)
                implementation(libs.ktor.cio)
                implementation(libs.coil)
                implementation(libs.coil.network.ktor)
                implementation(libs.connectivity.core)
                implementation(libs.connectivity.device)
                implementation(libs.sentry)

                implementation(projects.core.l10n)
                implementation(projects.core.preferences)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.activity)
                implementation(libs.androidx.browser)
                implementation(libs.ktor.android)
                implementation(libs.coil.gif)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.darwin)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
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
    namespace = "com.livefast.eattrash.raccoonforfriendica.core.utils"
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

    buildTypes {
        all {
            val props = loadProperties("build.properties")
            resValue("string", "sentry_dsn", props.getProperty("sentry_dsn"))
        }
    }
}

private fun loadProperties(name: String): Properties =
    File("$projectDir/$name").inputStream().use {
        Properties().apply { load(it) }
    }

kotlin.sourceSets.commonMain.configure {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
