package com.bangkit.team18.core.domain.usecase

import android.net.Uri
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserUseCase {

  suspend fun uploadUserImage(userId: String, imageUri: Uri): Flow<ResponseWrapper<Uri>>

  suspend fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>>

  suspend fun getUser(userId: String): Flow<ResponseWrapper<User?>>

  suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File? = null
  ): Flow<ResponseWrapper<Boolean>>

  suspend fun uploadUserKtp(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>>
  suspend fun uploadUserSelfie(userId: String, uri: Uri): Flow<ResponseWrapper<Uri>>
  suspend fun updateUserVerification(
    userId: String,
    ktpUrl: String,
    selfieUrl: String
  ): Flow<ResponseWrapper<Boolean>>

  suspend fun updatePersonalData(
    userId: String,
    user: User
  ): Flow<ResponseWrapper<Boolean>>
}