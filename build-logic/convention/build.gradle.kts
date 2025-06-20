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
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    implementation(libs.kotlinpoet)
}

gradlePlugin {
    plugins {
        register("sentryDsn") {
            id = "com.livefast.eattrash.sentryDsn"
            implementationClass = "plugins.SentryDsnPlugin"
        }

        register("innerTranslationApi") {
            id = "com.livefast.eattrash.innerTranslationApi"
            implementationClass = "plugins.InnerTranslationApiPlugin"
        }

        register("composeMultiplatform") {
            id = "com.livefast.eattrash.composeMultiplatform"
            implementationClass = "plugins.ComposeMultiplatformPlugin"
        }

        register("kotlinMultiplatform") {
            id = "com.livefast.eattrash.kotlinMultiplatform"
            implementationClass = "plugins.KotlinMultiplatformPlugin"
        }

        register("serializationPlugin") {
            id = "com.livefast.eattrash.serialization"
            implementationClass = "plugins.SerializationPlugin"
        }

        register("spotlessPlugin") {
            id = "com.livefast.eattrash.spotless"
            implementationClass = "plugins.SpotlessPlugin"
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
