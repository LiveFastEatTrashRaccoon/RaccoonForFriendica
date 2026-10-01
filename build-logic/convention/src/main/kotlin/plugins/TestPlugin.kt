package plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import extensions.configureTest
import extensions.configureTestAndroidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getPluginId
import utils.libs

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("kotlinx-kover"))
                apply(libs.getPluginId("ksp"))
                apply(libs.getPluginId("mokkery"))
            }
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                configureTest(this)

                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    configureTestAndroidLibrary(this)
                }
            }
        }
}
