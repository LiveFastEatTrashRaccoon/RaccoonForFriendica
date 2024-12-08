plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.koinWithKsp")
    id("com.livefast.eattrash.test")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.stately.common)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tab)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)

            implementation(projects.domain.content.data)
        }
    }
}
