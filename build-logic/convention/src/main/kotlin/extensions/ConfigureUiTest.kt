package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getBundle
import utils.getLibrary
import utils.libs

internal fun Project.configureUiTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        sourceSets.apply {
            configureEach {
                when (name) {
                    "androidHostTest" -> {
                        dependencies {
                            implementation(libs.getBundle("compose-test"))
                            implementation(libs.getLibrary("robolectric"))
                        }
                    }
                    "androidDeviceTest" -> {
                        dependencies {
                            implementation(kotlin("test"))
                            implementation(libs.getBundle("androidx-device-test"))
                            implementation(libs.getLibrary("espresso"))
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
