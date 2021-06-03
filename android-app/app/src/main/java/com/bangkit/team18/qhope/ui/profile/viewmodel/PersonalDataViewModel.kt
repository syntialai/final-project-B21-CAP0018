package com.bangkit.team18.qhope.ui.profile.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.Timestamp
import java.io.File

class PersonalDataViewModel(private val userUseCase: UserUseCase, authUseCase: AuthUseCase) :
  BaseViewModelWithAuth(authUseCase) {
  private var _profilePicture = MutableLiveData<File>()
  val profilePicture: LiveData<File> get() = _profilePicture
  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc
  private val _mode = MutableLiveData(ModeType.VIEW)
  val mode: LiveData<ModeType> get() = _mode
  private var _birthDate = MutableLiveData<Long>()
  val birthDate: LiveData<Long> get() = _birthDate

  init {
    initAuthStateListener()
  }

  fun getUserDoc() {
    getUserId()?.let {
      launchViewModelScope({
        userUseCase.getUser(it).runFlow({
          _userDoc.value = it
        })
      })
    }
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
    ktpNumber: String,
    placeOfBirth: String,
    address: String,
    gender: GenderType
  ) {
    getUserId()?.let {
      if (profilePicture.value.isNotNull()) {
        val imageUri = Uri.fromFile(_profilePicture.value)
        launchViewModelScope({
          userUseCase.uploadUserImage(it, imageUri).runFlow({ uri ->
            val user = User(
              imageUrl = uri.toString(),
              name = name,
              ktpNumber = ktpNumber,
              placeOfBirth = placeOfBirth,
              address = address,
              gender = gender,
              birthDate = Timestamp(birthDate.value as Long / 1000, 0)
            )
            launchViewModelScope({
              userUseCase.updatePersonalData(it, user).runFlow({
                changeMode()
              })
            })
          })
        })
      } else {
        val user = User(
          imageUrl = userDoc.value?.imageUrl ?: "",
          name = name,
          ktpNumber = ktpNumber,
          placeOfBirth = placeOfBirth,
          address = address,
          gender = gender,
          birthDate = Timestamp(birthDate.value as Long / 1000, 0)
        )
        launchViewModelScope({
          userUseCase.updatePersonalData(it, user).runFlow({
            changeMode()
          })
        })
      }
    }
  }

  fun setProfilePicture(filePath: String) {
    _profilePicture.value = File(filePath)
  }

  enum class ModeType {
    VIEW, EDIT
  }
}