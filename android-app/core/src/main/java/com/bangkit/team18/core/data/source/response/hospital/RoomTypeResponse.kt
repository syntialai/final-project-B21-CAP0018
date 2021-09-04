package com.bangkit.team18.core.data.source.response.hospital

import com.google.firebase.firestore.DocumentId

data class RoomTypeResponse(

  @DocumentId
  val id: String,

  val name: String,

  val price: Double,

  val total_room: Int,

  val available_room: Int,
) {
  constructor() : this(
    "",
    "",
    0.0,
    0,
    0
  )
}