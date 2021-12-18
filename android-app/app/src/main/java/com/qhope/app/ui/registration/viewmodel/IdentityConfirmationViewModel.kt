package com.qhope.app.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.source.request.user.IdentityConfirmationRequest
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.user.GenderType
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase

class IdentityConfirmationViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  private var _saveEvent = MutableLiveData<Boolean>()
  val saveEvent: LiveData<Boolean> get() = _saveEvent

  fun getUserDoc() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userDoc.value = it
      })
    })
  }

  fun setBirthDate(birthDate: Long) {
    _userDoc.value?.let { user ->
      user.birthDate = birthDate
      _userDoc.value = user
    }
  }

  fun save(
    name: String,
    gender: GenderType,
    placeOfBirth: String,
    address: String,
    district: String,
    city: String,
    village: String,
    hamlet: String,
    bloodType: String,
    religion: String
  ) {
    val request = constructSaveRequest(
      name,
      gender,
      placeOfBirth,
      address,
      district,
      city,
      village,
      hamlet,
      bloodType,
      religion
    )
    launchViewModelScope({
      userUseCase.confirmUserIdentity(request).runFlow({
        _saveEvent.value = true
      }, {
        _saveEvent.value = false
      })
    })
  }

  private fun constructSaveRequest(
    name: String,
    gender: GenderType,
    placeOfBirth: String,
    address: String,
    district: String,
    city: String,
    village: String,
    hamlet: String,
    bloodType: String,
    religion: String
  ): IdentityConfirmationRequest {
    val user = _userDoc.value
    return IdentityConfirmationRequest(
      nik = user?.ktpNumber.orEmpty(),
      name = name,
      gender = gender.name,
      birth_place = placeOfBirth,
      date_of_birth = user?.birthDate ?: 0L,
      blood_type = bloodType,
      ktp_address = address,
      district = district,
      village = village,
      city = city,
      neighborhood = user?.neighborhood.orEmpty() ,
      hamlet = hamlet,
      religion = religion
    )
  }
}