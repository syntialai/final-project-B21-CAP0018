package com.bangkit.team18.qhope.ui.login.viewmodel

import android.app.Activity
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.domain.repository.AuthRepository
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginViewModel(private val authRepository: AuthRepository) :
  BaseViewModelWithAuth(authRepository) {
  private val _countDown = MutableLiveData<Int>()
  val countDown: LiveData<Int> get() = _countDown
  private val _detectedToken = MutableLiveData<String>()
  val detectedToken: LiveData<String> get() = _detectedToken
  private var timer: CountDownTimer? = null
  private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
  private var storedVerificationId = ""
  private var authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
      credential.smsCode?.let {
        _detectedToken.postValue(it)
      }
      launchViewModelScope({
        authRepository.signInWithCredential(credential).runFlow({})
      })
    }

    override fun onVerificationFailed(e: FirebaseException) {
      showErrorResponse("An internal error has occurred. Please try again later.")
    }

    override fun onCodeSent(
      verificationId: String,
      token: PhoneAuthProvider.ForceResendingToken
    ) {
      storedVerificationId = verificationId
      resendToken = token
    }
  }

  init {
    authRepository.addAuthStateListener(this)
  }

  private fun startCountDown() {
    if (timer == null) {
      timer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
          _countDown.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
          timer = null
        }
      }
      timer?.start()
    }
  }

  fun clearCountDown() {
    timer?.cancel()
    timer = null
  }

  fun resendOtp(activity: Activity, phoneNumber: String) {
    authRepository.requestToken(activity, phoneNumber, resendToken, authCallbacks)
    startCountDown()
  }

  fun requestOtp(activity: Activity, phoneNumber: String) {
    authRepository.requestToken(activity, phoneNumber, null, authCallbacks)
    startCountDown()
  }

  fun verifyCode(code: String) {
    if (detectedToken.value.isNullOrEmpty()) {
      if (storedVerificationId.isEmpty()) {
        clearCountDown()
        showErrorResponse("Session is not valid, Try resend OTP again.")
        return
      }
      launchViewModelScope({
        val credential = authRepository.getCredential(storedVerificationId, code)
        authRepository.signInWithCredential(credential).runFlow({})
      })
    }
  }
}