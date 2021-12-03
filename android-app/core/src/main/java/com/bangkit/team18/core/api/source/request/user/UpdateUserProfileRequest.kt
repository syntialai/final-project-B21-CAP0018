package com.bangkit.team18.core.api.source.request.user

data class UpdateUserProfileRequest(

  var name: String? = null,

  var date_of_birth: Long? = null,

  var gender: String? = null,

  var phone_number: String? = null,

  val birth_place: String? = null,

  val address: String? = null
)
