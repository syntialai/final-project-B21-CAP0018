package com.qhope.app.ui.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.history.History
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.RoomBookingUseCase

class HistoryViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val roomBookingUseCase: RoomBookingUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _bookingHistories = MutableLiveData<List<History>>()
  val bookingHistories: LiveData<List<History>>
    get() = _bookingHistories

  init {
    initAuthStateListener()
  }

  fun fetchUserBookingHistories() {
    launchViewModelScope({
      roomBookingUseCase.getUserBookings().runFlow(::setRoomBookingData)
    })
  }

  private fun setRoomBookingData(response: List<History>) {
    _bookingHistories.value = response
  }
}