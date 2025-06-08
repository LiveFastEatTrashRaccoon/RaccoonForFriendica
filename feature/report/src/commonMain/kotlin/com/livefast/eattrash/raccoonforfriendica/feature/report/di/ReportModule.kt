package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

internal data class CreateReportMviModelParams(val userId: String, val entryId: String)

val reportModule =
    DI.Module("ReportModule") {
        bind<CreateReportMviModel> {
            factory { params: CreateReportMviModelParams ->
                CreateReportViewModel(
                    userId = params.userId,
                    entryId = params.entryId,
                    nodeInfoRepository = instance(),
                    supportedFeatureRepository = instance(),
                    reportRepository = instance(),
                    userCache = instance(),
                    entryCache = instance(),
                )
            }
        }
    }
