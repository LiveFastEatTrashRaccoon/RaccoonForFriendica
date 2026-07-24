package com.livefast.eattrash.raccoonforfriendica.core.navigation.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator

fun getNavigationCoordinator(): NavigationCoordinator = getByInjection(NavigationCoordinator::class)

@Composable
fun rememberNavigationCoordinator(): NavigationCoordinator = remember { getNavigationCoordinator() }

fun getDrawerCoordinator(): DrawerCoordinator = getByInjection(DrawerCoordinator::class)

@Composable
fun rememberDrawerCoordinator() = remember { getDrawerCoordinator() }

fun getMainRouter(): MainRouter = getByInjection(MainRouter::class)

@Composable
fun rememberMainRouter() = remember { getMainRouter() }
