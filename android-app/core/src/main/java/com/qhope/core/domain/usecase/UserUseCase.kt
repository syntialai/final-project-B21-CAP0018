package com.qhope.core.domain.usecase

import com.qhope.core.api.source.request.user.IdentityConfirmationRequest
import com.qhope.core.api.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserUseCase {

  suspend fun getUserProfile(maskNik: Boolean = false): Flow<ResponseWrapper<User>>

  suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File? = null
  ): Flow<ResponseWrapper<User>>

  suspend fun uploadUserVerification(ktp: File, selfie: File): Flow<ResponseWrapper<User>>

  suspend fun confirmUserIdentity(
    identityConfirmationRequest: IdentityConfirmationRequest
  ): Flow<ResponseWrapper<Boolean>>
}