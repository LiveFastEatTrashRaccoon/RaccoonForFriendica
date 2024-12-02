package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.appicon")
internal class AppIconModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.calendar")
internal class CalendarModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.debug")
internal class DebugModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.fs")
internal class FileSystemModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.gallery")
internal class GalleryModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.share")
internal class ShareModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.url")
internal class UrlModule

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate")
internal class VibrateModule
