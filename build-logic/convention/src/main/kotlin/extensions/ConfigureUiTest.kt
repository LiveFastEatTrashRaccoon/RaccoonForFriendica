package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
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

        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
            withDeviceTest {
                instrumentationRunner = "org.robolectric.RobolectricTestRunner"
            }
        }
    }
