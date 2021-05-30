package com.bangkit.team18.qhope.ui.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseViewModelWithAuth(private val authRepository: AuthRepository) : BaseViewModel(),
  FirebaseAuth.AuthStateListener {
  private var _user = MutableLiveData<FirebaseUser?>()
  val user: LiveData<FirebaseUser?> get() = _user

  override fun onCleared() {
    authRepository.removeAuthStateListener(this)
  }

  override fun onAuthStateChanged(auth: FirebaseAuth) {
    _user.value = auth.currentUser
  }
}