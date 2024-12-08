plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.koinWithKsp")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.lyricist)
            }
        }
    }
}
