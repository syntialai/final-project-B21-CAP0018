package com.bangkit.team18.qhope.ui.login.viewmodel

import android.app.Activity
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.team18.core.data.repository.AuthSharedPrefRepository
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.usecase.AuthUseCase
import com.bangkit.team18.qhope.ui.base.viewmodel.BaseViewModelWithAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginViewModel(
  private val authSharedPrefRepository: AuthSharedPrefRepository,
  private val authUseCase: AuthUseCase
) : BaseViewModelWithAuth(authSharedPrefRepository, authUseCase) {

  private val _countDown = MutableLiveData<Int>()
  val countDown: LiveData<Int> get() = _countDown
  private val _detectedToken = MutableLiveData<String>()
  val detectedToken: LiveData<String> get() = _detectedToken
  private var timer: CountDownTimer? = null
  private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
  private var storedVerificationId = ""

  private val _userDoc = MutableLiveData<User>()
  val userDoc: LiveData<User> get() = _userDoc

  private var _phoneNumber: String? = null

  private var authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
      credential.smsCode?.let {
        _detectedToken.postValue(it)
      }
      launchViewModelScope({
        authUseCase.signInWithCredential(credential).runFlow({})
      })
    }

    override fun onVerificationFailed(e: FirebaseException) {
      showErrorResponse(e.message.toString())
      clearCountDown()
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
    initAuthStateListener()
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
    _countDown.value = 0
    timer?.cancel()
    timer = null
  }

  fun setPhoneNumber(phoneNumber: String) {
    _phoneNumber = phoneNumber
  }

  fun resendOtp(activity: Activity, phoneNumber: String) {
    authUseCase.requestToken(activity, phoneNumber, resendToken, authCallbacks)
    startCountDown()
  }

  fun requestOtp(activity: Activity, phoneNumber: String) {
    authUseCase.requestToken(activity, phoneNumber, null, authCallbacks)
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
        val credential = authUseCase.getCredential(storedVerificationId, code)
        authUseCase.signInWithCredential(credential).runFlow({})
      })
    }
  }

  fun registerUser() {
    launchViewModelScope({
      _phoneNumber?.let { phoneNumber ->
        authUseCase.registerUser(phoneNumber).runFlow({
          _userDoc.value = it
        })
      }
    })
  }
}