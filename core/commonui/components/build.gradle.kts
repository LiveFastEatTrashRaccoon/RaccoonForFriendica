plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coil.compose)
                implementation(libs.compose.colorpicker)
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.kodein)

                implementation(projects.core.appearance)
                implementation(projects.core.di)
                implementation(projects.core.l10n)
                implementation(projects.core.l10n.testutils)
                implementation(projects.core.resources)
                implementation(projects.core.resources.testutils)
                implementation(projects.core.utils)
            }
        }
    }
}
