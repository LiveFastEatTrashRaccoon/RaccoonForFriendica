plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.components.resources)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)

                implementation(projects.core.di)
            }
        }
    }
}

spotless {
    kotlin {
        target("**/ProvideStrings.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:compositionlocal-allowlist"
        }
    }
}
