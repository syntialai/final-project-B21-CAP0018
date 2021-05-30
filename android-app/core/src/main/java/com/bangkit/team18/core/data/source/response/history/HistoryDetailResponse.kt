package com.bangkit.team18.core.data.source.response.history

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

// TODO: Add user and letter data
data class HistoryDetailResponse(

  @DocumentId
  val id: String,

  val hospital_id: String,

  val hospital_name: String,

  val hospital_image_path: String,

  val hospital_address: String,

  val hospital_type: String,

  val booked_at: Timestamp,

  val check_in_at: Timestamp,

  val check_out_at: Timestamp,

  val room_type: String,

  val room_cost_per_day: Double,

  val status: String,

  val user_id: String
) {
  constructor() : this(
    "",
    "",
    "",
    "",
    "",
    "",
    Timestamp.now(),
    Timestamp.now(),
    Timestamp.now(),
    "",
    0.0,
    "",
    ""
  )
}