package com.qhope.core.data.source.response.user

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.qhope.core.domain.model.user.GenderType
import com.qhope.core.domain.model.user.VerificationStatus

@Suppress("unused")
data class UserResponse(
  @DocumentId
  val id: String,
  val name: String,
  val phone_number: String,
  val image_url: String,
  val birth_date: Timestamp?,
  val verification_status: VerificationStatus?,
  val no_ktp: String,
  val gender: GenderType?,
  val place_of_birth: String,
  val address: String
) {
  constructor() : this(
    "",
    "",
    "",
    "",
    null,
    null,
    "",
    null,
    "",
    ""
  )
}
