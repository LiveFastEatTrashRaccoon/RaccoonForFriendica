package extensions

import org.gradle.api.Project
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getBundle
import utils.libs

internal fun Project.configureComposeMultiplatform(extension: KotlinMultiplatformExtension) =
    extension.apply {
        val composeDeps = extensions.getByType(ComposePlugin.Dependencies::class.java)
        sourceSets.apply {
            commonMain {
                dependencies {
                    implementation(libs.getBundle("compose-ui"))
                    implementation(libs.getBundle("compose-adaptive"))
                    implementation(libs.getBundle("compose-navigation"))
                }
            }
            jvmMain {
                dependencies {
                    implementation(composeDeps.desktop.currentOs)
                }
            }
        }
    }
