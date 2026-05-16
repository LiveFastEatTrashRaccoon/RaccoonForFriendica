import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    alias(libs.plugins.compose.desktop.linux.deps)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.shared)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.livefast.eattrash.raccoonforfriendica.Main"
        nativeDistributions {
            javaHome = System.getenv("JAVA_HOME")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Raccoon"
            packageVersion = (rootProject.properties["versionName"] as? String)?.substringBefore("-")
            version = (rootProject.properties["buildNumber"] as? Int) ?: 1
            includeAllModules = true
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
            }
            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}

linuxDebConfig {
    // set StartupWMClass to fix taskbar icon
    startupWMClass.set("Main")

    // for t64 dependencies compatibility with older OSes
    enableT64AlternativeDeps.set(true)
}
