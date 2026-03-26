import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.serialization")
    alias(libs.plugins.compose.desktop.linux.deps)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.mokkery)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.material)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)

                implementation(libs.androidx.navigation.compose)
                implementation(libs.coil)
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.kodein.compose)
                implementation(libs.ktor.client.core)

                implementation(projects.core.api)
                implementation(projects.core.appearance)
                implementation(projects.core.architecture)
                implementation(projects.core.di)
                implementation(projects.core.commonui.components)
                implementation(projects.core.commonui.content)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.notifications)
                implementation(projects.core.persistence)
                implementation(projects.core.preferences)
                implementation(projects.core.resources)
                implementation(projects.core.utils)

                implementation(projects.domain.content.data)
                implementation(projects.domain.content.pagination)
                implementation(projects.domain.content.repository)
                implementation(projects.domain.content.usecase)
                implementation(projects.domain.identity.data)
                implementation(projects.domain.identity.repository)
                implementation(projects.domain.identity.usecase)
                implementation(projects.domain.pullnotifications)
                implementation(projects.domain.pushnotifications)
                implementation(projects.domain.urlhandler)

                implementation(projects.feature.acknowledgements)
                implementation(projects.feature.announcements)
                implementation(projects.feature.calendar)
                implementation(projects.feature.circles)
                implementation(projects.feature.composer)
                implementation(projects.feature.directmessages)
                implementation(projects.feature.drawer)
                implementation(projects.feature.entrydetail)
                implementation(projects.feature.explore)
                implementation(projects.feature.favorites)
                implementation(projects.feature.followrequests)
                implementation(projects.feature.gallery)
                implementation(projects.feature.hashtag)
                implementation(projects.feature.imagedetail)
                implementation(projects.feature.inbox)
                implementation(projects.feature.licences)
                implementation(projects.feature.login)
                implementation(projects.feature.manageblocks)
                implementation(projects.feature.nodeinfo)
                implementation(projects.feature.profile)
                implementation(projects.feature.report)
                implementation(projects.feature.search)
                implementation(projects.feature.settings)
                implementation(projects.feature.shortcuts)
                implementation(projects.feature.thread)
                implementation(projects.feature.timeline)
                implementation(projects.feature.unpublished)
                implementation(projects.feature.userdetail)
                implementation(projects.feature.userlist)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
            }
        }
    }
}

customKotlinMultiplatformExtension {
    baseName.set("shared")
    // Required when using NativeSQLiteDriver
    iOSCustomLinkerOptions.set(listOf("-lsqlite3"))
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

dependencies {
    kover(projects.core.appearance)
    kover(projects.core.navigation)
    kover(projects.core.notifications)
    kover(projects.core.preferences)
    kover(projects.core.utils)
    kover(projects.domain.content.pagination)
    kover(projects.domain.content.repository)
    kover(projects.domain.content.usecase)
    kover(projects.domain.identity.repository)
    kover(projects.domain.identity.usecase)
    kover(projects.domain.pushnotifications)
    kover(projects.domain.urlhandler)
    kover(projects.feature.nodeinfo)
}

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                packages(
                    "*.resources",
                    "*.di",
                    "*.auth",
                    "*.bottomnavigation",
                    "*.core.appearance.data",
                    "*.core.appearance.theme",
                    "*.core.utils.appicon",
                    "*.core.utils.compose",
                    "*.core.utils.datetime",
                    "*.core.utils.calendar",
                    "*.core.utils.debug",
                    "*.core.utils.fs",
                    "*.core.utils.gallery",
                    "*.core.utils.imageload",
                    "*.core.utils.network",
                    "*.core.utils.share",
                    "*.core.utils.url",
                    "*.core.utils.uuid",
                    "*.core.utils.validation",
                    "*.core.utils.vibrate",
                    "*.pushnotifications.receiver",
                    "*.pushnotifications.utils",
                )
            }
        }
    }
}

spotless {
    kotlin {
        target("*+/App.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:modifier-missing-check"
        }
        target("*+/main.ios.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:function-naming"
        }
        target("*+/main.android.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:function-naming"
        }
    }
}
