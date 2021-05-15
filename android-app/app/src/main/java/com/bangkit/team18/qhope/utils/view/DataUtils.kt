package com.bangkit.team18.qhope.utils.view

import androidx.lifecycle.LiveData

object DataUtils {

  fun <T> T.isNull() = this == null

  fun <T> T.isNotNull() = this.isNull().not()

  fun Int?.orZero() = this ?: 0

  fun LiveData<Boolean>.orFalse() = this.value ?: false
}