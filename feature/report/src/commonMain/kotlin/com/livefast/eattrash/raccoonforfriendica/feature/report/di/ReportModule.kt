package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportViewModel
import org.koin.dsl.module

val featureReportModule =
    module {
        factory<CreateReportMviModel> { params ->
            CreateReportViewModel(
                userId = params[0],
                entryId = params[1],
                nodeInfoRepository = get(),
                reportRepository = get(),
                userCache = get(),
                entryCache = get(),
            )
        }
    }
