package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultLocalItemCache
import org.koin.core.annotation.Single

@Single
internal class CircleLocalItemCache : DefaultLocalItemCache<CircleModel>()
