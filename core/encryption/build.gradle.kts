plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.junit.ktx)
                implementation(libs.espresso)
                implementation(libs.junit)
                implementation(kotlin("test"))
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.cryptography.core)
                implementation(libs.cryptography.provider.optimal)
                implementation(libs.kodein)
                implementation(libs.kotlinx.coroutines)
                implementation(projects.core.di)
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.compilations.getByName("main") {
            cinterops {
                val security by creating {
                    packageName("core.encryption")
                }
            }
        }
    }
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
