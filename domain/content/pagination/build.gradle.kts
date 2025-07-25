plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.kotlinx.coroutines)

                implementation(projects.core.notifications)
                implementation(projects.core.utils)

                implementation(projects.domain.identity.data)
                implementation(projects.domain.identity.repository)
                implementation(projects.domain.content.data)
                implementation(projects.domain.content.repository)
            }
        }
    }
}
