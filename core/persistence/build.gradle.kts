plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.spotless")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kodein)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.room.sqlite)
                implementation(libs.room.runtime)

                implementation(projects.core.appearance)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)
            }
        }
        val iosMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata")
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.ksp)
    add("kspIosX64", libs.room.ksp)
    add("kspIosArm64", libs.room.ksp)
    add("kspIosSimulatorArm64", libs.room.ksp)
}
