package com.qhope.app.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.api.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase
import com.qhope.core.utils.view.DataUtils.isNotNull
import java.io.File

class RegistrationViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  authUseCase: AuthUseCase,
  private val userUseCase: UserUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture

  private var _isSubmitted = MutableLiveData<Boolean>()
  val isSubmitted: LiveData<Boolean> get() = _isSubmitted

  private var _birthDate = MutableLiveData<Long>()
  val birthDate: LiveData<Long> get() = _birthDate

  fun setProfilePicture(filePath: String) {
    _profilePicture.value = File(filePath)
  }

  fun submitData(name: String) {
    launchViewModelScope({
      val request = UpdateUserProfileRequest(
        name = name,
        birth_date = _birthDate.value
      )
      userUseCase.updateUser(request, _profilePicture.value).runFlow({ user ->
        _isSubmitted.value = user.isNotNull()
      })
    })
  }

  fun setBirthDate(birthDate: Long) {
    _birthDate.value = birthDate
  }
}