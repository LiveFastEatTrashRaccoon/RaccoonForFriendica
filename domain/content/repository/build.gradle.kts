plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.koinWithKsp")
    id("com.livefast.eattrash.test")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktorfit.lib)
                implementation(libs.ktorfit.converters.response)

                implementation(projects.core.api)
                implementation(projects.core.utils)

                implementation(projects.core.persistence)
                implementation(projects.domain.content.data)
            }
        }
    }
}
