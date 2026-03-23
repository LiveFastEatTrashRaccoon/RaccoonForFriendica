package com.livefast.eattrash.raccoonforfriendica.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CirclesWithCircleMembersScreen(
    model: CirclesMviModel,
    modifier: Modifier = Modifier,
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Destination>()
    val scope = rememberCoroutineScope()

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = scaffoldNavigator.scaffoldDirective,
        scaffoldState = scaffoldNavigator.scaffoldState,
        listPane = {
            AnimatedPane {
                CirclesScreen(
                    model = model,
                    customOpenAction = { circleId ->
                        scope.launch {
                            val target = Destination.CircleMembers(circleId)
                            scaffoldNavigator.navigateTo(ThreePaneScaffoldRole.Primary, target)
                        }
                    },
                )
            }
        },
        detailPane = {
            val destination = scaffoldNavigator.currentDestination?.contentKey as? Destination.CircleMembers
            if (destination != null) {
                AnimatedPane {
                    CircleMembersScreen(
                        id = destination.circleId,
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
