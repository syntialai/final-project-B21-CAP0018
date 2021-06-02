package com.bangkit.team18.qhope.ui.splash.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bangkit.team18.qhope.databinding.ActivitySplashScreenBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.splash.viewmodel.SplashScreenViewModel
import com.bangkit.team18.qhope.utils.Router
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity :
  BaseActivityViewModel<ActivitySplashScreenBinding, SplashScreenViewModel>(
    ActivitySplashScreenBinding::inflate,
    SplashScreenViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    // No Implementation Needed
  }

  override fun setupObserver() {
    super.setupObserver()
    viewModel.user.observe(this, { user ->
      routeByUserValue(user)
    })
  }

  override fun onClick(view: View?) {
    // No Implementation Needed
  }

  private fun routeByUserValue(user: FirebaseUser?) {
    lifecycleScope.launch {
      delay(300)
      user?.let {
        Router.goToLogin(this@SplashScreenActivity)
        finish()
      } ?: run {
        Router.goToMain(this@SplashScreenActivity)
        finish()
      }
    }
  }
}