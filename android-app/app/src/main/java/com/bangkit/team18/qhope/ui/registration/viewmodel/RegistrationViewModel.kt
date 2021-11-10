package com.bangkit.team18.qhope.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
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
    user.value?.let {
      launchViewModelScope({
        val request = UpdateUserProfileRequest(
          name = name,
          date_of_birth = _birthDate.value
        )
        userUseCase.updateUser(request, _profilePicture.value).runFlow({ success ->
          _isSubmitted.value = success
        })
      })
    }
  }

  fun setBirthDate(birthDate: Long) {
    _birthDate.value = birthDate
  }
}