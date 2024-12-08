package tasks

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import utils.PACKAGE_PREFIX
import java.io.File
import java.util.Properties

open class GenerateSentryConfigTask : DefaultTask() {
    private val inputDirectory: File = project.projectDir.absoluteFile

    private val outputDirectory: File =
        project.file("${project.layout.buildDirectory.asFile.get().path}/generated/custom")

    @TaskAction
    fun generateSentryConfig() {
        val props = loadProperties()
        val dsn = props.getProperty("sentry_dsn")
        val file =
            FileSpec
                .builder(packageName = PACKAGE_PREFIX, fileName = "SentryConfigurationValues")
                .addType(
                    TypeSpec
                        .objectBuilder(name = "SentryConfigurationValues")
                        .addProperty(
                            PropertySpec
                                .builder(name = "DSN", type = String::class)
                                .initializer(format = "%S", dsn)
                                .build(),
                        ).build(),
                ).build()
        file.writeTo(outputDirectory)
    }

    private fun loadProperties(): Properties =
        File(inputDirectory, "build.properties").inputStream().use {
            Properties().apply { load(it) }
        }
}
