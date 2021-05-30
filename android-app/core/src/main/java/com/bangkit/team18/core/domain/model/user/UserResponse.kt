package com.bangkit.team18.core.domain.model.user

import com.google.firebase.Timestamp


data class User(
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
