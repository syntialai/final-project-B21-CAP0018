package com.bangkit.team18.core.api.source.service

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.config.ApiConstants
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface UserService {

  @GET(ApiConstants.USER_PROFILE)
  suspend fun getUserProfile(): UserResponse

  @Multipart
  @PATCH(ApiConstants.USER_PROFILE_UPDATE)
  suspend fun updateUserProfile(
    @Body updateUserProfileRequest: UpdateUserProfileRequest,
    @Part image: MultipartBody.Part? = null
  ): UserResponse

  @Multipart
  @PATCH(ApiConstants.USER_PROFILE_UPDATE)
  suspend fun updateUserProfile(
    @Body updateUserProfileRequest: UpdateUserProfileRequest,
    @Part ktp: MultipartBody.Part,
    @Part selfie: MultipartBody.Part
  ): UserResponse

  @POST(ApiConstants.USER_SIGN_OUT)
  suspend fun signOut()
}