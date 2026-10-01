package plugins

import extensions.configureDi
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.getPluginId
import utils.libs

class DiPlugin : Plugin<Project>  {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("metro"))
            }
            configureDi()
        }
    }
}
