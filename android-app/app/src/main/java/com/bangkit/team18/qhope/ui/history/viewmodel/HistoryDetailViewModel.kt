package com.bangkit.team18.qhope.ui.history.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.history.HistoryDetail
import com.bangkit.team18.core.domain.usecase.RoomBookingUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel

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