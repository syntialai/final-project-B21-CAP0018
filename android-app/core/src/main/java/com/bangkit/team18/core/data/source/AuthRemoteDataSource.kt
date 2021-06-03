package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
  fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<FirebaseUser>>
  fun logout()
  fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)
  fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)
}