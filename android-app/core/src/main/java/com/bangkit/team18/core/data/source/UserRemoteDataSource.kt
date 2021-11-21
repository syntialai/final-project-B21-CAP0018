package com.bangkit.team18.core.data.source

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.api.source.response.user.UserResponse
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
}