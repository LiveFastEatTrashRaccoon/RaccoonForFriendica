package com.livefast.eattrash.raccoonforfriendica.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineScreen

@Composable
internal fun bottomGetEntryProvider(
    timelineViewModel: TimelineMviModel,
    timelineLazyListState: LazyListState,
    exploreViewModel: ExploreMviModel,
    exploreLazyListState: LazyListState,
    inboxViewModel: InboxMviModel,
    inboxLazyListState: LazyListState,
    profileViewModel: ProfileMviModel,
    myAccountViewModel: MyAccountMviModel,
    myAccountLazyListState: LazyListState,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<BottomNavigationSection.Home> {
        TimelineScreen(
            model = timelineViewModel,
            lazyListState = timelineLazyListState,
        )
    }
    entry<BottomNavigationSection.Explore> {
        ExploreScreen(
            model = exploreViewModel,
            lazyListState = exploreLazyListState,
        )
    }
    entry<BottomNavigationSection.Inbox> {
        InboxScreen(
            model = inboxViewModel,
            lazyListState = inboxLazyListState,
        )
    }
    entry<BottomNavigationSection.Profile> {
        ProfileScreen(
            model = profileViewModel,
            myAccountModel = myAccountViewModel,
            myAccountLazyListState = myAccountLazyListState,
        )
    }
}
