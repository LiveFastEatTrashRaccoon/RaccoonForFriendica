plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.multiplatform.settings)
                implementation(libs.androidx.security.crypto)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.multiplatform.settings)
            }
        }
    }
}
