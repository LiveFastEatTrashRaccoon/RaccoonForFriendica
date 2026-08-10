package extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.koin.compiler.plugin.KoinGradleExtension
import utils.dependency
import utils.libs
import utils.pluginId

interface CustomDiExtension {
    fun useCompose(withViewModels: Boolean = false)
    fun useAnnotations()
    fun useCompilerPlugin(enableValidation: Boolean = true)
}

internal fun Project.configureDi(extension: KotlinMultiplatformExtension) {
    extensions.create(
        CustomDiExtension::class.java,
        "customDiExtension",
        CustomDiExtensionImpl::class.java,
        this,
    )
    extension.apply {
        sourceSets.apply {
            commonMain {
                dependencies {
                    implementation(project.dependencies.platform(libs.findLibrary("koin-bom").dependency))
                    implementation(libs.findLibrary("koin-core").dependency)
                }
            }
        }
    }
}

open class CustomDiExtensionImpl(private val target: Project) : CustomDiExtension {
    override fun useCompose(withViewModels: Boolean) {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.findLibrary("koin-compose").dependency)
                    if (withViewModels) {
                        implementation(libs.findLibrary("koin-compose-viewmodel").dependency)
                    }
                }
            }
        }
    }

    override fun useAnnotations() {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.findLibrary("koin-annotations").dependency)
                }
            }
        }
    }

    override fun useCompilerPlugin(enableValidation: Boolean) {
        with(target) {
            pluginManager.apply(libs.findPlugin("koin-compiler").pluginId)
            if (!enableValidation) {
                extensions.configure(KoinGradleExtension::class.java) {
                    compileSafety.set(false)
                }
            }
        }
    }
}
