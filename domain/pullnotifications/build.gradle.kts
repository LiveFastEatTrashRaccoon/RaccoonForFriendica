plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.work.runtime)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)

                implementation(projects.core.appearance)
                implementation(projects.core.di)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.preferences)
                implementation(projects.core.resources)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
                implementation(projects.domain.content.repository)
                implementation(projects.domain.identity.data)
                implementation(projects.domain.identity.repository)
            }
        }
    }
}
