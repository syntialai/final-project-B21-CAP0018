package com.bangkit.team18.core.domain.model.history

data class UserHistory(

  val ktpNumber: String,

  val name: String,

  val birthDate: String,

  val gender: String,

  val phoneNumber: String
) {
  constructor() : this(
    "",
    "",
    "",
    "",
    ""
  )
}
