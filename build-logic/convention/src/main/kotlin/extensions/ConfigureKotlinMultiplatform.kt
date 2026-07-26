package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import utils.PACKAGE_PREFIX
import utils.libs
import utils.version

interface CustomKotlinMultiplatformExtension {
    fun iosFramework(
        baseName: String? = null,
        linkerOptions: List<String> = emptyList(),
        exports: List<Any> = emptyList(),
    )
}

internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) =
    extension.apply {
        applyDefaultHierarchyTemplate()

        iosArm64()
        iosSimulatorArm64()

        project.extensions.create(
            CustomKotlinMultiplatformExtension::class.java,
            "customKotlinMultiplatformExtension",
            CustomKotlinMultiplatformExtensionImpl::class.java,
            project,
            this,
        )

        jvm()
    }

open class CustomKotlinMultiplatformExtensionImpl(
    private val target: Project,
    private val extension: KotlinMultiplatformExtension,
) : CustomKotlinMultiplatformExtension {
    override fun iosFramework(
        baseName: String?,
        linkerOptions: List<String>,
        exports: List<Any>,
    ) {
        val moduleName = target.path.split(":").drop(1).joinToString(".")
        extension.targets.withType(KotlinNativeTarget::class.java).configureEach {
            if (konanTarget.family.isAppleFamily) {
                binaries.framework {
                    this.baseName = baseName ?: moduleName
                    isStatic = true
                    linkerOpts.addAll(linkerOptions)
                    exports.forEach { export(it) }
                }
            }
        }
    }
}

internal fun Project.configureKotlinMultiplatformAndroidLibrary(target: KotlinMultiplatformAndroidLibraryTarget) =
    target.apply {
        val moduleName = path.split(":").drop(1).joinToString(".")
        namespace = if (moduleName.isNotEmpty()) "$PACKAGE_PREFIX.$moduleName" else PACKAGE_PREFIX

        compileSdk = libs.findVersion("android-compileSdk").version
        minSdk = libs.findVersion("android-minSdk").version

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        androidResources {
            enable = true
        }
    }
