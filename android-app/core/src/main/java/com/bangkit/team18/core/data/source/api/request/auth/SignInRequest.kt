package com.bangkit.team18.core.data.source.api.request.auth

data class SignInRequest(

  var phone_number: String? = null,

  var otp: String? = null
)
