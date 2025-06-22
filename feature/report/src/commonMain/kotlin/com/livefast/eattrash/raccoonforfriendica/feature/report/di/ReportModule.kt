package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportViewModel
import org.kodein.di.DI
import org.kodein.di.instance

internal data class CreateReportViewModelArgs(val userId: String, val entryId: String) : ViewModelCreationArgs

val reportModule =
    DI.Module("ReportModule") {
        bindViewModelWithArgs { args: CreateReportViewModelArgs ->
            CreateReportViewModel(
                userId = args.userId,
                entryId = args.entryId,
                nodeInfoRepository = instance(),
                supportedFeatureRepository = instance(),
                reportRepository = instance(),
                userCache = instance(),
                entryCache = instance(),
            )
        }
    }
