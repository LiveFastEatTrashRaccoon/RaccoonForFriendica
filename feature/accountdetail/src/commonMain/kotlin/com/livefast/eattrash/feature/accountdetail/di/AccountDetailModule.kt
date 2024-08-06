package com.livefast.eattrash.feature.accountdetail.di

import com.livefast.eattrash.feature.accountdetail.AccountDetailMviModel
import com.livefast.eattrash.feature.accountdetail.AccountDetailViewModel
import org.koin.dsl.module

val featureAccountDetailModule =
    module {
        factory<AccountDetailMviModel> { params ->
            AccountDetailViewModel(
                id = params[0],
                accountRepository = get(),
                paginationManager = get(),
            )
        }
    }
