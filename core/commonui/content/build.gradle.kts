plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.calf)
                implementation(libs.koin.core)
                implementation(libs.ksoup.html)
                implementation(libs.voyager.navigator)

                implementation(projects.core.appearance)
                implementation(projects.core.commonui.components)
                implementation(projects.core.htmlparse)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
            }
        }
    }
}
