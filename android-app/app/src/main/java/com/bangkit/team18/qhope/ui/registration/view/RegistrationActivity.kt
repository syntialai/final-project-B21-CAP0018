package com.bangkit.team18.qhope.ui.registration.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityRegistrationBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.registration.viewmodel.RegistrationViewModel

class RegistrationActivity :
  BaseActivityViewModel<ActivityRegistrationBinding, RegistrationViewModel>(
    ActivityRegistrationBinding::inflate,
    RegistrationViewModel::class
  ) {
  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      logout.setOnClickListener(this@RegistrationActivity)
    }
    viewModel.user.observe(this, {
      if (it.isNull()) {
        finish()
      }
    })
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.logout -> viewModel.logOut()
    }
  }
}