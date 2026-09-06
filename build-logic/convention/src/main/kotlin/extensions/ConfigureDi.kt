package extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

interface CustomDiExtension {
    fun useCompose(withViewModels: Boolean = false)
}

internal fun Project.configureDi() {
    extensions.create(
        CustomDiExtension::class.java,
        "customDiExtension",
        CustomDiExtensionImpl::class.java,
        this,
    )
}

open class CustomDiExtensionImpl(private val target: Project) : CustomDiExtension {
    override fun useCompose(withViewModels: Boolean) {
        with(target) {
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").dependencies {
                    if (withViewModels) {
                        implementation(libs.findLibrary("metrox-viewmodel-compose").dependency)
                    }
                }
            }
        }
    }
}
