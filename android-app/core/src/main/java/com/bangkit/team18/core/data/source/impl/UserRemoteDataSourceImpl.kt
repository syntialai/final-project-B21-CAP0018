package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.request.user.IdentityConfirmationRequest
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.api.source.service.UserService
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class UserRemoteDataSourceImpl(
  private val userService: UserService
) : UserRemoteDataSource, BaseRemoteDataSource() {

  override suspend fun updateUser(
    userProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): UserResponse {
    val imageFileBody = image?.asRequestBody("image/*".toMediaTypeOrNull())
    val requestMap = mapOf(
      "image" to imageFileBody,
      "name" to getTextRequestBody(userProfileRequest.name),
      "date_of_birth" to getTextRequestBody(userProfileRequest.date_of_birth.toString()),
      "gender" to getTextRequestBody(userProfileRequest.gender),
      "phone_number" to getTextRequestBody(userProfileRequest.phone_number),
      "birth_place" to getTextRequestBody(userProfileRequest.birth_place),
      "address" to getTextRequestBody(userProfileRequest.address)
    )
    return userService.updateUserProfile(requestMap)
  }

  override suspend fun getUserProfile(): UserResponse {
    return userService.getUserProfile()
  }

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): UserResponse {
    val ktpFileBody = ktp.asRequestBody("image/*".toMediaTypeOrNull())
    val ktpFilePart = MultipartBody.Part.createFormData("ktp", ktp.name.orEmpty(), ktpFileBody)

    val selfieFileBody = selfie.asRequestBody("image/*".toMediaTypeOrNull())
    val selfieFilePart =
      MultipartBody.Part.createFormData("selfie", selfie.name.orEmpty(), selfieFileBody)

    return userService.uploadUserVerification(ktpFilePart, selfieFilePart)
  }

  override suspend fun confirmUserIdentity(
    identityConfirmationRequest: IdentityConfirmationRequest
  ): UserResponse {
    return userService.confirmUserIdentity(identityConfirmationRequest)
  }

  private fun getTextRequestBody(value: String?): RequestBody? {
    return value?.toRequestBody("text/plain".toMediaTypeOrNull());
  }
}

