package com.qhope.app.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.usecase.AuthUseCase
import com.qhope.core.domain.usecase.UserUseCase

class ProfileViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  authUseCase: AuthUseCase,
  private val userUseCase: UserUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  fun getUserDoc() {
    launchViewModelScope({
      userUseCase.getUserProfile().runFlow({
        _userDoc.value = it
      })
    })
  }
}