package com.bangkit.team18.qhope.ui.splash.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivitySplashScreenBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.splash.viewmodel.SplashScreenViewModel
import com.bangkit.team18.qhope.utils.Router
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity :
  BaseActivityViewModel<ActivitySplashScreenBinding, SplashScreenViewModel>(
    ActivitySplashScreenBinding::inflate,
    SplashScreenViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    supportActionBar?.hide()
  }

  override fun onResume() {
    super.onResume()

    viewModel.user.observe(this, {
      if (it.isNull()) {
        Router.goToLogin(this)
      } else {
        viewModel.getUserDoc()
      }
    })

    viewModel.userDoc.observe(this, {
      if (it.isNull()) {
        showErrorToast(
          null,
          R.string.fill_information_message
        )
        lifecycleScope.launch {
          delay(500)
          Router.goToRegistration(this@SplashScreenActivity)
        }
      } else {
        lifecycleScope.launch {
          delay(500)
          Router.goToMain(this@SplashScreenActivity)
        }
      }
    })
  }

  override fun showLoadingState(isLoading: Boolean) {}

  override fun onClick(view: View?) {
    // No Implementation Needed
  }
}