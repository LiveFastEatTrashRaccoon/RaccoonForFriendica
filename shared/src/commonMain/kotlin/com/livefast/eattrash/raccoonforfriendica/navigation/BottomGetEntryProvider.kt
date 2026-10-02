package com.livefast.eattrash.raccoonforfriendica.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMvi
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMvi
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMvi
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMvi
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMvi
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineScreen

@Composable
internal fun bottomGetEntryProvider(
    timelineViewModel: TimelineMvi,
    timelineLazyListState: LazyListState,
    exploreViewModel: ExploreMvi,
    exploreLazyListState: LazyListState,
    inboxViewModel: InboxMvi,
    inboxLazyListState: LazyListState,
    profileViewModel: ProfileMvi,
    myAccountViewModel: MyAccountMvi,
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
