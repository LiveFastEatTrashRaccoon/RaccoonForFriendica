package com.livefast.eattrash.raccoonforfriendica.feature.report.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.feature.report")
internal class ReportModule

val featureReportModule = ReportModule().module
