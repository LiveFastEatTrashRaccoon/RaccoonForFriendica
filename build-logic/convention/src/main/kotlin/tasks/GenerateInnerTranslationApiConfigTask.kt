package tasks

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import utils.PACKAGE_PREFIX
import java.io.File
import java.util.Properties

private const val OUTPUT_CLASS_NAME = "InnerTranslationApiConfigurationValues"

open class GenerateInnerTranslationApiConfigTask : DefaultTask() {
    private val inputDirectory: File = project.projectDir.absoluteFile

    private val outputDirectory: File = project.file("build/generated/custom")

    @TaskAction
    fun generateSentryConfig() {
        val props = loadProperties()
        val file =
            FileSpec
                .builder(packageName = PACKAGE_PREFIX, fileName = OUTPUT_CLASS_NAME)
                .addType(
                    TypeSpec
                        .objectBuilder(name = OUTPUT_CLASS_NAME)
                        .addProperty(
                            PropertySpec
                                .builder(name = "TRANSLATION_API_URL", type = String::class)
                                .initializer(format = "%S", props.getProperty("translation.api.url"))
                                .build(),
                        )
                        .addProperty(
                            PropertySpec
                                .builder(name = "TRANSLATION_API_KEY", type = String::class)
                                .initializer(format = "%S", props.getProperty("translation.api.key"))
                                .build(),
                        )
                        .build(),
                ).build()
        file.writeTo(outputDirectory)
    }

    private fun loadProperties(): Properties =
        File(inputDirectory, "build.properties").inputStream().use {
            Properties().apply { load(it) }
        }
}
