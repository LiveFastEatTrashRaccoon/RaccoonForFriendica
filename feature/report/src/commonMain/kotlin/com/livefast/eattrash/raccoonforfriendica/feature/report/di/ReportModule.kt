package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal data class CreateReportViewModelArgs(val userId: String, val entryId: String)

val reportModule = module {
    viewModel { params ->
        val args: CreateReportViewModelArgs = params.get()
        CreateReportViewModel(
            userId = args.userId,
            entryId = args.entryId,
            nodeInfoRepository = get(),
            supportedFeatureRepository = get(),
            reportRepository = get(),
            userCache = get(),
        )
    }
}
