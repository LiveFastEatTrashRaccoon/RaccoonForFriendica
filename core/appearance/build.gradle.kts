plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.serialization")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.activity)
            }
        }
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.materialKolor)

                implementation(projects.core.di)
                implementation(projects.core.l10n)
                implementation(projects.core.resources)
            }
        }
    }
}

spotless {
    kotlin {
        target("**/BarColorProvider.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:naming-check"
        }
        target("**/Colors.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:property-naming"
        }
    }
}
