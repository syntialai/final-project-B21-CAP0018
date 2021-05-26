package com.bangkit.team18.core.utils.view

import androidx.lifecycle.LiveData
import java.util.Calendar
import java.util.Locale

object DataUtils {

  fun <T> T.isNull() = this == null

  fun <T> T.isNotNull() = this.isNull().not()

  fun Int?.orZero() = this ?: 0

  fun Long?.orZero() = this ?: 0L

  fun Double?.orZero() = this ?: 0.0

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
}