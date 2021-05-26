package com.bangkit.team18.core.di

import com.bangkit.team18.core.domain.usecase.HospitalUseCase
import com.bangkit.team18.core.domain.usecase.interactor.HospitalInteractor
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
  factory { HospitalInteractor(get()) } bind HospitalUseCase::class
}
