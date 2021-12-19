package com.qhope.app.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.home.Hospital
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.HospitalUseCase
import com.qhope.core.domain.usecase.UserUseCase

class HomeViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val hospitalUseCase: HospitalUseCase,
  private val authUseCase: AuthUseCase,
  private val userUseCase: UserUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _hospitals = MutableLiveData<List<Hospital>>()
  val hospitals: LiveData<List<Hospital>>
    get() = _hospitals

  private var _searchHospitalResults = MutableLiveData<List<Hospital>>()
  val searchHospitalResults: LiveData<List<Hospital>>
    get() = _searchHospitalResults

  private var _userData = MutableLiveData<User>()
  val userData: LiveData<User>
    get() = _userData

  fun fetchUserData() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userData.value = it
      })
    })
  }

  fun fetchHospitals() {
    launchViewModelScope({
      hospitalUseCase.getHospitals().runFlow(::setHospitalData)
    })
  }

  fun searchHospital(query: String) {
    if (query.length >= 2) {
      launchViewModelScope({
        hospitalUseCase.searchHospitals(query).runFlow(::setSearchResultData)
      })
    }
  }

  private fun setHospitalData(response: List<Hospital>) {
    _hospitals.value = response
  }

  private fun setSearchResultData(response: List<Hospital>) {
    _searchHospitalResults.value = response
  }
}