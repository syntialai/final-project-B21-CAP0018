package com.bangkit.team18.qhope.di

import com.bangkit.team18.qhope.ui.booking.viewmodel.BookingConfirmationViewModel
import com.bangkit.team18.qhope.ui.booking.viewmodel.HospitalDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryViewModel
import com.bangkit.team18.qhope.ui.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { HomeViewModel(get()) }
  viewModel { HospitalDetailViewModel(get()) }
  viewModel { BookingConfirmationViewModel(get()) }
  viewModel { HistoryViewModel() }
  viewModel { HistoryDetailViewModel() }
}