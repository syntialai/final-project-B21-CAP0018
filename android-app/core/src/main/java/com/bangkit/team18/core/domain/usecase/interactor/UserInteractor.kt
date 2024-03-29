package com.bangkit.team18.core.domain.usecase.interactor

import com.bangkit.team18.core.api.source.request.user.IdentityConfirmationRequest
import com.bangkit.team18.core.api.source.request.user.UpdateUserProfileRequest
import com.bangkit.team18.core.data.source.response.wrapper.ResponseWrapper
import com.bangkit.team18.core.domain.model.user.User
import com.bangkit.team18.core.domain.repository.UserRepository
import com.bangkit.team18.core.domain.usecase.UserUseCase
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