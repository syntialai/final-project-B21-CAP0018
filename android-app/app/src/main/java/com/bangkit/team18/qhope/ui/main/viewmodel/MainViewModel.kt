package com.bangkit.team18.qhope.ui.main.viewmodel

import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

class MainViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  init {
    initAuthStateListener()
  }
}