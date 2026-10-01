package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getLibrary
import utils.libs

internal fun Project.configureTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        sourceSets.apply {
            commonTest {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.getLibrary("kotlinx-coroutines-test"))
                    implementation(libs.getLibrary("turbine"))
                }
            }
        }
    }

internal fun Project.configureTestAndroidLibrary(extension: KotlinMultiplatformAndroidLibraryExtension) =
    extension.apply {
        withHostTest {
            isIncludeAndroidResources = true
        }
    }
