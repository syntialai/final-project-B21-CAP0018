package com.qhope.app.ui.main.viewmodel

import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.usecase.AuthUseCase

class MainViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  init {
    initAuthStateListener()
  }
}