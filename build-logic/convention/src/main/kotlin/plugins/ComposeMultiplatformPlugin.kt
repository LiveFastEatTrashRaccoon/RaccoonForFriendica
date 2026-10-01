package plugins

import extensions.configureComposeMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getLibrary
import utils.getPluginId
import utils.libs

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("jetbrains-compose"))
                apply(libs.getPluginId("compose-compiler"))
            }

            dependencies {
                add("androidRuntimeClasspath", libs.getLibrary("compose-ui-tooling"))
            }

            extensions.configure(KotlinMultiplatformExtension::class.java) {
                configureComposeMultiplatform(this)
            }
        }
}
