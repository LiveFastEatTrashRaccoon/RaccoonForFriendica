plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.serialization")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.ktor.client.core)

                implementation(projects.core.api)
                implementation(projects.core.appearance)
                implementation(projects.core.di)
                implementation(projects.core.persistence)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
                implementation(projects.domain.identity.data)
            }
        }
    }
}
