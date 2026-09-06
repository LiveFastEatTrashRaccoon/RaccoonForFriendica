plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.test")
    id("com.livefast.eattrash.spotless")
    id("com.livefast.eattrash.serialization")
    alias(libs.plugins.sentry)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.mokkery)
}

sentryKmp {
    autoInstall {
        enabled.set(false)
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.material)
                implementation(libs.compose.ui)
                implementation(libs.coil)
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.ktor.client.core)

                api(projects.core.api)
                api(projects.core.appearance)
                api(projects.core.architecture)
                api(projects.core.commonui.components)
                api(projects.core.commonui.content)
                api(projects.core.di.utils)
                api(projects.core.l10n)
                api(projects.core.navigation)
                api(projects.core.notifications)
                api(projects.core.persistence)
                api(projects.core.preferences)
                api(projects.core.resources)
                api(projects.core.translation)
                api(projects.core.utils)

                api(projects.domain.content.data)
                api(projects.domain.content.pagination)
                api(projects.domain.content.repository)
                api(projects.domain.content.usecase)
                api(projects.domain.identity.data)
                api(projects.domain.identity.repository)
                api(projects.domain.identity.usecase)
                api(projects.domain.pullnotifications)
                api(projects.domain.pushnotifications)
                api(projects.domain.urlhandler)

                api(projects.feature.acknowledgements)
                api(projects.feature.announcements)
                api(projects.feature.calendar)
                api(projects.feature.circles)
                api(projects.feature.composer)
                api(projects.feature.directmessages)
                api(projects.feature.drawer)
                api(projects.feature.entrydetail)
                api(projects.feature.entrylist)
                api(projects.feature.explore)
                api(projects.feature.followrequests)
                api(projects.feature.gallery)
                api(projects.feature.hashtag)
                api(projects.feature.imagedetail)
                api(projects.feature.inbox)
                api(projects.feature.licences)
                api(projects.feature.login)
                api(projects.feature.manageblocks)
                api(projects.feature.nodeinfo)
                api(projects.feature.profile)
                api(projects.feature.report)
                api(projects.feature.search)
                api(projects.feature.settings)
                api(projects.feature.shortcuts)
                api(projects.feature.thread)
                api(projects.feature.timeline)
                api(projects.feature.unpublished)
                api(projects.feature.userdetail)
                api(projects.feature.userlist)
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
    iosFramework(
        baseName = "shared",
        linkerOptions =
            listOf(
                // required when using NativeSQLiteDriver
                "-lsqlite3",
                // tell the linker to look for Sentry.framework
                "-framework",
                "Sentry",
            ),
        exports =
            listOf(
                projects.domain.identity.repository,
            ),
    )
}

customDiExtension {
    useCompose(withViewModels = true)
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
