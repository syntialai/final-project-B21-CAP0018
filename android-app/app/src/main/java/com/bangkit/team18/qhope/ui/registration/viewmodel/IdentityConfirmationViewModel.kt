package com.bangkit.team18.qhope.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

  fun constructSaveRequest(
    ktpNumber: String,
    name: String,
    gender: GenderType,
    placeOfBirth: String,
    address: String
  ) {

  }

  fun save() {
    val request = true
    launchViewModelScope({

    })
    _saveEvent.value = true
    _saveEvent.value = false
  }
}