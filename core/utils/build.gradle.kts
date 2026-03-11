plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.sentryDsn")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

// generate a properties file so the JVM can read the version at runtime
val generateVersionProperties by tasks.registering {
    val versionName = rootProject.findProperty("versionName")?.toString() ?: "0.0.1"
    val outputDir = layout.buildDirectory.dir("generated/app-info")
    val outputFile = outputDir.map { it.file("version.properties") }
    inputs.property("version", versionName)
    outputs.dir(outputDir)
    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText("version=$versionName")
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coil)
                implementation(libs.coil.network.ktor)
                implementation(libs.connectivity.core)
                implementation(libs.kodein)
                implementation(libs.ktor.cio)
                implementation(libs.sentry)

                implementation(projects.core.di)
                implementation(projects.core.l10n)
                implementation(projects.core.preferences)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.activity)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.browser)
                implementation(libs.coil.gif)
                implementation(libs.connectivity.device)
                implementation(libs.ktor.android)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.connectivity.device)
                implementation(libs.ktor.darwin)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
            }
        }
        jvmMain {
            resources.srcDir(generateVersionProperties)
        }
    }
}

spotless {
    kotlin {
        target("**/FileSystemManager.kt", "**/GalleryHelper.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:naming-check"
        }
    }
}
