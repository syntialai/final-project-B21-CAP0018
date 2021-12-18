package com.qhope.app.ui.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModel
import com.qhope.core.data.mapper.DataMapper
import com.qhope.core.data.mapper.HospitalMapper
import com.qhope.core.domain.model.hospital.HospitalDetail
import com.qhope.core.domain.model.hospital.RoomType
import com.qhope.core.domain.usecase.HospitalUseCase

class HospitalDetailViewModel(private val hospitalUseCase: HospitalUseCase) : BaseViewModel() {

  private var _id: String? = null

  private var _hospital = MutableLiveData<HospitalDetail>()
  val hospital: LiveData<HospitalDetail>
    get() = _hospital

  private var selectedRoomType: RoomType? = null

  fun fetchHospitalDetails() {
    _id?.let { id ->
      launchViewModelScope({
        hospitalUseCase.getHospitalDetail(id).runFlow({ data ->
          _hospital.value = data
        })
      })
    }
  }

  fun getBookedHospital() = _hospital.value?.let {
    HospitalMapper.mapToBookedHospital(it)
  }

  fun getSelectedRoomType() = selectedRoomType

  fun initializeId(id: String) {
    _id = id
  }

  fun mapToFormattedPrice(price: Double): String = DataMapper.toFormattedPrice(price)

  fun selectRoomType(roomType: RoomType) {
    selectedRoomType = roomType
  }
}