package com.bangkit.team18.qhope.ui.login.view

import android.view.View
import com.bangkit.team18.qhope.databinding.FragmentLoginBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
  override fun setupViews() {
    binding.apply {
      loginContinue.setOnClickListener {
        loginContinue.setOnClickListener {
          OtpFragment.newInstance(loginPhoneNumber.text.toString()) {
          }
            .show(parentFragmentManager, TAG)
        }
      }
    }
  }

  override fun onClick(v: View?) {
  }

  companion object {
    private const val TAG = "login.LoginFragment"
  }
}