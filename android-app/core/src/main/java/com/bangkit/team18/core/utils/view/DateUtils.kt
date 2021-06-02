package com.bangkit.team18.core.utils.view

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
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

  @SuppressLint("SimpleDateFormat")
  fun Timestamp.toStringFormat(pattern: String): String {
    val dateFormat = SimpleDateFormat(pattern)
    return dateFormat.format(this.toDate())
  }
}