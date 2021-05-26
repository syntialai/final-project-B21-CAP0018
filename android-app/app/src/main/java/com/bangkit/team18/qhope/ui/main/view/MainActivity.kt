package com.bangkit.team18.qhope.ui.main.view

import android.os.Bundle
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.team18.qhope.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
=======
import android.view.View
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityMainBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivity
import com.bangkit.team18.qhope.ui.main.adapter.MainAdapter
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
    }
  }

  override fun onClick(v: View?) {
    // No implementation needed
>>>>>>> ad59e5d9f9ef41705d4e091e6e8a1a2e236aee1c
  }
}