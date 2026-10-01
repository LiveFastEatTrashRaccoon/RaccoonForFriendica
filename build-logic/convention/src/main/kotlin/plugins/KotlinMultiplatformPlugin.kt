package plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import extensions.configureKotlinMultiplatform
import extensions.configureKotlinMultiplatformAndroidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.getPluginId
import utils.libs

class KotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("kotlin-multiplatform"))
                apply(libs.getPluginId("android-kmp-library"))
            }

            extensions.configure(KotlinMultiplatformExtension::class.java) {
                configureKotlinMultiplatform(this)

                compilerOptions {
                    freeCompilerArgs.addAll(
                        buildList {
                            this += "-Xexpect-actual-classes"
                            this += "-Xreturn-value-checker=check"
                            this += "-Xname-based-destructuring=only-syntax"
                        }
                    )
                }

                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    configureKotlinMultiplatformAndroidLibrary(this)
                }
            }
        }
}
