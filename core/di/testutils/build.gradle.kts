plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.kodein)
                implementation(projects.core.di)
            }
        }
    }
}
