package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

internal fun Project.configureTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        sourceSets.apply {
            commonTest {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.findLibrary("kotlinx-coroutines-test").dependency)
                    implementation(libs.findLibrary("turbine").dependency)
                }
            }

            configureEach {
                when (name) {
                    "androidHostTest", "androidDeviceTest" -> {
                        dependencies {
                            implementation(project(":core:testutils"))
                        }
                    }
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
