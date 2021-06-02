package com.bangkit.team18.qhope.ui.splash.viewmodel

import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

class SplashScreenViewModel(authUseCase: AuthUseCase) : BaseViewModelWithAuth(authUseCase) {

  init {
    initAuthStateListener()
  }
}