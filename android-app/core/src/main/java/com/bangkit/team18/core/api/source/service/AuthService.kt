package com.bangkit.team18.core.api.source.service

import com.bangkit.team18.core.api.source.request.auth.RegisterUserRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.config.ApiConstants
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

  @POST(ApiConstants.USER_REGISTER)
  suspend fun registerUser(@Body registerUserRequest: RegisterUserRequest): UserResponse
}