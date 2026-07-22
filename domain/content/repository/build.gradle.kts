plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.ktor.client.core)

                implementation(projects.core.api)
                implementation(projects.core.di)
                implementation(projects.core.translation)
                implementation(projects.core.utils)

                implementation(projects.core.persistence)
                implementation(projects.domain.content.data)
            }
        }
    }
}
