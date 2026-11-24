package plugins

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.getPluginId
import utils.libs

class DetektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("detekt"))
            }
            extensions.configure(DetektExtension::class.java) {
                // common ruleset
                config.setFrom(rootProject.file("detekt/detekt.yml"))
                // per-module baselines
                baseline.set(file("detekt-baseline.xml"))
            }

            // ensure :<module>:detekt runs all detekt tasks in that module
            tasks.named("detekt").configure {
                dependsOn(
                    tasks.withType(Detekt::class.java).matching {
                        it != this && !it.name.contains("SourceSet")
                    },
                )
            }
            // ensure ':<module>:detektBaseline' runs all baseline tasks in that module
            tasks.named("detektBaseline").configure {
                dependsOn(
                    tasks.withType(DetektCreateBaselineTask::class.java).matching {
                        it != this && !it.name.contains("SourceSet")
                    },
                )
            }
            val hasPlatformSources = file("src/androidMain").exists() || file("src/jvmMain").exists()
            tasks.withType(Detekt::class.java).configureEach {
                // exclude generated files / build directory
                setSource(source.filter { file -> !file.path.contains("/build/") })

                // setup platform-specific baselines
                val specificBaseline = when {
                    name.endsWith("MainAndroid") -> file("detekt-baseline-main-android.xml")
                    name.endsWith("MainJvm") -> file("detekt-baseline-main-jvm.xml")
                    else -> null
                }
                if (specificBaseline != null) {
                    val mainBaseline = file("detekt-baseline-main.xml")
                    if (specificBaseline.exists() || (hasPlatformSources && !mainBaseline.exists())) {
                        baseline.set(specificBaseline)
                    }
                }
            }
            tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
                setSource(source.filter { file -> !file.path.contains("/build/") })
                if (hasPlatformSources) {
                    when {
                        name.endsWith("MainAndroid") -> baseline.set(file("detekt-baseline-main-android.xml"))
                        name.endsWith("MainJvm") -> baseline.set(file("detekt-baseline-main-jvm.xml"))
                    }
                }
            }
        }
    }
}
