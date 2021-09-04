package com.bangkit.team18.core.domain.usecase.interactor

import android.app.Activity
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val authRepository: AuthRepository) : AuthUseCase {
  override fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>> =
    authRepository.signInWithCredential(credential)

  override fun requestToken(
    activity: Activity,
    phoneNumber: String,
    resendToken: PhoneAuthProvider.ForceResendingToken?,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  ) = authRepository.requestToken(activity, phoneNumber, resendToken, callbacks)

  override fun getCredential(verificationId: String, token: String): PhoneAuthCredential =
    authRepository.getCredential(verificationId, token)

  override fun logout() = authRepository.logout()

  override fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) =
    authRepository.addAuthStateListener(authStateListener)

  override fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) =
    authRepository.removeAuthStateListener(authStateListener)
}