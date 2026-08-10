package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

data class CreateReportViewModelArgs(val userId: String, val entryId: String)

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.report")
class ReportModule
