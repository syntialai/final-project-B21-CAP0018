package com.bangkit.team18.qhope.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.domain.usecase.HospitalUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel
import com.firebase.geofire.GeoLocation

class HomeViewModel(private val hospitalUseCase: HospitalUseCase) : BaseViewModel() {

  private var _nearbyHospitals = MutableLiveData<List<Hospital>>()
  val nearbyHospitals: LiveData<List<Hospital>>
    get() = _nearbyHospitals

  private var _searchHospitalResults = MutableLiveData<List<Hospital>>()
  val searchHospitalResults: LiveData<List<Hospital>>
    get() = _searchHospitalResults

  fun fetchNearbyHospitals(latitude: Double, longitude: Double) {
    val location = GeoLocation(latitude, longitude)
    launchViewModelScope({
      hospitalUseCase.getNearbyHospitals(location).runFlow(::setHospitalData)
    })
  }

  fun searchHospital(query: String) {
    if (query.length >= 3) {
      launchViewModelScope({
        hospitalUseCase.searchHospitals(query).runFlow(::setSearchResultData)
      })
    }
  }

  private fun setHospitalData(response: List<Hospital>) {
    _nearbyHospitals.value = response
  }

  private fun setSearchResultData(response: List<Hospital>) {
    _searchHospitalResults.value = response
  }
}