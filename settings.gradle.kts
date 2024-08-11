rootProject.name = "RaccoonForFriendica"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":core:api")
include(":core:appearance")
include(":core:architecture")
include(":core:commonui:components")
include(":core:commonui:content")
include(":core:htmlparse")
include(":core:l10n")
include(":core:navigation")
include(":core:persistence")
include(":core:preferences")
include(":core:resources")
include(":core:utils")

include(":domain:content:data")
include(":domain:content:pagination")
include(":domain:content:repository")
include(":domain:identity:data")
include(":domain:identity:repository")
include(":domain:identity:usecase")

include(":feature:drawer")
include(":feature:entrydetail")
include(":feature:explore")
include(":feature:hashtag")
include(":feature:inbox")
include(":feature:login")
include(":feature:profile")
include(":feature:settings")
include(":feature:timeline")
include(":feature:userdetail")
include(":feature:userlist")
