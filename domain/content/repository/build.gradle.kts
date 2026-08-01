plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.ktor.client.core)

                implementation(projects.core.api)
                implementation(projects.core.translation)
                implementation(projects.core.utils)

                implementation(projects.core.persistence)
                implementation(projects.domain.content.data)
            }
        }
    }
}
