plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.kodein)

                implementation(projects.core.di)
            }
        }
    }
}
