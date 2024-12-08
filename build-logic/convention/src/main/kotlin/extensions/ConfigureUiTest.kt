package extensions

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import utils.dependency
import utils.libs

internal fun Project.configureUiTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        val composeDeps = extensions.getByType(ComposePlugin.Dependencies::class.java)
        sourceSets.apply {
            androidUnitTest {
                dependencies {
                    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                    implementation(composeDeps.uiTest)
                    implementation(libs.findLibrary("compose-ui-test").dependency)
                    implementation(libs.findLibrary("robolectric").dependency)
                    implementation(project(":core:testutils"))
                }
            }
        }
        androidTarget {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        }

        dependencies {
            add("debugImplementation", libs.findLibrary("compose-ui-test-manifest").dependency)
        }
    }

internal fun Project.configureUiTestAndroid(extension: LibraryExtension) =
    extension.apply {
        defaultConfig {
            testOptions.unitTests.isIncludeAndroidResources = true
            testInstrumentationRunner = "org.robolectric.RobolectricTestRunner"
        }
    }
