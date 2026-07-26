package com.livefast.eattrash.raccoonforfriendica.core.resources.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

@OptIn(DelicateDiApi::class)
@Composable
fun rememberCoreResources(): CoreResources = remember { getByInjection(CoreResources::class) }
