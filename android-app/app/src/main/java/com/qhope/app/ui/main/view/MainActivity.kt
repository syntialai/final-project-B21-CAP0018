package com.qhope.app.ui.main.view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.qhope.app.R
import com.qhope.app.databinding.ActivityMainBinding
import com.qhope.app.ui.base.view.BaseActivityViewModel
import com.qhope.app.ui.main.viewmodel.MainViewModel
import com.qhope.app.utils.setupWithNavController
import com.qhope.core.utils.view.DataUtils.orFalse
import com.qhope.core.utils.view.ViewUtils.showOrRemove

class MainActivity : BaseActivityViewModel<ActivityMainBinding, MainViewModel>(
  ActivityMainBinding::inflate,
  MainViewModel::class
) {

  companion object {
    private const val SHOW_ACTION_BAR = "SHOW_ACTION_BAR"
    private const val SHOW_BOTTOM_NAV = "SHOW_BOTTOM_NAV"
  }

  private var currentNavController: LiveData<NavController>? = null

  private val destinationChangedListener =
    NavController.OnDestinationChangedListener { _, destination, _ ->
      changeElevation(destination.id)
      showActionBar(destination.id)
      showBottomNav(destination.id)
    }

  override fun setupViews(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
      setupBottomNavigationBar()
    }
  }

  override fun onClick(v: View?) {
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(SHOW_ACTION_BAR, supportActionBar?.isShowing.orFalse())
    outState.putBoolean(SHOW_BOTTOM_NAV, binding.mainBottomNavBnv.isVisible)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    setupBottomNavigationBar()
    showActionBar(null, savedInstanceState.getBoolean(SHOW_ACTION_BAR, true))
    showBottomNav(null, savedInstanceState.getBoolean(SHOW_BOTTOM_NAV, false))
  }

  private fun setupBottomNavigationBar() {
    val navGraphIds =
      listOf(
        R.navigation.home_navigation,
        R.navigation.history_navigation,
        R.navigation.profile_navigation
      )

    val controller = binding.mainBottomNavBnv.setupWithNavController(
      navGraphIds = navGraphIds,
      fragmentManager = supportFragmentManager,
      containerId = R.id.nav_host_fragment,
      intent = intent,
      destinationChangedListener = destinationChangedListener
    )

    setSupportActionBar(binding.mainToolbar)
    controller.observeForever {
      setupActionBarWithNavController(it)
    }
    currentNavController = controller
  }

  private fun changeElevation(id: Int) {
    val elevation = when (id) {
      R.id.profileFragment -> 0f
      else -> resources.getDimension(R.dimen.dp_4)
    }
    supportActionBar?.elevation = elevation
  }

  private fun getShouldShowActionBar(id: Int) = when (id) {
    R.id.hospitalDetailFragment -> false
    R.id.profileVerificationResultFragment -> false
    else -> true
  }

  private fun getShouldShowBottomNav(id: Int) = when (id) {
    R.id.homeFragment -> true
    R.id.historyFragment -> true
    R.id.profileFragment -> true
    else -> false
  }

  private fun showActionBar(id: Int?, defaultValue: Boolean = false) {
    id?.let {
      if (getShouldShowActionBar(it)) {
        supportActionBar?.show()
      } else {
        supportActionBar?.hide()
      }
    } ?: run {
      if (defaultValue) {
        supportActionBar?.show()
      } else {
        supportActionBar?.hide()
      }
    }
  }

  private fun showBottomNav(id: Int?, defaultValue: Boolean = false) {
    binding.mainBottomNavBnv.showOrRemove(id?.let {
      getShouldShowBottomNav(it)
    } ?: defaultValue)
  }

  override fun onSupportNavigateUp(): Boolean {
    return currentNavController?.value?.navigateUp() ?: false
  }
}