plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.spotless")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    sourceSets {
        commonMain {
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
        iosMain {
            kotlin.srcDir("build/generated/ksp/metadata")
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.ksp)
    add("kspJvm", libs.room.ksp)
    add("kspIosArm64", libs.room.ksp)
    add("kspIosSimulatorArm64", libs.room.ksp)
}
