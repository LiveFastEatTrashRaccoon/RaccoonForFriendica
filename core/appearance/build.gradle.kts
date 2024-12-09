plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.koinWithKsp")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.materialKolor)

                implementation(projects.core.l10n)
                implementation(projects.core.resources)
            }
        }
    }
}
