package com.bangkit.team18.qhope.ui.registration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.core.domain.usecase.UserUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

class VerificationResultViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val userUseCase: UserUseCase,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  init {
    initAuthStateListener()
  }

  fun getUserDoc() {
    launchViewModelScope({
      getUserId()?.let { uid ->
        userUseCase.getUser(uid).runFlow({
          it?.let { user ->
            _userDoc.value = user
          }
        })
      }
    })
  }
}