package com.bangkit.team18.qhope.ui.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

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