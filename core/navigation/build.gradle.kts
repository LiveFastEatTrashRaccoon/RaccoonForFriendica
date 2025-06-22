plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kodein)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tab)

            implementation(projects.core.di)
            implementation(projects.core.utils)
            implementation(projects.domain.content.data)
        }
    }
}
