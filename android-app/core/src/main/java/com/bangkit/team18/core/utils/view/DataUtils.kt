package com.bangkit.team18.core.utils.view

import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*

object DataUtils {

  const val MMMM_D_YYYY = "MMMM d, yyyy"
  const val HH_MM_A_12H_FORMAT = "hh:mm a"

  fun <T> T.isNull() = this == null

  fun <T> T.isNotNull() = this.isNull().not()

  fun areNotEmpty(vararg data: String) = data.all { it.isNotBlank() }

  fun Int?.orZero() = this ?: 0

  fun Long?.orZero() = this ?: 0L

  fun Double?.orZero() = this ?: 0.0

  fun Boolean?.orFalse() = this ?: false

  fun String?.orHyphen(): String {
    return if (this.isNullOrEmpty()) "-"
    else this
  }

  fun LiveData<Boolean>.orFalse() = this.value ?: false

  fun <T : Enum<T>> Enum<T>?.getText() = this?.name?.replaceFirstChar {
    it.titlecase(Locale.ROOT)
  }.orEmpty()

  fun getCurrentAnd3DaysLaterTimestamp(): Pair<Long, Long> {
    val currentTimestamp = System.currentTimeMillis()
    val calendar = Calendar.getInstance().apply {
      timeInMillis = currentTimestamp
    }
    calendar.add(Calendar.DAY_OF_MONTH, 3)
    return Pair(currentTimestamp, calendar.timeInMillis)
  }

  fun toFormattedDateTime(date: Date, pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
  }
}