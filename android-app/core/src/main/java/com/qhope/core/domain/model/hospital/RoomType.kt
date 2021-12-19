package com.qhope.core.domain.model.hospital

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomType(

  val id: String,

  val name: String,

  val price: Double,

  val availableRoomCount: Int
) : Parcelable
