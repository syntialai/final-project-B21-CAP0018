package com.qhope.core.data.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.qhope.core.api.source.response.user.UserResponse
import com.qhope.core.data.mapper.UserMapper
import com.qhope.core.data.repository.base.FetchDataWrapper
import com.qhope.core.data.source.AuthRemoteDataSource
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.repository.AuthRepository
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

  override suspend fun registerUser(phoneNumber: String): Flow<ResponseWrapper<User>> {
    return object : FetchDataWrapper<UserResponse, User>() {
      override suspend fun fetchData(): UserResponse {
        return authRemoteDataSource.registerUser(phoneNumber)
      }

      override suspend fun mapData(response: UserResponse): User {
        return UserMapper.mapToUser(response)
      }
    }.updateData()
  }
}