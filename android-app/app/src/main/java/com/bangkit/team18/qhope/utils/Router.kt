package com.bangkit.team18.qhope.utils

import android.content.Context
import android.content.Intent
import com.bangkit.team18.qhope.ui.booking.view.RoomBookingActivity
import com.bangkit.team18.qhope.ui.main.adapter.MainAdapter
import com.bangkit.team18.qhope.ui.main.view.MainActivity

object Router {

  const val PARAM_HOSPITAL_ID = "PARAM_HOSPITAL_ID"
  const val PARAM_MAIN_FIRST_FRAGMENT = "PARAM_MAIN_FIRST_FRAGMENT"

  fun goToHospitalDetails(context: Context, id: String) {
    val intent = Intent(context, RoomBookingActivity::class.java).apply {
      putExtra(PARAM_HOSPITAL_ID, id)
    }
    context.startActivity(intent)
  }

  fun goToMain(context: Context, firstFragment: Int = MainAdapter.HOME_FRAGMENT_INDEX) {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra(PARAM_MAIN_FIRST_FRAGMENT, firstFragment)
      addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }
}