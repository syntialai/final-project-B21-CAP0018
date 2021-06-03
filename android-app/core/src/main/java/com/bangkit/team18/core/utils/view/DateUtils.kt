package com.bangkit.team18.core.utils.view

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
  fun Long.toDateString(pattern: String, isMillis: Boolean = false): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(
      this * if (isMillis) {
        1000
      } else {
        1
      }
    )

}