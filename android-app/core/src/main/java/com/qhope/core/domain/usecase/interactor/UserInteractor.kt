package com.qhope.core.domain.usecase.interactor

import com.qhope.core.api.source.request.user.IdentityConfirmationRequest
import com.qhope.core.api.source.request.user.UpdateUserProfileRequest
import com.qhope.core.data.source.response.wrapper.ResponseWrapper
import com.qhope.core.domain.model.user.User
import com.qhope.core.domain.repository.UserRepository
import com.qhope.core.domain.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import java.io.File

class UserInteractor(private val userRepository: UserRepository) : UserUseCase {

  override suspend fun getUserProfile(maskNik: Boolean): Flow<ResponseWrapper<User>> =
    userRepository.getUserProfile(maskNik)

  override suspend fun updateUser(
    updateUserProfileRequest: UpdateUserProfileRequest,
    image: File?
  ): Flow<ResponseWrapper<User>> = userRepository.updateUser(updateUserProfileRequest, image)

  override suspend fun uploadUserVerification(
    ktp: File,
    selfie: File
  ): Flow<ResponseWrapper<User>> {
    return userRepository.uploadUserVerification(ktp, selfie)
  }

  override suspend fun confirmUserIdentity(
    identityConfirmationRequest: IdentityConfirmationRequest
  ): Flow<ResponseWrapper<Boolean>> {
    return userRepository.confirmUserIdentity(identityConfirmationRequest)
  }
}