package com.bangkit.team18.core.domain.model.user

data class User(
  val id: String = "",
  val name: String = "",
  val phoneNumber: String = "",
  val imageUrl: String = "",
  var birthDate: Long? = null,
  val verificationStatus: VerificationStatus? = null,
  val ktpNumber: String = "",
  val gender: GenderType? = null,
  val placeOfBirth: String = "",
  val address: String = ""
)
