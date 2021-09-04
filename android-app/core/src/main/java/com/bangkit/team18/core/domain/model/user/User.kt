package com.bangkit.team18.core.domain.model.user

import com.google.firebase.Timestamp

data class User(
  val id: String = "",
  val name: String = "",
  val phoneNumber: String = "",
  val imageUrl: String = "",
  val birthDate: Timestamp? = null,
  val verificationStatus: VerificationStatus? = null,
  val ktpNumber: String = "",
  val gender: GenderType? = null,
  val placeOfBirth: String = "",
  val address: String = ""
)
