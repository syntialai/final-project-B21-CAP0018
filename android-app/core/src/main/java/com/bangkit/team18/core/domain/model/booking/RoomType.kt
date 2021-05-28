package com.bangkit.team18.core.domain.model.booking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomType(

    val id: String,

    val name: String,

    val price: String,

    val availableRoomCount: Int
): Parcelable
