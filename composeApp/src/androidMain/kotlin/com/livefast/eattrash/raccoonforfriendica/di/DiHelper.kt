package com.livefast.eattrash.raccoonforfriendica.di

import com.livefast.eattrash.feature.accountdetail.di.featureAccountDetailModule
import com.livefast.eattrash.raccoonforfriendica.core.api.di.coreApiModule
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.coreAppearanceModule
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.coreL10nModule
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.coreNavigationModule
import com.livefast.eattrash.raccoonforfriendica.core.preferences.di.corePreferencesModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsFileSystemModule
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.coreUtilsModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di.domainContentPaginationModule
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.domainContentRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.domainIdentityRepositoryModule
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di.featureEntryDetailModule
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.di.featureTimelineModule
import org.koin.dsl.module

val sharedHelperModule =
    module {
        includes(
            sharedModule,
            coreApiModule,
            coreAppearanceModule,
            coreL10nModule,
            corePreferencesModule,
            coreNavigationModule,
            coreResourceModule,
            coreUtilsFileSystemModule,
            coreUtilsModule,
            domainContentPaginationModule,
            domainContentRepositoryModule,
            domainIdentityRepositoryModule,
            featureTimelineModule,
            featureAccountDetailModule,
            featureEntryDetailModule,
        )
    }
