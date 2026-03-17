package extensions

import org.gradle.api.Project
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

internal fun Project.configureComposeMultiplatform(extension: KotlinMultiplatformExtension) =
    extension.apply {
        val composeDeps = extensions.getByType(ComposePlugin.Dependencies::class.java)
        sourceSets.apply {
            commonMain {
                dependencies {
                    implementation(libs.findLibrary("compose-runtime").dependency)
                    implementation(libs.findLibrary("compose-foundation").dependency)
                    implementation(libs.findLibrary("compose-m3").dependency)
                    implementation(libs.findLibrary("compose-m3-adaptive").dependency)
                    implementation(libs.findLibrary("compose-m3-adaptive-layout").dependency)
                    implementation(libs.findLibrary("compose-m3-adaptive-navigation").dependency)
                    implementation(libs.findLibrary("compose-m3-wsc").dependency)
                    implementation(libs.findLibrary("androidx-lifecycle-viewmodel-compose").dependency)
                    implementation(libs.findLibrary("compose-ui-backhandler").dependency)
                }
            }
            jvmMain {
                dependencies {
                    implementation(composeDeps.desktop.currentOs)
                }
            }
        }
    }
