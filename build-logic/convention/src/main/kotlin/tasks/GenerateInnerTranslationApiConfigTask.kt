package tasks

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import utils.PACKAGE_PREFIX
import java.io.File
import java.util.Properties

open class GenerateInnerTranslationApiConfigTask : DefaultTask() {
    private val inputDirectory: File = project.projectDir.absoluteFile

    private val outputDirectory: File =
        project.file("${project.layout.buildDirectory.asFile.get().path}/generated/custom")

    @TaskAction
    fun generateSentryConfig() {
        val props = loadProperties()
        val url = props.getProperty("inner_translation_api_url")
        val file =
            FileSpec
                .builder(packageName = PACKAGE_PREFIX, fileName = "InnerTranslationApiConfigurationValues")
                .addType(
                    TypeSpec
                        .objectBuilder(name = "InnerTranslationApiConfigurationValues")
                        .addProperty(
                            PropertySpec
                                .builder(name = "URL", type = String::class)
                                .initializer(format = "%S", url)
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
