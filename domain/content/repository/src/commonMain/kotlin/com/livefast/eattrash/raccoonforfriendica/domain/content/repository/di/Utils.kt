package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.core.di.DelicateDiApi
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AttachmentCache

@DelicateDiApi
fun getAttachmentCache(): AttachmentCache = getByInjection(AttachmentCache::class)
