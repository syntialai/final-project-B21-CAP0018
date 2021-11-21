package com.bangkit.team18.core.domain.repository

import android.app.Activity
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

  fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>>

  fun requestToken(
    activity: Activity,
    phoneNumber: String,
    resendToken: PhoneAuthProvider.ForceResendingToken?,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  )

  fun getCredential(verificationId: String, token: String): PhoneAuthCredential

  fun logout()

  fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)

  fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)

  suspend fun registerUser(phoneNumber: String): Flow<ResponseWrapper<User>>
}