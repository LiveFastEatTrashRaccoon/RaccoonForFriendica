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
                implementation(libs.ktorfit.lib)
                implementation(libs.ktorfit.converters.response)

                implementation(projects.core.api)
                implementation(projects.core.di)
                implementation(projects.core.utils)

                implementation(projects.core.persistence)
                implementation(projects.domain.content.data)
            }
        }
    }
}
