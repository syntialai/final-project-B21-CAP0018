package com.bangkit.team18.qhope.utils

import android.content.Context
import android.content.Intent
import com.bangkit.team18.qhope.ui.booking.view.RoomBookingActivity

object Router {

  const val PARAM_HOSPITAL_ID = "PARAM_HOSPITAL_ID"

  fun goToHospitalDetails(context: Context, id: String) {
    val intent = Intent(context, RoomBookingActivity::class.java).apply {
      putExtra(PARAM_HOSPITAL_ID, id)
    }
    context.startActivity(intent)
  }
}