plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.koin.core)
            }
        }
    }
}
