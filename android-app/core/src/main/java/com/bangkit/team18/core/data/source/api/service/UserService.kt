package com.bangkit.team18.core.data.source.api.service

import com.bangkit.team18.core.data.source.api.config.ApiConstants
import com.bangkit.team18.core.data.source.api.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.source.api.response.user.UploadVerificationResponse
import com.bangkit.team18.core.data.source.api.response.user.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserService {

  @GET(ApiConstants.USER_PROFILE)
  suspend fun getUserProfile(): UserResponse

  @PUT(ApiConstants.USER_PROFILE_UPDATE)
  suspend fun updateUserProfile(updateUserProfileRequest: UpdateUserProfileRequest)

  @Multipart
  @POST(ApiConstants.UPLOAD_VERIFICATION_ATTACHMENTS)
  suspend fun uploadVerificationAttachments(
    @Part ktp: MultipartBody.Part,
    @Part selfie: MultipartBody.Part
  ): UploadVerificationResponse

  @POST(ApiConstants.USER_SIGN_OUT)
  suspend fun signOut()
}