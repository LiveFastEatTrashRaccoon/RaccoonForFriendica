plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.contentnegotiation)
                implementation(libs.ktor.json)
                implementation(libs.ktor.serialization)

                implementation(projects.core.di)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.ktor.mock)
            }
        }
    }
}
