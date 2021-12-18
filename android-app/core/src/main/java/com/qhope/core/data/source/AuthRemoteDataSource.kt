package com.qhope.core.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.qhope.core.data.source.response.user.UserResponse
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {

  fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>>

  fun logout()

  fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)

  fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)

  suspend fun registerUser(phoneNumber: String): UserResponse
}