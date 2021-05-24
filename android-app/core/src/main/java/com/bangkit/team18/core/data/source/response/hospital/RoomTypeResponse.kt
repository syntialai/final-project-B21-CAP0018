package com.bangkit.team18.core.data.source.response.hospital

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class RoomTypeResponse(

    @DocumentId
    val id: String,

    val price: Double,

    @PropertyName("total_room")
    val totalRoom: Int,

    @PropertyName("available_room")
    val availableRoom: Int,
) {
  constructor() : this(
      "",
      0.0,
      0,
      0
  )
}