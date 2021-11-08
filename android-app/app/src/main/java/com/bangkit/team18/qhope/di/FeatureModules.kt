package com.bangkit.team18.qhope.di

import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import com.bangkit.team18.qhope.ui.booking.viewmodel.HospitalDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel
import com.bangkit.team18.qhope.ui.home.viewmodel.HomeViewModel
import com.bangkit.team18.qhope.ui.login.viewmodel.LoginViewModel
import com.bangkit.team18.qhope.ui.main.viewmodel.MainViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.PersonalDataViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileIdVerificationViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileVerificationResultViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdVerificationViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.RegistrationViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.VerificationResultViewModel
import com.bangkit.team18.qhope.ui.splash.viewmodel.SplashScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { SplashScreenViewModel(get(), get(), get()) }
  viewModel { MainViewModel(get(), get()) }
  viewModel { HomeViewModel(get(), get(), get(), get()) }
  viewModel { HospitalDetailViewModel(get()) }
  viewModel { BookingConfirmationViewModel(get(), get(), get(), get()) }
  viewModel { HistoryViewModel(get(), get(), get()) }
  viewModel { HistoryDetailViewModel(get()) }
  viewModel { LoginViewModel(get(), get(), get()) }
  viewModel { RegistrationViewModel(get(), get(), get()) }
  viewModel { ProfileViewModel(get(), get(), get()) }
  viewModel { IdVerificationViewModel(get(), get(), get()) }
  viewModel { VerificationResultViewModel(get(), get(), get()) }
  viewModel { ProfileVerificationResultViewModel(get(), get(), get()) }
  viewModel { ProfileIdVerificationViewModel(get(), get(), get()) }
  viewModel { PersonalDataViewModel(get(), get(), get()) }
}