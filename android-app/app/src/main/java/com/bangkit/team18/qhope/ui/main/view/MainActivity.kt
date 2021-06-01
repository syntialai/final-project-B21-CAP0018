package com.bangkit.team18.qhope.ui.main.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.bangkit.team18.core.utils.view.DataUtils.isNull
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityMainBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.main.viewmodel.MainViewModel
import com.bangkit.team18.qhope.utils.Router
import com.bangkit.team18.qhope.utils.setupWithNavController

class MainActivity : BaseActivityViewModel<ActivityMainBinding, MainViewModel>(
  ActivityMainBinding::inflate,
  MainViewModel::class
) {
  private var currentNavController: LiveData<NavController>? = null

  private val destinationChangedListener =
    NavController.OnDestinationChangedListener { _, destination, _ ->
      changeElevation(destination.id)
      showActionBar(destination.id)
    }

  override fun setupViews(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
      setupBottomNavigationBar()
    }
    viewModel.user.observe(this, {
      if (it.isNull()) {
        Router.goToLogin(this)
      }
    })
  }

  override fun onClick(v: View?) {
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    setupBottomNavigationBar()
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

  private fun showActionBar(id: Int) {
    // TODO: Add cases when fragment is hospitalDetail
    val show = true
    if (show) {
      supportActionBar?.show()
    } else {
      supportActionBar?.hide()
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    return currentNavController?.value?.navigateUp() ?: false
  }
}