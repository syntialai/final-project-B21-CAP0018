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

  val address: String = "",

  val ktpAddress: String = "",

  val bloodType: String = "",

  val district: String = "",

  val village: String = "",

  val city: String = "",

  val neighborhood: String = "",

  val hamlet: String = "",

  val religion: String = ""
)
