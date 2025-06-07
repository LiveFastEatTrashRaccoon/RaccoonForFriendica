plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.kodein)
                implementation(projects.core.di)
            }
        }
    }
}
