package com.bangkit.team18.qhope.ui.registration.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.Timestamp
import java.io.File

class RegistrationViewModel(
  authUseCase: AuthUseCase,
  private val userUseCase: UserUseCase
) : BaseViewModelWithAuth(authUseCase) {
  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture
  private var _isSubmitted = MutableLiveData<Boolean>()
  val isSubmitted: LiveData<Boolean> get() = _isSubmitted
  private var _birthDate = MutableLiveData<Long>()
  val birthDate: LiveData<Long> get() = _birthDate

  init {
    initAuthStateListener()
  }

  fun setProfilePicture(filePath: String) {
    _profilePicture.value = File(filePath)
  }

  fun submitData(name: String) {
    user.value?.let {
      launchViewModelScope({
        if (profilePicture.value.isNotNull()) {
          val imageUri = Uri.fromFile(_profilePicture.value)
          userUseCase.uploadUserImage(it.uid, imageUri).runFlow({ uri ->
            val user = User(
              it.uid,
              name,
              it.phoneNumber.toString(),
              uri.toString(),
              Timestamp(birthDate.value as Long / 1000, 0),
              VerificationStatus.NOT_UPLOAD
            )
            submitUser(user)
          }, {})
        } else {
          val user = User(
            it.uid,
            name,
            it.phoneNumber.toString(),
            "",
            Timestamp(birthDate.value as Long / 1000, 0),
            VerificationStatus.NOT_UPLOAD
          )
          submitUser(user)
        }
      })
    }
  }

  private fun submitUser(user: User) {
    launchViewModelScope({
      userUseCase.addUser(user.id, user).runFlow({
        _isSubmitted.value = it
      }, {})
    })
  }

  fun setBirthDate(birthDate: Long) {
    _birthDate.value = birthDate
  }
}