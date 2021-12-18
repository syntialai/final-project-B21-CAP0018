package com.qhope.app.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.api.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.user.GenderType
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.utils.view.DataUtils.isNotNull

class EditPersonalDataViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  private var _birthDate = MutableLiveData<Long>()
  val birthDate: LiveData<Long> get() = _birthDate

  private var _saved = MutableLiveData<Boolean>()
  val saved: LiveData<Boolean> get() = _saved

  init {
    initAuthStateListener()
  }

  fun getUserDoc() {
    launchViewModelScope({
      userUseCase.getUserProfile(true).runFlow({
        _userDoc.value = it
      })
    })
  }

  fun setBirthDate(birthDate: Long) {
    _birthDate.value = birthDate
  }

  fun update(
    name: String,
    placeOfBirth: String,
    address: String,
    gender: GenderType
  ) {
    val request = UpdateUserProfileRequest(
      name = name,
      birth_place = placeOfBirth,
      address = address,
      gender = gender.name,
      birth_date = _birthDate.value
    )
    launchViewModelScope({
      userUseCase.updateUser(request).runFlow({
        _saved.value = it.isNotNull()
      })
    })
  }
}