package com.livefast.eattrash.feature.userdetail.di

import com.livefast.eattrash.feature.userdetail.UserDetailMviModel
import com.livefast.eattrash.feature.userdetail.UserDetailViewModel
import org.koin.dsl.module

val featureUserDetailModule =
    module {
        factory<UserDetailMviModel> { params ->
            UserDetailViewModel(
                id = params[0],
                userRepository = get(),
                paginationManager = get(),
            )
        }
    }
