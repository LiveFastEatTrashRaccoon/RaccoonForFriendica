package utils

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * Accessor for the default "libs" [VersionCatalog].
 */
internal val Project.libs: VersionCatalog get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Retrieves a plugin ID from the version catalog by its [alias].
 */
internal fun VersionCatalog.getPluginId(alias: String): String = findPlugin(alias).get().get().pluginId

/**
 * Retrieves a version value from the version catalog by its [alias] as an [Int].
 */
internal fun VersionCatalog.getVersion(alias: String): Int = findVersion(alias).get().requiredVersion.toInt()

/**
 * Retrieves a library dependency provider from the version catalog by its [alias].
 */
internal fun VersionCatalog.getLibrary(alias: String): Provider<MinimalExternalModuleDependency> = findLibrary(alias).get()

/**
 * Retrieves an external module dependency bundle provider from the version catalog by its [alias].
 */
internal fun VersionCatalog.getBundle(alias: String): Provider<ExternalModuleDependencyBundle> = findBundle(alias).get()
