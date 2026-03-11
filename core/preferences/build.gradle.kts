plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.multiplatform.settings)
                implementation(libs.androidx.security.crypto)
            }
        }
        commonMain {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.multiplatform.settings)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}
