plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.detekt")
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
                implementation(libs.multiplatform.settings)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}
