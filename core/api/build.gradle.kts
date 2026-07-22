plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.ktor.contentnegotiation)
                implementation(libs.ktor.auth)
                implementation(libs.ktor.json)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.serialization)

                implementation(projects.core.di)
                implementation(projects.core.utils)
            }
        }
    }
}
