package plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import extensions.configureUiTest
import extensions.configureUiTestAndroidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class UiTestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                configureUiTest(this)

                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    configureUiTestAndroidLibrary(this)
                }
            }

            addOpensJvmArgsToTestTask()
        }

    private fun Project.addOpensJvmArgsToTestTask() {
        tasks.withType(Test::class.java).configureEach {
            jvmArgs(
                "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "--add-opens=java.base/java.util=ALL-UNNAMED",
            )
        }
    }
}
