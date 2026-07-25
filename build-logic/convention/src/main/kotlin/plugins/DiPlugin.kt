package plugins

import extensions.configureDi
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DiPlugin : Plugin<Project>  {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
               configureDi(this)
            }
        }
    }
}
