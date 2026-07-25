plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.koin.test)

                implementation(projects.core.di)
            }
        }
    }
}
