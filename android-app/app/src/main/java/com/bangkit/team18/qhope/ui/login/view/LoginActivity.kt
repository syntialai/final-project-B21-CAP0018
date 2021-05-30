package com.bangkit.team18.qhope.ui.login.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.bangkit.team18.core.utils.view.DataUtils.isNotNull
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityLoginBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.login.viewmodel.LoginViewModel
import com.bangkit.team18.qhope.ui.registration.view.RegistrationActivity

class LoginActivity : BaseActivityViewModel<ActivityLoginBinding, LoginViewModel>(
  ActivityLoginBinding::inflate,
  LoginViewModel::class
) {
  companion object {
    private const val TAG = "login.LoginFragment"
  }

  private var otpBottomSheet: OtpFragment? = null
  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      loginPhoneNumber.doAfterTextChanged { text ->
        loginContinue.isEnabled = text?.length ?: 0 > 0
        viewModel.clearCountDown()
      }
      loginContinue.setOnClickListener(this@LoginActivity)
    }
    viewModel.user.observe(this, {
      if (it.isNotNull()) {
        otpBottomSheet?.dismiss()
        navigateToRegistration()
      }
    })
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.login_continue -> {
        val phoneNumber = String.format(
          getString(
            R.string.indonesian_phone_code_pattern,
            binding.loginPhoneNumber.text.toString()
          )
        )
        viewModel.requestOtp(this, phoneNumber)
        otpBottomSheet = OtpFragment.newInstance(
          phoneNumber,
          viewModel.countDown,
          ::resendOtp,
          ::verifyOtp,
          ::loggedIn
        )
        otpBottomSheet?.show(supportFragmentManager, TAG)
      }
    }
  }

  private fun loggedIn(
    otpCode1: EditText,
    otpCode2: EditText,
    otpCode3: EditText,
    otpCode4: EditText,
    otpCode5: EditText,
    otpCode6: EditText
  ) {
    viewModel.detectedToken.observe(this, {
      otpCode1.setText(it[0].toString())
      otpCode2.setText(it[1].toString())
      otpCode3.setText(it[2].toString())
      otpCode4.setText(it[3].toString())
      otpCode5.setText(it[4].toString())
      otpCode6.setText(it[5].toString())
    })
  }

  private fun navigateToRegistration() {
    val intent = Intent(this, RegistrationActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun verifyOtp(code: String) {
    viewModel.verifyCode(code)
  }

  private fun resendOtp(phoneNumber: String) {
    viewModel.resendOtp(this, phoneNumber)
  }
}