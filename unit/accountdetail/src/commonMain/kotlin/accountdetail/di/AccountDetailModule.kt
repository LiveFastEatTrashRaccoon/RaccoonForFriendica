package accountdetail.di

import accountdetail.AccountDetailMviModel
import accountdetail.AccountDetailViewModel
import org.koin.dsl.module

val unitAccountDetailModule =
    module {
        factory<AccountDetailMviModel> { params ->
            AccountDetailViewModel(
                id = params[0],
                accountRepository = get(),
                paginationManager = get(),
            )
        }
    }
