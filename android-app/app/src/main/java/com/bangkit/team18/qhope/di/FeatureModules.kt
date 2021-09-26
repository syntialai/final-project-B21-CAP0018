package com.bangkit.team18.qhope.di

import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import com.bangkit.team18.qhope.ui.booking.viewmodel.HospitalDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel
import com.bangkit.team18.qhope.ui.home.viewmodel.HomeViewModel
import com.bangkit.team18.qhope.ui.login.viewmodel.LoginViewModel
import com.bangkit.team18.qhope.ui.main.viewmodel.MainViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.*
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdVerificationViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.RegistrationViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.VerificationResultViewModel
import com.bangkit.team18.qhope.ui.splash.viewmodel.SplashScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { SplashScreenViewModel(get(), get()) }
  viewModel { MainViewModel(get()) }
  viewModel { HomeViewModel(get(), get(), get()) }
  viewModel { HospitalDetailViewModel(get()) }
  viewModel { BookingConfirmationViewModel(get(), get(), get()) }
  viewModel { HistoryViewModel(get(), get()) }
  viewModel { HistoryDetailViewModel(get()) }
  viewModel { LoginViewModel(get(), get()) }
  viewModel { RegistrationViewModel(get(), get()) }
  viewModel { ProfileViewModel(get(), get()) }
  viewModel { IdVerificationViewModel(get(), get()) }
  viewModel { VerificationResultViewModel(get(), get()) }
  viewModel { ProfileVerificationResultViewModel(get(), get()) }
  viewModel { ProfileIdVerificationViewModel(get(), get()) }
  viewModel { PersonalDataViewModel(get(), get()) }
  viewModel { ProfilePictureViewModel(get(), get()) }
  viewModel { EditPersonalDataViewModel(get(), get()) }
}