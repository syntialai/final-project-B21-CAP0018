package com.qhope.app.ui.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModel
import com.qhope.core.domain.model.history.HistoryDetail
import com.qhope.core.domain.usecase.RoomBookingUseCase

class HistoryDetailViewModel(private val roomBookingUseCase: RoomBookingUseCase) : BaseViewModel() {

  private var _bookingHistory = MutableLiveData<HistoryDetail>()
  val bookingHistory: LiveData<HistoryDetail>
    get() = _bookingHistory

  private var _historyId: String? = null

  fun fetchUserBookingHistory() {
    _historyId?.let { id ->
      launchViewModelScope({
        roomBookingUseCase.getUserBookingDetail(id).runFlow(::setRoomBookingData)
      })
    }
  }

  fun initializeHistoryId(id: String?) {
    _historyId = id
  }

  private fun setRoomBookingData(response: HistoryDetail) {
    _bookingHistory.value = response
  }
}