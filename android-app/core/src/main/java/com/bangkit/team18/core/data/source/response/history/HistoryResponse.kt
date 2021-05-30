package com.bangkit.team18.core.data.source.response.history

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class HistoryResponse(

  @DocumentId
  val id: String,

  val hospital_name: String,

  val hospital_image_path: String,

  val booked_at: Timestamp,

  val check_in_at: Timestamp,

  val check_out_at: Timestamp,

  val room_cost_per_day: Double,

  val status: String
) {
  constructor() : this(
    "",
    "",
    "",
    Timestamp.now(),
    Timestamp.now(),
    Timestamp.now(),
    0.0,
    ""
  )
}