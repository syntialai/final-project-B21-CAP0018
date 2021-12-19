package com.qhope.app.di

import com.qhope.app.ui.booking.viewmodel.BookingConfirmationViewModel
import com.qhope.app.ui.booking.viewmodel.HospitalDetailViewModel
import com.qhope.app.ui.history.viewmodel.HistoryDetailViewModel
import com.qhope.app.ui.history.viewmodel.HistoryViewModel
import com.qhope.app.ui.home.viewmodel.HomeViewModel
import com.qhope.app.ui.login.viewmodel.LoginViewModel
import com.qhope.app.ui.main.viewmodel.MainViewModel
import com.qhope.app.ui.profile.viewmodel.EditPersonalDataViewModel
import com.qhope.app.ui.profile.viewmodel.PersonalDataViewModel
import com.qhope.app.ui.profile.viewmodel.ProfileIdVerificationViewModel
import com.qhope.app.ui.profile.viewmodel.ProfilePictureViewModel
import com.qhope.app.ui.profile.viewmodel.ProfileVerificationResultViewModel
import com.qhope.app.ui.profile.viewmodel.ProfileViewModel
import com.qhope.app.ui.registration.viewmodel.IdVerificationViewModel
import com.qhope.app.ui.registration.viewmodel.IdentityConfirmationViewModel
import com.qhope.app.ui.registration.viewmodel.RegistrationViewModel
import com.qhope.app.ui.registration.viewmodel.VerificationResultViewModel
import com.qhope.app.ui.splash.viewmodel.SplashScreenViewModel
import com.qhope.app.ui.tnc.viewmodel.TermsAndConditionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { SplashScreenViewModel(get(), get(), get()) }
  viewModel { MainViewModel(get(), get()) }
  viewModel { HomeViewModel(get(), get(), get(), get()) }
  viewModel { HospitalDetailViewModel(get()) }
  viewModel { BookingConfirmationViewModel(get(), get(), get(), get(), get()) }
  viewModel { HistoryViewModel(get(), get(), get()) }
  viewModel { HistoryDetailViewModel(get()) }
  viewModel { LoginViewModel(get(), get()) }
  viewModel { RegistrationViewModel(get(), get(), get()) }
  viewModel { ProfileViewModel(get(), get(), get()) }
  viewModel { IdVerificationViewModel(get(), get(), get()) }
  viewModel { VerificationResultViewModel(get(), get(), get()) }
  viewModel { ProfileVerificationResultViewModel(get(), get(), get()) }
  viewModel { ProfileIdVerificationViewModel(get(), get(), get()) }
  viewModel { PersonalDataViewModel(get(), get(), get()) }
  viewModel { IdentityConfirmationViewModel(get(), get(), get()) }
  viewModel { ProfilePictureViewModel(get(), get(), get()) }
  viewModel { EditPersonalDataViewModel(get(), get(), get()) }
  viewModel { TermsAndConditionViewModel(get(), get()) }
}