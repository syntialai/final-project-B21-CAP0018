package com.qhope.app.ui.tnc.viewmodel

import com.qhope.app.ui.base.viewmodel.BaseViewModelWithAuth
import com.qhope.core.data.repository.AuthSharedPrefRepository
import com.qhope.core.domain.usecase.AuthUseCase

class TermsAndConditionViewModel(
  authSharedPrefRepository: AuthSharedPrefRepository,
  authUseCase: AuthUseCase,
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

}