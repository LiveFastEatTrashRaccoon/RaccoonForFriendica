package com.livefast.eattrash.raccoonforfriendica.core.appearance.di

import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ColorSchemeProvider
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.koin.ksp.generated.module

internal actual val nativeAppearanceModule: Module = NativeAppearanceModule().module

actual fun getThemeRepository(): ThemeRepository {
    val res: ThemeRepository by inject(ThemeRepository::class.java)
    return res
}

actual fun getColorSchemeProvider(): ColorSchemeProvider {
    val res by inject<ColorSchemeProvider>(ColorSchemeProvider::class.java)
    return res
}

actual fun getBarColorProvider(): BarColorProvider {
    val res by inject<BarColorProvider>(BarColorProvider::class.java)
    return res
}
