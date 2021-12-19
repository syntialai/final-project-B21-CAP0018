package com.qhope.core.data.source.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.qhope.core.data.source.request.auth.RegisterUserRequest
import com.qhope.core.data.source.response.user.UserResponse
import com.qhope.core.data.source.service.AuthService
import com.qhope.core.data.source.AuthRemoteDataSource
import com.qhope.core.data.source.base.BaseRemoteDataSource
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class AuthRemoteDataSourceImpl(
  private val firebaseAuth: FirebaseAuth,
  private val authService: AuthService
) : BaseRemoteDataSource(), AuthRemoteDataSource {

  override fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>> =
    firebaseAuth.signInWithCredential(credential).loadData()

  override fun logout() {
    firebaseAuth.signOut()
  }

  override fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) {
    firebaseAuth.addAuthStateListener(authStateListener)
  }

  override fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener) {
    firebaseAuth.removeAuthStateListener(authStateListener)
  }

  override suspend fun registerUser(phoneNumber: String): UserResponse {
    val request = RegisterUserRequest(phoneNumber)
    return authService.registerUser(request)
  }
}