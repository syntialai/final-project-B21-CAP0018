package com.qhope.core.domain.model.booking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookedHospital(

  val id: String,

  val name: String,

  val imagePath: String,

  val type: String,

  val address: String
) : Parcelable
