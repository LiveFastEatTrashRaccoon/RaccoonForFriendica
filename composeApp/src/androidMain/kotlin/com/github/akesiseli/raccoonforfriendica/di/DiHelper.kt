package com.github.akesiseli.raccoonforfriendica.di

import com.github.akesiseli.raccoonforfriendica.core.api.di.coreApiModule
import com.github.akesiseli.raccoonforfriendica.core.appearance.di.coreAppearanceModule
import com.github.akesiseli.raccoonforfriendica.core.l10n.di.coreL10nModule
import com.github.akesiseli.raccoonforfriendica.core.navigation.di.coreNavigationModule
import com.github.akesiseli.raccoonforfriendica.core.preferences.di.corePreferencesModule
import com.github.akesiseli.raccoonforfriendica.core.utils.di.coreUtilsFileSystemModule
import com.github.akesiseli.raccoonforfriendica.core.utils.di.coreUtilsModule
import com.github.akesiseli.raccoonforfriendica.domain.content.pagination.di.domainContentPaginationModule
import com.github.akesiseli.raccoonforfriendica.domain.content.repository.di.domainContentRepositoryModule
import com.github.akesiseli.raccoonforfriendica.domain.identity.repository.di.domainIdentityRepositoryModule
import com.github.akesiseli.raccoonforfriendica.feature.timeline.di.featureTimelineModule
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
        )
    }
