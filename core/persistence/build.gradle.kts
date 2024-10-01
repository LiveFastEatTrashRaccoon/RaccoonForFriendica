import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
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
            baseName = "core.persistence"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)

                implementation(libs.koin.core)
                implementation(libs.room.sqlite)
                implementation(libs.room.runtime)

                implementation(projects.core.appearance)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)
            }
        }
    }
}

android {
    namespace = "com.livefast.eattrash.raccoonforfriendica.core.persistence"
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

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.room.ksp)
    add("kspIosX64", libs.room.ksp)
    add("kspIosArm64", libs.room.ksp)
    add("kspIosSimulatorArm64", libs.room.ksp)
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.withType<KspTaskMetadata>())
}
