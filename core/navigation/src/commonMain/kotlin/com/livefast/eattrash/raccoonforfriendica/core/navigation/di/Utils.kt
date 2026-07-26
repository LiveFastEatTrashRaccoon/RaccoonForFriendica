package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator

@OptIn(DelicateDiApi::class)
@Composable
fun rememberNavigationCoordinator(): NavigationCoordinator = remember { getByInjection(NavigationCoordinator::class) }

@OptIn(DelicateDiApi::class)
@Composable
fun rememberDrawerCoordinator() = remember { getByInjection(DrawerCoordinator::class) }

@DelicateDiApi
fun getMainRouter(): MainRouter = getByInjection(MainRouter::class)

@OptIn(DelicateDiApi::class)
@Composable
fun rememberMainRouter() = remember { getMainRouter() }
