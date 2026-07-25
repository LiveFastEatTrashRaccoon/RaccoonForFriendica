package extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

interface CustomDiExtension {
    fun useViewModels()
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
    override fun useViewModels() {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.findLibrary("koin-compose-viewmodel").dependency)
                }
            }
        }
    }
}
