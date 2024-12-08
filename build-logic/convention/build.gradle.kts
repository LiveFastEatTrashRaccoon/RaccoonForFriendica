plugins {
    `kotlin-dsl`
}

group = "com.livefast.eattrash.raccoonforfriendica.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.gradle)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    implementation(libs.kotlinpoet)
}

gradlePlugin {
    plugins {
        register("sentryDsn") {
            id = "com.livefast.eattrash.sentryDsn"
            implementationClass = "plugins.SentryDsnPlugin"
        }

        register("composeMultiplatform") {
            id = "com.livefast.eattrash.composeMultiplatform"
            implementationClass = "plugins.ComposeMultiplatformPlugin"
        }

        register("koinWithKsp") {
            id = "com.livefast.eattrash.koinWithKsp"
            implementationClass = "plugins.KoinWithKspPlugin"
        }

        register("kotlinMultiplatform") {
            id = "com.livefast.eattrash.kotlinMultiplatform"
            implementationClass = "plugins.KotlinMultiplatformPlugin"
        }

        register("serializationPlugin") {
            id = "com.livefast.eattrash.serialization"
            implementationClass = "plugins.SerializationPlugin"
        }

        register("testPlugin") {
            id = "com.livefast.eattrash.test"
            implementationClass = "plugins.TestPlugin"
        }

        register("uiTestPlugin") {
            id = "com.livefast.eattrash.uiTest"
            implementationClass = "plugins.UiTestPlugin"
        }
    }
}
