plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.kodein)

                implementation(projects.core.appearance)
                implementation(projects.core.architecture)
                implementation(projects.core.commonui.components)
                implementation(projects.core.commonui.content)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.notifications)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
                implementation(projects.domain.content.pagination)
                implementation(projects.domain.content.repository)
                implementation(projects.domain.content.usecase)
                implementation(projects.domain.identity.data)
                implementation(projects.domain.identity.repository)
                implementation(projects.domain.identity.usecase)
            }
        }
    }
}
