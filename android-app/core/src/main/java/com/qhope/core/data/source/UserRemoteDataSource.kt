package com.qhope.core.data.source

import com.qhope.core.data.source.request.user.IdentityConfirmationRequest
import com.qhope.core.data.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.source.response.user.UserResponse
import java.io.File

interface UserRemoteDataSource {

  suspend fun updateUser(
    userProfileRequest: UpdateUserProfileRequest,
    image: File? = null
  ): UserResponse

  suspend fun getUserProfile(): UserResponse

  suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): UserResponse

  suspend fun confirmUserIdentity(
    identityConfirmationRequest: IdentityConfirmationRequest
  ): UserResponse
}