plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.components.resources)
                implementation(libs.compose.multiplatform.media.player)

                implementation(projects.core.di)
            }
        }
    }
}

customDiExtension {
    useCompose()
}

spotless {
    kotlin {
        target("**/ProvideResources.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:compositionlocal-allowlist"
        }
    }
}
