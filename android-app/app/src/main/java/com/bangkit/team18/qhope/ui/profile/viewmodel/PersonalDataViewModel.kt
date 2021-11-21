package com.bangkit.team18.qhope.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import java.io.File

class PersonalDataViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  private val _mode = MutableLiveData(ModeType.VIEW)
  val mode: LiveData<ModeType> get() = _mode

  private var _birthDate = MutableLiveData<Long>()
  val birthDate: LiveData<Long> get() = _birthDate

  fun getUserDoc() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userDoc.value = it
      })
    })
  }

  fun changeMode() {
    if (mode.value == ModeType.VIEW) {
      _mode.value = ModeType.EDIT
    } else {
      _mode.value = ModeType.VIEW
    }
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
      date_of_birth = _birthDate.value,
      address = address,
      birth_place = placeOfBirth,
      gender = gender.name
    )
    launchViewModelScope({
      userUseCase.updateUser(request, _profilePicture.value).runFlow({
        changeMode()
      })
    })
  }

  fun setProfilePicture(filePath: String) {
    _profilePicture.value = File(filePath)
  }

  enum class ModeType {
    VIEW, EDIT
  }
}