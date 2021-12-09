package com.bangkit.team18.core.api.source.service

import com.bangkit.team18.core.api.source.request.user.IdentityConfirmationRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.config.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface UserService {

  @GET(ApiConstants.USER)
  suspend fun getUserProfile(): UserResponse

  @Multipart
  @PATCH(ApiConstants.USER)
  suspend fun updateUserProfile(
    @PartMap requestMap: @JvmSuppressWildcards Map<String, RequestBody>,
    @Part photo: MultipartBody.Part
  ): UserResponse

  @Multipart
  @POST(ApiConstants.USER_IDENTITY_VERIFICATION)
  suspend fun uploadUserVerification(
    @Part ktp: MultipartBody.Part,
    @Part selfie: MultipartBody.Part
  ): UserResponse

  @POST(ApiConstants.USER_IDENTITY_CONFIRMATION)
  suspend fun confirmUserIdentity(
    @Body identityConfirmationRequest: IdentityConfirmationRequest
  ): UserResponse

  @POST(ApiConstants.USER_SIGN_OUT)
  suspend fun signOut()
}