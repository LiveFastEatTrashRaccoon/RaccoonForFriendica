package plugins

import extensions.configureUiTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class UiTestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            extensions.configure(
                KotlinMultiplatformExtension::class.java,
                ::configureUiTest,
            )
        }
}
