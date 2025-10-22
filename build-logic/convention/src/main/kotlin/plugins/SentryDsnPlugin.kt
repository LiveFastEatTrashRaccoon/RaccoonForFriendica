package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tasks.GenerateSentryConfigTask

class SentryDsnPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            val codeGenerator =
                project.tasks
                    .register("generateSentryConfig", GenerateSentryConfigTask::class.java)
                    .apply {
                        group = "generation"
                        description = "Generate Sentry configuration file"
                    }
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.apply {
                    commonMain {
                        kotlin.srcDir("build/generated/custom")
                    }
                }
            }
            tasks.withType(KotlinCompile::class.java).configureEach {
                dependsOn(codeGenerator)
            }
        }
}
