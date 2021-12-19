package com.qhope.core.data.source.service

import com.qhope.core.data.source.request.auth.RegisterUserRequest
import com.qhope.core.data.source.response.user.UserResponse
import com.qhope.core.config.ApiConstants
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

  @POST(ApiConstants.USER)
  suspend fun registerUser(@Body registerUserRequest: RegisterUserRequest): UserResponse
}