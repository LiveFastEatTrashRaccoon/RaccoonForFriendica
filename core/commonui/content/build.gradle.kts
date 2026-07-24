plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.calf)
                implementation(libs.ksoup.html)

                implementation(projects.core.appearance)
                implementation(projects.core.di)
                implementation(projects.core.commonui.components)
                implementation(projects.core.htmlparse)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.resources)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
            }
        }
    }
}
