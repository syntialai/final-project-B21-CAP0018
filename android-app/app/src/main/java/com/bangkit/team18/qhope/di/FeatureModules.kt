package com.bangkit.team18.qhope.di

import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import com.bangkit.team18.qhope.ui.booking.viewmodel.HospitalDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel
import com.bangkit.team18.qhope.ui.home.viewmodel.HomeViewModel
import com.bangkit.team18.qhope.ui.login.viewmodel.LoginViewModel
import com.bangkit.team18.qhope.ui.main.viewmodel.MainViewModel
import com.bangkit.team18.qhope.ui.profile.viewmodel.ProfileViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.IdVerificationViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { MainViewModel(get()) }
  viewModel { HomeViewModel(get()) }
  viewModel { HospitalDetailViewModel(get()) }
  viewModel { BookingConfirmationViewModel(get(), get(), get()) }
  viewModel { HistoryViewModel(get(), get()) }
  viewModel { HistoryDetailViewModel(get()) }
  viewModel { LoginViewModel(get(), get()) }
  viewModel { RegistrationViewModel(get(), get()) }
  viewModel { ProfileViewModel(get(), get()) }
  viewModel { IdVerificationViewModel(get(), get()) }
}