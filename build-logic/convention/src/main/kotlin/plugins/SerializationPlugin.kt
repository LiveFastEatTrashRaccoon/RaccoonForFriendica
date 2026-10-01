package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getLibrary
import utils.getPluginId
import utils.libs

class SerializationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("ksp"))
                apply(libs.getPluginId("kotlinx-serialization"))
            }
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                sourceSets.apply {
                    commonMain {
                        dependencies {
                            implementation(libs.getLibrary("kotlinx-serialization-json"))
                        }
                    }
                }
            }
        }
}
