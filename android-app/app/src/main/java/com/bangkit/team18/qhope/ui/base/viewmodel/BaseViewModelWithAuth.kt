package com.bangkit.team18.qhope.ui.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseViewModelWithAuth(private val authUseCase: AuthUseCase) : BaseViewModel(),
  FirebaseAuth.AuthStateListener {

  private var _user = MutableLiveData<FirebaseUser?>()
  val user: LiveData<FirebaseUser?>
    get() = _user

  override fun onCleared() {
    authUseCase.removeAuthStateListener(this)
  }

  override fun onAuthStateChanged(auth: FirebaseAuth) {
    _user.value = auth.currentUser
  }

  fun logOut() {
    authUseCase.logout()
  }

  protected fun initAuthStateListener() {
    authUseCase.addAuthStateListener(this)
  }

  protected fun getUserId() = _user.value?.uid
}