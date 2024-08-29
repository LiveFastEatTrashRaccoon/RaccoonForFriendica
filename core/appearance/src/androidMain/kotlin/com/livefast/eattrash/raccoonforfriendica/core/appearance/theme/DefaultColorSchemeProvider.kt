package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.materialkolor.dynamicColorScheme

internal class DefaultColorSchemeProvider(
    private val context: Context,
) : ColorSchemeProvider {
    override val supportsDynamicColors: Boolean
        @ChecksSdkIntAtLeast(31)
        get() {
            return Build.VERSION.SDK_INT >= 31
        }

    @SuppressLint("NewApi")
    override fun getColorScheme(
        theme: UiTheme,
        dynamic: Boolean,
        customSeed: Color?,
        isSystemInDarkTheme: Boolean,
    ): ColorScheme =
        when (theme) {
            UiTheme.Dark -> {
                when {
                    dynamic -> {
                        dynamicDarkColorScheme(context)
                    }

                    customSeed != null -> {
                        dynamicColorScheme(
                            seedColor = customSeed,
                            isDark = true,
                            isAmoled = false,
                        )
                    }

                    else -> {
                        DarkColors
                    }
                }
            }

            UiTheme.Black -> {
                when {
                    dynamic -> {
                        dynamicDarkColorScheme(context).blackify()
                    }

                    customSeed != null -> {
                        dynamicColorScheme(
                            seedColor = customSeed,
                            isDark = true,
                            isAmoled = true,
                        )
                    }

                    else -> {
                        BlackColors
                    }
                }
            }

            UiTheme.Light ->
                when {
                    dynamic -> {
                        dynamicLightColorScheme(context)
                    }

                    customSeed != null -> {
                        dynamicColorScheme(
                            seedColor = customSeed,
                            isDark = false,
                            isAmoled = false,
                        )
                    }

                    else -> {
                        LightColors
                }
            }

            else -> {
                when {
                    dynamic -> {
                        dynamicLightColorScheme(context)
                    }

                    customSeed != null -> {
                        dynamicColorScheme(
                            seedColor = customSeed,
                            isDark = isSystemInDarkTheme,
                            isAmoled = false,
                        )
                    }

                    else -> {
                        if (isSystemInDarkTheme) {
                            DarkColors
                        } else {
                            LightColors
                        }
                    }
                }
            }
        }
}
