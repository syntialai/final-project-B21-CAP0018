package com.qhope.core.di

import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.HospitalUseCase
import com.qhope.core.domain.usecase.MerchantUseCase
import com.qhope.core.domain.usecase.RoomBookingUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.domain.usecase.interactor.AuthInteractor
import com.qhope.core.domain.usecase.interactor.HospitalInteractor
import com.qhope.core.domain.usecase.interactor.MerchantInteractor
import com.qhope.core.domain.usecase.interactor.RoomBookingInteractor
import com.qhope.core.domain.usecase.interactor.UserInteractor
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
  factory { HospitalInteractor(get()) } bind HospitalUseCase::class
  factory { RoomBookingInteractor(get()) } bind RoomBookingUseCase::class
  factory { AuthInteractor(get()) } bind AuthUseCase::class
  factory { UserInteractor(get()) } bind UserUseCase::class
  factory { MerchantInteractor(get()) } bind MerchantUseCase::class
}
