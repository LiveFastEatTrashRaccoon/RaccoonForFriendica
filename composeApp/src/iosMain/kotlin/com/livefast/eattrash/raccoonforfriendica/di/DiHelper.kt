package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.feature.userdetail.di.featureUserDetailModule
import com.livefast.eattrash.raccoonforfriendica.core.api.di.coreApiModule
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.coreAppearanceModule
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.coreCommonUiComponentsModule
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.coreL10nModule
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.coreNavigationModule
import com.livefast.eattrash.raccoonforfriendica.core.persistence.di.corePersistenceModule
import com.livefast.eattrash.raccoonforfriendica.core.preferences.di.corePreferencesModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreHapticFeedbackModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsDebugModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsFileSystemModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsGalleryModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsShareModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsUrlModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di.domainContentPaginationModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.domainContentRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.domainIdentityRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.domainIdentityUseCaseModule
import com.livefast.eattrash.raccoonforfriendica.feature.circles.di.featureCirclesModule
import com.livefast.eattrash.raccoonforfriendica.feature.composer.di.featureComposerModule
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.di.featureDrawerModule
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di.featureEntryDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.explore.di.featureExploreModule
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.di.featureFavoritesModule
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.di.featureFollowRequestsModule
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.di.featureHashtagModule
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.di.featureImageDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.di.featureInboxModule
import com.livefast.eattrash.raccoonforfriendica.feature.login.di.featureLoginModule
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.di.featureManageBlocksModule
import com.livefast.eattrash.raccoonforfriendica.feature.profile.di.featureProfileModule
import com.livefast.eattrash.raccoonforfriendica.feature.settings.di.featureSettingsModule
import com.livefast.eattrash.raccoonforfriendica.feature.thread.di.featureThreadModule
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.di.featureTimelineModule
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.di.featureUserListModule
import com.livefast.eattrash.raccoonforfriendica.feaure.search.di.featureSearchModule
import org.koin.core.Koin
import org.koin.core.context.startKoin

fun initKoin(): Koin {
    val koinApp =
        startKoin {
            modules(
                sharedModule,
                coreApiModule,
                coreAppearanceModule,
                coreCommonUiComponentsModule,
                coreHapticFeedbackModule,
                coreL10nModule,
                corePersistenceModule,
                corePreferencesModule,
                coreNavigationModule,
                coreResourceModule,
                coreUtilsDebugModule,
                coreUtilsFileSystemModule,
                coreUtilsGalleryModule,
                coreUtilsModule,
                coreUtilsShareModule,
                coreUtilsUrlModule,
                domainContentPaginationModule,
                domainContentRepositoryModule,
                domainIdentityRepositoryModule,
                domainIdentityUseCaseModule,
                featureCirclesModule,
                featureComposerModule,
                featureTimelineModule,
                featureEntryDetailModule,
                featureExploreModule,
                featureFollowRequestsModule,
                featureHashtagModule,
                featureImageDetailModule,
                featureInboxModule,
                featureLoginModule,
                featureManageBlocksModule,
                featureProfileModule,
                featureSettingsModule,
                featureUserDetailModule,
                featureUserListModule,
                featureDrawerModule,
                featureFavoritesModule,
                featureSearchModule,
                featureThreadModule,
            )
        }
    return koinApp.koin
}
