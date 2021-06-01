package com.bangkit.team18.core.data.repository

import android.app.Activity
import com.bangkit.team18.core.data.source.AuthRemoteDataSource
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class AuthRepositoryImpl(private val authRemoteDataSource: AuthRemoteDataSource) : AuthRepository {
  override fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>> =
    authRemoteDataSource.signInWithCredential(credential)

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

  override fun logout() = authRemoteDataSource.logout()

  override fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) =
    authRemoteDataSource.addAuthStateListener(authStateListener)

  override fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) =
    authRemoteDataSource.removeAuthStateListener(authStateListener)
}