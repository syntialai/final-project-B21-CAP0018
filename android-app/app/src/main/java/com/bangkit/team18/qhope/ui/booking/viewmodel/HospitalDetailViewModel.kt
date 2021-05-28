package com.bangkit.team18.qhope.ui.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.booking.HospitalDetail
import com.bangkit.team18.core.domain.model.booking.RoomType
import com.bangkit.team18.core.domain.usecase.HospitalUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel

class HospitalDetailViewModel(private val hospitalUseCase: HospitalUseCase) : BaseViewModel() {

  private lateinit var _id: String

  private var _hospital = MutableLiveData<HospitalDetail>()
  val hospital: LiveData<HospitalDetail>
    get() = _hospital

  private var _hospitalRoomTypes = MutableLiveData<List<RoomType>>()
  val hospitalRoomTypes: LiveData<List<RoomType>>
    get() = _hospitalRoomTypes

  private var selectedRoomType: RoomType? = null

  fun fetchHospitalDetails() {
    if (isIdInitialized()) {
      launchViewModelScope({
        hospitalUseCase.getHospitalDetail(_id).runFlow({ data ->
          _hospital.value = data
        })
      })
      fetchHospitalRoomTypes()
    }
  }

  fun getSelectedRoomType() = selectedRoomType

  fun getId() = _id

  fun selectRoomType(roomType: RoomType) {
    selectedRoomType = roomType
  }

  private fun fetchHospitalRoomTypes() {
    launchViewModelScope({
      hospitalUseCase.getHospitalRoomTypes(_id).runFlow({ data ->
        _hospitalRoomTypes.value = data
      })
    })
  }

  fun initializeId(id: String) {
    _id = id
  }

  private fun isIdInitialized() = this::_id.isInitialized
}