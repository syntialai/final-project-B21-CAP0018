package com.bangkit.team18.core.data.source.api.service

import com.bangkit.team18.core.data.source.api.config.ApiConstants
import com.bangkit.team18.core.data.source.api.request.auth.RegisterUserRequest
import com.bangkit.team18.core.data.source.api.request.auth.SignInRequest
import retrofit2.http.POST

interface AuthService {

  @POST(ApiConstants.SIGN_IN)
  fun signIn(signInRequest: SignInRequest)

  @POST(ApiConstants.USER_REGISTER)
  fun registerUser(registerUserRequest: RegisterUserRequest)
}