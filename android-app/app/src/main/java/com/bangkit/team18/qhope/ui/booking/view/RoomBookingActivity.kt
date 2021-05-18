package com.bangkit.team18.qhope.ui.booking.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityRoomBookingBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivity

class RoomBookingActivity :
    BaseActivity<ActivityRoomBookingBinding>(ActivityRoomBookingBinding::inflate) {

  companion object {
    private const val KEY_HIDE_ACTION_BAR = "KEY_HIDE_ACTION_BAR"
  }

  private lateinit var navController: NavController

  private var hideActionBar = false

  private val destinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
    hideActionBar = when (destination.id) {
      R.id.hospitalDetailFragment -> true
      else -> false
    }
  }

  override fun setupViews(savedInstanceState: Bundle?) {
    savedInstanceState?.let {
      hideActionBar = it.getBoolean(KEY_HIDE_ACTION_BAR, false)
    }
    setupNavigation()
    showActionBar(hideActionBar)
  }

  override fun onResume() {
    super.onResume()
    navController.addOnDestinationChangedListener(destinationChangedListener)
  }

  override fun onPause() {
    super.onPause()
    navController.removeOnDestinationChangedListener(destinationChangedListener)
  }

  @SuppressLint("MissingSuperCall")
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(KEY_HIDE_ACTION_BAR, hideActionBar)
  }

  override fun onClick(view: View?) {
    // No Implementation needed
  }

  private fun setupNavigation() {
    navController = (supportFragmentManager.findFragmentById(
        R.id.act_room_booking_host_fragment) as NavHostFragment).navController
    setupActionBarWithNavController(navController)
  }

  private fun showActionBar(show: Boolean) {
    if (show) {
      supportActionBar?.show()
    } else {
      supportActionBar?.hide()
    }
  }
}