package com.bangkit.team18.core.api.source.response.user

data class UserResponse(

  var id: String? = null,

  var name: String? = null,

  var phone_number: String? = null,

  var image_url: String? = null,

  var birth_date: Long? = null,

  var verification_status: String? = null,

  var no_ktp: String? = null,

  var gender: String? = null,

  var place_of_birth: String? = null,

  var address: String? = null
)