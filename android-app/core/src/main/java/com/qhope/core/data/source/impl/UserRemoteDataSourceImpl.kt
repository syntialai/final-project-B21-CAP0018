package com.qhope.core.data.source.impl

import com.qhope.core.data.source.request.user.IdentityConfirmationRequest
import com.qhope.core.data.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.source.response.user.UserResponse
import com.qhope.core.data.source.service.UserService
import com.qhope.core.data.mapper.UserMapper
import com.qhope.core.data.source.UserRemoteDataSource
import com.qhope.core.data.source.base.BaseRemoteDataSource
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
    val requestMap = UserMapper.constructUpdateUserRequest(userProfileRequest, image)
    val photoBody = requestMap["photo"]
    val photo = UserMapper.constructImageFile(image, photoBody)
    return userService.updateUserProfile(requestMap, photo)
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
}

