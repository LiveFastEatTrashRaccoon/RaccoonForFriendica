package plugins

import extensions.configureTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.libs
import utils.pluginId

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("kotlinx-kover").pluginId)
                apply(libs.findPlugin("ksp").pluginId)
                apply(libs.findPlugin("mokkery").pluginId)
            }
            extensions.configure(
                KotlinMultiplatformExtension::class.java,
                ::configureTest,
            )
        }
}
