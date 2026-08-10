package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.raccoonforfriendica.core.api.di.ApiModule
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.AppearanceModule
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.CommonUiComponentsModule
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.L10nModule
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.NavigationModule
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.NotificationsModule
import com.livefast.eattrash.raccoonforfriendica.core.persistence.di.PersistenceModule
import com.livefast.eattrash.raccoonforfriendica.core.preferences.di.PreferencesModule
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.ResourcesModule
import com.livefast.eattrash.raccoonforfriendica.core.translation.di.TranslationModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.UtilsModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di.ContentPaginationModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.ContentRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.di.ContentUseCaseModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.IdentityRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.IdentityUseCaseModule
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di.PullNotificationsModule
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di.PushNotificationsModule
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.di.UrlHandlerModule
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.di.AcknowledgementsModule
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.di.AnnouncementsModule
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.di.CalendarModule
import com.livefast.eattrash.raccoonforfriendica.feature.circles.di.CirclesModule
import com.livefast.eattrash.raccoonforfriendica.feature.composer.di.ComposerModule
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.di.DirectMessagesModule
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.di.DrawerModule
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di.EntryDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.di.EntryListModule
import com.livefast.eattrash.raccoonforfriendica.feature.explore.di.ExploreModule
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di.FollowRequestsModule
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.di.GalleryModule
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di.HashtagModule
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di.ImageDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.di.InboxModule
import com.livefast.eattrash.raccoonforfriendica.feature.licences.di.LicencesModule
import com.livefast.eattrash.raccoonforfriendica.feature.login.di.LoginModule
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di.ManageBlocksModule
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di.NodeInfoModule
import com.livefast.eattrash.raccoonforfriendica.feature.profile.di.ProfileModule
import com.livefast.eattrash.raccoonforfriendica.feature.report.di.ReportModule
import com.livefast.eattrash.raccoonforfriendica.feature.settings.di.SettingsModule
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di.ShortcutsModule
import com.livefast.eattrash.raccoonforfriendica.feature.thread.di.ThreadModule
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.di.TimelineModule
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.di.UnpublishedModule
import com.livefast.eattrash.raccoonforfriendica.feature.userdetail.di.UserDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.di.UserListModule
import com.livefast.eattrash.raccoonforfriendica.feaure.search.di.SearchModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.plugin.module.dsl.modules

fun KoinApplication.getAuthManager(): AuthManager = koin.get()

fun setupDi(config: KoinAppDeclaration? = null): KoinApplication = startKoin {
    includes(config)

    // core modules
    modules(
        ApiModule::class,
        AppearanceModule::class,
        CommonUiComponentsModule::class,
        L10nModule::class,
        NavigationModule::class,
        NotificationsModule::class,
        PersistenceModule::class,
        PreferencesModule::class,
        ResourcesModule::class,
        TranslationModule::class,
        UtilsModule::class,
    )

    // domain
    modules(
        ContentPaginationModule::class,
        ContentRepositoryModule::class,
        ContentUseCaseModule::class,
        IdentityRepositoryModule::class,
        IdentityUseCaseModule::class,
        PullNotificationsModule::class,
        PushNotificationsModule::class,
        UrlHandlerModule::class,
    )

    // features
    modules(
        AcknowledgementsModule::class,
        AnnouncementsModule::class,
        CalendarModule::class,
        CirclesModule::class,
        ComposerModule::class,
        DirectMessagesModule::class,
        DrawerModule::class,
        EntryDetailModule::class,
        ExploreModule::class,
        EntryListModule::class,
        FollowRequestsModule::class,
        GalleryModule::class,
        HashtagModule::class,
        ImageDetailModule::class,
        InboxModule::class,
        LicencesModule::class,
        LoginModule::class,
        ManageBlocksModule::class,
        NodeInfoModule::class,
        ProfileModule::class,
        ReportModule::class,
        SearchModule::class,
        SettingsModule::class,
        ShortcutsModule::class,
        ThreadModule::class,
        TimelineModule::class,
        UnpublishedModule::class,
        UserDetailModule::class,
        UserListModule::class,
    )

    // shared
    modules(
        AuthModule::class,
        MainModule::class,
        MainRouterModule::class,
    )
}
