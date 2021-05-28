package com.bangkit.team18.qhope.ui.registration.viewmodel

import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth

class RegistrationViewModel(private val authRepository: AuthRepository) : BaseViewModelWithAuth(authRepository) {
    init {
        authRepository.addAuthStateListener(this)
    }
    fun logOut() {
        authRepository.logout()
    }
}