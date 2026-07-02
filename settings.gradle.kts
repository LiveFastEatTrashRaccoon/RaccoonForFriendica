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
    includeBuild("build-logic")
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
        // for compose-multiplatform-media-player -> compose-webview-multiplatform -> kcef -> jcef -> jogl-all
        // jogl 2.5.0 is not on Maven Central, "https://jogamp.org/deployment/maven" is the official repo
        // unfortunately is often down which causes CI errors in dependency resolution, so we're using a mirror
        maven("https://maven.scijava.org/content/repositories/public/")
    }
}

include(":shared")
include(":androidApp")
include(":desktopApp")

include(":core:api")
include(":core:appearance")
include(":core:architecture")
include(":core:di")
include(":core:di:testutils")
include(":core:commonui:components")
include(":core:commonui:content")
include(":core:htmlparse")
include(":core:l10n")
include(":core:navigation")
include(":core:notifications")
include(":core:persistence")
include(":core:preferences")
include(":core:resources")
include(":core:translation")
include(":core:utils")

include(":domain:content:data")
include(":domain:content:pagination")
include(":domain:content:repository")
include(":domain:content:usecase")
include(":domain:identity:data")
include(":domain:identity:repository")
include(":domain:identity:usecase")
include(":domain:pullnotifications")
include(":domain:pushnotifications")
include(":domain:urlhandler")

include(":feature:acknowledgements")
include(":feature:announcements")
include(":feature:calendar")
include(":feature:circles")
include(":feature:composer")
include(":feature:directmessages")
include(":feature:drawer")
include(":feature:entrydetail")
include(":feature:entrylist")
include(":feature:explore")
include(":feature:followrequests")
include(":feature:gallery")
include(":feature:hashtag")
include(":feature:imagedetail")
include(":feature:inbox")
include(":feature:licences")
include(":feature:login")
include(":feature:manageblocks")
include(":feature:nodeinfo")
include(":feature:profile")
include(":feature:report")
include(":feature:search")
include(":feature:settings")
include(":feature:shortcuts")
include(":feature:thread")
include(":feature:timeline")
include(":feature:unpublished")
include(":feature:userdetail")
include(":feature:userlist")
