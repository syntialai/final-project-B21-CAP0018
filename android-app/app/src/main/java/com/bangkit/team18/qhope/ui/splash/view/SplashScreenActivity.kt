package com.bangkit.team18.qhope.ui.splash.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.qhope.databinding.ActivitySplashScreenBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.splash.viewmodel.SplashScreenViewModel

class SplashScreenActivity :
  BaseActivityViewModel<ActivitySplashScreenBinding, SplashScreenViewModel>(
    ActivitySplashScreenBinding::inflate,
    SplashScreenViewModel::class
  ) {

  override fun setupViews(savedInstanceState: Bundle?) {
    // No Implementation Needed
  }

  override fun onClick(view: View?) {
    // No Implementation Needed
  }
}