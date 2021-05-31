package com.bangkit.team18.core.data.source.response.user

import com.bangkit.team18.core.domain.model.user.VerificationStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class UserResponse(
  @DocumentId
  val id: String,
  val name: String,
  val phone_number: String,
  val image_url: String,
  val birth_date: Timestamp?,
  val verification_status: VerificationStatus?
) {
  constructor() : this(
    "",
    "",
    "",
    "",
    null,
    null
  )
}
