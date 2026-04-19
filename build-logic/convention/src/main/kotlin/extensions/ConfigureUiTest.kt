package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

internal fun Project.configureUiTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        sourceSets.apply {
            configureEach {
                when (name) {
                    "androidHostTest" -> {
                        dependencies {
                            implementation(libs.findLibrary("compose-ui-test").dependency)
                            implementation(libs.findLibrary("compose-ui-test-manifest").dependency)
                            implementation(libs.findLibrary("robolectric").dependency)
                            implementation(project(":core:testutils"))
                        }
                    }
                    "androidDeviceTest" -> {
                        dependencies {
                            implementation(kotlin("test"))
                            implementation(libs.findLibrary("androidx-test-core").dependency)
                            implementation(libs.findLibrary("androidx-test-runner").dependency)
                            implementation(libs.findLibrary("androidx-test-junit").dependency)
                            implementation(libs.findLibrary("androidx-test-junit-ktx").dependency)
                            implementation(libs.findLibrary("espresso").dependency)
                        }
                    }
                }
            }
        }
    }

internal fun Project.configureUiTestAndroidLibrary(extension: KotlinMultiplatformAndroidLibraryExtension) =
    extension.apply {
        withDeviceTest {
            instrumentationRunner = "org.robolectric.RobolectricTestRunner"
        }
    }
