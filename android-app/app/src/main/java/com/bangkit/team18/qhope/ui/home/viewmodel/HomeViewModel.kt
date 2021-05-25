package com.bangkit.team18.qhope.ui.home.viewmodel

import com.bangkit.team18.core.domain.usecase.HospitalUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModel

class HomeViewModel(private val hospitalUseCase: HospitalUseCase) : BaseViewModel() {}