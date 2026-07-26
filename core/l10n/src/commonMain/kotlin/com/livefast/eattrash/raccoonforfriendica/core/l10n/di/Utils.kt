package com.livefast.eattrash.raccoonforfriendica.core.l10n.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import org.koin.core.parameter.parametersOf

@OptIn(DelicateDiApi::class)
@Composable
fun rememberL10nManager() = remember { getByInjection(L10nManager::class) }

@DelicateDiApi
internal fun getStrings(lang: String): Strings = getByInjection(clazz = Strings::class, parameters = { parametersOf(lang) })
