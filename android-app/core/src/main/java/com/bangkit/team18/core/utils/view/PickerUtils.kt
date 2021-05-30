package com.bangkit.team18.core.utils.view

import androidx.annotation.StringRes
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object PickerUtils {

  private const val HOUR_12 = 12
  private const val MINUTE_0 = 0

  fun getTimePicker(
    @StringRes titleRes: Int, hour: Int? = null,
    minute: Int? = null
  ): MaterialTimePicker {
    return MaterialTimePicker.Builder()
      .setTimeFormat(TimeFormat.CLOCK_12H)
      .setHour(hour ?: HOUR_12)
      .setMinute(minute ?: MINUTE_0)
      .setTitleText(titleRes)
      .build()
  }
}