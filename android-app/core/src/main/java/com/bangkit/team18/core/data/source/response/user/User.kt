package com.bangkit.team18.core.data.source.response.user

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class User(
  @DocumentId
  val id: String,
  val name: String,
  val phoneNumber: String,
  val imageUrl: String,
  val birthDate: Timestamp?
) {
  constructor() : this(
    "",
    "",
    "",
    "",
    null
  )
}
