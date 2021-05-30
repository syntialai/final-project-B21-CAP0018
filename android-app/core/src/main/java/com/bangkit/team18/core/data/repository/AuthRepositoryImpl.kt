package com.bangkit.team18.core.data.repository

import android.app.Activity
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {
  override fun signInWithCredential(credential: PhoneAuthCredential) = callbackFlow {
    val signIn = firebaseAuth.signInWithCredential(credential)
    signIn.addOnCompleteListener { task ->
      GlobalScope.launch {
        when {
          task.isSuccessful -> {
            trySend(ResponseWrapper.Success(true))
          }
          task.exception is IOException -> {
            trySend(ResponseWrapper.NetworkError<Boolean>())
          }
          else -> {
            trySend(ResponseWrapper.Error<Boolean>(task.exception?.message))
          }
        }
      }
    }
    awaitClose { }
  }

  override fun requestToken(
    activity: Activity,
    phoneNumber: String,
    resendToken: PhoneAuthProvider.ForceResendingToken?,
    callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  ) {
    val options = PhoneAuthOptions.newBuilder(Firebase.auth)
      .setPhoneNumber(phoneNumber)
      .setTimeout(30L, TimeUnit.SECONDS)
      .setActivity(activity)
      .setCallbacks(callbacks)

    resendToken?.let {
      options.setForceResendingToken(it)
    }
    PhoneAuthProvider.verifyPhoneNumber(options.build())
  }

  override fun getCredential(verificationId: String, token: String): PhoneAuthCredential {
    return PhoneAuthProvider.getCredential(verificationId, token)
  }

  override fun logout() {
    firebaseAuth.signOut()
  }

  override fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) {
    firebaseAuth.addAuthStateListener(authStateListener)
  }

  override fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) {
    firebaseAuth.removeAuthStateListener(authStateListener)
  }
}