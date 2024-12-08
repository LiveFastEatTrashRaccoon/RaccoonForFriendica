package extensions

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.dependency
import utils.libs

internal fun Project.configureTest(extension: KotlinMultiplatformExtension) =
    extension.apply {
        sourceSets.apply {
            commonTest {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.findLibrary("kotlinx-coroutines-test").dependency)
                    implementation(libs.findLibrary("turbine").dependency)
                }
            }
            androidUnitTest {
                dependencies {
                    implementation(project(":core:testutils"))
                }
            }
        }
    }
