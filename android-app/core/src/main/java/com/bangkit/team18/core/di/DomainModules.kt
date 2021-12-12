package com.bangkit.team18.core.di

import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.HospitalUseCase
import com.bangkit.team18.core.domain.usecase.MerchantUseCase
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.core.domain.usecase.interactor.AuthInteractor
import com.bangkit.team18.core.domain.usecase.interactor.HospitalInteractor
import com.bangkit.team18.core.domain.usecase.interactor.MerchantInteractor
import com.bangkit.team18.core.domain.usecase.interactor.RoomBookingInteractor
import com.bangkit.team18.core.domain.usecase.interactor.UserInteractor
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
  factory { HospitalInteractor(get()) } bind HospitalUseCase::class
  factory { RoomBookingInteractor(get()) } bind RoomBookingUseCase::class
  factory { AuthInteractor(get()) } bind AuthUseCase::class
  factory { UserInteractor(get()) } bind UserUseCase::class
  factory { MerchantInteractor(get()) } bind MerchantUseCase::class
}
