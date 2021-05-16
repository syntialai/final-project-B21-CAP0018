package com.bangkit.team18.qhope.utils.view

import androidx.lifecycle.LiveData
import java.util.Locale

object DataUtils {

  fun <T> T.isNull() = this == null

  fun <T> T.isNotNull() = this.isNull().not()

  fun Int?.orZero() = this ?: 0

  fun Long?.orZero() = this ?: 0L

  fun LiveData<Boolean>.orFalse() = this.value ?: false

  fun <T : Enum<T>> Enum<T>?.getText() = this?.name?.replaceFirstChar {
    it.titlecase(Locale.ROOT)
  }.orEmpty()
}