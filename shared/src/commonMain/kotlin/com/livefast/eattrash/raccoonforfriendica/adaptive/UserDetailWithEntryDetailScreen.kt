package com.livefast.eattrash.raccoonforfriendica.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di.getEntryCache
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun UserDetailWithEntryDetailScreen(
    id: String,
    modifier: Modifier = Modifier,
    otherInstance: String? = null,
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Destination>()
    val scope = rememberCoroutineScope()

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = scaffoldNavigator.scaffoldDirective,
        scaffoldState = scaffoldNavigator.scaffoldState,
        listPane = {
            AnimatedPane {
                UserDetailScreen(
                    id = id,
                    otherInstance = otherInstance,
                    customOnSelectAction = { entry ->
                        scope.launch {
                            val entryCache = getEntryCache()
                            entryCache.put(entry.id, entry)

                            val target = Destination.EntryDetail(
                                entryId = entry.id,
                                swipeNavigationEnabled = false,
                                otherInstance = otherInstance,
                            )
                            scaffoldNavigator.navigateTo(ThreePaneScaffoldRole.Primary, target)
                        }
                    },
                )
            }
        },
        detailPane = {
            val destination = scaffoldNavigator.currentDestination?.contentKey as? Destination.EntryDetail
            if (destination != null) {
                AnimatedPane {
                    EntryDetailScreen(
                        id = destination.entryId,
                        swipeNavigationEnabled = destination.swipeNavigationEnabled,
                        otherInstance = destination.otherInstance,
                        customBackAction = {
                            scope.launch {
                                scaffoldNavigator.navigateBack()
                            }
                        }
                    )
                }
            }
        },
    )
}
