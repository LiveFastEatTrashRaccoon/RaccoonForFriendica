plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.koinWithKsp")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)

                implementation(projects.core.appearance)
                implementation(projects.core.architecture)
                implementation(projects.core.commonui.components)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.utils)
            }
        }
    }
}
