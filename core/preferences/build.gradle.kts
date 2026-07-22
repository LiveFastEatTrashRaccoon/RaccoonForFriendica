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
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}
