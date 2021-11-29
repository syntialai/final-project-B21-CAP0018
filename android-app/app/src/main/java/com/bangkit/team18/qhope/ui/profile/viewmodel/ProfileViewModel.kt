package com.bangkit.team18.qhope.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

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