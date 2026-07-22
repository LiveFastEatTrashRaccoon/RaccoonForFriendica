plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.ktor.client.core)

                implementation(projects.core.appearance)
                implementation(projects.core.architecture)
                implementation(projects.core.commonui.components)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.resources)
                implementation(projects.core.utils)
            }
        }
    }
}
