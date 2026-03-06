plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.sentryDsn")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coil)
                implementation(libs.coil.network.ktor)
                implementation(libs.connectivity.core)
                implementation(libs.kodein)
                implementation(libs.ktor.cio)

                implementation(projects.core.di)
                implementation(projects.core.l10n)
                implementation(projects.core.preferences)
            }
        }
        val androidMain by getting {
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
                implementation(libs.sentry)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.connectivity.device)
                implementation(libs.ktor.darwin)
                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
            }
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
