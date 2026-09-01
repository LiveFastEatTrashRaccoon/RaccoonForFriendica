plugins {
    id("com.livefast.eattrash.kotlinMultiplatform")
    id("com.livefast.eattrash.composeMultiplatform")
    id("com.livefast.eattrash.di")
    id("com.livefast.eattrash.spotless")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.appearance)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.resources)
                implementation(projects.core.utils)

                implementation(projects.domain.content.usecase)
                implementation(projects.domain.urlhandler)
            }
        }
    }
}

customDiExtension {
    useCompose()
    useCompilerPlugin()
    useAnnotations()
}

spotless {
    kotlin {
        target("**/ProvideUiDeps.kt")
        suppressLintsFor {
            step = "ktlint"
            shortCode = "compose:compositionlocal-allowlist"
        }
    }
}
