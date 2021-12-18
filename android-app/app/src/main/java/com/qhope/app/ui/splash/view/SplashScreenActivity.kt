package com.qhope.app.ui.splash.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.qhope.app.databinding.ActivitySplashScreenBinding
import com.qhope.app.ui.base.view.BaseActivityViewModel
import com.qhope.app.ui.splash.viewmodel.SplashScreenViewModel
import com.qhope.app.utils.Router
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity :
  BaseActivityViewModel<ActivitySplashScreenBinding, SplashScreenViewModel>(
    ActivitySplashScreenBinding::inflate,
    SplashScreenViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    supportActionBar?.hide()
    lifecycleScope.launch {
      checkIdToken()
      delay(300)
      Router.goToMain(this@SplashScreenActivity)
    }
  }

  private fun checkIdToken() {
    if (viewModel.isIdTokenEmpty()) {
      Router.goToLogin(this@SplashScreenActivity)
      finish()
    }
  }

  override fun showLoadingState(isLoading: Boolean) {}

  override fun onClick(view: View?) {
    // No Implementation Needed
  }
}