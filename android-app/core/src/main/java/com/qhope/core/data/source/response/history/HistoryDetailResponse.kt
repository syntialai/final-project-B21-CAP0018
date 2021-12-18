package com.qhope.core.data.source.response.history

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

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

  val check_out_at: Timestamp? = null,

  val room_type: RoomTypeHistoryResponse,

  val room_cost_per_day: Double,

  val status: String,

  val referral_letter_name: String,

  val referral_letter_url: String,

  val user_id: String,

  val user_data: UserHistoryResponse
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
    RoomTypeHistoryResponse(),
    0.0,
    "",
    "",
    "",
    "",
    UserHistoryResponse()
  )
}