package com.bangkit.team18.core.domain.repository

import android.app.Activity
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithCredential(credential: PhoneAuthCredential): Flow<ResponseWrapper<Boolean>>
    fun requestToken(
        activity: Activity,
        phoneNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken?,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    fun getCredential(storedVerificationId: String, token: String): PhoneAuthCredential
    fun logout()
    fun addAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)
    fun removeAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener)
}