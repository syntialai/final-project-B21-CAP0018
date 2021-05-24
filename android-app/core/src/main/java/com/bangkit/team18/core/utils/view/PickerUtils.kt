package com.bangkit.team18.core.utils.view

import androidx.annotation.StringRes
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object PickerUtils {

  private const val HOUR_12 = 12
  private const val MINUTE_0 = 0

  fun getTimePicker(@StringRes titleRes: Int): MaterialTimePicker {
    return MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(HOUR_12)
        .setMinute(MINUTE_0)
        .setTitleText(titleRes)
        .build()
  }
}