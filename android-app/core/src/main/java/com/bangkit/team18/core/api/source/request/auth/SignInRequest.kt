package com.bangkit.team18.core.api.source.request.auth

data class SignInRequest(

  var phone_number: String? = null,

  var otp: String? = null
)
