package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.feature.userdetail.di.userDetailModule
import com.livefast.eattrash.raccoonforfriendica.core.api.di.apiModule
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.appearanceModule
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.viewModelFactoryModule
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.commonUiComponentsModule
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.encryption.di.encryptionModule
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.l10nModule
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.navigationModule
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.notificationsModule
import com.livefast.eattrash.raccoonforfriendica.core.persistence.di.persistenceModule
import com.livefast.eattrash.raccoonforfriendica.core.preferences.di.preferencesModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.utilsModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di.contentPaginationModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.contentRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.di.contentUseCaseModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getAuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.identityRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.identityUseCaseModule
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di.pullNotificationsModule
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di.pushNotificationsModule
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di.urlHandlerModule
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.di.acknowledgementsModule
import com.livefast.eattrash.raccoonforfriendica.feat.licences.di.licenceModule
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.di.announcementsModule
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.di.calendarModule
import com.livefast.eattrash.raccoonforfriendica.feature.circles.di.circlesModule
import com.livefast.eattrash.raccoonforfriendica.feature.composer.di.composerModule
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di.directMessagesModule
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.di.drawerModule
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di.entryDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.explore.di.exploreModule
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.di.favoritesModule
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di.followRequestsModule
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.di.galleryModule
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di.hashtagModule
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di.imageDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.di.inboxModule
import com.livefast.eattrash.raccoonforfriendica.feature.login.di.loginModule
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di.manageBlocksModule
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di.nodeInfoModule
import com.livefast.eattrash.raccoonforfriendica.feature.profile.di.profileModule
import com.livefast.eattrash.raccoonforfriendica.feature.report.di.reportModule
import com.livefast.eattrash.raccoonforfriendica.feature.settings.di.settingsModule
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di.shortcutsModule
import com.livefast.eattrash.raccoonforfriendica.feature.thread.di.threadModule
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.di.timelineModule
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.di.unpublishedModule
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.di.userListModule
import com.livefast.eattrash.raccoonforfriendica.feaure.search.di.searchModule
import org.kodein.di.DI

fun initDi(additionalBuilder: DI.Builder.() -> Unit = {}) {
    RootDI.di =
        DI {
            additionalBuilder()

            // core modules
            importAll(
                apiModule,
                appearanceModule,
                commonUiComponentsModule,
                encryptionModule,
                l10nModule,
                navigationModule,
                notificationsModule,
                persistenceModule,
                preferencesModule,
                utilsModule,
            )

            // domain
            importAll(
                contentPaginationModule,
                contentRepositoryModule,
                contentUseCaseModule,
                identityRepositoryModule,
                identityUseCaseModule,
                pullNotificationsModule,
                pushNotificationsModule,
                urlHandlerModule,
            )

            // features
            importAll(
                acknowledgementsModule,
                announcementsModule,
                calendarModule,
                circlesModule,
                composerModule,
                directMessagesModule,
                drawerModule,
                entryDetailModule,
                exploreModule,
                favoritesModule,
                followRequestsModule,
                galleryModule,
                hashtagModule,
                imageDetailModule,
                inboxModule,
                licenceModule,
                loginModule,
                manageBlocksModule,
                nodeInfoModule,
                profileModule,
                reportModule,
                searchModule,
                settingsModule,
                shortcutsModule,
                threadModule,
                timelineModule,
                unpublishedModule,
                userDetailModule,
                userListModule,
            )

            // shared
            importAll(
                authModule,
                mainRouterModule,
                mainModule,
                resourcesModule,
                viewModelFactoryModule,
            )
        }
}

// Needed for the OAuth flow on iOS
fun provideAuthManager(): AuthManager = getAuthManager()
