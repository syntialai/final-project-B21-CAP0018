package com.bangkit.team18.qhope.ui.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseViewModelWithAuth(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val authUseCase: AuthUseCase
) : BaseViewModel(), FirebaseAuth.AuthStateListener {

  private var _user = MutableLiveData<FirebaseUser?>()
  val user: LiveData<FirebaseUser?>
    get() = _user

  private var _loggedOut = MutableLiveData<Boolean>()
  val loggedOut: LiveData<Boolean>
    get() = _loggedOut

  override fun onCleared() {
    authUseCase.removeAuthStateListener(this)
  }

  override fun onAuthStateChanged(auth: FirebaseAuth) {
    _user.value = auth.currentUser
  }

  fun saveIdToken(onSuccessFetch: (() -> Unit)? = null) {
    _user.value?.let { safeUser ->
      safeUser.getIdToken(true).addOnSuccessListener { tokenResult ->
        authSharedPrefRepository.idToken = tokenResult.token.orEmpty()
        onSuccessFetch?.invoke()
      }
    }
  }

  fun logOut() {
    authUseCase.logout()
    authSharedPrefRepository.clearSharedPrefs()
    _loggedOut.value = true
  }

  fun resetLogOutValue() {
    _loggedOut.value = false
  }

  protected fun initAuthStateListener() {
    authUseCase.addAuthStateListener(this)
  }
}