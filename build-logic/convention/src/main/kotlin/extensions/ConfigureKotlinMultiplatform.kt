package extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

interface CustomKotlinMultiplatformExtension {
    val additionalLinkerOptionForIos: Property<String>
}

internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) =
    extension.apply {
        applyDefaultHierarchyTemplate()
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        val moduleName = path.split(":").drop(1).joinToString(".")
        val customExtension =
            project.extensions
                .create<CustomKotlinMultiplatformExtension>("customKotlinMultiplatformExtension")
        val linkerOption = customExtension.additionalLinkerOptionForIos.getOrElse("")
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach {
            it.binaries.framework {
                baseName = moduleName
                isStatic = true
                if (linkerOption.isNotEmpty()) {
                    linkerOpts.add(linkerOption)
                }
            }
        }
    }
