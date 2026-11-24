plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.detekt")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.l10n)
                implementation(projects.core.utils)
                implementation(projects.core.resources)
            }
        }
    }
}
