package com.bangkit.team18.core.data.source.api.request.auth

data class RegisterUserRequest(

  var phone_number: String? = null,

  var name: String? = null,

  var image: String? = null,

  var birth_date: Long? = null
)
