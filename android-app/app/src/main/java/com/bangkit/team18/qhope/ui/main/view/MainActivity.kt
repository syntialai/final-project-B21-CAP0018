package com.bangkit.team18.qhope.ui.main.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityMainBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivity
import com.bangkit.team18.qhope.ui.main.adapter.MainAdapter
import com.bangkit.team18.qhope.utils.Router
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      viewPagerMain.adapter = MainAdapter(supportFragmentManager, lifecycle)
      TabLayoutMediator(tabLayoutMain, viewPagerMain) { tab, position ->
        tab.text = when (position) {
          MainAdapter.HOME_FRAGMENT_INDEX -> getString(R.string.home_label)
          MainAdapter.HISTORY_FRAGMENT_INDEX -> getString(R.string.history_label)
          MainAdapter.PROFILE_FRAGMENT_INDEX -> getString(R.string.profile_label)
          else -> null
        }
      }.attach()
      viewPagerMain.setCurrentItem(
        intent.getIntExtra(Router.PARAM_MAIN_FIRST_FRAGMENT, MainAdapter.HOME_FRAGMENT_INDEX),
        true
      )
    }
  }

  override fun onClick(v: View?) {
    // No implementation needed
  }
}