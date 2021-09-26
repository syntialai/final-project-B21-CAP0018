package com.bangkit.team18.qhope.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.model.user.GenderType
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.Timestamp

class EditPersonalDataViewModel(authUseCase: AuthUseCase, private val userUseCase: UserUseCase) :
  BaseViewModelWithAuth(authUseCase) {
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
    getUserId()?.let {
      launchViewModelScope({
        userUseCase.getUser(it).runFlow({
          _userDoc.value = it
        })
      })
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
      val user = User(
        imageUrl = userDoc.value?.imageUrl ?: "",
        name = name,
        ktpNumber = ktpNumber,
        placeOfBirth = placeOfBirth,
        address = address,
        gender = gender,
        birthDate = Timestamp(birthDate.value as Long, 0)
      )
      launchViewModelScope({
        userUseCase.updatePersonalData(it, user).runFlow({
          _saved.value = it
        })
      })
    }
  }
}