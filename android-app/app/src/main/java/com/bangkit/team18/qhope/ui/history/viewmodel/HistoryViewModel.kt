package com.bangkit.team18.qhope.ui.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.history.History
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel

class HistoryViewModel(private val roomBookingUseCase: RoomBookingUseCase) : BaseViewModel() {

  private var _bookingHistories = MutableLiveData<List<History>>()
  val bookingHistories: LiveData<List<History>>
    get() = _bookingHistories

  private var _userId: String? = null

  fun fetchUserBookingHistories() {
    _userId?.let { id ->
      launchViewModelScope({
        roomBookingUseCase.getUserBookings(id).runFlow(::setRoomBookingData)
      })
    }
  }

  fun initializeUserId() {
    // TODO: Complete this
  }

  private fun setRoomBookingData(response: List<History>) {
    _bookingHistories.value = response
  }
}