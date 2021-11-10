package com.bangkit.team18.core.domain.usecase

import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserUseCase {

  suspend fun addUser(userId: String, user: User): Flow<ResponseWrapper<Boolean>>

  suspend fun getUser(userId: String): Flow<ResponseWrapper<User?>>

  suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File? = null
  ): Flow<ResponseWrapper<Boolean>>

  suspend fun uploadUserVerification(ktp: File, selfie: File): Flow<ResponseWrapper<Boolean>>
}