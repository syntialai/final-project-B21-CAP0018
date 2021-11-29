package com.bangkit.team18.qhope.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.api.source.request.user.IdentityConfirmationRequest
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

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
    village: String
  ) {
    val request = constructSaveRequest(name, gender, placeOfBirth, address, district, city, village)
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
    village: String
  ): IdentityConfirmationRequest {
    val user = _userDoc.value
    return IdentityConfirmationRequest(
      name = name,
      gender = gender.name,
      birth_place = placeOfBirth,
      date_of_birth = user?.birthDate ?: 0L,
      blood_type = user?.bloodType.orEmpty(),
      ktp_address = address,
      district = district,
      village = village,
      city = city,
      neighborhood = user?.neighborhood.orEmpty() ,
      hamlet = user?.hamlet.orEmpty(),
      religion = user?.religion.orEmpty()
    )
  }
}