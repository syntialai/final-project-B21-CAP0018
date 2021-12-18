package com.qhope.core.data.source.response.history

import com.google.firebase.Timestamp

data class UserHistoryResponse(

  val no_ktp: String,

  val name: String,

  val place_of_birth: String,

  val birth_date: Timestamp,

  val gender: String,

  val phone_number: String
) {
  constructor() : this(
    "",
    "",
    "",
    Timestamp.now(),
    "",
    ""
  )
}
