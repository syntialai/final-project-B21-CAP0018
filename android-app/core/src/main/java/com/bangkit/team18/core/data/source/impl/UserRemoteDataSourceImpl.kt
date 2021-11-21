package com.bangkit.team18.core.data.source.impl

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
import com.bangkit.team18.core.api.source.service.UserService
import com.bangkit.team18.core.data.source.UserRemoteDataSource
import com.bangkit.team18.core.data.source.base.BaseRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@ExperimentalCoroutinesApi
class UserRemoteDataSourceImpl(
  private val userService: UserService
) : UserRemoteDataSource, BaseRemoteDataSource() {

  override suspend fun updateUser(
    userProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): UserResponse {
    val fileBody = image?.asRequestBody("image/*".toMediaTypeOrNull())
    val filePart = fileBody?.let {
      MultipartBody.Part.createFormData("image", image.name.orEmpty(), it)
    }
    return userService.updateUserProfile(userProfileRequest, filePart)
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
}

