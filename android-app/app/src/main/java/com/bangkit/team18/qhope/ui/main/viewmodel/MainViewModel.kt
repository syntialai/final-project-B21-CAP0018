package com.bangkit.team18.qhope.ui.main.viewmodel

import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

class MainViewModel(authUseCase: AuthUseCase) : BaseViewModelWithAuth(authUseCase) {
  init {
    initAuthStateListener()
  }
}