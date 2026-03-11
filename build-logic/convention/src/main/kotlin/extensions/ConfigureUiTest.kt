package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

internal fun Project.configureUiTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        val composeDeps = extensions.getByType(ComposePlugin.Dependencies::class.java)
        sourceSets.apply {
            androidUnitTest {
                dependsOn(commonTest.get())
                dependencies {
                    @OptIn(ExperimentalComposeLibrary::class)
                    implementation(composeDeps.uiTest)
                    implementation(libs.findLibrary("compose-ui-test").dependency)
                    implementation(libs.findLibrary("robolectric").dependency)
                    implementation(project(":core:testutils"))
                }
            }
            androidInstrumentedTest {
                dependsOn(commonTest.get())
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

        targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
            withDeviceTest {
                instrumentationRunner = "org.robolectric.RobolectricTestRunner"
            }
        }
    }
